package com.seasugar.bio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class BIOClient {
    public static void main(String[] args) throws IOException {
        SocketChannel sc = SocketChannel.open();
        sc.connect(new InetSocketAddress("127.0.0.1", 9999));
        System.out.println("已连接上服务端");
        sc.write(ByteBuffer.wrap("hello".getBytes()));
    }
}
