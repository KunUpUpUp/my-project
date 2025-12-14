package com.seasugar.registry.event.spi.listener;

import com.alibaba.fastjson2.JSON;
import com.seasugar.registry.coder.TcpMsg;
import com.seasugar.registry.constants.TcpConstants;
import com.seasugar.registry.enums.EventEnum;
import com.seasugar.registry.event.model.SyncEvent;
import com.seasugar.registry.ioc.CommonCache;
import com.seasugar.registry.model.ServiceInstance;

import java.util.Collection;

public class SyncListener implements Listener<SyncEvent> {
    @Override
    public void onReceive(SyncEvent event) {
        byte[] serviceInstanceBytes = JSON.toJSONBytes(CommonCache.NODE_LIST.values());
        TcpMsg replicaMsg = new TcpMsg(TcpConstants.MAGIC,
                EventEnum.REPLICATION_DATA.getCode(),
                serviceInstanceBytes.length,
                serviceInstanceBytes);
        event.getCtx().writeAndFlush(replicaMsg);
    }
}