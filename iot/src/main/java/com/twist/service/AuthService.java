package com.twist.service;

/**
 * @description: 用户名和密码验证服务
 * @author: chenyingjie
 * @create: 2019-01-07 20:13
 **/
public interface AuthService {
    /**
     * 验证用户名和密码是否匹配
     * @param username
     * @param password
     * @return
     */
    boolean checkValid(String username, String password);
}
