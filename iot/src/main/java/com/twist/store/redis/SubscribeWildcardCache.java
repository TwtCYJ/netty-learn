package com.twist.store.redis;

import com.twist.bean.SubscribeStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @description: 遗嘱的订阅操作实现
 * @author: chenyingjie
 * @create: 2019-01-07 20:49
 **/
@Service
public class SubscribeWildcardCache {
    private static final String CACHE_PRE = "feioou:subnotwildcard:";
    private static final String CACHE_CLIENT_PRE = "feioou:client:";

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private RedisTemplate<String, Serializable> redisCacheTemplate;

    /**
     * 存储订阅信息
     *
     * @param topic
     * @param clientId
     * @param subscribeStore
     * @return
     */
    public SubscribeStore put(String topic, String clientId, SubscribeStore subscribeStore) {
        redisCacheTemplate.opsForHash().put(CACHE_PRE + topic, clientId, subscribeStore);
        stringRedisTemplate.opsForSet().add(CACHE_CLIENT_PRE + clientId, topic);
        return subscribeStore;
    }

    /**
     * 根据主题和客户端ID 获取订阅信息
     *
     * @param topic
     * @param clientId
     * @return
     */
    public SubscribeStore get(String topic, String clientId) {
        return (SubscribeStore) redisCacheTemplate.opsForHash().get(CACHE_PRE + topic, clientId);
    }

    /**
     * 根据主题和客户端ID 验证是否存在订阅
     *
     * @param topic
     * @param clientId
     * @return
     */
    public boolean containsKey(String topic, String clientId) {
        return redisCacheTemplate.opsForHash().hasKey(CACHE_PRE + topic, clientId);
    }

    /**
     * 根据主题和客户端ID移除订阅
     *
     * @param topic
     * @param clientId
     */
    public void remove(String topic, String clientId) {
        stringRedisTemplate.opsForSet().remove(CACHE_CLIENT_PRE + clientId, topic);
        redisCacheTemplate.opsForHash().delete(CACHE_PRE + topic, clientId);
    }

    /**
     * 根据客户端ID 移除其所有订阅主题
     *
     * @param clientId
     */
    public void removeForClient(String clientId) {
        for (String topic : stringRedisTemplate.opsForSet().members(CACHE_CLIENT_PRE + clientId)) {
            redisCacheTemplate.opsForHash().delete(CACHE_PRE + topic, clientId);
        }
        stringRedisTemplate.delete(CACHE_CLIENT_PRE + clientId);
    }

    /**
     * 获取所有订阅
     *
     * @return
     */
    public Map<String, ConcurrentHashMap<String, SubscribeStore>> all() {
        Map<String, ConcurrentHashMap<String, SubscribeStore>> map = new HashMap<>();
        Set<String> set = redisCacheTemplate.keys(CACHE_PRE + "*");
        if (set != null && !set.isEmpty()) {
            set.forEach(
                    entry -> {
                        ConcurrentHashMap<String, SubscribeStore> map1 = new ConcurrentHashMap<>();
                        Map<Object, Object> map2 = redisCacheTemplate.opsForHash().entries(entry);
                        if (map2 != null && !map2.isEmpty()) {
                            map2.forEach((k, v) -> {
                                map1.put((String) k, (SubscribeStore) v);
                            });
                            map.put(entry.substring(CACHE_PRE.length()), map1);
                        }
                    }
            );
        }
        return map;
    }

    /**
     * 根据主题获取该主题的所有订阅信息
     *
     * @param topic
     * @return
     */
    public List<SubscribeStore> all(String topic) {
        List<SubscribeStore> list = new ArrayList<>();
        Map<Object, Object> map = redisCacheTemplate.opsForHash().entries(CACHE_PRE + topic);
        if (map != null && !map.isEmpty()) {
            map.forEach((k, v) -> {
                list.add((SubscribeStore) v);
            });
        }
        return list;
    }
}
