package com.qzx.net;

/**
 * @Auther: qzx
 * @Date: 2020/9/2 - 09 - 02 - 10:11 上午
 * @Description: com.qzx.netty.test09
 * 自定义消息
 * @version: 1.0
 */
public class TankMsg {
    public int x,y;

    public TankMsg(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public String toString() {
        return "TankMsg{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}
