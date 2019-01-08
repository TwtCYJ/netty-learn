package com.twist.mqtt.protocol;

import com.twist.mqtt.service.SubscribeStoreService;
import io.netty.channel.Channel;
import io.netty.handler.codec.mqtt.MqttFixedHeader;
import io.netty.handler.codec.mqtt.MqttMessageFactory;
import io.netty.handler.codec.mqtt.MqttMessageIdVariableHeader;
import io.netty.handler.codec.mqtt.MqttMessageType;
import io.netty.handler.codec.mqtt.MqttQoS;
import io.netty.handler.codec.mqtt.MqttUnsubAckMessage;
import io.netty.handler.codec.mqtt.MqttUnsubscribeMessage;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @description: 取消订阅
 * @author: chenyingjie
 * @create: 2019-01-07 19:36
 **/
@Slf4j
public class UnSubscribe {

    private SubscribeStoreService subscribeStoreService;

    public UnSubscribe(SubscribeStoreService subscribeStoreService) {
        this.subscribeStoreService = subscribeStoreService;
    }

    public void processUnSubscribe(Channel channel, MqttUnsubscribeMessage msg) {
        List<String> topicFilters = msg.payload().topics();
        String clientId = (String) channel.attr(AttributeKey.valueOf("clientId")).get();
        topicFilters.forEach(topicFilter -> {
            subscribeStoreService.remove(topicFilter,clientId);
            log.info("UNSUBSCRIBE - clientId: {}, topicFilter: {}", clientId, topicFilter);
        });
        MqttUnsubAckMessage unsubAckMessage = (MqttUnsubAckMessage) MqttMessageFactory.newMessage(
                new MqttFixedHeader(MqttMessageType.UNSUBACK, false, MqttQoS.AT_MOST_ONCE, false, 0),
                MqttMessageIdVariableHeader.from(msg.variableHeader().messageId()),
                null);
        channel.writeAndFlush(unsubAckMessage);
    }
}
