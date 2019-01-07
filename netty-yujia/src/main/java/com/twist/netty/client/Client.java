package com.twist.netty.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoop;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.concurrent.TimeUnit;

/**
 * @description: ${description}
 * @author: chenyingjie
 * @create: 2019-01-07 10:31
 **/
@Slf4j
@Service("nettyClient")
public class Client {

    @Value("${server.host}")
    private String host;
    @Value("${server.port}")
    private Integer port;
    // 客户端唯一标志
    private boolean flag = true;

    private EventLoopGroup group;
    private ChannelFuture channelFuture;

    /**
     * Netty创建全部都是实现自AbstractBootstrap。 客户端的是Bootstrap，服务端的则是 ServerBootstrap。
     **/
    @PostConstruct
    public void init() {
        group = new NioEventLoopGroup();
        reConnect(new Bootstrap(), group);
    }

    /**
     * 重连业务
     *
     * @param bootstrap
     * @param group
     */
    public void reConnect(Bootstrap bootstrap, EventLoopGroup group) {
        try {
            if (bootstrap != null) {
                bootstrap.group(group);
                bootstrap.channel(NioSocketChannel.class);
                bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
                bootstrap.handler(new ClientInitializer());
                bootstrap.remoteAddress(host, port);
                channelFuture = bootstrap.connect().addListener((ChannelFuture listener) -> {
                    final EventLoop eventLoop = listener.channel().eventLoop();
                    if (!listener.isSuccess()) {
                        log.info("断开连接，即将尝试重新连接");
                        eventLoop.schedule(() -> reConnect(new Bootstrap(), eventLoop), 10, TimeUnit.SECONDS);
                    }
                });
                if (flag) {
                    log.info("started client");
                    flag = false;
                }
            }
        } catch (Exception e) {
            log.info("connetc server fail:" + e.getMessage());
        }
    }

    @PreDestroy
    public void shutdown() throws InterruptedException {
        log.info("stopping client ......");
        try {
            channelFuture.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully();
        }
        log.info("stopped client");
    }


}
