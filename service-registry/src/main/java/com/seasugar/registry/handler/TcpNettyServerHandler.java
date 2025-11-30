package com.seasugar.registry.handler;

import com.seasugar.registry.coder.TcpMsg;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

@ChannelHandler.Sharable
public class TcpNettyServerHandler extends SimpleChannelInboundHandler {
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Object msg) throws Exception {
        TcpMsg tcpMsg = (TcpMsg) msg;
        System.out.println(tcpMsg);
    }
}
