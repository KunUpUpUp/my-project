package com.seasugar.registry.handler;

import com.alibaba.fastjson2.JSON;
import com.seasugar.registry.constants.PropertiesConstants;
import com.seasugar.registry.constants.RegistryConstants;
import com.seasugar.registry.enums.EventEnum;
import com.seasugar.registry.event.model.HeartBeatEvent;
import com.seasugar.registry.event.model.RegisterEvent;
import com.seasugar.registry.ioc.CommonCache;
import io.netty.channel.*;
import com.seasugar.registry.coder.TcpMsg;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

@ChannelHandler.Sharable
public class TcpNettyClientHandler extends SimpleChannelInboundHandler<TcpMsg> {
    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        // 不要在这种方法中做耗时操作，另起线程处理
        new Thread(() -> {
            RegisterEvent registerEvent = new RegisterEvent();
            String ipPort = ctx.channel().localAddress().toString().split("/")[1];
            String ip = ipPort.split(":")[0];
            Integer port = Integer.parseInt(ipPort.split(":")[1]);
            registerEvent.setId(ipPort);
            registerEvent.setHost(ip);
            registerEvent.setPort(port);
            registerEvent.setBrokerId(0);
            registerEvent.setAdvertisedListener("myhost.dev");
            registerEvent.setCtx(ctx);
            registerEvent.setTimeStamp(System.currentTimeMillis());
            byte[] registerBytes = JSON.toJSONBytes(registerEvent, StandardCharsets.UTF_8);
            TcpMsg hostMsg = new TcpMsg(RegistryConstants.MAGIC, EventEnum.REGISTER.getCode(), registerBytes.length, registerBytes);
            ctx.writeAndFlush(hostMsg);
            HeartBeatEvent heartBeat = new HeartBeatEvent();
            heartBeat.setId(ipPort);
            while (true) {
                try {
                    heartBeat.setCtx(ctx);
                    heartBeat.setTimeStamp(System.currentTimeMillis());
                    byte[] msgByte = JSON.toJSONBytes(heartBeat);
                    TcpMsg msg = new TcpMsg(RegistryConstants.MAGIC, EventEnum.HEART_BEAT.getCode(), msgByte.length, msgByte);
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
