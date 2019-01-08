package com.twist.mqtt.service;

import com.twist.mqtt.pojo.RetainMessageStore;

import java.util.List;

/**
 * @description: 保留信息存储
 * @author: chenyingjie
 * @create: 2019-01-08 14:44
 **/
public interface RetainMessageStoreService {

    /**
     * 存储retain标志消息
     */
    void put(String topic, RetainMessageStore retainMessageStore);

    /**
     * 获取retain消息
     */
    RetainMessageStore get(String topic);

    /**
     * 删除retain标志消息
     */
    void remove(String topic);

    /**
     * 判断指定topic的retain消息是否存在
     */
    boolean containsKey(String topic);

    /**
     * 获取retain消息集合
     */
    List<RetainMessageStore> search(String topicFilter);
}
