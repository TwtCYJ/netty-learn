package com.twist.mqtt.server;

import com.twist.mqtt.protocol.ProtocolProcess;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.mqtt.MqttDecoder;
import io.netty.handler.codec.mqtt.MqttEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.ResourceLeakDetector;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * @description: mqtt 服务器
 * @author: chenyingjie
 * @create: 2019-01-07 19:41
 **/
@Component
@ConfigurationProperties(prefix = "mqtt")
@Slf4j
public class MqttServer {

    @Value("${mqtt.host}")
    private String host;
    @Value("${mqtt.port}")
    private Integer port;
    @Value("${mqtt.adaptor}")
    private String adaptorName;
    @Value("${mqtt.timeout}")
    private int timeout;
    @Value("${mqtt.netty.use_epoll}")
    private boolean useEpoll;

    @Value("${mqtt.netty.leak_detector_level}")
    private String leakDetectorLevel;
    @Value("${mqtt.netty.boss_group_thread_count}")
    private Integer bossGroupThreadCount;
    @Value("${mqtt.netty.worker_group_thread_count}")
    private Integer workerGroupThreadCount;
    @Value("${mqtt.netty.max_payload_size}")
    private Integer maxPayloadSize;

    @Autowired
    private ProtocolProcess protocolProcess;

    private Channel serverChannel;
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;

    @PostConstruct
    public void init() throws Exception {
        log.info("Setting resource leak detector level to {}", leakDetectorLevel);
        ResourceLeakDetector.setLevel(ResourceLeakDetector.Level.valueOf(leakDetectorLevel.toUpperCase()));
        log.info("Starting MQTT transport...");

        log.info("Starting MQTT transport server");
        bossGroup = useEpoll? new EpollEventLoopGroup(bossGroupThreadCount) : new NioEventLoopGroup(bossGroupThreadCount);
        workerGroup = useEpoll? new EpollEventLoopGroup(workerGroupThreadCount) : new NioEventLoopGroup(workerGroupThreadCount);
        ServerBootstrap b = new ServerBootstrap();
        b.group(bossGroup, workerGroup)
                .channel(useEpoll ? EpollServerSocketChannel.class : NioServerSocketChannel.class)
                .handler(new LoggingHandler(LogLevel.INFO))
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        ChannelPipeline pipeline = socketChannel.pipeline();
                        pipeline.addLast("decoder", new MqttDecoder(maxPayloadSize));
                        pipeline.addLast("encoder", MqttEncoder.INSTANCE);
                        // Netty提供的心跳检测
                        pipeline.addFirst("idle", new IdleStateHandler(0, 0, timeout));
//                        pipeline.addLast("idleStateHandler", new IdleStateHandler(10, 2, 12, TimeUnit.SECONDS));
                        MqttTransportHandler handler = new MqttTransportHandler(protocolProcess);
                        pipeline.addLast(handler);
                    }
                });

        serverChannel = b.bind(host, port).sync().channel();

        log.info("Mqtt transport started!");
    }

    @PreDestroy
    public void shutdown() throws InterruptedException {
        log.info("Stopping MQTT transport!");
        try {
            serverChannel.close().sync();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
        log.info("MQTT transport stopped!");
    }
}
