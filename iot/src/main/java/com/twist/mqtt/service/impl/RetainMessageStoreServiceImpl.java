package com.twist.mqtt.service.impl;

import cn.hutool.core.util.StrUtil;
import com.twist.mqtt.pojo.RetainMessageStore;
import com.twist.mqtt.service.RetainMessageStoreService;
import com.twist.mqtt.store.redis.RetainMessageCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @description: 保留信息 实现类
 * @author: chenyingjie
 * @create: 2019-01-08 14:49
 **/
@Service
public class RetainMessageStoreServiceImpl implements RetainMessageStoreService {

    @Autowired
    private RetainMessageCache retainMessageCache;

    @Override
    public void put(String topic, RetainMessageStore retainMessageStore) {
        retainMessageCache.put(topic,retainMessageStore);
    }

    @Override
    public RetainMessageStore get(String topic) {
        return retainMessageCache.get(topic);
    }

    @Override
    public void remove(String topic) {
        retainMessageCache.remove(topic);
    }

    @Override
    public boolean containsKey(String topic) {
        return retainMessageCache.containsKey(topic);
    }

    @Override
    public List<RetainMessageStore> search(String topicFilter) {
        List<RetainMessageStore> retainMessageStores = new ArrayList<RetainMessageStore>();
        if (!StrUtil.contains(topicFilter, '#') && !StrUtil.contains(topicFilter, '+')) {
            if (retainMessageCache.containsKey(topicFilter)) {
                retainMessageStores.add(retainMessageCache.get(topicFilter));
            }
        } else {
            retainMessageCache.all().forEach((topic, val) -> {
                if (StrUtil.split(topic, '/').size() >= StrUtil.split(topicFilter, '/').size()) {
                    List<String> splitTopics = StrUtil.split(topic, '/');
                    List<String> spliteTopicFilters = StrUtil.split(topicFilter, '/');
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
                        retainMessageStores.add(val);
                    }
                }
            });
        }
        return retainMessageStores;
    }
}
