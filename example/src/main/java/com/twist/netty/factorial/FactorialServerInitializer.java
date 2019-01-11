package com.twist.netty.factorial;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.ssl.SslContext;

/**
 * @description: ${description}
 * @author: chenyingjie
 * @create: 2019-01-11 13:07
 **/
public class FactorialServerInitializer extends ChannelInitializer<SocketChannel> {

    private final SslContext sslCtx;

    public FactorialServerInitializer(SslContext sslCtx) {
        this.sslCtx = sslCtx;
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();

        if (sslCtx != null) {
            pipeline.addLast(sslCtx.newHandler(ch.alloc()));
        }

        //添加编解码
        pipeline.addLast(new BigIntegerDecoder());
        pipeline.addLast(new NumberEncoder());

        //添加处理器
        pipeline.addLast(new FactorialServerHandler());
    }
}
