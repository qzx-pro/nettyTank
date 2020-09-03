package com.qzx.net;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.GlobalEventExecutor;

public class Server {
    // 一组Channel,用来保存所有连接到该服务器的客户端的Channel
    public static ChannelGroup clients = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    public void start(){
        // 主线程池，就像是餐厅老板做接待工作(初始化)
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        // 工作线程池，就像是餐厅的服务员做服务(数据处理)
        EventLoopGroup workerGroup = new NioEventLoopGroup(2);
        // 辅助启动器
        ServerBootstrap bootstrap = new ServerBootstrap();

        try {
            ChannelFuture future = bootstrap.group(bossGroup,workerGroup)// 指定线程池
                    .channel(NioServerSocketChannel.class)
                    .handler(new SimpleServerHandler()) // 服务器启动的流程
                    // 这里指定的handler是child也就是workerGroup的线程进行处理,在Channel初始化完毕后执行
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            // 初始化Channel后就可以获取客户端发送的数据了
                            ChannelPipeline pipeline = socketChannel.pipeline();
                            pipeline.addLast(new TankJoinMsgDecoder())    // 添加解码器
                                    .addLast(new ServerReadHandler());
                        }
                    })
                    .bind(8888)
                    .sync();// 等待绑定端口成功
            ServerFrame.getInstance().updateServerMsg("server started!");
            future.channel().closeFuture().sync();//阻塞当前线程
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new Server().start();
    }
}

class ServerReadHandler extends ChannelInboundHandlerAdapter{
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Server.clients.add(ctx.channel());// 将初始化的通道放在通道组里面
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        // 现在接受的msg就是TankMsg类型的数据
        TankJoinMsg tankJoinMsg = (TankJoinMsg) msg;
        ServerFrame.getInstance().updateClientMsg(tankJoinMsg.toString());
        ReferenceCountUtil.release(msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        // 处理异常，同时关闭上下文，这样客户端那边就会关闭Future
        cause.printStackTrace();
        Server.clients.remove(ctx.channel());
        ctx.close();
    }
}

class SimpleServerHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ServerFrame.getInstance().updateServerMsg("channelActive");
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        ServerFrame.getInstance().updateServerMsg("channelRegistered");
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        ServerFrame.getInstance().updateServerMsg("handlerAdded");
    }
}