package com.seasugar.registry.event;

import com.google.common.collect.Lists;
import com.seasugar.registry.event.spi.listener.Listener;
import com.seasugar.registry.event.model.Event;
import com.seasugar.registry.utils.ReflectUtils;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;

// 事件总线，负责注册和发布事件（为的是消息解耦）
public class EventBus {
    private static final Map<Class<? extends Event>, List<Listener>> eventListenerMap = new ConcurrentHashMap<>();

    public void init() {
        // spi
        ServiceLoader<Listener> serviceLoader = ServiceLoader.load(Listener.class);
        for (Listener listener : serviceLoader) {
            Class clazz = ReflectUtils.getInterfaceT(listener, 0);
            register(clazz, listener);
        }
    }

    private <E extends Event> void register(Class<? extends Event> clazz, Listener<E> listener) {
        List<Listener> listeners = eventListenerMap.get(clazz);
        if (CollectionUtils.isEmpty(listeners)) {
            eventListenerMap.put(clazz, Lists.newArrayList(listener));
        }
    }

    // 发布事件后，对应的监听者就应该处理事件
    // 应该用一个队列，构成生产消费
    public static void publish(Event event) {
        List<Listener> listeners = eventListenerMap.get(event.getClass());
        for (Listener listener : listeners) {
            listener.onReceive(event);
        }
    }

//    public static void main(String[] args) {
//        ServiceLoader<Listener> serviceLoader = ServiceLoader.load(Listener.class);
//        for (Listener listener : serviceLoader) {
//            Class<?> interfaceT = ReflectUtils.getInterfaceT(listener, 0);
//            System.out.println(interfaceT);
//        }
//    }
}
