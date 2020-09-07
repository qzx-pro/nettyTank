package com.qzx.net;

import com.qzx.frame.Dir;
import com.qzx.frame.Tank;
import com.qzx.frame.TankFrame;

import java.io.*;
import java.util.UUID;

/**
 * @Auther: qzx
 * @Date: 2020/9/7 - 09 - 07 - 10:40 上午
 * @Description: com.qzx.net
 * @version: 1.0
 */
public class TankMoveMsg extends Msg {
    public int x, y;// 坦克开始移动的位置
    public Dir dir;// 坦克移动的方向
    public UUID id;

    public TankMoveMsg(int x, int y, Dir dir, UUID id) {
        this.x = x;
        this.y = y;
        this.dir = dir;
        this.id = id;
    }

    public TankMoveMsg() {
    }

    public TankMoveMsg(Tank tank) {
        this.x = tank.x;
        this.y = tank.y;
        this.dir = tank.dir;
        this.id = tank.id;
    }

    @Override
    public MsgType getMsgType() {
        return MsgType.TankMoveMsg;
    }

    @Override
    public void handle() {
        // 接受到的消息如果是自己发的就不做处理
        if (this.id.equals(TankFrame.INSTANCE.getMyTank().id)) {
            return;
        }
        // 不是自己发的就让对应的敌方坦克按照消息给定的方向和位置开始移动
        Tank enemy = TankFrame.INSTANCE.getTank(this.id);
        if (enemy != null) {
            enemy.setX(x);
            enemy.setY(y);
            enemy.setMoving(true);
            enemy.setDir(dir);
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

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public Dir getDir() {
        return dir;
    }

    public void setDir(Dir dir) {
        this.dir = dir;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "TankMoveMsg{" +
                "x=" + x +
                ", y=" + y +
                ", dir=" + dir +
                ", id=" + id +
                '}';
    }
}
