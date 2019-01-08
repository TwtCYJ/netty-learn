package com.twist.mqtt.store.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.io.Serializable;

/**
 * @description: messageIdCache 操作封装
 * @author: chenyingjie
 * @create: 2019-01-08 14:53
 **/
@Service
public class MessageIdCache {
    private static final String CACHE_PRE = "feioou:messageid:num";

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private RedisTemplate<String, Serializable> redisCacheTemplate;

    public long incr() {
        return redisCacheTemplate.opsForValue().increment(CACHE_PRE);
    }

    public void init() {
        redisCacheTemplate.delete(CACHE_PRE);
    }
}
