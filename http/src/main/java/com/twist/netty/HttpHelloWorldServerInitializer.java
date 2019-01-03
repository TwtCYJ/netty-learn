package com.twist.netty;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.HttpServerExpectContinueHandler;

/**
 * @description: ${description}
 * @author: chenyingjie
 * @create: 2019-01-03 22:02
 **/
public class HttpHelloWorldServerInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        // 也可以使用HttpRequestDecoder & HttpResponseEncoder
        pipeline.addLast(new HttpServerCodec());

        //处理post
        pipeline.addLast(new HttpObjectAggregator(1024 * 1024));
        pipeline.addLast(new HttpServerExpectContinueHandler());
        pipeline.addLast(new HttpHelloWorldServerHandler());
    }
}
