package com.qzx.frame;

import com.qzx.net.Client;
import com.qzx.net.TankDieMsg;
import com.qzx.net.TankFireMsg;

import java.awt.*;
import java.util.UUID;

public class Bullet {
    private int x,y;//初始位置
    private Dir dir;//子弹的初始方向
    private static final int SPEED = 10;//子弹移动的速度
    private static final int BULLET_WIDTH = ResourceManager.bulletD.getWidth();//子弹的宽度
    private static final int BULLET_HEIGHT = ResourceManager.bulletD.getHeight();//子弹的高度
    public boolean isAlive = true;//子弹是否消失
    private TankFrame tf;
    Group group;//当前发射的子弹的敌友标识,和发射该子弹的坦克的标识一致
    Rectangle recBullet = null;
    public UUID id;

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

    public Bullet(int x, int y, Dir dir, TankFrame tf, Group group, UUID id) {
        this.x = x;
        this.y = y;
        this.dir = dir;
        this.tf = tf;
        this.group = group;
        this.id = id;
        recBullet = new Rectangle(x, y, BULLET_WIDTH, BULLET_HEIGHT);
    }

    public Bullet() {
    }

    public Bullet(TankFireMsg msg) {
        this.x = msg.x;
        this.y = msg.y;
        this.dir = msg.dir;
        this.tf = TankFrame.INSTANCE;
        this.group = msg.group;
        this.id = msg.id;
        recBullet = new Rectangle(x, y, BULLET_WIDTH, BULLET_HEIGHT);
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public Dir getDir() {
        return dir;
    }

    public void setDir(Dir dir) {
        this.dir = dir;
    }

    public void paint(Graphics g) {
        switch (dir) {
            case LEFT:
                g.drawImage(ResourceManager.bulletL, x, y, null);
                break;
            case RIGHT:
                g.drawImage(ResourceManager.bulletR, x, y, null);
                break;
            case UP:
                g.drawImage(ResourceManager.bulletU,x,y,null);
                break;
            case DOWN:
                g.drawImage(ResourceManager.bulletD,x,y,null);
                break;
        }
        move();//子弹移动
    }

    private void move() {
        //根据子弹的方向进行移动
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
        if (x < 0 || x > tf.GAME_WIDTH || y < 0 || y > tf.GAME_HEIGHT) {
            //超过边界，设置子弹消失状态
            isAlive = false;
        }
        //更新recBullet的位置
        recBullet.x = x;
        recBullet.y = y;
    }

    //子弹与坦克发生碰撞检测
    public void collideWith(Tank tank) {
        if (this.id.equals(tank.id)) return;//如果是我方发射的子弹不做碰撞检测,也就是不开启队友伤害
        if (recBullet.intersects(tank.recTank)) {
            //如果2个矩形相交就说明发生碰撞
            this.isAlive = false;
            tank.isAlive = false;
            int x = tank.getX() + Tank.TANK_WIDTH / 2 - Explode.WIDTH / 2;//爆炸的位置x为坦克的中心位置x
            int y = tank.getY() + Tank.TANK_HEIGHT / 2 - Explode.HEIGHT / 2;//爆炸的位置y为坦克的中心位置y
            Explode explode = new Explode(x, y, tf);
            tf.explodes.add(explode);
            Client.INSTANCE.send(new TankDieMsg(this.id, tank.id, x, y));
        }
    }

    @Override
    public String toString() {
        return "Bullet{" +
                "x=" + x +
                ", y=" + y +
                ", dir=" + dir +
                ", isAlive=" + isAlive +
                ", tf=" + tf +
                ", group=" + group +
                ", recBullet=" + recBullet +
                ", id=" + id +
                '}';
    }
}
