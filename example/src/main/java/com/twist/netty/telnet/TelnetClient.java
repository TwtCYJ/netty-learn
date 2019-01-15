package com.twist.netty.telnet;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * @description: ${description}
 * @author: chenyingjie
 * @create: 2019-01-15 14:17
 **/
public final class TelnetClient {
    static final boolean SSL = System.getProperty("ssl") != null;
    static final String HOST = System.getProperty("host", "127.0.0.1");
    static final int PORT = Integer.parseInt(System.getProperty("port", SSL? "8992" : "8023"));


    public static void main(String[] args) throws Exception{
        // Configure SSL.
        final SslContext sslCtx;
        if (SSL) {
            sslCtx = SslContextBuilder.forClient()
                    .trustManager(InsecureTrustManagerFactory.INSTANCE).build();
        } else {
            sslCtx = null;
        }


        EventLoopGroup group = new NioEventLoopGroup();

        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .handler(new TelnetClientInitializer(sslCtx));

            Channel ch = b.connect(HOST,PORT).sync().channel();

            ChannelFuture f = null;
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            for (; ; ) {
                String line = reader.readLine();
                if (line == null) {
                    break;
                }

                f = ch.writeAndFlush(line + "\r\n");

                //关闭连接
                if ("9".equals(line)) {
                    ch.closeFuture().sync();
                    break;
                }

                //关闭前要先把所有信息发送出去
                if (f != null) {
                    f.sync();
                }
            }
        } finally {
            group.shutdownGracefully();
        }
    }

}
