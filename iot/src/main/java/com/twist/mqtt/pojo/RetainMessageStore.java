package com.twist.mqtt.pojo;

import java.io.Serializable;

/**
 * @description: 要保留的信息
 * @author: chenyingjie
 * @create: 2019-01-08 14:48
 **/
public class RetainMessageStore implements Serializable {
    private static final long serialVersionUID = 276400892193365152L;

    private String topic;

    private byte[] messageBytes;

    private int mqttQoS;

    public String getTopic() {
        return topic;
    }

    public RetainMessageStore setTopic(String topic) {
        this.topic = topic;
        return this;
    }

    public byte[] getMessageBytes() {
        return messageBytes;
    }

    public RetainMessageStore setMessageBytes(byte[] messageBytes) {
        this.messageBytes = messageBytes;
        return this;
    }

    public int getMqttQoS() {
        return mqttQoS;
    }

    public RetainMessageStore setMqttQoS(int mqttQoS) {
        this.mqttQoS = mqttQoS;
        return this;
    }
}
