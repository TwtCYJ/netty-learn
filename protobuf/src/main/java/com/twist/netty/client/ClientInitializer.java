package com.twist.netty.client;

import com.twist.netty.protobuf.UserMsg;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

/**
 * @description: ${description}
 * @author: chenyingjie
 * @create: 2019-01-07 10:32
 **/
public class ClientInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline pipeline = socketChannel.pipeline();
        //设置 读超时事件 写超时事件 所有类型超时时间 单位
        //传输协议、编码和解码应该一致，还有心跳的读写时间应该小于服务端所设置的时间 这里就是会4秒发一次心跳包
        pipeline.addLast(new IdleStateHandler(0, 4, 0, TimeUnit.SECONDS));

        //设置编解码 协议为protbuf
        pipeline.addLast(new ProtobufVarint32FrameDecoder())
                .addLast(new ProtobufDecoder(UserMsg.User.getDefaultInstance()))
                .addLast(new ProtobufVarint32LengthFieldPrepender())
                .addLast(new ProtobufEncoder());

        //业务逻辑处理器
        pipeline.addLast("clientHandler",new ClientHandler());
    }
}
