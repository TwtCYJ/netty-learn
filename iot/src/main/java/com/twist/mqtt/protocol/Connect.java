package com.twist.mqtt.protocol;

import cn.hutool.core.util.StrUtil;
import com.twist.mqtt.pojo.DupPubRelMessageStore;
import com.twist.mqtt.pojo.DupPublishMessageStore;
import com.twist.mqtt.pojo.SessionStore;
import com.twist.mqtt.service.AuthService;
import com.twist.mqtt.service.DupPubRelMessageStoreService;
import com.twist.mqtt.service.DupPublishMessageStoreService;
import com.twist.mqtt.service.SessionStoreService;
import com.twist.mqtt.service.SubscribeStoreService;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.handler.codec.mqtt.MqttConnAckMessage;
import io.netty.handler.codec.mqtt.MqttConnAckVariableHeader;
import io.netty.handler.codec.mqtt.MqttConnectMessage;
import io.netty.handler.codec.mqtt.MqttConnectReturnCode;
import io.netty.handler.codec.mqtt.MqttFixedHeader;
import io.netty.handler.codec.mqtt.MqttIdentifierRejectedException;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.codec.mqtt.MqttMessageFactory;
import io.netty.handler.codec.mqtt.MqttMessageIdVariableHeader;
import io.netty.handler.codec.mqtt.MqttMessageType;
import io.netty.handler.codec.mqtt.MqttPublishMessage;
import io.netty.handler.codec.mqtt.MqttPublishVariableHeader;
import io.netty.handler.codec.mqtt.MqttQoS;
import io.netty.handler.codec.mqtt.MqttUnacceptableProtocolVersionException;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.AttributeKey;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @description: 连接
 * @author: chenyingjie
 * @create: 2019-01-07 19:30
 **/
@Slf4j
public class Connect {

    private AuthService authService;

    private SessionStoreService sessionStoreService;

    private SubscribeStoreService subscribeStoreService;

    private DupPublishMessageStoreService dupPublishMessageStoreService;

    private DupPubRelMessageStoreService dupPubRelMessageStoreService;

    public Connect(AuthService authService,
                   SessionStoreService sessionStoreService,
                   SubscribeStoreService subscribeStoreService,
                   DupPublishMessageStoreService dupPublishMessageStoreService,
                   DupPubRelMessageStoreService dupPubRelMessageStoreService) {
        this.authService = authService;
        this.sessionStoreService = sessionStoreService;
        this.subscribeStoreService = subscribeStoreService;
        this.dupPublishMessageStoreService = dupPublishMessageStoreService;
        this.dupPubRelMessageStoreService = dupPubRelMessageStoreService;
    }

