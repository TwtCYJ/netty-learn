package com.twist.mqtt.protocol;

import com.twist.mqtt.service.DupPubRelMessageStoreService;
import io.netty.channel.Channel;
import io.netty.handler.codec.mqtt.MqttMessageIdVariableHeader;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;

/**
 * @description: 发布完成（Qos2, step 3）
 * @author: chenyingjie
 * @create: 2019-01-07 19:33
 **/
@Slf4j
public class PubComp {

    private DupPubRelMessageStoreService dupPubRelMessageStoreService;

    public PubComp(DupPubRelMessageStoreService dupPubRelMessageStoreService) {
        this.dupPubRelMessageStoreService = dupPubRelMessageStoreService;
    }

    public void processPubComp(Channel channel, MqttMessageIdVariableHeader variableHeader) {
        int messageId = variableHeader.messageId();
        log.info("PUBCOMP - clientId: {}, messageId: {}", (String) channel.attr(AttributeKey.valueOf("clientId")).get(), messageId);
        dupPubRelMessageStoreService.remove((String)channel.attr(AttributeKey.valueOf("clientId")).get(), variableHeader.messageId());
    }
}
