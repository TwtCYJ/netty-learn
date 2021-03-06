package com.twist.netty.client;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

/**
 * @description: ${description}
 * @author: chenyingjie
 * @create: 2019-01-03 17:44
 **/
public class ClientInitializer extends ChannelInitializer<SocketChannel> {

    private static final StringDecoder DECODER = new StringDecoder();
    private static final StringEncoder ENCODER = new StringEncoder();

    private static final ClientHandler CLIENT_HANDLER = new ClientHandler();

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();

        //这里用帧限定符 Delimiters.lineDelimiter() 防止粘包
        pipeline.addLast(new DelimiterBasedFrameDecoder(8192, Delimiters.lineDelimiter()));
        //编解码
        pipeline.addLast(ENCODER);
        pipeline.addLast(DECODER);

        //业务处理
        pipeline.addLast(CLIENT_HANDLER);
    }
}
