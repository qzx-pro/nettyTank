package com.qzx.net;

import com.qzx.frame.TankFrame;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetSocketAddress;

public class Client {
    public static final Client INSTANCE = new Client();
    private Channel channel = null;
    public static final TankJoinMsg tankJoinMsg = new TankJoinMsg(TankFrame.INSTANCE.tank);//当前主战坦克消息

    private Client() {
    }

    public void connect() {
        // 事件处理的线程池
        EventLoopGroup eventLoopGroup = new NioEventLoopGroup();
        // 辅助启动器
        Bootstrap bootstrap = new Bootstrap();

        try {
            ChannelFuture future = bootstrap.group(eventLoopGroup)// 指定线程池
                    .channel(NioSocketChannel.class) // 指定Channel的类型
                    .handler(new InitChannelHandler()) // 对于当前Channel的处理器
                    .connect(new InetSocketAddress("localhost", 8888));

            future.addListener(new ChannelFutureListener() { // connect过程监听器
                @Override
                public void operationComplete(ChannelFuture channelFuture) throws Exception {
                    if (channelFuture.isSuccess()){
                        System.out.println("connected!");
                        // 连接成功就初始化聚合的channel
                        channel = channelFuture.channel();
                    }else {
                        System.out.println("not connected!");
                    }
                }
            });
            future.sync(); // 等待ChannelFuture的结束
            System.out.println("client started!");
            future.channel().closeFuture().sync();// 阻塞当前线程
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            eventLoopGroup.shutdownGracefully();
        }
    }

    public void send(TankJoinMsg msg) {
        // 发送消息
        channel.writeAndFlush(msg);
    }


    public static void main(String[] args) {
        new Client().connect();
    }
}

class InitChannelHandler extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel channel) throws Exception {
        // Channel进行初始化完毕后就进行后续的数据处理操作
        ChannelPipeline pipeline = channel.pipeline();
        pipeline.addLast(new TankJoinMsgEncoder())   // 添加编码器
                .addLast(new TankJoinMsgDecoder())   // 添加解码器
                .addLast(new ClientReadHandler());
    }
}

class ClientReadHandler extends SimpleChannelInboundHandler<TankJoinMsg> {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        // 通道可用的时候就写一个主战坦克的TankJoinMsg的消息给服务器
        ctx.writeAndFlush(Client.tankJoinMsg);
    }


    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, TankJoinMsg tankJoinMsg) throws Exception {
        tankJoinMsg.handle();//消息处理逻辑
    }
}
