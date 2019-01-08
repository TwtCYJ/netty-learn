package com.twist.mqtt.service.impl;

import com.twist.mqtt.pojo.SessionStore;
import com.twist.mqtt.service.SessionStoreService;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @description: session 回话存储 实现
 * @author: chenyingjie
 * @create: 2019-01-07 20:32
 **/
@Service
public class SessionCacheServiceImpl implements SessionStoreService {

    private Map<String, SessionStore> sessionCacheMap = new ConcurrentHashMap<>();

    @Override
    public void put(String clientId, SessionStore sessionStore) {
        sessionCacheMap.put(clientId,sessionStore);
    }

    @Override
    public SessionStore get(String clientId) {
        return sessionCacheMap.get(clientId);
    }

    @Override
    public boolean containsKey(String clientId) {
        return sessionCacheMap.containsKey(clientId);
    }

    @Override
    public void remove(String clientId) {
        sessionCacheMap.remove(clientId);
    }
}
