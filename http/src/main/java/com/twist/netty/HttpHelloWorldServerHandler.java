package com.twist.netty;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.twist.netty.pojo.User;
import com.twist.netty.serialize.impl.JSONSerializer;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.util.AsciiString;
import org.apache.commons.codec.CharEncoding;
import org.apache.commons.codec.Charsets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;
import java.util.Map;

import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

/**
 * @description: ${description}
 * @author: chenyingjie
 * @create: 2019-01-03 22:06
 **/
public class HttpHelloWorldServerHandler extends SimpleChannelInboundHandler<HttpObject> {

    private static final Logger logger = LoggerFactory.getLogger(HttpHelloWorldServerHandler.class);

    private HttpHeaders headers;


    private static final String FAVICION_ICO = "/favicon.ico";
    private static final AsciiString CONTENT_TYPE = AsciiString.cached("Content-Type");
    private static final AsciiString CONTENT_LENGTH = AsciiString.cached("Content-Length");
    private static final AsciiString CONNECTION = AsciiString.cached("Connection");
    private static final AsciiString KEEP_ALIVE = AsciiString.cached("keep-alive");


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HttpObject msg) throws Exception {
        User user = new User();
        user.setUserName("twist");
        user.setDate(new Date());

        if (msg instanceof HttpRequest) {
            HttpRequest request = (HttpRequest) msg;
            headers = request.headers();
            String uri = request.uri();
            logger.info("http uri : " + uri);

            if (FAVICION_ICO.equals(uri)) {
                return;
            }

            HttpMethod method = request.method();
            if (method.equals(HttpMethod.GET)) {
                QueryStringDecoder decoder = new QueryStringDecoder(uri, Charsets.toCharset(CharEncoding.UTF_8));
                Map<String, List<String>> attrs = decoder.parameters();
                //打印请求参数
                for (Map.Entry<String, List<String>> attr : attrs.entrySet()) {
                    for (String val : attr.getValue()) {
                        logger.info(attr.getKey() + ":" + val);
                    }
                }
                user.setMethod("get");
            } else if (method.equals(HttpMethod.POST)) {
                //将msg 转FullHttpRequest 获取数据
                FullHttpRequest full = (FullHttpRequest) msg;
                dealWithContentType(full);
                user.setMethod("post");
            }

            JSONSerializer serializer = new JSONSerializer();
            byte[] content = serializer.serializer(user);
            FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK, Unpooled.wrappedBuffer(content));
            response.headers().set(CONTENT_TYPE, "text/plain");
            response.headers().setInt(CONTENT_LENGTH, response.content().readableBytes());

            boolean keepAlive = HttpUtil.isKeepAlive(request);
            if (!keepAlive) {
                ctx.write(response).addListener(ChannelFutureListener.CLOSE);
            } else {
                response.headers().set(CONNECTION, KEEP_ALIVE);
                ctx.write(response);
            }
        }
    }

    /**
     * 根据Content-Type 处理 POST 请求
     * @param full
     */
    private void dealWithContentType(FullHttpRequest full) {
        String type = headers.get("Content-Type");
        String[] list = type.split(";");
        String contentType = list[0];
        if (contentType.equals("application/json")) {
            String jsonStr = full.content().toString(Charsets.toCharset(CharEncoding.UTF_8));
            JSONObject obj = JSON.parseObject(jsonStr);
            for (Map.Entry<String, Object> entry : obj.entrySet()) {
                logger.info(entry.getKey() +  ":" + entry.getValue());
            }
        } else if (contentType.equals("application/x-www-form-urlencoded")) {
            String jsonStr = full.content().toString(Charsets.toCharset(CharEncoding.UTF_8));
            QueryStringDecoder decoder = new QueryStringDecoder(jsonStr,false);
            Map<String,List<String>> attrs = decoder.parameters();
            for (Map.Entry<String, List<String>> entry : attrs.entrySet()) {
                for (String val : entry.getValue()) {
                    logger.info(entry.getKey() + " : "+ val);
                }
            }
        } else if (contentType.equals("multipart/form-data")) {
            // TODO: 2019-01-03 文件上传
        } else {
            // do nothing
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        ctx.close();
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.close();
    }
}
