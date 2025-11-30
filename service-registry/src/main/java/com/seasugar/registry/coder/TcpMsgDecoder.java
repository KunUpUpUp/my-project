package com.seasugar.registry.coder;

import com.seasugar.registry.constants.RegistryConstants;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public class TcpMsgDecoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf byteBuf, List<Object> in) throws Exception {
        // 如果出现粘包拆包，需要判断数据是否完整
        if (byteBuf.readableBytes() > 1 + 4 + 4) {
            if (byteBuf.readByte() != RegistryConstants.MAGIC) {
                ctx.close();
                return;
            }
            int code = byteBuf.readInt();
            int length = byteBuf.readInt();
            if (byteBuf.readableBytes() < length) {
                // 数据不完整，等待下一次读取
                ctx.close();
                return;
            }
            byte[] body = new byte[length];
            byteBuf.readBytes(body);
            TcpMsg tcpMsg = new TcpMsg();
            tcpMsg.setMagic(RegistryConstants.MAGIC);
            tcpMsg.setCode(code);
            tcpMsg.setLength(length);
            tcpMsg.setBody(body);
            in.add(tcpMsg);
        }
    }
}
