package com.qzx.test;

import com.qzx.frame.Dir;
import com.qzx.frame.Group;
import com.qzx.frame.Tank;
import com.qzx.net.TankJoinMsg;
import com.qzx.net.TankJoinMsgDecoder;
import com.qzx.net.TankJoinMsgEncoder;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
import org.junit.Assert;
import org.junit.Test;


import java.util.UUID;

/**
 * @Auther: qzx
 * @Date: 2020/9/3 - 09 - 03 - 10:52 上午
 * @Description: com.qzx.test
 * @version: 1.0
 */
public class TankJoinMsgTest {
    @Test
    public void TankJoinMsgEncoderTest(){
        //编码器测试
        Tank tank = new Tank();
        TankJoinMsg tankJoinMsg = new TankJoinMsg(1,2, Dir.DOWN,false, Group.ALLY,tank.id);
        EmbeddedChannel channel = new EmbeddedChannel();
        channel.pipeline().addLast(new TankJoinMsgEncoder());
        channel.writeOutbound(tankJoinMsg);
        ByteBuf byteBuf = (ByteBuf) channel.readOutbound();
        int x = byteBuf.readInt();
        int y = byteBuf.readInt();
        int dirOrdinal = byteBuf.readInt();
        Dir dir = Dir.values()[dirOrdinal];
        boolean moving = byteBuf.readBoolean();
        int groupOrdinal = byteBuf.readInt();
        Group group = Group.values()[groupOrdinal];
        long msb = byteBuf.readLong();
        long lsb = byteBuf.readLong();
        UUID uuid = new UUID(msb,lsb);
        Assert.assertTrue(x==1&&y==2&&dir==Dir.DOWN&& !moving &&group==Group.ALLY&& uuid.equals(tank.id));
    }

    @Test
    public void TankJoinMsgDecoderTest(){
        Tank tank = new Tank();
        TankJoinMsg tankJoinMsg = new TankJoinMsg(1,2, Dir.DOWN,false, Group.ALLY,tank.id);
        ByteBuf byteBuf = Unpooled.buffer();
        byteBuf.writeInt(tankJoinMsg.x);// 4个字节
        byteBuf.writeInt(tankJoinMsg.y);// 4个字节
        byteBuf.writeInt(tankJoinMsg.dir.ordinal());// 写入当前坦克方向在Dir类中的枚举下标,4个字节
        byteBuf.writeBoolean(tankJoinMsg.moving);// 1个字节
        byteBuf.writeInt(tankJoinMsg.group.ordinal());// 4个字节
        byteBuf.writeLong(tankJoinMsg.id.getMostSignificantBits()); // 写入uuid的高64位 8字节
        byteBuf.writeLong(tankJoinMsg.id.getLeastSignificantBits()); // 写入uuid的低64位 8字节

        EmbeddedChannel channel = new EmbeddedChannel();
        channel.pipeline().addLast(new TankJoinMsgDecoder());
        channel.writeInbound(byteBuf);
        TankJoinMsg msg = (TankJoinMsg) channel.readInbound();
        Assert.assertTrue(msg.x==tankJoinMsg.x&&msg.y==tankJoinMsg.y
                &&msg.dir==tankJoinMsg.dir
                &&msg.moving==tankJoinMsg.moving
                &&msg.id.equals(tankJoinMsg.id)&&msg.group==tankJoinMsg.group);
    }
}
