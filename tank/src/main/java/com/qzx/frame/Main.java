package com.qzx.frame;

import com.qzx.net.Client;

import java.util.concurrent.TimeUnit;

/**
 * @Auther: qzx
 * @Date: 2020/8/5 - 08 - 05 - 7:22
 * @Description: com.qzx.frame
 * @version: 1.0
 */
public class Main {
    public static void main(String[] args) {
        // 启动单独一个线程绘制图形界面，
        new Thread(() -> {
            while (true) {
                try {
                    TimeUnit.MILLISECONDS.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                TankFrame.INSTANCE.repaint();
            }
        }).start();
        Client.INSTANCE.connect();
    }
}
