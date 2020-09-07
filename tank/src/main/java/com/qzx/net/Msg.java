package com.qzx.net;

/**
 * @Auther: qzx
 * @Date: 2020/9/7 - 09 - 07 - 9:18 上午
 * @Description: com.qzx.net
 * @version: 1.0
 */
public abstract class Msg {
    public abstract MsgType getMsgType();// 获取消息类型

    public abstract void handle(); // 消息处理

    public abstract byte[] toBytes(); // 消息编码

    public abstract void parse(byte[] bytes); // 消息解码
}
