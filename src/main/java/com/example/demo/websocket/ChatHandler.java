package com.example.demo.websocket;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.time.LocalDateTime;

//TextWebSocketFrame 在netty中，用于为websocket专门处理文本的对象，frame是消息的载体
public class ChatHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    //用于记录和管理所有客户端的channel
    private static ChannelGroup clients =
            new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, TextWebSocketFrame textWebSocketFrame) throws Exception {
        String content = textWebSocketFrame.text();
        System.out.println("接收到的数据" + content);

        for (Channel channel: clients) {
            channel.writeAndFlush(new TextWebSocketFrame("服务器接受到消息" + LocalDateTime.now() + content));
        }
        //此方法与for循环一致
        //clients.writeAndFlush(new TextWebSocketFrame("服务器接受到消息" + LocalDateTime.now() + content));
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        clients.add(ctx.channel());
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        //当触发handlerRemoved, ChannelGroup会自动移除对应的channel
        clients.remove(ctx.channel());
    }
}
