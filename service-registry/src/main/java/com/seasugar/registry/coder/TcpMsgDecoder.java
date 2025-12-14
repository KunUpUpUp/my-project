package com.seasugar.registry.coder;

import com.seasugar.registry.constants.TcpConstants;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public class TcpMsgDecoder extends ByteToMessageDecoder {
    private static final int HEADER_SIZE = 1 + 1 + 4;

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf byteBuf, List<Object> in) throws Exception {
        // 如果出现粘包拆包，需要判断数据是否完整
        byte magicNum = byteBuf.readByte();
        if (byteBuf.readableBytes() < HEADER_SIZE || magicNum != TcpConstants.MAGIC) {
            return;
        }

        Byte code = byteBuf.readByte();
        Integer length = byteBuf.readInt();

        if (byteBuf.readableBytes() < length) {
            // 消息体不完整，重置读指针
            byteBuf.resetReaderIndex();
            return;
        }

        byte[] body = new byte[length];
        byteBuf.readBytes(body);
        TcpMsg tcpMsg = new TcpMsg();
        tcpMsg.setMagic(TcpConstants.MAGIC);
        tcpMsg.setCode(code);
        tcpMsg.setLength(length);
        tcpMsg.setBody(body);
        in.add(tcpMsg);
    }
}
