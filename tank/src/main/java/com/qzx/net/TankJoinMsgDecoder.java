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
        if (byteBuf.readableBytes() < 8) return;// 如果消息头没有读取完整就返回
        // 记录当前位置
        byteBuf.markReaderIndex();
        // 首先读取消息类型
        MsgType msgType = MsgType.values()[byteBuf.readInt()];
        // 然后读取消息长度
        int length = byteBuf.readInt();
        // 判断后面的消息长度是否小于length,是就返回
        if (byteBuf.readableBytes() < length) {
            byteBuf.resetReaderIndex();// 重置读取位置
            return;
        }
        byte[] bytes = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(bytes); // 从buf中读取数据,读取的数据保存在bytes中
        // 根据不同的消息类型进行不同的处理
        switch (msgType) {
            case TankJoinMsg:
                TankJoinMsg tankJoinMsg = new TankJoinMsg();
                tankJoinMsg.parse(bytes);
                list.add(tankJoinMsg);
                break;
            default:
                break;
        }

    }
}
