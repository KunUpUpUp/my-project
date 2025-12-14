package com.seasugar.registry.enums;

public enum EventEnum {
    HEART_BEAT("心跳检测", (byte) 0x01),
    REGISTER("注册", (byte) 0x02),
    UNREGISTER("下线", (byte) 0x03),
    CREATE_TOPIC("新增主题", (byte) 0x04),
    REPLICATION_DATA("主从数据复制", (byte) 0x05),
        ;

    String name;
    Byte code;

    EventEnum(String name, Byte code) {
        this.name = name;
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public Byte getCode() {
        return code;
    }
}
