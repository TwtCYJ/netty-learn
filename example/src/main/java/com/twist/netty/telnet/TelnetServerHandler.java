package com.twist.netty.telnet;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.net.InetAddress;
import java.util.Date;

/**
 * @description: ${description}
 * @author: chenyingjie
 * @create: 2019-01-15 14:16
 **/
public final class TelnetServerHandler extends SimpleChannelInboundHandler<String> {

    /**
     * 建立连接
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ctx.writeAndFlush("收到连接： " + InetAddress.getLocalHost().getHostName() + " , 在 " + new Date() + ".\r\n");
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        String response;
        boolean close = false;
        if (msg.isEmpty()) {
            response = "接收到数据为空!";
        } else if ("9".equals(msg)) {
            response = "再见";
            close = true;
        } else {
            response = "接收到数据为" + msg + "。";
        }

        ChannelFuture future = ctx.write(response);

        if (close) {
            //添加监听端口关闭
            future.addListener(ChannelFutureListener.CLOSE);
        }
    }


    /**
     * 数据写出
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
