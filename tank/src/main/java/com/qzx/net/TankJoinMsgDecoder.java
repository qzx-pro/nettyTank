package com.qzx.net;

import com.qzx.frame.Dir;
import com.qzx.frame.Group;
import com.qzx.frame.Tank;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;
import java.util.UUID;

/**
 * @Auther: qzx
 * @Date: 2020/9/2 - 09 - 02 - 10:15 上午
 * @Description: com.qzx.netty.test09
 * @version: 1.0
 */
public class TankJoinMsgDecoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        if(byteBuf.readableBytes()<33) return;// 解决tcp拆包、粘包问题
        Tank tank = new Tank();

        tank.x = byteBuf.readInt();// 先写的x就先读x
        tank.y = byteBuf.readInt();
        int dirOrdinal = byteBuf.readInt();
        tank.dir = Dir.values()[dirOrdinal];
        tank.moving = byteBuf.readBoolean();
        int groupOrdinal = byteBuf.readInt();
        tank.group = Group.values()[groupOrdinal];
        long msb = byteBuf.readLong();
        long lsb = byteBuf.readLong();
        tank.id = new UUID(msb,lsb);
        list.add(new TankJoinMsg(tank));
    }
}
