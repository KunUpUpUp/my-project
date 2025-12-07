package com.seasugar.registry.event.model;

import lombok.Data;

@Data
public class HeartBeatEvent extends Event{
    private String id;
}
