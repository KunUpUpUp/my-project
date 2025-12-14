package com.seasugar.registry.event.model;

import io.netty.channel.ChannelHandlerContext;
import lombok.Data;

@Data
public abstract class Event {
    private Long timeStamp;
    private ChannelHandlerContext ctx;
}
