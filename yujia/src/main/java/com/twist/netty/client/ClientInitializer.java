package com.twist.netty.client;

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
 * @create: 2019-01-07 10:32
 **/
public class ClientInitializer extends ChannelInitializer<SocketChannel> {

    private static final StringDecoder DECODER = new StringDecoder();
    private static final StringEncoder ENCODER = new StringEncoder();

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline pipeline = socketChannel.pipeline();
        //设置 读超时事件 写超时事件 所有类型超时时间 单位
        //传输协议、编码和解码应该一致，还有心跳的读写时间应该小于服务端所设置的时间 这里就是会10秒发一次心跳包
        pipeline.addLast(new IdleStateHandler(0, 10, 0, TimeUnit.SECONDS));

        pipeline.addLast(DECODER);
        pipeline.addLast(ENCODER);

        //业务逻辑处理器
        pipeline.addLast("clientHandler",new ClientHandler());
    }
}
