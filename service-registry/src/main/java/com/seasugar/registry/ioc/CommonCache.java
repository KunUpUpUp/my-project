package com.seasugar.registry.ioc;

import com.seasugar.registry.model.ServiceInstance;
import com.seasugar.registry.utils.AssertUtils;
import com.seasugar.registry.utils.PropertiesUtils;
import io.netty.channel.ChannelHandlerContext;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class CommonCache {
    public static final Map<String, String> PROP = new HashMap<>();
    public static final Map<String, ServiceInstance> NODE_LIST = new ConcurrentHashMap<>();
    public static List<ChannelHandlerContext> ALIVE_CHANNELLIST = new CopyOnWriteArrayList<>();

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