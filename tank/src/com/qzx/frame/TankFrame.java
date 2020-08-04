package com.qzx.frame;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.Frame;
import java.awt.Graphics;
/**
 * @Auther: qzx
 * @Date: 2020/8/5 - 08 - 05 - 7:20
 * @Description: com.qzx.frame
 * @version: 1.0
 */
public class TankFrame extends Frame{
    int x=200,y=200;//初始位置
    public TankFrame() {
        this.setSize(800, 600);//初始大小
        this.setResizable(false);//设置大小不可变
        this.setVisible(true);//设置可见
        this.setTitle("tank war");//设置标题
        //设置关闭窗口响应事件
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
        //响应键盘事件
        this.addKeyListener(new MyKey());
    }
    @Override
    public void paint(Graphics g) {
        g.fillRect(x, y, 50, 50);
    }
    class MyKey extends KeyAdapter {

        @Override
        public void keyPressed(KeyEvent e) {
            int key = e.getKeyCode();
            switch (key) {
                case KeyEvent.VK_LEFT:
                    x -= 10;
                    break;
                case KeyEvent.VK_RIGHT:
                    x += 10;
                    break;
                case KeyEvent.VK_UP:
                    y -= 10;
                    break;
                case KeyEvent.VK_DOWN:
                    y += 10;
                    break;
                default:
                    break;
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {
            int key = e.getKeyCode();
            switch (key) {
                case KeyEvent.VK_LEFT:
                    x -= 10;
                    break;
                case KeyEvent.VK_RIGHT:
                    x += 10;
                    break;
                case KeyEvent.VK_UP:
                    y -= 10;
                    break;
                case KeyEvent.VK_DOWN:
                    y += 10;
                    break;
                default:
                    break;
            }
        }

    }
}
