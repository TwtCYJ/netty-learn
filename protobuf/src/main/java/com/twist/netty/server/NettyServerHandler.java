package com.twist.netty.server;

import com.twist.netty.protobuf.UserMsg;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @description: ${description}
 * @author: chenyingjie
 * @create: 2019-01-04 16:13
 **/
@Slf4j
public class NettyServerHandler extends ChannelInboundHandlerAdapter {

    //用原子操作 有助于提高性能
    /** 空闲次数 */
    private AtomicInteger idle_count = new AtomicInteger(1);
    /** 发送次数 */
    private AtomicInteger count = new AtomicInteger(1);

    /**
     * 这是建立连接,看看连接的客户端是什么
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("连接的客户端IP：" + ctx.channel().remoteAddress());
        UserMsg.User user = UserMsg.User.newBuilder().setId(1).setName("yingjie").setAge(26).setState(0).build();
        ctx.writeAndFlush(user);
        super.channelActive(ctx);
    }


    /**
     * 心跳检查
     * @param ctx
     * @param evt
     * @throws Exception
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            //这里就判断 event 这个读通道是不是空闲
            if (IdleState.READER_IDLE.equals(event.state())) {
                if (idle_count.get() > 1) {
                    log.info("关闭掉不活跃的连接：" + ctx.channel().remoteAddress());
                    ctx.channel().close();
                }
                idle_count.getAndIncrement();
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }
}
