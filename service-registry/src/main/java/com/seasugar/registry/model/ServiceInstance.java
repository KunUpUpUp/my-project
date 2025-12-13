package com.seasugar.registry.model;

import jdk.nashorn.internal.objects.annotations.Constructor;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Data
@NoArgsConstructor
public class ServiceInstance {
    private Integer brokerId;
    private String host;
    private Integer port;
    private String advertisedListener;
    private Long registerTime;
    private Long lastHeartBeatTime;
}
