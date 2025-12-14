package com.seasugar.registry.model;

import lombok.Data;

@Data
public class ServiceInstance {
    private Integer brokerId;
    private String host;
    private Integer port;
    private String advertisedListener;
    private Long registerTime;
    private Long lastHeartBeatTime;
}