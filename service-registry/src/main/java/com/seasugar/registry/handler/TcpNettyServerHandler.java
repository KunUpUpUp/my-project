package com.seasugar.registry.handler;

import com.alibaba.fastjson2.JSON;
import com.seasugar.registry.coder.TcpMsg;
import com.seasugar.registry.enums.EventEnum;
import com.seasugar.registry.event.EventBus;
import com.seasugar.registry.event.model.*;
import com.seasugar.registry.ioc.CommonCache;
import io.netty.channel.*;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

public class TcpNettyServerHandler extends SimpleChannelInboundHandler<TcpMsg> {
    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        CommonCache.ALIVE_CHANNELLIST.add(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        String clientIpPort = ctx.channel().remoteAddress().toString().split("/")[1];
        CommonCache.NODE_LIST.remove(clientIpPort);
        CommonCache.ALIVE_CHANNELLIST.remove(ctx);
        System.out.println(clientIpPort + "主动下线");
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
        } else if (Objects.equals(code, EventEnum.CREATE_TOPIC.getCode())) {
            event = JSON.parseObject(tcpMsg.getBody(), BrokerInfoEvent.class);
        } else if (Objects.equals(code, EventEnum.REPLICATION_DATA.getCode())) {
            event = JSON.parseObject(tcpMsg.getBody(), SyncEvent.class);
            event.setCtx(ctx);
        }
        EventBus.publish(event);
    }
}
