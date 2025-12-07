package com.seasugar.registry.event.model;

import io.netty.channel.ChannelHandlerContext;
import lombok.Data;

@Data
public abstract class Event {
    private long timeStamp;
    private ChannelHandlerContext ctx;
}
