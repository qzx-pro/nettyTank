package com.qzx.net;

import com.qzx.frame.Dir;
import com.qzx.frame.Group;
import com.qzx.frame.Tank;
import com.qzx.frame.TankFrame;

import java.io.*;
import java.util.UUID;

/**
 * @Auther: qzx
 * @Date: 2020/9/2 - 09 - 02 - 10:11 上午
 * @Description: com.qzx.netty.test09
 * 坦克加入消息
 * @version: 1.0
 */
public class TankJoinMsg extends Msg {
    public int x, y; // 位置
    public Dir dir;//坦克的初始方向
    public boolean moving = false;//标识坦克是否移动,用来实现坦克静止,初始状态没有移动
    public Group group;//当前坦克的敌友标识
    public UUID id; // 坦克的唯一标识

    public TankJoinMsg(int x, int y, Dir dir, boolean moving, Group group, UUID id) {
        this.x = x;
        this.y = y;
        this.dir = dir;
        this.moving = moving;
        this.group = group;
        this.id = id;
    }

    public TankJoinMsg(Tank tank) {
        this.x = tank.x;
        this.y = tank.y;
        this.dir = tank.dir;
        this.moving = tank.moving;
        this.group = tank.group;
        this.id = tank.id;
    }

    public TankJoinMsg() {
    }

    @Override
    public String toString() {
        return "TankJoinMsg{" +
                "x=" + x +
                ", y=" + y +
                ", dir=" + dir +
                ", moving=" + moving +
                ", group=" + group +
                ", id=" + id +
                '}';
    }

    @Override
    public MsgType getMsgType() {
        return MsgType.TankJoinMsg;
    }

    /*
        消息处理函数:
        对于每一个客户端接受到服务端发送的有新的坦克加入游戏，首先判断该消息是不是自己发的，
        然后判断列表中是否已经存在当前接受到消息的坦克，最后再给新加入的坦克发送自己在游戏中的消息。
        如果消息是自己发的无需做处理，只需接受其他坦克给自己发消息就行，然后把它们添加到敌方坦克列表中即可。
        如果不是自己发的消息，需要根据该消息构建坦克并且添加到自己的敌方列表中，然后自己给敌方发消息让
        它添加自己到它的敌方列表中.
        如果已经将其加入到敌方坦克中就无需处理当前消息。这一点非常重要，如果没有判断双方会一直发送消息。
         */
    @Override
    public void handle() {
        UUID id = TankFrame.INSTANCE.getMyTank().id;// 自己主战坦克的id
        if (this.id.equals(id) || TankFrame.INSTANCE.getTank(this.id) != null) {
            // 发送消息的就是自己或者发送消息的坦克已经添加到自己的敌方列表中不做处理
            return;
        }
        // 该加入消息不是自己发的，并且敌方坦克没有被添加到列表中了
        this.group = Group.ENEMY;// 标志敌方坦克
        Tank tank = new Tank(this);// 根据当前消息构建坦克
        TankFrame.INSTANCE.addEnemyTank(tank);// 添加敌方坦克（相对于当前客户端）
        // 将当前客户端的主战坦克发送给敌方
        Client.INSTANCE.send(Client.tankJoinMsg);
    }

    // 将该消息转化为字节数组
    @Override
    public byte[] toBytes() {
        ByteArrayOutputStream baos = null;
        DataOutputStream dos = null;
        try {
            baos = new ByteArrayOutputStream();
            dos = new DataOutputStream(baos);

            dos.writeInt(x);// 4个字节
            dos.writeInt(y);// 4个字节
            dos.writeInt(dir.ordinal());// 写入当前坦克方向在Dir类中的枚举下标,4个字节
            dos.writeBoolean(moving);// 1个字节
            dos.writeInt(group.ordinal());// 4个字节
            dos.writeLong(id.getMostSignificantBits()); // 写入uuid的高64位 8字节
            dos.writeLong(id.getLeastSignificantBits()); // 写入uuid的低64位 8字节
            dos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                assert dos != null;
                dos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                baos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return baos.toByteArray();
    }


    // 将字节数组转化为该消息
    @Override
    public void parse(byte[] bytes) {
        ByteArrayInputStream bais = null;
        DataInputStream dis = null;
        try {
            bais = new ByteArrayInputStream(bytes);
            dis = new DataInputStream(bais);
            this.x = dis.readInt();
            this.y = dis.readInt();
            int dirOrdinal = dis.readInt();
            this.dir = Dir.values()[dirOrdinal];
            this.moving = dis.readBoolean();
            int groupOrdinal = dis.readInt();
            this.group = Group.values()[groupOrdinal];
            long msb = dis.readLong();
            long lsb = dis.readLong();
            this.id = new UUID(msb, lsb);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                assert dis != null;
                dis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                bais.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
