package com.twist.protocol;

import com.twist.bean.SessionStore;
import com.twist.service.DupPubRelMessageStoreService;
import com.twist.service.DupPublishMessageStoreService;
import com.twist.service.SessionStoreService;
import com.twist.service.SubscribeStoreService;
import io.netty.channel.Channel;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;

/**
 * @description: 断开连接
 * @author: chenyingjie
 * @create: 2019-01-07 19:31
 **/
@Slf4j
public class DisConnect {

    private SessionStoreService sessionStoreService;

    private SubscribeStoreService subscribeStoreService;

    private DupPublishMessageStoreService dupPublishMessageStoreService;

    private DupPubRelMessageStoreService dupPubRelMessageStoreService;

    public DisConnect(SessionStoreService sessionStoreService,
                      SubscribeStoreService subscribeStoreService,
                      DupPublishMessageStoreService dupPublishMessageStoreService,
                      DupPubRelMessageStoreService dupPubRelMessageStoreService) {
        this.sessionStoreService = sessionStoreService;
        this.subscribeStoreService = subscribeStoreService;
        this.dupPublishMessageStoreService = dupPublishMessageStoreService;
        this.dupPubRelMessageStoreService = dupPubRelMessageStoreService;
    }

    public void processDisConnect(Channel channel, MqttMessage msg) {
        String clientId = (String) channel.attr(AttributeKey.valueOf("clientId")).get();
        SessionStore sessionStore = sessionStoreService.get(clientId);

        if (sessionStore != null && sessionStore.isCleanSession()) {
            subscribeStoreService.removeForClient(clientId);
            dupPublishMessageStoreService.removeByClient(clientId);
            dupPubRelMessageStoreService.removeByClient(clientId);
        }
        log.info("DISCONNECT - clientId: {}, cleanSession: {}", clientId, sessionStore.isCleanSession());
        sessionStoreService.remove(clientId);
        channel.close();
    }
}
