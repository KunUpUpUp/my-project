package com.seasugar.registry.event.model;

import lombok.Data;

@Data
public class RegisterEvent extends Event{
    private String id;
    private Integer brokerId;
    private String host;
    private Integer port;
    private String advertisedListener;
}
