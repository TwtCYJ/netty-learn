package com.twist.mqtt.service;

import com.twist.mqtt.pojo.InnerMessage;

/**
 * @description: kafka 服务
 * @author: chenyingjie
 * @create: 2019-01-08 12:58
 **/
public interface KafkaService {

    void send(InnerMessage innerMessage);
}
