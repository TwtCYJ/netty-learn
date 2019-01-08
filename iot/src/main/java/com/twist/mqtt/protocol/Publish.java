package com.twist.mqtt.protocol;

import com.twist.mqtt.pojo.DupPublishMessageStore;
import com.twist.mqtt.pojo.InnerMessage;
import com.twist.mqtt.pojo.RetainMessageStore;
import com.twist.mqtt.pojo.SubscribeStore;
import com.twist.mqtt.service.DupPublishMessageStoreService;
import com.twist.mqtt.service.KafkaService;
import com.twist.mqtt.service.MessageIdService;
import com.twist.mqtt.service.RetainMessageStoreService;
import com.twist.mqtt.service.SessionStoreService;
import com.twist.mqtt.service.SubscribeStoreService;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.handler.codec.mqtt.MqttFixedHeader;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.codec.mqtt.MqttMessageFactory;
import io.netty.handler.codec.mqtt.MqttMessageIdVariableHeader;
import io.netty.handler.codec.mqtt.MqttMessageType;
import io.netty.handler.codec.mqtt.MqttPubAckMessage;
import io.netty.handler.codec.mqtt.MqttPublishMessage;
import io.netty.handler.codec.mqtt.MqttPublishVariableHeader;
import io.netty.handler.codec.mqtt.MqttQoS;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @description: 发布消息
 * @author: chenyingjie
 * @create: 2019-01-07 19:34
 **/
@Slf4j
public class Publish {

    private KafkaService kafkaService;

    private SubscribeStoreService subscribeStoreService;

    private SessionStoreService sessionStoreService;

    private MessageIdService messageIdService;

    private DupPublishMessageStoreService dupPublishMessageStoreService;

    private RetainMessageStoreService retainMessageStoreService;

    public Publish(KafkaService kafkaService, SubscribeStoreService subscribeStoreService, SessionStoreService sessionStoreService, MessageIdService messageIdService, DupPublishMessageStoreService dupPublishMessageStoreService, RetainMessageStoreService retainMessageStoreService) {
        this.kafkaService = kafkaService;
        this.subscribeStoreService = subscribeStoreService;
        this.sessionStoreService = sessionStoreService;
        this.messageIdService = messageIdService;
        this.dupPublishMessageStoreService = dupPublishMessageStoreService;
        this.retainMessageStoreService = retainMessageStoreService;
    }

    public void processPublish(Channel channel, MqttPublishMessage msg) {
        String clientId = (String) channel.attr(AttributeKey.valueOf("clientId")).get();
        //QoS=0
        if (msg.fixedHeader().qosLevel() == MqttQoS.AT_MOST_ONCE) {
            byte[] messageBytes = new byte[msg.payload().readableBytes()];
            msg.payload().getBytes(msg.payload().readerIndex(), messageBytes);
            InnerMessage innerMessage = new InnerMessage()
                    .setTopic(msg.variableHeader().topicName())
                    .setMqttQoS(msg.fixedHeader().qosLevel().value())
                    .setMessageBytes(messageBytes)
                    .setDup(false)
                    .setRetain(false)
                    .setClientId(clientId);
            kafkaService.send(innerMessage);
            this.sendPublishMessage(msg.variableHeader().topicName(), msg.fixedHeader().qosLevel(), messageBytes, false, false);
        }
        //QoS=1
        if (msg.fixedHeader().qosLevel() == MqttQoS.AT_LEAST_ONCE) {
            byte[] messageBytes = new byte[msg.payload().readableBytes()];
            msg.payload().getBytes(msg.payload().readerIndex(), messageBytes);
            InnerMessage innerMessage = new InnerMessage()
                    .setTopic(msg.variableHeader().topicName())
                    .setMqttQoS(msg.fixedHeader().qosLevel().value())
                    .setMessageBytes(messageBytes)
                    .setDup(false)
                    .setRetain(false)
                    .setClientId(clientId);
            kafkaService.send(innerMessage);
            this.sendPublishMessage(msg.variableHeader().topicName(), msg.fixedHeader().qosLevel(), messageBytes, false, false);
            this.sendPubAckMessage(channel, msg.variableHeader().packetId());
        }
        //QoS=2
        if (msg.fixedHeader().qosLevel() == MqttQoS.EXACTLY_ONCE) {
            byte[] messageBytes = new byte[msg.payload().readableBytes()];
            msg.payload().getBytes(msg.payload().readerIndex(), messageBytes);
            InnerMessage innerMessage = new InnerMessage()
                    .setTopic(msg.variableHeader().topicName())
                    .setMqttQoS(msg.fixedHeader().qosLevel().value())
                    .setMessageBytes(messageBytes)
                    .setDup(false)
                    .setRetain(false)
                    .setClientId(clientId);
            kafkaService.send(innerMessage);
            this.sendPublishMessage(msg.variableHeader().topicName(), msg.fixedHeader().qosLevel(), messageBytes, false, false);
            this.sendPubRecMessage(channel, msg.variableHeader().packetId());
        }
        //retain=1, 保留信息
        if (msg.fixedHeader().isRetain()) {
            byte[] messageBytes = new byte[msg.payload().readableBytes()];
            msg.payload().getBytes(msg.payload().readerIndex(), messageBytes);
            if (messageBytes.length == 0) {
                retainMessageStoreService.remove(msg.variableHeader().topicName());
            } else {
                RetainMessageStore retainMessageStore = new RetainMessageStore()
                        .setTopic(msg.variableHeader().topicName())
                        .setMqttQoS(msg.fixedHeader().qosLevel().value())
                        .setMessageBytes(messageBytes);
                retainMessageStoreService.put(msg.variableHeader().topicName(),retainMessageStore);
            }
        }
    }

