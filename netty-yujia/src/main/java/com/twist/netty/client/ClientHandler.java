package com.twist.netty.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.EventLoop;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @description: ${description}
 * @author: chenyingjie
 * @create: 2019-01-07 10:32
 **/
// 主要是为了多个handler可以被多个channel安全地共享，也就是保证线程安全。
@ChannelHandler.Sharable
@Slf4j
public class ClientHandler extends ChannelInboundHandlerAdapter {

    @Autowired
    private Client client;

    /**
     * 循环次数
     */
    private AtomicInteger count = new AtomicInteger(1);

    /**
     * 建立连接时
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("连接服务端：" + new Date());
        ctx.fireChannelActive();
    }

    /**
     * 关闭连接时
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.info("关闭连接时间为" + new Date());
        final EventLoop eventLoop = ctx.channel().eventLoop();
        client.reConnect(new Bootstrap(), eventLoop);
        super.channelInactive(ctx);
    }

    /**
     * 心跳触发 这里就会是10秒一次发包
     *
     * @param ctx
     * @param evt
     * @throws Exception
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        log.info("心跳请求时间：" + new Date() + ",次：" + count.get());
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            //这里判断写通道是不是空闲的 如果是就发送心跳
            if (IdleState.WRITER_IDLE.equals(event.state())) {
                ctx.channel().writeAndFlush("FA FB 2F 00 00 00 00 00 01 20 05 01 00 11 01 25 3F 00 00 38 00 00 00 02 12 21 00 00 00 00 00 03 00 00 00 00 00 00 00 02 01 07 50 02 50 08 25 00 00 00 00 00 02 07 10 02 63 19 26 00 00 00 00 00 00 72 4B");
                count.getAndIncrement();
            }
        }
    }

    /**
     * 业务处理
     *
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            /**
             * 这里书写业务逻辑
             */
            log.info("收到信息：" + msg);
        } finally {
            ReferenceCountUtil.release(msg);
        }

    }
}
