package com.twist.server;

import com.twist.protocol.ProtocolProcess;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.mqtt.MqttMessage;

/**
 * @description: mqtt handler
 * @author: chenyingjie
 * @create: 2019-01-07 19:48
 **/
public class MqttTransportHandler extends SimpleChannelInboundHandler<MqttMessage> {

    private ProtocolProcess protocolProcess;

    public MqttTransportHandler(ProtocolProcess process) {
        this.protocolProcess = process;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MqttMessage msg) throws Exception {

    }
}
