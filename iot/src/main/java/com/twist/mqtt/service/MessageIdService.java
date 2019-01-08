package com.twist.mqtt.service;

/**
 * @description: 分布式生成报文标识符
 * @author: chenyingjie
 * @create: 2019-01-08 13:21
 **/
public interface MessageIdService {
    /**
     * 获取报文标识符
     */
    int getNextMessageId();
}
