package com.seasugar.registry.coder;

public class TcpMsg {
    private Byte magic;
    private Byte code;
    private Integer length;
    private byte[] body;

    public Byte getMagic() {
        return magic;
    }

    public void setMagic(Byte magic) {
        this.magic = magic;
    }

    public void setCode(Byte code) {
        this.code = code;
    }

    public Integer getLength() {
        return length;
    }

    public Byte getCode() {
        return code;
    }

    public void setLength(Integer length) {
        this.length = length;
    }

    public byte[] getBody() {
        return body;
    }

    public void setBody(byte[] body) {
        this.body = body;
    }

    public TcpMsg() {
    }

    public TcpMsg(Byte magic, Byte code, Integer length, byte[] body) {
        this.magic = magic;
        this.code = code;
        this.length = length;
        this.body = body;
    }
}
