package com.seasugar.registry.model;

import jdk.nashorn.internal.objects.annotations.Constructor;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Data
@NoArgsConstructor
public class ServiceInstance {
    private String host;
    private Long lastHeartBeatTime;

    public ServiceInstance(String host) {
        this.host = host;
    }
}
