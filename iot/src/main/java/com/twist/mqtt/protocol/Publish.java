package com.twist.mqtt.protocol;

import io.netty.channel.Channel;
import io.netty.handler.codec.mqtt.MqttPublishMessage;
import io.netty.handler.codec.mqtt.MqttQoS;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;

/**
 * @description: 发布消息
 * @author: chenyingjie
 * @create: 2019-01-07 19:34
 **/
@Slf4j
public class Publish {

    public void processPublish(Channel channel, MqttPublishMessage msg) {
        String clientId = (String) channel.attr(AttributeKey.valueOf("clientId")).get();
        //Qos=0
        if (msg.fixedHeader().qosLevel() == MqttQoS.AT_MOST_ONCE) {
            byte[] messageBytes = new byte[msg.payload().readableBytes()];
            msg.payload().getBytes(msg.payload().readerIndex(),messageBytes);

        }
    }


}
