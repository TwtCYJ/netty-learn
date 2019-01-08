package com.twist.mqtt.service.impl;

import com.twist.mqtt.service.MessageIdService;
import com.twist.mqtt.store.redis.MessageIdCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 * @description:
 * @author: chenyingjie
 * @create: 2019-01-08 13:26
 **/
@Service
public class MessageIdServiceImpl implements MessageIdService {

    @Autowired
    private MessageIdCache messageIdCache;
    @Override
    public int getNextMessageId() {
        try {
            int nextMsgId = (int) (messageIdCache.incr() % 65536);
            if (nextMsgId == 0)
                return this.getNextMessageId();
            return nextMsgId;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    @PostConstruct
    public void init() {
        messageIdCache.init();
    }
}