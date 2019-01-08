package com.twist.mqtt.service.impl;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.RSA;
import com.twist.mqtt.service.AuthService;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.security.interfaces.RSAPrivateKey;

/**
 * @description: 用户名和密码验证服务实现
 * @author: chenyingjie
 * @create: 2019-01-07 20:14
 **/
@Service
public class AuthServiceImpl implements AuthService {

    private RSAPrivateKey privateKey;

    @Override
    public boolean checkValid(String username, String password) {
        if (StrUtil.isBlank(username)) {
            return false;
        }
        if (StrUtil.isBlank(password)) {
            return false;
        }

        RSA rsa = new RSA(privateKey,null);
        String value = rsa.encryptBcd(username, KeyType.PrivateKey);
        return value.equals(password) ? true : false;
    }

    @PostConstruct
    public void init() {
        privateKey = IoUtil.readObj(AuthServiceImpl.class.getClassLoader().getResourceAsStream("keystore/auth-private.key"));
    }
}
