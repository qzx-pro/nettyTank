package com.qzx.frame;

import com.qzx.net.Client;
import com.qzx.net.TankFireMsg;
import com.qzx.net.TankJoinMsg;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;
import java.util.UUID;

public class Tank {
    public int x,y;//初始位置
    public Dir dir ;//坦克的初始方向
    public static final int SPEED = 5;//坦克移动的速度
    public boolean moving = false;//标识坦克是否移动,用来实现坦克静止,初始状态没有移动
    public TankFrame tf;
    static final int TANK_WIDTH = ResourceManager.tankD.getWidth();//坦克宽度
    static final int TANK_HEIGHT = ResourceManager.tankD.getHeight();//坦克高度
    public boolean isAlive = true;//坦克是否消失(遭到敌方攻击时消失)
    public Group group;//当前坦克的敌友标识
    Random random = new Random();//让坦克随机发射子弹
    boolean init = true;//是否是初始状态
    Rectangle recTank = null;//坦克的所处位置的矩形，用来做碰撞检测
    public UUID id = UUID.randomUUID(); // 坦克的唯一标识

    public Tank(TankJoinMsg tankJoinMsg) {
        this.x = tankJoinMsg.x;
        this.y = tankJoinMsg.y;
        this.dir = tankJoinMsg.dir;
        this.moving = tankJoinMsg.moving;
        this.group = tankJoinMsg.group;
        this.id = tankJoinMsg.id;
        recTank = new Rectangle(x, y, TANK_WIDTH, TANK_HEIGHT);
    }

    public Tank(int x, int y, Dir dir, TankFrame tf, Group group) {
        this.x = x;
        this.y = y;
        this.dir = dir;
        this.tf = tf;
        this.group = group;
        recTank = new Rectangle(x, y, TANK_WIDTH, TANK_HEIGHT);
    }

    public Group getGroup() {
        return group;
    }

    public boolean isMoving() {
        return moving;
    }

    public void setMoving(boolean moving) {
        this.moving = moving;
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

    public void setGroup(Group group) {
        this.group = group;
    }

    public Tank() {
    }

    public Dir getDir() {
        return dir;
    }

    public void setDir(Dir dir) {
        this.dir = dir;
    }

    public void paint(Graphics g) {
        //坦克消失不用画出
        if (!this.isAlive) {
            return;
        }
        Color color = g.getColor();
        g.setColor(Color.cyan);
        g.drawString(id.toString(), x, y - 10);
        g.setColor(color);
        switch (dir) {
            case LEFT:
                BufferedImage tankL = group == Group.ALLY ? ResourceManager.MyTankL : ResourceManager.tankL;
                g.drawImage(tankL, x, y, null);
                break;
            case RIGHT:
                BufferedImage tankR = group == Group.ALLY ? ResourceManager.MyTankR : ResourceManager.tankR;
                g.drawImage(tankR, x, y, null);
                break;
            case UP:
                BufferedImage tankU = group == Group.ALLY ? ResourceManager.MyTankU : ResourceManager.tankU;
                g.drawImage(tankU,x,y,null);
                break;
            case DOWN:
                BufferedImage tankD = group == Group.ALLY ? ResourceManager.MyTankD : ResourceManager.tankD;
                g.drawImage(tankD,x,y,null);
                break;
        }
        move();//坦克移动
    }

    private void move() {
        if(!isMoving()) return;//没有移动
        if (init&&group==Group.ALLY){
            //我方坦克在初始化时不要移动
            init = false;
            setMoving(false);
            return;
        }
        //根据坦克的方向进行移动
        switch (dir){
            case LEFT:
                x -= SPEED;
                break;
            case RIGHT:
                x += SPEED;
                break;
            case UP:
                y -= SPEED;
                break;
            case DOWN:
                y += SPEED;
                break;
        }
        //在移动过程中需要做边界检测，不能移动到边界之外
        boundCheck();
        //更新recTank的位置
        recTank.x = x;
        recTank.y = y;
    }

    private void boundCheck() {
        boolean isOutOfBound = false;//判断是否越界
        if (this.x<20){
            x = 20;
            isOutOfBound = true;
        }
        if (this.y<40) {
            y = 40;
            isOutOfBound = true;
        }
        if (this.x>TankFrame.GAME_WIDTH-Tank.TANK_WIDTH-20){
            x = TankFrame.GAME_WIDTH-Tank.TANK_WIDTH-20;
            isOutOfBound = true;
        }
        if (this.y>TankFrame.GAME_HEIGHT-Tank.TANK_HEIGHT-20) {
            y = TankFrame.GAME_HEIGHT-Tank.TANK_HEIGHT-20;
            isOutOfBound = true;
        }
        if (isOutOfBound&&group==Group.ENEMY){
            randomDir();//敌方坦克在越界后直接改变方向
        }
    }

    private void randomDir() {
        this.setDir(Dir.values()[random.nextInt(4)]);
    }

    public void fire() {
        int x = this.x + TANK_WIDTH / 2 - ResourceManager.bulletD.getWidth() / 2;//发射子弹的初始位置x
        int y = this.y + TANK_HEIGHT / 2 - ResourceManager.bulletD.getHeight() / 2;//发射子弹的初始位置y
        Bullet bullet = new Bullet(x, y, this.dir, this.tf, this.group, this.id);
        tf.bullets.add(bullet);
        bullet.setGroup(Group.ENEMY);// 发送消息给其他用户的的当前坦克发射的子弹为敌方子弹
        Client.INSTANCE.send(new TankFireMsg(bullet));
    }

    @Override
    public String toString() {
        return "Tank{" +
                "x=" + x +
                ", y=" + y +
                ", dir=" + dir +
                ", moving=" + moving +
                ", tf=" + tf +
                ", isAlive=" + isAlive +
                ", group=" + group +
                ", random=" + random +
                ", init=" + init +
                ", recTank=" + recTank +
                ", id=" + id +
                '}';
    }
}
