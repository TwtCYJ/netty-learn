package com.twist.netty.factorial;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.ssl.SslContext;

/**
 * @description: ${description}
 * @author: chenyingjie
 * @create: 2019-01-11 13:34
 **/
public class FactorialClientInitializer extends ChannelInitializer<SocketChannel> {

    /**
     * 直接复制啦
     */
    private final SslContext sslCtx;

    public FactorialClientInitializer(SslContext sslCtx) {
        this.sslCtx = sslCtx;
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();

        if (sslCtx != null) {
            pipeline.addLast(sslCtx.newHandler(ch.alloc(), FactorialClient.HOST, FactorialClient.PORT));
        }

        //这里要和服务端一致
        pipeline.addLast(new BigIntegerDecoder());
        pipeline.addLast(new NumberEncoder());

        pipeline.addLast(new FactorialClientHandler());

    }
}
