package com.qzx.net;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * @Auther: qzx
 * @Date: 2020/9/2 - 09 - 02 - 10:12 上午
 * @Description: com.qzx.netty.test09
 * 自定义编码器,将TankJoinMsg转化为byteBuf
 * @version: 1.0
 */
public class TankJoinMsgEncoder extends MessageToByteEncoder<TankJoinMsg> {

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, TankJoinMsg tankJoinMsg, ByteBuf byteBuf) throws Exception {
        // 总共写入33个字节
        byteBuf.writeInt(tankJoinMsg.x);// 4个字节
        byteBuf.writeInt(tankJoinMsg.y);// 4个字节
        byteBuf.writeInt(tankJoinMsg.dir.ordinal());// 写入当前坦克方向在Dir类中的枚举下标,4个字节
        byteBuf.writeBoolean(tankJoinMsg.moving);// 1个字节
        byteBuf.writeInt(tankJoinMsg.group.ordinal());// 4个字节
        byteBuf.writeLong(tankJoinMsg.id.getMostSignificantBits()); // 写入uuid的高64位 8字节
        byteBuf.writeLong(tankJoinMsg.id.getLeastSignificantBits()); // 写入uuid的低64位 8字节
    }
}
