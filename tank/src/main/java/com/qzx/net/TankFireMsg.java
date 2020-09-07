package com.qzx.net;

import com.qzx.frame.*;

import java.io.*;
import java.util.UUID;

/**
 * @Auther: qzx
 * @Date: 2020/9/7 - 09 - 07 - 2:15 下午
 * @Description: com.qzx.net
 * @version: 1.0
 */
public class TankFireMsg extends Msg {
    public UUID id;
    public int x, y;
    public Dir dir;
    public Group group;

    public TankFireMsg(UUID id, int x, int y, Dir dir, Group group) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.dir = dir;
        this.group = group;
    }

    public TankFireMsg() {
    }

    public TankFireMsg(Bullet bullet) {
        this.id = bullet.id;
        this.x = bullet.getX();
        this.y = bullet.getY();
        this.dir = bullet.getDir();
        this.group = bullet.getGroup();
    }

    @Override
    public MsgType getMsgType() {
        return MsgType.TankFireMsg;
    }

    @Override
    public void handle() {
        // 接受到的消息如果是自己发的就不做处理
        if (this.id.equals(TankFrame.INSTANCE.getMyTank().id)) {
            return;
        }
        // 不是自己发的就让对应的敌方坦克按照消息给定的方向和位置开始发射子弹
        Tank enemy = TankFrame.INSTANCE.getTank(this.id);
        if (enemy != null) {
            Bullet bullet = new Bullet(this);// 根据当前消息构造子弹
            TankFrame.INSTANCE.addBullet(bullet);
        }
    }

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
            dos.writeInt(group.ordinal());// 写入当前坦克方向在Group类中的枚举下标,4个字节
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
            this.group = Group.values()[dis.readInt()];
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

    @Override
    public String toString() {
        return "TankFireMsg{" +
                "id=" + id +
                ", x=" + x +
                ", y=" + y +
                ", dir=" + dir +
                ", group=" + group +
                '}';
    }
}
