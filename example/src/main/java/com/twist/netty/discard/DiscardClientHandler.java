package com.twist.netty.discard;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * @description: ${description}
 * @author: chenyingjie
 * @create: 2019-01-11 11:29
 **/
@Slf4j
public class DiscardClientHandler extends SimpleChannelInboundHandler<Object> {

    private ByteBuf byteBuf;

    private ChannelHandlerContext ctx;

    private long couter = 0;


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        //初始化一个消息
        this.ctx = ctx;
        byteBuf = ctx.alloc().directBuffer(256).writeZero(256);
        //重复发送相同消息
        generateTraffice();
        log.debug("发送消息第" + couter + "次!");
    }

    private void generateTraffice() {
        ctx.writeAndFlush(byteBuf.retainedDuplicate()).addListener(trafficeGenerator);
    }

    private final ChannelFutureListener trafficeGenerator = new ChannelFutureListener() {
        @Override
        public void operationComplete(ChannelFuture future) throws Exception {
            if (future.isSuccess()) {
                couter ++ ;
                generateTraffice();
            } else {
                future.cause().printStackTrace();
                future.channel().close();
            }
        }
    };

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        byteBuf.release();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.channel().close();
    }
}
