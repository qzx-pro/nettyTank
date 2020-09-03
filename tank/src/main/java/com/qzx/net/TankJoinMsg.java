package com.qzx.net;

import com.qzx.frame.Dir;
import com.qzx.frame.Group;
import com.qzx.frame.Tank;

import java.util.UUID;

/**
 * @Auther: qzx
 * @Date: 2020/9/2 - 09 - 02 - 10:11 上午
 * @Description: com.qzx.netty.test09
 * 坦克加入消息
 * @version: 1.0
 */
public class TankJoinMsg {
    public int x,y; // 位置
    public Dir dir ;//坦克的初始方向
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
}
