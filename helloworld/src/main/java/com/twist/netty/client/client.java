package com.twist.netty.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * @description: ${description}
 * @author: chenyingjie
 * @create: 2019-01-03 17:41
 **/
public class client {

    public static void main(String[] args) throws Exception {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group).channel(NioSocketChannel.class).handler(new ClientInitializer());
            Channel channel = b.connect("127.0.0.1", 8282).sync().channel();
            ChannelFuture write = null;
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            for (; ; ) {
                String line = in.readLine();
                if (line == null) {
                    break;
                }
                write = channel.writeAndFlush(line + "\r\n");

                if ("byebye".equals(line.toLowerCase())) {
                    channel.closeFuture().sync();
                    break;
                }
            }

            if (write != null) {
                write.sync();
            }
        } finally {
            group.shutdownGracefully();
        }
    }
}
