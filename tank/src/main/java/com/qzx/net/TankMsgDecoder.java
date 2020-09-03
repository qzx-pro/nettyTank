package com.qzx.net;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * @Auther: qzx
 * @Date: 2020/9/2 - 09 - 02 - 10:15 上午
 * @Description: com.qzx.netty.test09
 * @version: 1.0
 */
public class TankMsgDecoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        if(byteBuf.readableBytes()<8) return;// 解决tcp拆包、粘包问题

        int x = byteBuf.readInt();// 先写的x就先读x
        int y = byteBuf.readInt();

        list.add(new TankMsg(x,y));
    }
}
