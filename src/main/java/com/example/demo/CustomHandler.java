package com.example.demo;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

import java.nio.charset.CharsetEncoder;

public class CustomHandler extends SimpleChannelInboundHandler<HttpObject> {
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, HttpObject httpObject) throws Exception {
        //获取channel
       Channel channel = channelHandlerContext.channel();

       if (httpObject instanceof HttpRequest) {
           //显示客户端远程地址
           System.out.println(channel.remoteAddress());

           ByteBuf content = Unpooled.copiedBuffer("Hello netty", CharsetUtil.UTF_8);

           //构建http response
           FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
                   HttpResponseStatus.OK,
                   content);
           response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain");
           response.headers().set(HttpHeaderNames.CONTENT_LENGTH, content.readableBytes());

           channelHandlerContext.writeAndFlush(response);
       }

    }
}
