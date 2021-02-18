package com.imooc.netty;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;


//TextWebSocketFrame 在netty中，用于为websocket专门处理文本的对象，frame是消息的载体
public class HeartBeatHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        //用于触发用户事件，包含读空闲和写空闲，读写空闲
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent)evt;
            if (event.state() == IdleState.READER_IDLE) {
                System.out.println("进入读空闲");
            } else if (event.state() == IdleState.WRITER_IDLE) {
                System.out.println("进入写空闲");
            } else if (event.state() == IdleState.ALL_IDLE) {
                Channel channel = ctx.channel();
                //关闭无用资源防止浪费
                channel.close();
                System.out.println("channel关闭后，users的数量" + ChatHandler.users.size());
            }
        }
    }
}
