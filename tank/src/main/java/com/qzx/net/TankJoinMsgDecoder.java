package com.qzx.net;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * @Auther: qzx
 * @Date: 2020/9/2 - 09 - 02 - 10:15 上午
 * @Description: com.qzx.netty.test09
 * @version: 1.0
 */
public class TankJoinMsgDecoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        if (byteBuf.readableBytes() < 33) return;// 解决tcp拆包、粘包问题
        TankJoinMsg tankJoinMsg = new TankJoinMsg();
        byte[] bytes = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(bytes); // 从buf中读取数据,读取的数据保存在bytes中
        tankJoinMsg.parse(bytes);
        list.add(tankJoinMsg);
    }
}
