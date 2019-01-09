package com.twist.mqtt.protocol;

import com.twist.mqtt.pojo.DupPubRelMessageStore;
import com.twist.mqtt.service.DupPubRelMessageStoreService;
import com.twist.mqtt.service.DupPublishMessageStoreService;
import io.netty.channel.Channel;
import io.netty.handler.codec.mqtt.MqttFixedHeader;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.codec.mqtt.MqttMessageFactory;
import io.netty.handler.codec.mqtt.MqttMessageIdVariableHeader;
import io.netty.handler.codec.mqtt.MqttMessageType;
import io.netty.handler.codec.mqtt.MqttQoS;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;

/**
 * @description: 发布确认收到 Qos2 step 1
 * @author: chenyingjie
 * @create: 2019-01-07 19:34
 **/
@Slf4j
public class PubRec {

    private DupPublishMessageStoreService dupPublishMessageStoreService;

    private DupPubRelMessageStoreService dupPubRelMessageStoreService;

    public PubRec(DupPublishMessageStoreService dupPublishMessageStoreService,
                  DupPubRelMessageStoreService dupPubRelMessageStoreService) {
        this.dupPublishMessageStoreService = dupPublishMessageStoreService;
        this.dupPubRelMessageStoreService = dupPubRelMessageStoreService;
    }

    public void processPubRec(Channel channel, MqttMessageIdVariableHeader variableHeader) {
        MqttMessage pubRelMessage = MqttMessageFactory.newMessage(
                new MqttFixedHeader(MqttMessageType.PUBREL, false, MqttQoS.AT_MOST_ONCE, false, 0),
                MqttMessageIdVariableHeader.from(variableHeader.messageId()),
                null);
        log.info("PUBREC - clientId: {}, messageId: {}", (String) channel.attr(AttributeKey.valueOf("clientId")).get(), variableHeader.messageId());
        dupPublishMessageStoreService.remove((String) channel.attr(AttributeKey.valueOf("clientId")).get(), variableHeader.messageId());
        DupPubRelMessageStore dupPubRelMessageStore = new DupPubRelMessageStore()
                .setClientId((String) channel.attr(AttributeKey.valueOf("clientId")).get())
                .setMessageId(variableHeader.messageId());
        dupPubRelMessageStoreService.put((String) channel.attr(AttributeKey.valueOf("clientId")).get(), dupPubRelMessageStore);
        channel.writeAndFlush(pubRelMessage);
    }
}
