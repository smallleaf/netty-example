package com.bit1024.http;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.*;

import java.nio.charset.Charset;
import java.util.Map;

import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

/**
 * \* @Author: yesheng
 * \* Date: 2020/9/27 15:22
 * \* Description:
 * \
 */
public class HttpServerHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        //判断是不是http请求
        if(msg instanceof HttpRequest){
            HttpRequest httpRequest = (HttpRequest) msg;
            parseUri(httpRequest);
            parseHttpMethod(httpRequest);
            parseHttpHeaders(httpRequest);
            parseBody(httpRequest);
            FullHttpResponse res = new DefaultFullHttpResponse(HTTP_1_1, OK, Unpooled.wrappedBuffer("ok".getBytes()));
            HttpUtil.setContentLength(res, res.content().readableBytes());
            ctx.writeAndFlush(res);
        }
        super.channelRead(ctx, msg);
    }

    /**
     * 获得请求方式
     * @param httpRequest
     */
    private HttpMethod parseHttpMethod(HttpRequest httpRequest){
        HttpMethod httpMethod = httpRequest.method();
        System.out.println("method:"+httpMethod.name());
        return httpMethod;
    }

    /**
     * 打印头部信息
     * @param httpRequest
     */
    private HttpHeaders parseHttpHeaders(HttpRequest httpRequest){
        HttpHeaders httpHeaders = httpRequest.headers();
        for (Map.Entry<String, String> entry : httpHeaders.entries()) {
            System.out.println(entry.getKey()+":"+entry.getValue());
        }
        return httpHeaders;
    }

    /**
     * 打印请求地址
     * @param httpRequest
     */
    private void parseUri(HttpRequest httpRequest){
        String uri = httpRequest.uri();
        System.out.println("uri:"+uri);
    }

    /**
     * 打印请求体
     * @param httpRequest
     */
    private void parseBody(HttpRequest httpRequest){
        if(httpRequest instanceof HttpContent){
            HttpContent httpContent = (HttpContent) httpRequest;
            System.out.println("content:"+httpContent.content().toString(Charset.defaultCharset()));
        }
    }

}