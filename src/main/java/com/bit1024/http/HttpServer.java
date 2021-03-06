package com.bit1024.http;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;

/**
 * \* @Author: yesheng
 * \* Date: 2020/9/27 15:16
 * \* Description:
 * \
 */
public class HttpServer {

    public void run(int port){
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workGroup = new NioEventLoopGroup();
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(bossGroup,workGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        //设置http解码器
                        ch.pipeline().addLast(new HttpRequestDecoder());
                        //设置http内容处理器
                        ch.pipeline().addLast(new HttpObjectAggregator(65536));
                        //设置http编码器
                        ch.pipeline().addLast(new HttpResponseEncoder());
                        //自定义服务处理器
                        ch.pipeline().addLast(new WebsocketServerHandler());
                    }
                });

        try {
            ChannelFuture future = bootstrap.bind(8088).sync();
            System.out.println("服务器启动成功");
            future.channel().closeFuture().sync();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) {
        new HttpServer().run(8080);
    }
}