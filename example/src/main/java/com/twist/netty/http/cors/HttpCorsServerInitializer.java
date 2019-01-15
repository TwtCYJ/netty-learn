package com.twist.netty.http.cors;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.codec.http.cors.CorsConfig;
import io.netty.handler.codec.http.cors.CorsConfigBuilder;
import io.netty.handler.codec.http.cors.CorsHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.stream.ChunkedWriteHandler;

/**
 * @description: 这里需要做一个跨域配置
 * @author: chenyingjie
 * @create: 2019-01-15 15:15
 **/
public final class HttpCorsServerInitializer extends ChannelInitializer<SocketChannel> {

    private final SslContext sslCtx;

    public HttpCorsServerInitializer(SslContext sslCtx) {
        this.sslCtx = sslCtx;
    }

    /**
     *
     *
     * corsConfig.allowedRequestHeaders("custom-request-header")
     *
     *
     *
     *
     *
     *
     *  <h3>Expose response headers</h3>
     *  * By default a browser only exposes the following simple header:
     *  * <ul>
     *  * <li>Cache-Control</li>
     *  * <li>Content-Language</li>
     *  * <li>Content-Type</li>
     *  * <li>Expires</li>
     *  * <li>Last-Modified</li>
     *  * <li>Pragma</li>
     *  * </ul>
     *  以上是浏览器默认的header
     *
     *  如果要开启其他的 就要使用corsConfig.exposedHeaders("custom-response-header");
     * @param ch
     * @throws Exception
     */
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        //配置corsConfig 允许跨域  .allowNullOrigin()： 允许空的orgin  ||  允许特定条件 allowedRequestHeaders("custom-request-header") .exposeHeaders("custom-response-header")
        CorsConfig corsConfig = CorsConfigBuilder.forAnyOrigin().allowedRequestHeaders("custom-request-header")
                .allowCredentials().exposeHeaders("custom-response-header").build();
        ChannelPipeline pipeline = ch.pipeline();

        if (sslCtx != null) {
            pipeline.addLast(sslCtx.newHandler(ch.alloc()));
        }

        pipeline.addLast(new HttpResponseEncoder())
                .addLast(new HttpRequestDecoder())
                .addLast(new HttpObjectAggregator(65536))
                .addLast(new ChunkedWriteHandler())
                .addLast(new CorsHandler(corsConfig))
                .addLast(new OkResponseHandler());
    }
}
