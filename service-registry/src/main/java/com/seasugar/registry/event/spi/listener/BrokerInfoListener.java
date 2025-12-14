package com.seasugar.registry.event.spi.listener;

import com.seasugar.registry.event.model.BrokerInfoEvent;

public class BrokerInfoListener implements Listener<BrokerInfoEvent>{
    @Override
    public void onReceive(BrokerInfoEvent event) {
        System.out.println("收到消息：" + event.getTopic());
    }
}