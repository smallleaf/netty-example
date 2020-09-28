package com.bit1024.http;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.websocketx.*;

/**
 * \* @Author: yesheng
 * \* Date: 2020/9/28 16:51
 * \* Description:
 * \
 */
public class WebsocketServerHandler extends ChannelInboundHandlerAdapter {

    private WebSocketServerHandshaker handshaker;


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if(msg instanceof FullHttpRequest){
            handleHttpRequest(ctx, (FullHttpRequest) msg);
        }else if(msg instanceof WebSocketFrame){
            handleWebsocketFrame(ctx, (WebSocketFrame) msg);
        }
        super.channelRead(ctx, msg);
    }

    private void handleHttpRequest(ChannelHandlerContext ctx,FullHttpRequest req){
        if(!req.decoderResult().isSuccess() || (!"websocket".equals(req.headers().get("Upgrade")))){
            sendHttpResponse(ctx,req,new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,HttpResponseStatus.BAD_REQUEST));
        }
        WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory("ws://localhost:8080/websocket",null,false);
        handshaker = wsFactory.newHandshaker(req);
        if(handshaker == null){
            WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
        }else{
            handshaker.handshake(ctx.channel(),req);
        }
    }

    private void sendHttpResponse(ChannelHandlerContext ctx, FullHttpRequest req, FullHttpResponse response){
        if(response.status() != HttpResponseStatus.OK){
            ByteBuf buf = Unpooled.copiedBuffer(response.status().toString().getBytes());
            response.content().writeBytes(buf);
            buf.release();
            HttpUtil.setContentLength(response,response.content().readableBytes());
        }
        ctx.channel().writeAndFlush(response);
    }

    private void handleWebsocketFrame(ChannelHandlerContext ctx,WebSocketFrame frame){
        if(frame instanceof CloseWebSocketFrame){
            handshaker.close(ctx.channel(),((CloseWebSocketFrame) frame).retain());
            return;
        }
        if(frame instanceof PingWebSocketFrame){
            ctx.channel().write(new PongWebSocketFrame(frame.content().retain()));
            return;
        }
        String request = ((TextWebSocketFrame)frame).text();
        System.out.println("request:"+request);
        ctx.channel().writeAndFlush(new TextWebSocketFrame("欢迎使用websocket 服务"));
    }
}