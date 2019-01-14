package com.twist.netty.file;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.util.CharsetUtil;

/**
 * @description: ${description}
 * @author: chenyingjie
 * @create: 2019-01-11 14:01
 **/
public class FileServerInitializer extends ChannelInitializer<SocketChannel> {

    private final SslContext sslCtx;

    public FileServerInitializer(SslContext sslCtx) {
        this.sslCtx = sslCtx;
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();

        if (sslCtx != null) {
            pipeline.addLast(sslCtx.newHandler(ch.alloc()));
        }

        pipeline.addLast(new StringEncoder(CharsetUtil.UTF_8));
        pipeline.addLast(new LineBasedFrameDecoder(8192));
        pipeline.addLast(new StringDecoder(CharsetUtil.UTF_8));
        pipeline.addLast(new ChunkedWriteHandler());
        pipeline.addLast(new FileServerHandler());
    }
}
