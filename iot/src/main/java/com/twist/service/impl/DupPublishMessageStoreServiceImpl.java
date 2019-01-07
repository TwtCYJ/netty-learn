package com.twist.service.impl;

import com.twist.bean.DupPublishMessageStore;
import com.twist.service.DupPublishMessageStoreService;
import com.twist.store.redis.DupPublishMessageCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @description: PUBLISH重发消息存储服务接口, 当QoS=1和QoS=2时存在该重发机制 实现
 * @author: chenyingjie
 * @create: 2019-01-07 21:10
 **/
@Service
public class DupPublishMessageStoreServiceImpl implements DupPublishMessageStoreService {

    @Autowired
    private DupPublishMessageCache dupPublishMessageCache;

    @Override
    public void put(String clientId, DupPublishMessageStore dupPublishMessageStore) {
        dupPublishMessageCache.put(clientId, dupPublishMessageStore.getMessageId(), dupPublishMessageStore);
    }

    @Override
    public List<DupPublishMessageStore> get(String clientId) {
        if (dupPublishMessageCache.containsKey(clientId)) {
            ConcurrentHashMap<Integer, DupPublishMessageStore> map = dupPublishMessageCache.get(clientId);
            Collection<DupPublishMessageStore> collection = map.values();
            return new ArrayList<>(collection);
        }
        return new ArrayList<>();
    }

    @Override
    public void remove(String clientId, int messageId) {
        dupPublishMessageCache.remove(clientId, messageId);
    }

    @Override
    public void removeByClient(String clientId) {
        dupPublishMessageCache.remove(clientId);
    }
}
