package com.seasugar.registry.core;

import com.seasugar.registry.coder.TcpMsgDecoder;
import com.seasugar.registry.coder.TcpMsgEncoder;
import com.seasugar.registry.handler.TcpNettyServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class RegistryStarter {
    private static final int PORT = 8080;

    public void startUp() throws InterruptedException {
        // 处理网络io的accept事件
        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<Channel>() {
                    @Override
                    protected void initChannel(Channel channel) throws Exception {
                        channel.pipeline().addLast(new TcpMsgDecoder());
                        channel.pipeline().addLast(new TcpMsgEncoder());
                        channel.pipeline().addLast(new TcpNettyServerHandler());
                    }
                });
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }));
        ChannelFuture channelFuture = bootstrap.bind(PORT).sync();
        // 启动心跳线程
        new Thread(new InvaildNodeRemoveTask()).start();
        System.out.println("start up success");
        channelFuture.channel().closeFuture().sync();
    }
}
