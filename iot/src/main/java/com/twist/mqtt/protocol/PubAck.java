package com.twist.mqtt.protocol;

import com.twist.mqtt.service.DupPublishMessageStoreService;
import io.netty.channel.Channel;
import io.netty.handler.codec.mqtt.MqttMessageIdVariableHeader;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;

/**
 * @description: PUBACK (发布确认)连接处理
 * @author: chenyingjie
 * @create: 2019-01-07 19:33
 **/
@Slf4j
public class PubAck {

    private DupPublishMessageStoreService dupPublishMessageStoreService;

    public PubAck(DupPublishMessageStoreService dupPublishMessageStoreService) {
        this.dupPublishMessageStoreService = dupPublishMessageStoreService;
    }

    public void processPubAck(Channel channel, MqttMessageIdVariableHeader variableHeader) {
        int messageId = variableHeader.messageId();
        log.info("PUBACK - clientId: {}, messageId: {}", (String) channel.attr(AttributeKey.valueOf("clientId")).get(), messageId);
        dupPublishMessageStoreService.remove((String) channel.attr(AttributeKey.valueOf("clientId")).get(),messageId);
    }
}
