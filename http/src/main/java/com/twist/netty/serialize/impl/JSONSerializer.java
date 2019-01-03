package com.twist.netty.serialize.impl;

import com.alibaba.fastjson.JSON;
import com.twist.netty.serialize.Serializer;

/**
 * @description: ${description}
 * @author: chenyingjie
 * @create: 2019-01-03 22:15
 **/
public class JSONSerializer implements Serializer {
    @Override
    public byte[] serializer(Object o) {
        return JSON.toJSONBytes(o);
    }

    @Override
    public <T> T deserializer(Class<T> clazz, byte[] bytes) {
        return JSON.parseObject(bytes, clazz);
    }
}
