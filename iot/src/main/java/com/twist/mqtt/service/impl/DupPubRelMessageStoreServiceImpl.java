package com.twist.mqtt.service.impl;

import com.twist.mqtt.pojo.DupPubRelMessageStore;
import com.twist.mqtt.service.DupPubRelMessageStoreService;
import com.twist.mqtt.store.redis.DupPubRelMessageCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @description: PUBREL重发消息存储服务实现
 * @author: chenyingjie
 * @create: 2019-01-07 21:20
 **/
@Service
public class DupPubRelMessageStoreServiceImpl implements DupPubRelMessageStoreService {
    @Autowired
    private DupPubRelMessageCache dupPubRelMessageCache;

    @Override
    public void put(String clientId, DupPubRelMessageStore dupPubRelMessageStore) {
        dupPubRelMessageCache.put(clientId, dupPubRelMessageStore.getMessageId(), dupPubRelMessageStore);
    }

    @Override
    public List<DupPubRelMessageStore> get(String clientId) {
        if (dupPubRelMessageCache.containsKey(clientId)) {
            ConcurrentHashMap<Integer, DupPubRelMessageStore> map = dupPubRelMessageCache.get(clientId);
            Collection<DupPubRelMessageStore> collection = map.values();
            return new ArrayList<>(collection);
        }
        return new ArrayList<>();
    }

    @Override
    public void remove(String clientId, int messageId) {
        dupPubRelMessageCache.remove(clientId, messageId);
    }

    @Override
    public void removeByClient(String clientId) {
        if (dupPubRelMessageCache.containsKey(clientId)) {
            dupPubRelMessageCache.remove(clientId);
        }
    }
}