    private void sendPubAckMessage(Channel channel, int messageId) {
        MqttPubAckMessage pubAckMessage = (MqttPubAckMessage) MqttMessageFactory.newMessage(
                new MqttFixedHeader(MqttMessageType.PUBACK, false, MqttQoS.AT_MOST_ONCE, false, 0),
                MqttMessageIdVariableHeader.from(messageId),
                null);
        channel.writeAndFlush(pubAckMessage);
    }

    private void sendPubRecMessage(Channel channel, int messageId) {
        MqttMessage pubRecMessage = MqttMessageFactory.newMessage(
                new MqttFixedHeader(MqttMessageType.PUBREC, false, MqttQoS.AT_MOST_ONCE, false, 0),
                MqttMessageIdVariableHeader.from(messageId),
                null
        );
        channel.writeAndFlush(pubRecMessage);
    }

    private void sendPublishMessage(String topic, MqttQoS mqttQoS, byte[] messageBytes, boolean retain, boolean dup) {
        List<SubscribeStore> subscribeStores = subscribeStoreService.search(topic);
        subscribeStores.forEach(subscribeStore -> {
            if (sessionStoreService.containsKey(subscribeStore.getClientId())) {
                //订阅者收到MQTT消息的QoS级别, 最终取决于发布消息的QoS和主题订阅的QoS
                MqttQoS respQoS = mqttQoS.value() > subscribeStore.getMqttQoS() ? MqttQoS.valueOf(subscribeStore.getMqttQoS()) : mqttQoS;
                if (respQoS == MqttQoS.AT_MOST_ONCE) {
                    MqttPublishMessage publishMessage = (MqttPublishMessage) MqttMessageFactory.newMessage(
                            new MqttFixedHeader(MqttMessageType.PUBLISH, dup, respQoS, retain, 0),
                            new MqttPublishVariableHeader(topic, 0),
                            Unpooled.buffer().writeBytes(messageBytes));
                    log.info("PUBLISH - clientId: {}, topic: {}, Qos: {}", subscribeStore.getClientId(), topic, respQoS.value());
                    sessionStoreService.get(subscribeStore.getClientId()).getChannel().writeAndFlush(publishMessage);
                }
                if (respQoS == MqttQoS.AT_LEAST_ONCE) {
                    int messageId = messageIdService.getNextMessageId();
                    MqttPublishMessage publishMessage = (MqttPublishMessage) MqttMessageFactory.newMessage(
                            new MqttFixedHeader(MqttMessageType.PUBLISH, dup, respQoS, retain, 0),
                            new MqttPublishVariableHeader(topic, messageId),
                            Unpooled.buffer().writeBytes(messageBytes));
                    log.info("PUBLISH - clientId: {}, topic: {}, Qos: {}, messageId: {}", subscribeStore.getClientId(), topic, respQoS.value(), messageId);
                    DupPublishMessageStore dupPublishMessageStore = new DupPublishMessageStore()
                            .setClientId(subscribeStore.getClientId())
                            .setTopic(topic)
                            .setMqttQoS(respQoS.value())
                            .setMessageBytes(messageBytes)
                            .setMessageId(messageId);
                    dupPublishMessageStoreService.put(subscribeStore.getClientId(), dupPublishMessageStore);
                    sessionStoreService.get(subscribeStore.getClientId()).getChannel().writeAndFlush(publishMessage);
                }
                if (respQoS == MqttQoS.EXACTLY_ONCE) {
                    int messageId = messageIdService.getNextMessageId();
                    MqttPublishMessage publishMessage = (MqttPublishMessage) MqttMessageFactory.newMessage(
                            new MqttFixedHeader(MqttMessageType.PUBLISH, dup, respQoS, retain, 0),
                            new MqttPublishVariableHeader(topic, messageId), Unpooled.buffer().writeBytes(messageBytes));
                    log.info("PUBLISH - clientId: {}, topic: {}, Qos: {}, messageId: {}", subscribeStore.getClientId(), topic, respQoS.value(), messageId);
                    DupPublishMessageStore dupPublishMessageStore = new DupPublishMessageStore()
                            .setClientId(subscribeStore.getClientId())
                            .setTopic(topic)
                            .setMqttQoS(respQoS.value())
                            .setMessageBytes(messageBytes)
                            .setMessageId(messageId);
                    dupPublishMessageStoreService.put(subscribeStore.getClientId(), dupPublishMessageStore);
                    sessionStoreService.get(subscribeStore.getClientId()).getChannel().writeAndFlush(publishMessage);
                }
            }
        });
    }


}
