package com.seasugar.registry.handler;

import com.alibaba.fastjson2.JSON;
import com.google.common.collect.Lists;
import com.seasugar.registry.constants.TcpConstants;
import com.seasugar.registry.enums.EventEnum;
import com.seasugar.registry.event.model.BrokerInfoEvent;
import com.seasugar.registry.event.model.HeartBeatEvent;
import com.seasugar.registry.event.model.SyncEvent;
import com.seasugar.registry.event.model.RegisterEvent;
import com.seasugar.registry.model.ServiceInstance;
import io.netty.channel.*;
import com.seasugar.registry.coder.TcpMsg;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class TcpNettyClientHandler extends SimpleChannelInboundHandler<TcpMsg> {
    private Integer brokerId;
    private List<ServiceInstance> cacheInstance;

    public TcpNettyClientHandler() {
    }

    public TcpNettyClientHandler(Integer brokerId) {
        this.brokerId = brokerId;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        // 不要在这种方法中做耗时操作，另起线程处理
        new Thread(() -> {
            heartBeat(ctx, brokerId);
        }).start();

        // 定期刷新元数据
        new Thread(() -> {
            replicationData(ctx, brokerId);
        }).start();
    }

    private static void replicationData(ChannelHandlerContext ctx, Integer brokerId) {
        while (true) {
            try {
                TimeUnit.MINUTES.sleep(5);
                SyncEvent syncEvent = new SyncEvent();
                syncEvent.setMasterId(99999);
                syncEvent.setSlaveId(brokerId);
                syncEvent.setTimeStamp(System.currentTimeMillis());
                byte[] copyEventBytes = JSON.toJSONBytes(syncEvent);
                TcpMsg msg = new TcpMsg(TcpConstants.MAGIC, EventEnum.REPLICATION_DATA.getCode(), copyEventBytes.length, copyEventBytes);
                ctx.writeAndFlush(msg);
            } catch (Exception e) {
                System.out.println(e);
            }
        }
    }

    private static void heartBeat(ChannelHandlerContext ctx, Integer brokerId) {
        String ipPort = register(ctx, brokerId);
        HeartBeatEvent heartBeat = new HeartBeatEvent();
        heartBeat.setId(ipPort);
        while (true) {
            try {
                heartBeat.setTimeStamp(System.currentTimeMillis());
                byte[] msgByte = JSON.toJSONBytes(heartBeat);
                TcpMsg msg = new TcpMsg(TcpConstants.MAGIC, EventEnum.HEART_BEAT.getCode(), msgByte.length, msgByte);
                ctx.channel().writeAndFlush(msg);
                TimeUnit.SECONDS.sleep(3);
            } catch (Exception e) {
                System.out.println(e);
            }
        }
    }

    private static void createTopic(ChannelHandlerContext ctx) {
        BrokerInfoEvent brokerInfoEvent = new BrokerInfoEvent();
        brokerInfoEvent.setBrokerId(0);
        brokerInfoEvent.setTopic("test");
        brokerInfoEvent.setPartition(0);
        brokerInfoEvent.setTimeStamp(System.currentTimeMillis());
        byte[] brokerInfoBytes = JSON.toJSONBytes(brokerInfoEvent, StandardCharsets.UTF_8);
        TcpMsg brokerInfoMsg = new TcpMsg(TcpConstants.MAGIC, EventEnum.CREATE_TOPIC.getCode(), brokerInfoBytes.length, brokerInfoBytes);
        ctx.writeAndFlush(brokerInfoMsg);
    }

    private static String register(ChannelHandlerContext ctx, Integer brokerId) {
        RegisterEvent registerEvent = new RegisterEvent();
        String ipPort = ctx.channel().localAddress().toString().split("/")[1];
        String ip = ipPort.split(":")[0];
        Integer port = Integer.parseInt(ipPort.split(":")[1]);
        registerEvent.setId(ipPort);
        registerEvent.setHost(ip);
        registerEvent.setPort(port);
        registerEvent.setBrokerId(brokerId);
        registerEvent.setAdvertisedListener("myhost.dev." + brokerId);
        registerEvent.setTimeStamp(System.currentTimeMillis());
        byte[] registerBytes = JSON.toJSONBytes(registerEvent, StandardCharsets.UTF_8);
        TcpMsg hostMsg = new TcpMsg(TcpConstants.MAGIC, EventEnum.REGISTER.getCode(), registerBytes.length, registerBytes);
        ctx.writeAndFlush(hostMsg);
        return ipPort;
    }


    // Server返还数据时可以接收
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, TcpMsg tcpMsg) throws Exception {
//        BufferedWriter writer = null;
//        try {
//            String dir = CommonCache.PROP.get(PropertiesConstants.LOG_DIR);
//            File file = new File(dir);
//            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, true), StandardCharsets.UTF_8));
//            writer.write(new String(tcpMsg.getBody(), StandardCharsets.UTF_8));
//            writer.newLine();
//            writer.flush();
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            writer.close();
//        }
        if (Objects.equals(tcpMsg.getCode(), EventEnum.REPLICATION_DATA.getCode())) {
            List<ServiceInstance> serviceInstances = JSON.parseArray(tcpMsg.getBody(), ServiceInstance.class);
            cacheInstance = serviceInstances;
            System.out.println(new Date() + "——已更新实例列表");
        }
    }
}
