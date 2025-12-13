package com.seasugar.registry.ioc;

import com.seasugar.registry.event.model.Event;
import com.seasugar.registry.model.ServiceInstance;
import com.seasugar.registry.utils.AssertUtils;
import com.seasugar.registry.utils.PropertiesUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

public class CommonCache {
    public static final Map<String, String> PROP = new HashMap<>();
    public static final Map<String, ServiceInstance> NODE_LIST = new ConcurrentHashMap<>();

    // 如果不加static，只要实例化就会执行一次大括号
    static {
        Properties props = PropertiesUtils.loadProperties();
        AssertUtils.AssertNotNull(props);
        for (Map.Entry<Object, Object> prop : props.entrySet()) {
            PROP.putIfAbsent((String) prop.getKey(), (String) prop.getValue());
        }
    }

    private CommonCache() {

    }



    public static void main(String[] args) {
    }
}