package com.twist.mqtt.pojo;

import java.io.Serializable;

/**
 * @description: kafka 消息
 * @author: chenyingjie
 * @create: 2019-01-08 12:55
 **/
public class InnerMessage implements Serializable {

    private static final long serialVersionUID = 2572723803354650251L;
    
    private String clientId;
    
    private String topic;
    
    private int mqttQoS;
    
    private byte[] messageBytes;
    
    private boolean retain;
    
    private boolean dup;

    public String getClientId() {
        return clientId;
    }

    public InnerMessage setClientId(String clientId) {
        this.clientId = clientId;
        return this;
    }

    public String getTopic() {
        return topic;
    }

    public InnerMessage setTopic(String topic) {
        this.topic = topic;
        return this;
    }

    public int getMqttQoS() {
        return mqttQoS;
    }

    public InnerMessage setMqttQoS(int mqttQoS) {
        this.mqttQoS = mqttQoS;
        return this;
    }

    public byte[] getMessageBytes() {
        return messageBytes;
    }

    public InnerMessage setMessageBytes(byte[] messageBytes) {
        this.messageBytes = messageBytes;
        return this;
    }

    public boolean isRetain() {
        return retain;
    }

    public InnerMessage setRetain(boolean retain) {
        this.retain = retain;
        return this;
    }

    public boolean isDup() {
        return dup;
    }

    public InnerMessage setDup(boolean dup) {
        this.dup = dup;
        return this;
    }
}
