package com.twist.mqtt.bean;

import java.io.Serializable;

/**
 * @description: PUBREL重发消息存储
 * @author: chenyingjie
 * @create: 2019-01-07 21:19
 **/
public class DupPubRelMessageStore implements Serializable {
    private static final long serialVersionUID = -7663550911852542322L;

    private String clientId;

    private int messageId;

    public DupPubRelMessageStore(){

    }

    public String getClientId() {
        return this.clientId;
    }

    public DupPubRelMessageStore setClientId(String clientId) {
        this.clientId = clientId;
        return this;
    }

    public int getMessageId() {
        return this.messageId;
    }

    public DupPubRelMessageStore setMessageId(int messageId) {
        this.messageId = messageId;
        return this;
    }
}
