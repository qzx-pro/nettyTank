package com.qzx.net;

import com.qzx.frame.Bullet;
import com.qzx.frame.Explode;
import com.qzx.frame.Tank;
import com.qzx.frame.TankFrame;

import java.io.*;
import java.util.UUID;

/**
 * @Auther: qzx
 * @Date: 2020/9/7 - 09 - 07 - 4:28 下午
 * @Description: com.qzx.net
 * @version: 1.0
 */
public class TankDieMsg extends Msg {
    public UUID bid;// 击杀坦克的子弹id
    public UUID tid;// 被击杀的坦克id
    private int x, y;// 坦克爆炸的位置

    public TankDieMsg(UUID bid, UUID tid, int x, int y) {
        this.bid = bid;
        this.tid = tid;
        this.x = x;
        this.y = y;
    }

    public TankDieMsg() {
    }

    @Override
    public MsgType getMsgType() {
        return MsgType.TankDieMsg;
    }

    @Override
    public void handle() {
        // 接受到的消息如果是自己发的就不做处理
        if (this.bid.equals(TankFrame.INSTANCE.tank.id)) {
            return;
        }
        // 将当前被击杀的坦克在所有其他客户端中进行处理
        Tank enemy = TankFrame.INSTANCE.getTank(this.tid);
        Bullet bullet = TankFrame.INSTANCE.getBullet(this.bid);
        bullet.isAlive = false; // 子弹消失
        Explode explode = new Explode(x, y, TankFrame.INSTANCE);
        TankFrame.INSTANCE.explodes.add(explode);
        if (enemy != null) {
            // 该被杀的坦克在本客户端中是敌人,移除该坦克
            enemy.isAlive = false;
        } else {
            // 说明是本客户端主战坦克被击杀
            TankFrame.INSTANCE.tank.isAlive = false;
        }
    }

    @Override
    public byte[] toBytes() {
        ByteArrayOutputStream baos = null;
        DataOutputStream dos = null;
        try {
            baos = new ByteArrayOutputStream();
            dos = new DataOutputStream(baos);

            dos.writeLong(bid.getMostSignificantBits()); // 写入uuid的高64位 8字节
            dos.writeLong(bid.getLeastSignificantBits()); // 写入uuid的低64位 8字节
            dos.writeLong(tid.getMostSignificantBits()); // 写入uuid的高64位 8字节
            dos.writeLong(tid.getLeastSignificantBits()); // 写入uuid的低64位 8字节
            dos.writeInt(x);// 4个字节
            dos.writeInt(y);// 4个字节
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
            this.bid = new UUID(dis.readLong(), dis.readLong());
            this.tid = new UUID(dis.readLong(), dis.readLong());
            this.x = dis.readInt();
            this.y = dis.readInt();
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
        return "TankDieMsg{" +
                "bid=" + bid +
                ", tid=" + tid +
                ", x=" + x +
                ", y=" + y +
                '}';
    }
}
