package com.seasugar.registry.event.model;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class SyncEvent extends Event{
    private Integer masterId;
    private Integer slaveId;
}
