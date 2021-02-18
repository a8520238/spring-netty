package com.example.demo;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class HelloServer {
    public static void main(String[] args) throws Exception{
        //主线程组
        EventLoopGroup bossGroup = new NioEventLoopGroup();

        //从线程组
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            //netty服务器，ServerBootstrap是一个启动类
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new HelloServerInitializer());

            //启动server,设置8088为启动端口号
            ChannelFuture channelFuture = serverBootstrap.bind(8088).sync();
            //监听关闭channel,设置位同步方式
            channelFuture.channel().closeFuture().sync();
        } finally {
             bossGroup.shutdownGracefully();
             workerGroup.shutdownGracefully();
        }
    }
}
