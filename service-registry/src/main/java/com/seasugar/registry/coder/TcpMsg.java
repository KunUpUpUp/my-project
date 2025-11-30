package com.seasugar.registry.coder;

public class TcpMsg {
    private Byte magic;
    private Integer code;
    private Integer length;
    private byte[] body;

    public Byte getMagic() {
        return magic;
    }

    public void setMagic(Byte magic) {
        this.magic = magic;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public Integer getLength() {
        return length;
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
}
