package com.twist.mqtt.service;

import com.twist.mqtt.pojo.DupPubRelMessageStore;

import java.util.List;

/**
 * @description: PUBREL重发消息存储服务接口, 当QoS=2时存在该重发机制
 * @author: chenyingjie
 * @create: 2019-01-07 21:19
 **/
public interface DupPubRelMessageStoreService {
    /**
     * 存储消息
     */
    void put(String clientId, DupPubRelMessageStore dupPubRelMessageStore);

    /**
     * 获取消息集合
     */
    List<DupPubRelMessageStore> get(String clientId);

    /**
     * 删除消息
     */
    void remove(String clientId, int messageId);

    /**
     * 删除消息
     */
    void removeByClient(String clientId);
}
