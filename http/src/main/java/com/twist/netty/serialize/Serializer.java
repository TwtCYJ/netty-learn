package com.twist.netty.serialize;

/**
 * @description: 序列化接口类
 * @author: chenyingjie
 * @create: 2019-01-03 22:12
 **/
public interface Serializer {

    /**
     * obj --> 二进制
     *
     * @param o
     * @return
     */
    byte[] serializer(Object o);

    /**
     *
     * 二进制 --> obj
     * @param clazz
     * @param bytes
     * @param <T>
     * @return
     */
    <T> T deserializer(Class<T> clazz, byte[] bytes);

}
