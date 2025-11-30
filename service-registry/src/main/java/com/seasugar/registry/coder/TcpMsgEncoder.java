package com.seasugar.registry.coder;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class TcpMsgEncoder extends MessageToByteEncoder{
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Object msg, ByteBuf out) throws Exception {
        TcpMsg tcpMsg = (TcpMsg) msg;
        out.writeByte(tcpMsg.getMagic());
        out.writeInt(tcpMsg.getCode());
        out.writeInt(tcpMsg.getLength());
        out.writeBytes(tcpMsg.getBody());
    }
}
