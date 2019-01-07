package com.twist.netty.server;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

/**
 * @description: ${description}
 * @author: chenyingjie
 * @create: 2019-01-04 16:03
 **/
public class ServerInitializer extends ChannelInitializer<SocketChannel> {

    private static final StringDecoder DECODER = new StringDecoder();
    private static final StringEncoder ENCODER = new StringEncoder();


    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline pipeline = socketChannel.pipeline();
        //设置 读超时事件 写超时事件 所有类型超时时间 单位 12
        pipeline.addLast(new IdleStateHandler(12, 0, 0, TimeUnit.SECONDS));

        pipeline.addLast(DECODER);
        pipeline.addLast(ENCODER);

        //业务逻辑处理器
        pipeline.addLast("serverHandler",new ServerHandler());
    }
}
