package com.seasugar.registry.event.spi.listener;

import com.seasugar.registry.event.model.HeartBeatEvent;
import com.seasugar.registry.ioc.CommonCache;
import com.seasugar.registry.model.ServiceInstance;

public class HeartBeatListener implements Listener<HeartBeatEvent>{
    @Override
    public void onReceive(HeartBeatEvent event) {
        // 把节点加入Cache
        ServiceInstance serviceInstance = CommonCache.NODE_LIST.getOrDefault(event.getId(), new ServiceInstance(event.getId()));
        serviceInstance.setLastHeartBeatTime(event.getTimeStamp());
        CommonCache.NODE_LIST.put(event.getId(), serviceInstance);
    }
}
