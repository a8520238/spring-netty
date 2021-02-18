package com.example.demo;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

import io.netty.handler.codec.http.HttpServerCodec;
public class HelloServerInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        // 通过SocketChannel 获得对应的管道
        ChannelPipeline pipeline = socketChannel.pipeline();

        pipeline.addLast("HttpServerCodec", new HttpServerCodec());

        pipeline.addLast("customHandler", new CustomHandler());
    }
}
