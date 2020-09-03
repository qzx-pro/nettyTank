package com.qzx.net;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * @Auther: qzx
 * @Date: 2020/9/1 - 09 - 01 - 5:20 下午
 * @Description: com.qzx.netty.test08
 * @version: 1.0
 */
public class ServerFrame extends Frame {
    private static final ServerFrame INSTANCE = new ServerFrame();
    TextArea taLeft = new TextArea();
    TextArea taRight = new TextArea();
    Server server = new Server();//初次加载就启动服务器

    public static ServerFrame getInstance(){
        return INSTANCE;
    }

    public ServerFrame () {
        this.setSize(1600, 600);
        this.setLocation(300, 30);
        Panel p = new Panel(new GridLayout(1, 2));
        p.add(taLeft);
        p.add(taRight);
        this.add(p);
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
        this.setVisible(true);
    }

    public void updateServerMsg(String newText){
        // 更新服务器的消息，主要是日志相关
        //将当前输入框内的数据显示到文本框中
        String text = taLeft.getText().equals("") ?newText:
                taLeft.getText()+System.getProperty("line.separator") + newText;
        taLeft.setText(text);
    }

    public void updateClientMsg(String newText){
        // 更新客户端发送过来的消息
        //将当前输入框内的数据显示到文本框中
        String text = taRight.getText().equals("") ?newText:
                taRight.getText()+System.getProperty("line.separator") + newText;
        taRight.setText(text);
    }

    public static void main(String[] args) {
        ServerFrame serverFrame = ServerFrame.getInstance();
        serverFrame.server.start();
    }
}
