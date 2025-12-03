package com.seasugar.registry.constants;

public class RegistryConstants {
    public static final Byte MAGIC = 0x01;
    
    // 消息类型代码
    public static final int SERVICE_REGISTER_REQUEST = 1;
    public static final int SERVICE_REGISTER_RESPONSE = 2;
    public static final int SERVICE_DISCOVER_REQUEST = 3;
    public static final int SERVICE_DISCOVER_RESPONSE = 4;
    public static final int HEARTBEAT_REQUEST = 5;
    public static final int HEARTBEAT_RESPONSE = 6;
}
