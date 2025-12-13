package com.seasugar.registry.event.spi.listener;

import com.seasugar.registry.event.model.HeartBeatEvent;
import com.seasugar.registry.excetions.ExceptionEnum;
import com.seasugar.registry.ioc.CommonCache;
import com.seasugar.registry.model.ServiceInstance;

public class HeartBeatListener implements Listener<HeartBeatEvent> {
    // 更新心跳时间
    @Override
    public void onReceive(HeartBeatEvent event) {
        ServiceInstance serviceInstance = CommonCache.NODE_LIST.get(event.getId());
        if (serviceInstance != null) {
            serviceInstance.setLastHeartBeatTime(event.getTimeStamp());
            System.out.println("收到节点 " + serviceInstance.getBrokerId() + " 的心跳");
        } else {
            throw new RuntimeException(ExceptionEnum.NO_SUCH_CLASS.getMsg());
        }
    }
}
