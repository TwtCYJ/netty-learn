package com.twist.service;

import com.twist.bean.DupPublishMessageStore;

import java.util.List;

/**
 * @description: PUBLISH重发消息存储服务接口, 当QoS=1和QoS=2时存在该重发机制
 * @author: chenyingjie
 * @create: 2019-01-07 21:08
 **/
public interface DupPublishMessageStoreService {
    void put(String clientId, DupPublishMessageStore dupPublishMessageStore);

    List<DupPublishMessageStore> get(String clientId);

    void remove(String clientId, int messageId);

    void removeByClient(String clientId);
}
