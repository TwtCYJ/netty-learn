package com.twist.mqtt.protocol;

import com.twist.mqtt.service.AuthService;
import com.twist.mqtt.service.DupPubRelMessageStoreService;
import com.twist.mqtt.service.DupPublishMessageStoreService;
import com.twist.mqtt.service.KafkaService;
import com.twist.mqtt.service.MessageIdService;
import com.twist.mqtt.service.RetainMessageStoreService;
import com.twist.mqtt.service.SessionStoreService;
import com.twist.mqtt.service.SubscribeStoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/**
 * @description: 协议处理
 * @author: chenyingjie
 * @create: 2019-01-07 19:46
 **/
@Component
public class ProtocolProcess {

    @Autowired
    private SessionStoreService sessionStoreService;
    @Autowired
    private SubscribeStoreService subscribeStoreService;
    @Autowired
    private AuthService authService;
    @Autowired
    private MessageIdService messageIdService;
    @Autowired
    private RetainMessageStoreService retainMessageStoreService;
    @Autowired
    private DupPublishMessageStoreService dupPublishMessageStoreService;
    @Autowired
    private DupPubRelMessageStoreService dupPubRelMessageStoreService;
    @Autowired
    private KafkaService kafkaService;


    private Connect connect;

    private Subscribe subscribe;

    private UnSubscribe unSubscribe;

    private Publish publish;

    private DisConnect disConnect;

    private PingReq pingReq;

    private PubRel pubRel;

    private PubAck pubAck;

    private PubRec pubRec;

    private PubComp pubComp;

    public Connect connect() {
        if (connect == null) {
            connect = new Connect(
                    authService,
                    sessionStoreService,
                    subscribeStoreService,
                    dupPublishMessageStoreService,
                    dupPubRelMessageStoreService);
        }
        return connect;
    }

    public Subscribe subscribe() {
        if (subscribe == null) {
            subscribe = new Subscribe(
                    subscribeStoreService,
                    messageIdService,
                    retainMessageStoreService);
        }
        return subscribe;
    }

    public UnSubscribe unSubscribe() {
        if (unSubscribe == null) {
            unSubscribe = new UnSubscribe(subscribeStoreService);
        }
        return unSubscribe;
    }

    public Publish publish() {
        if (publish == null) {
            publish = new Publish(
                    kafkaService,
                    subscribeStoreService,
                    sessionStoreService,
                    messageIdService,
                    dupPublishMessageStoreService,
                    retainMessageStoreService);
        }
        return publish;
    }

    public DisConnect disConnect() {
        if (disConnect == null) {
            disConnect = new DisConnect(
                    sessionStoreService,
                    subscribeStoreService,
                    dupPublishMessageStoreService,
                    dupPubRelMessageStoreService);
        }
        return disConnect;
    }

    public PingReq pingReq() {
        if (pingReq == null) {
            pingReq = new PingReq();
        }
        return pingReq;
    }

    public PubRel pubRel() {
        if (pubRel == null) {
            pubRel = new PubRel();
        }
        return pubRel;
    }

    public PubAck pubAck() {
        if (pubAck == null) {
            pubAck = new PubAck(dupPublishMessageStoreService);
        }
        return pubAck;
    }

    public PubRec pubRec() {
        if (pubRec == null) {
            pubRec = new PubRec(dupPublishMessageStoreService, dupPubRelMessageStoreService);
        }
        return pubRec;
    }

    public PubComp pubComp() {
        if (pubComp == null) {
            pubComp = new PubComp(dupPubRelMessageStoreService);
        }
        return pubComp;
    }

    public SessionStoreService getSessionStoreService() {
        return sessionStoreService;
    }
}
