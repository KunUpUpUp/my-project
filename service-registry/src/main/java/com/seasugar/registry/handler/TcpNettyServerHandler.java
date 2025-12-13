package com.seasugar.registry.handler;

import com.alibaba.fastjson2.JSON;
import com.seasugar.registry.coder.TcpMsg;
import com.seasugar.registry.enums.EventEnum;
import com.seasugar.registry.event.EventBus;
import com.seasugar.registry.event.model.Event;
import com.seasugar.registry.event.model.HeartBeatEvent;
import com.seasugar.registry.event.model.RegisterEvent;
import com.seasugar.registry.event.model.UnRegisterEvent;
import com.seasugar.registry.ioc.CommonCache;
import io.netty.channel.*;

import java.net.SocketAddress;
import java.util.Objects;

@ChannelHandler.Sharable
public class TcpNettyServerHandler extends SimpleChannelInboundHandler<TcpMsg> {
    @Override
    public void channelActive(ChannelHandlerContext ctx) {
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        String clientIpPort = ctx.channel().remoteAddress().toString().split("/")[1];
        CommonCache.NODE_LIST.remove(clientIpPort);
        System.out.println(clientIpPort + "主动下线");
        for (String s : CommonCache.NODE_LIST.keySet()) {
            System.out.print(s + " ");
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TcpMsg tcpMsg) {
        Byte code = tcpMsg.getCode();
        Event event = null;
        if (Objects.equals(code, EventEnum.HEART_BEAT.getCode())) {
            event = JSON.parseObject(tcpMsg.getBody(), HeartBeatEvent.class);
        } else if (Objects.equals(code, EventEnum.REGISTER.getCode())) {
            event = JSON.parseObject(tcpMsg.getBody(), RegisterEvent.class);
        } else if (Objects.equals(code, EventEnum.UNREGISTER.getCode())) {
            event = JSON.parseObject(tcpMsg.getBody(), UnRegisterEvent.class);
        }
//        EventEnum eventEnum = EventEnum.valueOf(EventEnum.HEART_BEAT.getName());
//        System.out.println(eventEnum);
        EventBus.publish(event);
    }
}
