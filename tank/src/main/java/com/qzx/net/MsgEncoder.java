package com.qzx.net;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * @Auther: qzx
 * @Date: 2020/9/2 - 09 - 02 - 10:12 上午
 * @Description: com.qzx.netty.test09
 * 自定义编码器
 * @version: 1.0
 */
public class MsgEncoder extends MessageToByteEncoder<Msg> {

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Msg msg, ByteBuf byteBuf) throws Exception {
        // 首先写入消息头，分为消息类型和消息长度
        byteBuf.writeInt(msg.getMsgType().ordinal());//消息头
        byte[] bytes = msg.toBytes();
        byteBuf.writeInt(bytes.length);//消息长度
        byteBuf.writeBytes(bytes);// 消息内容
    }
}
