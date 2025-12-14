package com.seasugar.registry.event.spi.listener;

import com.alibaba.fastjson2.JSON;
import com.seasugar.registry.coder.TcpMsg;
import com.seasugar.registry.constants.TcpConstants;
import com.seasugar.registry.enums.EventEnum;
import com.seasugar.registry.event.model.HeartBeatEvent;
import com.seasugar.registry.event.model.RegisterEvent;
import com.seasugar.registry.ioc.CommonCache;
import com.seasugar.registry.model.ServiceInstance;

public class RegisterListener implements Listener<RegisterEvent> {
    @Override
    public void onReceive(RegisterEvent event) {
        // 把节点加入Cache
        ServiceInstance serviceInstance = new ServiceInstance();
        serviceInstance.setBrokerId(event.getBrokerId());
        serviceInstance.setHost(event.getHost());
        serviceInstance.setPort(event.getPort());
        serviceInstance.setAdvertisedListener(event.getAdvertisedListener());
        serviceInstance.setRegisterTime(event.getTimeStamp());
        CommonCache.NODE_LIST.put(event.getId(), serviceInstance);
        System.out.println("节点 " + serviceInstance.getBrokerId() + " 已注册");
        // 数据同步，像所有节点发送注册事件
        CommonCache.ALIVE_CHANNELLIST.forEach(ctx -> {
            byte[] serviceInstanceBytes = JSON.toJSONBytes(CommonCache.NODE_LIST.values());
            TcpMsg replicaMsg = new TcpMsg(TcpConstants.MAGIC,
                    EventEnum.REPLICATION_DATA.getCode(),
                    serviceInstanceBytes.length,
                    serviceInstanceBytes);
            ctx.writeAndFlush(replicaMsg);
        });
    }
}
