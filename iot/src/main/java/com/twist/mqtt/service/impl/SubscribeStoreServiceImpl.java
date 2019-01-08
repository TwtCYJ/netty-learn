package com.twist.mqtt.service.impl;

import cn.hutool.core.util.StrUtil;
import com.twist.mqtt.bean.SubscribeStore;
import com.twist.mqtt.service.SubscribeStoreService;
import com.twist.mqtt.store.redis.SubscribeNotWildcardCache;
import com.twist.mqtt.store.redis.SubscribeWildcardCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @description: 订阅存储服务接口 实现类
 * @author: chenyingjie
 * @create: 2019-01-07 20:42
 **/
@Service
public class SubscribeStoreServiceImpl implements SubscribeStoreService {
    
    @Autowired
    private SubscribeWildcardCache subscribeWildcardCache;
    @Autowired
    private SubscribeNotWildcardCache subscribeNotWildcardCache;

    @Override
    public void put(String topicFilter, SubscribeStore subscribeStore) {
        if (StrUtil.contains(topicFilter,'#') || StrUtil.contains(topicFilter,'+')){
            subscribeWildcardCache.put(topicFilter,subscribeStore.getClientId(),subscribeStore);
        }else{
            subscribeNotWildcardCache.put(topicFilter,subscribeStore.getClientId(),subscribeStore);
        }
    }

    @Override
    public void remove(String topicFilter, String clientId) {
        if (StrUtil.contains(topicFilter,'#') || StrUtil.contains(topicFilter,'+')){
            subscribeWildcardCache.remove(topicFilter,clientId);
        }else {
            subscribeNotWildcardCache.remove(topicFilter,clientId);
        }
    }

    @Override
    public void removeForClient(String clientId) {
        subscribeNotWildcardCache.removeForClient(clientId);
        subscribeWildcardCache.removeForClient(clientId);
    }

    @Override
    public List<SubscribeStore> search(String topic) {
        List<SubscribeStore> subscribeStores = new ArrayList<SubscribeStore>();
        List<SubscribeStore> list = subscribeNotWildcardCache.all(topic);
        if (list.size() > 0) {
            subscribeStores.addAll(list);
        }
        subscribeWildcardCache.all().forEach((topicFilter, map) -> {
            if (StrUtil.split(topic, '/').size() >= StrUtil.split(topicFilter, '/').size()) {
                List<String> splitTopics = StrUtil.split(topic, '/');//a
                List<String> spliteTopicFilters = StrUtil.split(topicFilter, '/');//#
                String newTopicFilter = "";
                for (int i = 0; i < spliteTopicFilters.size(); i++) {
                    String value = spliteTopicFilters.get(i);
                    if (value.equals("+")) {
                        newTopicFilter = newTopicFilter + "+/";
                    } else if (value.equals("#")) {
                        newTopicFilter = newTopicFilter + "#/";
                        break;
                    } else {
                        newTopicFilter = newTopicFilter + splitTopics.get(i) + "/";
                    }
                }
                newTopicFilter = StrUtil.removeSuffix(newTopicFilter, "/");
                if (topicFilter.equals(newTopicFilter)) {
                    Collection<SubscribeStore> collection = map.values();
                    List<SubscribeStore> list2 = new ArrayList<SubscribeStore>(collection);
                    subscribeStores.addAll(list2);
                }
            }
        });
        return subscribeStores;
    }
}
