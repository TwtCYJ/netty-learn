package com.twist.netty.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 * @description: 服务端启动器
 * @author: chenyingjie
 * @create: 2019-01-03 16:57
 **/
public final class Server {
    public static void main(String[] args) throws Exception{
        //创建两个 EventLoopGroup boos和worker
        //用于接收客户端连接
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        //用于进行socketChannel 的数据读写
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            //创建ServerBootstrap 对象
            ServerBootstrap b = new ServerBootstrap();
            //设置EventLoopGroup
            b.group(bossGroup, workerGroup)
                    //设置将被实例化的NioServerSocketChannel类
                    .channel(NioServerSocketChannel.class)
                    //设置处理器
                    .handler(new LoggingHandler(LogLevel.INFO))
                    //设置客户端处理器
                    .childHandler(new ServerInitializer());

            // 绑端口
            ChannelFuture f = b.bind(8282);
            //关闭,阻塞等待
            f.channel().closeFuture().sync();
        } finally {
            //最后关闭对象
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }
}
