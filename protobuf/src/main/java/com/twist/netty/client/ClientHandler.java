package com.twist.netty.client;

import com.twist.netty.protobuf.UserMsg;
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
        log.info("连接客户端：" + new Date());
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
     * 心跳触发 这里就会是4秒一次发包
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
                UserMsg.User.Builder state = UserMsg.User.newBuilder().setState(2);
                ctx.channel().writeAndFlush(state);
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
            if (msg instanceof UserMsg.User) {
                UserMsg.User user = (UserMsg.User) msg;
                //这里就打印一下user的值
                log.info(user.toString());

                //返回接收到了 改变状态
                UserMsg.User.Builder state = UserMsg.User.newBuilder().setState(1);
                ctx.writeAndFlush(state);
                log.info("发送回去给服务端啦");
            } else {
                log.info("error data:" + msg);
                return;
            }
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }
}
