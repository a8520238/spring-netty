package com.example.demo.websocket;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;

public class WSServerInitialzer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {

        ChannelPipeline pipeline = socketChannel.pipeline();

        //http编解码器
        pipeline.addLast(new HttpServerCodec());
        //对写大数据流处理
        pipeline.addLast(new ChunkedWriteHandler());
        //对httpMessage 进行聚合 FullHttpRequest或FullHttpResponse
        //几乎在netty中的编程，都会用到此handler
        pipeline.addLast(new HttpObjectAggregator(1024*64));
        //以上支持http协议

        // websocket 服务器处理协议 用于指定给客户端连接访问的路由：/ws
        //本handler 会帮助处理一些繁重的事情
        //会帮助处理握手动作 close ping pong
        //对于websocket 来说，都是以frames进行传输的，不同的数据类型对应的frames也不同
        pipeline.addLast(new WebSocketServerProtocolHandler("/ws"));
        //自定义handler
        pipeline.addLast(new ChatHandler());
    }
}
