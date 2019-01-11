package com.twist.netty.factorial;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.CorruptedFrameException;

import java.math.BigInteger;
import java.util.List;

/**
 * @description: ${description}
 * @author: chenyingjie
 * @create: 2019-01-11 13:11
 **/
public class BigIntegerDecoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (in.readableBytes() < 5) {
            return;
        }

        in.markReaderIndex();

        /**
         * magic number： 'F' ox46
         * 42  --> { 'F', 0, 0, 0, 1, 42 }
         */
        //这里检查首位是不是F开头
        int magicNumber = in.readUnsignedByte();
        if (magicNumber != 'F') {
            in.resetReaderIndex();
            throw new CorruptedFrameException("Invalid magic number:" + magicNumber);
        }

        //数据传不完整 就直接返回 粘包？
        int len = in.readInt();
        if (in.readableBytes() < len) {
            in.resetReaderIndex();
            return;
        }

        byte[] decoded = new byte[len];
        in.readBytes(decoded);

        out.add(new BigInteger(decoded));
    }
}
