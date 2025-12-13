package com.seasugar.registry.event.spi.listener;

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
    }
}
