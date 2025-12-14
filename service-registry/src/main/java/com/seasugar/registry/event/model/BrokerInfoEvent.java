package com.seasugar.registry.event.model;

import lombok.Data;

@Data
public class BrokerInfoEvent extends Event{
    private Integer brokerId;
    private String topic;
    private Integer partition;
}
