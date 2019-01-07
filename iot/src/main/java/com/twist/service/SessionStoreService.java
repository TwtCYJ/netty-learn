package com.twist.service;

import com.twist.bean.SessionStore;

/**
 * @description: session 回话存储
 * @author: chenyingjie
 * @create: 2019-01-07 20:23
 **/
public interface SessionStoreService {

    /**
     * 存储会话
     */
    void put(String clientId, SessionStore sessionStore);

    /**
     * 获取
     */
    SessionStore get(String clientId);

    /**
     * 是否存在
     */
    boolean containsKey(String clientId);

    /**
     * 删除
     */
    void remove(String clientId);
}
