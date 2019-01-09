package com.twist.mqtt.service.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.twist.mqtt.pojo.InnerMessage;
import com.twist.mqtt.service.KafkaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

/**
 * @description: kafka 服务实现
 * @author: chenyingjie
 * @create: 2019-01-08 12:59
 **/
@Service
public class KafkaServiceImpl implements KafkaService {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    private static Gson gson = new GsonBuilder().create();

    @Override
    public void send(InnerMessage innerMessage) {
        kafkaTemplate.send(innerMessage.getTopic(), gson.toJson(innerMessage));
    }
}
