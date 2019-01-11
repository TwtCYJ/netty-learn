package com.twist.netty.factorial;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.math.BigInteger;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * @description: ${description}
 * @author: chenyingjie
 * @create: 2019-01-11 13:34
 **/
public class FactorialClientHandler extends SimpleChannelInboundHandler<BigInteger> {

    private ChannelHandlerContext ctx;
    private int received;
    private int next = 1;
    final BlockingDeque<BigInteger> answer = new LinkedBlockingDeque<>();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, BigInteger msg) throws Exception {
        received++;
        if (received == FactorialClient.COUNT) {
            ctx.channel().close().addListener((ChannelFutureListener) future -> {
                boolean offered = answer.offer(msg);
                assert offered;
            });
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        this.ctx = ctx;
        sendNumbers();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

    public BigInteger getFactorial() {
        boolean interrupted = false;
        try {
            for (; ; ) {
                try {
                    return answer.take();
                } catch (InterruptedException ignore) {
                    interrupted = true;
                }
            }
        } finally {
            if (interrupted) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private void sendNumbers() {
        ChannelFuture future = null;
        for (int i = 0; i < 4096 && next <= FactorialClient.COUNT; i++) {
            future = ctx.write(Integer.valueOf(next));
            next++;
        }
        if (next <= FactorialClient.COUNT) {
            assert future != null;
            future.addListener(numberSender);
        }
        ctx.flush();
    }

    private final ChannelFutureListener numberSender = future -> {
        if (future.isSuccess()) {
            sendNumbers();
        } else {
            future.cause().printStackTrace();
            future.channel().close();
        }
    };
}
