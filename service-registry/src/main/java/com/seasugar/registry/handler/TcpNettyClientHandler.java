package com.seasugar.registry.handler;

import com.alibaba.fastjson2.JSON;
import com.seasugar.registry.constants.PropertiesConstants;
import com.seasugar.registry.constants.RegistryConstants;
import com.seasugar.registry.ioc.CommonCache;
import com.seasugar.registry.model.HeartBeat;
import io.netty.channel.*;
import com.seasugar.registry.coder.TcpMsg;

import java.io.*;
import java.nio.charset.StandardCharsets;

@ChannelHandler.Sharable
public class TcpNettyClientHandler extends SimpleChannelInboundHandler<TcpMsg> {
    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        System.out.println(ctx.channel().remoteAddress() + "已连接");
        // 不要在这种方法中做耗时操作，另起线程处理
        new Thread(() -> {
            HeartBeat heartBeat = new HeartBeat();
            String ipPort = ctx.channel().localAddress().toString().split("/")[1];
            heartBeat.setId(ipPort);
            while (true) {
                try {
                    heartBeat.setLastHeartBeatTime(System.currentTimeMillis());
                    byte[] msgByte = JSON.toJSONBytes(heartBeat);
                    TcpMsg msg = new TcpMsg(RegistryConstants.MAGIC, RegistryConstants.HEART_BEAT, msgByte.length, msgByte);
                    ctx.channel().writeAndFlush(msg);
                    Thread.sleep(3000);
                } catch (Exception e) {
                    System.out.println(e);
                }
            }
        }).start();
    }


    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, TcpMsg tcpMsg) throws Exception {
        BufferedWriter writer = null;
        try {
            String dir = CommonCache.PROP.get(PropertiesConstants.LOG_DIR);
            File file = new File(dir);
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, true), StandardCharsets.UTF_8));
            writer.write(new String(tcpMsg.getBody(), StandardCharsets.UTF_8));
            writer.newLine();
            writer.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            writer.close();
        }
    }
}
