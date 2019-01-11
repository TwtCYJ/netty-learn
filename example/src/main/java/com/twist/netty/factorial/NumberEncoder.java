package com.twist.netty.factorial;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.math.BigInteger;

/**
 * @description: ${description}
 * @author: chenyingjie
 * @create: 2019-01-11 13:19
 **/
public class NumberEncoder extends MessageToByteEncoder<Number> {
    @Override
    protected void encode(ChannelHandlerContext ctx, Number msg, ByteBuf out) throws Exception {
        BigInteger integer;

        if (msg instanceof BigInteger) {
            integer = (BigInteger) msg;
        } else {
            integer = new BigInteger(String.valueOf(msg));
        }

        // integer --> byte[]
        byte[] data = integer.toByteArray();
        int len = data.length;

        out.writeByte((byte)'F'); //header
        out.writeInt(len);//length
        out.writeBytes(data);//data
    }
}
