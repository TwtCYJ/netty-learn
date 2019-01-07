package com.twist.service;

import com.twist.bean.SubscribeStore;

import java.util.List;

/**
 * @description: 订阅存储服务接口
 * @author: chenyingjie
 * @create: 2019-01-07 20:38
 **/
public interface SubscribeStoreService {
    /**
     * 存储订阅
     * @param topicFilter
     * @param subscribeStore
     */
    void put(String topicFilter, SubscribeStore subscribeStore);

    /**
     * 删除订阅
     * @param topicFilter
     * @param clientId
     */
    void remove(String topicFilter, String clientId);

    /**
     * 删除clientId的订阅
     * @param clientId
     */
    void removeForClient(String clientId);

    /**
     * 获取订阅存储集
     * @param topic
     * @return
     */
    List<SubscribeStore> search(String topic);
}
