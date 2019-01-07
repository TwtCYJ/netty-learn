package com.twist.netty.server;

import com.twist.netty.protobuf.UserMsg;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @description: ${description}
 * @author: chenyingjie
 * @create: 2019-01-04 16:13
 **/
@Slf4j
public class ServerHandler extends ChannelInboundHandlerAdapter {

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
     * 心跳检查,如果5秒没接收到客户端心跳就触发，超时两次关闭连接
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
                log.info("已经5秒没有接收到客户端的信息了");
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

    /**
     * 业务逻辑
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        log.info("服务端接收到数据：第" + count.get() + "次，内容为：" + msg );
        try {
            if (msg instanceof UserMsg.User) {
                UserMsg.User user = (UserMsg.User) msg;
                if (user.getState() == 1) {
                    log.info("success");
                } else if (user.getState() == 2) {
                    log.info("heart packet");
                } else {
                    log.info("unknown");
                }
            } else {
                log.info("error data:" + msg);
                return;
            }
        }finally {
            //继承ChannelInboundHandlerAdapter则不会自动释放，需要手动调用ReferenceCountUtil.release()等方法进行释放。
            // 继承该类不需要指定数据格式。
            // 所以在这里，个人推荐服务端继承ChannelInboundHandlerAdapter，手动进行释放，防止数据未处理完就自动释放了。
            ReferenceCountUtil.release(msg);
        }
        count.getAndIncrement();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
