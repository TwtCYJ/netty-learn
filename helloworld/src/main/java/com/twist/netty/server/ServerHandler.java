package com.twist.netty.server;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.net.Inet4Address;
import java.util.Date;

/**
 * @description: ${description}
 * @author: chenyingjie
 * @create: 2019-01-03 17:23
 **/
@ChannelHandler.Sharable
public class ServerHandler extends SimpleChannelInboundHandler<String> {

    /**
     * 这里处理业务
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        String response;
        boolean close = false;
        if (msg.isEmpty()) {
            response = "null ~~~\r\n";
        } else if ("byebye".equals(msg)) {
            response = "bye bye bye!\r\n";
            close = true;
        } else {
            response = "收到客户端说：" + msg + "\r\n";
        }
        ChannelFuture f = ctx.write(response);

        if (close) {
            f.addListener(ChannelFutureListener.CLOSE);
        }
    }

    /**
     * 建立连接
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ctx.write("连接上啦～：" + Inet4Address.getLocalHost().getHostName() +  "; 在 " + new Date() + "\r\n");
        ctx.flush();
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        ctx.close();
    }
}