    public void processConnect(Channel channel, MqttConnectMessage msg) {
        //消息解码器异常
        if (msg.decoderResult().isFailure()) {
            Throwable cause = msg.decoderResult().cause();
            if (cause instanceof MqttUnacceptableProtocolVersionException) {
                //协议版本不支持
                MqttConnAckMessage connAckMessage = (MqttConnAckMessage) MqttMessageFactory.newMessage(
                        new MqttFixedHeader(MqttMessageType.CONNACK, false, MqttQoS.AT_MOST_ONCE, false, 0),
                        new MqttConnAckVariableHeader(MqttConnectReturnCode.CONNECTION_REFUSED_UNACCEPTABLE_PROTOCOL_VERSION, false), null);
                channel.writeAndFlush(connAckMessage);
                channel.close();
                return;
            } else if (cause instanceof MqttIdentifierRejectedException) {
                //clentId 不对的异常
                MqttConnAckMessage connAckMessage = (MqttConnAckMessage) MqttMessageFactory.newMessage(
                        new MqttFixedHeader(MqttMessageType.CONNACK, false, MqttQoS.AT_MOST_ONCE, false, 0),
                        new MqttConnAckVariableHeader(MqttConnectReturnCode.CONNECTION_REFUSED_IDENTIFIER_REJECTED, false), null);
                channel.writeAndFlush(connAckMessage);
                channel.close();
                return;
            }
            channel.close();
            return;
        }

        //clientId 为空或者null等情况下 不管cleanSession是否为1，此处没有参考标准协议实现
        if (StrUtil.isBlank(msg.payload().clientIdentifier())) {
            MqttConnAckMessage connAckMessage = (MqttConnAckMessage) MqttMessageFactory.newMessage(
                    new MqttFixedHeader(MqttMessageType.CONNACK, false, MqttQoS.AT_MOST_ONCE, false, 0),
                    new MqttConnAckVariableHeader(MqttConnectReturnCode.CONNECTION_REFUSED_IDENTIFIER_REJECTED, false), null);
            channel.writeAndFlush(connAckMessage);
            channel.close();
            return;
        }

        //验证用户名和密码。不管是否设置用户名标志和密码标志为1, 此处没有参考标准协议实现
        String username = msg.payload().userName();
        String password = msg.payload().passwordInBytes() == null ? null : new String(msg.payload().passwordInBytes(), CharsetUtil.UTF_8);
        if (!authService.checkValid(username, password)) {
            MqttConnAckMessage connAckMessage = (MqttConnAckMessage) MqttMessageFactory.newMessage(
                    new MqttFixedHeader(MqttMessageType.CONNACK, false, MqttQoS.AT_MOST_ONCE, false, 0),
                    new MqttConnAckVariableHeader(MqttConnectReturnCode.CONNECTION_REFUSED_BAD_USER_NAME_OR_PASSWORD, false), null);
            channel.writeAndFlush(connAckMessage);
            channel.close();
            return;
        }


        //判断是否已存在连接，有则关闭之前的连接
        if (sessionStoreService.containsKey(msg.payload().clientIdentifier())) {
            SessionStore sessionStore = sessionStoreService.get(msg.payload().clientIdentifier());
            Channel pre = sessionStore.getChannel();
            boolean cleanSession = sessionStore.isCleanSession();
            if (cleanSession) {
                sessionStoreService.remove(msg.payload().clientIdentifier());
                subscribeStoreService.removeForClient(msg.payload().clientIdentifier());
                dupPublishMessageStoreService.removeByClient(msg.payload().clientIdentifier());
                dupPubRelMessageStoreService.removeByClient(msg.payload().clientIdentifier());
            }
            pre.close();
        }

        //处理遗嘱
        SessionStore sessionStore = new SessionStore(msg.payload().clientIdentifier(), channel, msg.variableHeader().isCleanSession(), null);
        if (msg.variableHeader().isWillFlag()) {
            MqttPublishMessage willMessage = (MqttPublishMessage) MqttMessageFactory.newMessage(
                    new MqttFixedHeader(MqttMessageType.PUBLISH, false, MqttQoS.valueOf(msg.variableHeader().willQos()),
                            msg.variableHeader().isWillRetain(), 0),
                    new MqttPublishVariableHeader(msg.payload().willTopic(), 0),
                    Unpooled.buffer().writeBytes(msg.payload().willMessageInBytes()));
            sessionStore.setWillMessage(willMessage);
        }

        //心跳包处理
        if (msg.variableHeader().keepAliveTimeSeconds() > 0) {
            if (channel.pipeline().names().contains("idle")) {
                channel.pipeline().remove("idle");
            }
            channel.pipeline().addFirst("idle",
                    new IdleStateHandler(0, 0,
                            Math.round(msg.variableHeader().keepAliveTimeSeconds() * 1.5f)));
        }

        //存储会话信息并返回已接收客户端
        sessionStoreService.put(msg.payload().clientIdentifier(), sessionStore);

        //clientId 存入channel 的map 中
        channel.attr(AttributeKey.valueOf("clientId")).set(msg.payload().clientIdentifier());

        //验证session 是否存好了
        boolean sessionPresent = sessionStoreService.containsKey(msg.payload().clientIdentifier()) &&
                !msg.variableHeader().isCleanSession();

        //组装resp
        MqttConnAckMessage okResp = (MqttConnAckMessage) MqttMessageFactory.newMessage(
                new MqttFixedHeader(MqttMessageType.CONNACK, false, MqttQoS.AT_MOST_ONCE, false, 0),
                new MqttConnAckVariableHeader(MqttConnectReturnCode.CONNECTION_ACCEPTED, sessionPresent),
                null);
        channel.writeAndFlush(okResp);
        log.info("CONNECT - clientId: {}, cleanSession: {}", msg.payload().clientIdentifier(), msg.variableHeader().isCleanSession());

        //如果cleanSession为0 就重新发同一个ClientId存储的未完成的Qos1和Qos2的DUP消息
        if (!msg.variableHeader().isCleanSession()) {
            List<DupPublishMessageStore> dupPublishMessageStores = dupPublishMessageStoreService.get(msg.payload().clientIdentifier());
            List<DupPubRelMessageStore> dupPubRelMessageStores = dupPubRelMessageStoreService.get(msg.payload().clientIdentifier());

            dupPublishMessageStores.forEach(dupPublishMessageStore -> {
                MqttPublishMessage publishMessage = (MqttPublishMessage) MqttMessageFactory.newMessage(
                        new MqttFixedHeader(MqttMessageType.PUBLISH, true,
                                MqttQoS.valueOf(dupPublishMessageStore.getMqttQoS()), false, 0),
                        new MqttPublishVariableHeader(dupPublishMessageStore.getTopic(), dupPublishMessageStore.getMessageId()),
                        Unpooled.buffer().writeBytes(dupPublishMessageStore.getMessageBytes()));
                channel.writeAndFlush(publishMessage);
            });

            dupPubRelMessageStores.forEach(dupPubRelMessageStore -> {
                MqttMessage pubRelMessage = MqttMessageFactory.newMessage(
                        new MqttFixedHeader(MqttMessageType.PUBREL, true, MqttQoS.AT_MOST_ONCE, false, 0),
                        MqttMessageIdVariableHeader.from(dupPubRelMessageStore.getMessageId()),
                        null
                );
                channel.writeAndFlush(pubRelMessage);
            });
        }
    }
}
