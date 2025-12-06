package com.seasugar.registry.handler;

import com.alibaba.fastjson2.JSON;
import com.seasugar.registry.coder.TcpMsg;
import com.seasugar.registry.constants.RegistryConstants;
import com.seasugar.registry.ioc.CommonCache;
import com.seasugar.registry.model.HeartBeat;
import com.seasugar.registry.utils.AssertUtils;
import io.netty.channel.*;

import java.util.Objects;

@ChannelHandler.Sharable
public class TcpNettyServerHandler extends SimpleChannelInboundHandler<TcpMsg> {
    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        System.out.println(ctx.channel().remoteAddress() + "已连接");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TcpMsg tcpMsg) {
        Byte code = tcpMsg.getCode();
        if (Objects.equals(code, RegistryConstants.HEART_BEAT)) {
            HeartBeat heartBeat = JSON.parseObject(tcpMsg.getBody(), HeartBeat.class);
            CommonCache.NODE_LIST.put(heartBeat.getId(), heartBeat);
        }
    }
}
