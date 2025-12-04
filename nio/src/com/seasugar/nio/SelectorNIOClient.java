package com.seasugar.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

public class SelectorNIOClient {
    public static void main(String[] args) throws IOException {
        SocketChannel sc = SocketChannel.open(new InetSocketAddress("localhost", 9999));
        System.out.println("连接服务端");
        sc.configureBlocking(false);
        Selector selector = Selector.open();
        sc.register(selector, SelectionKey.OP_CONNECT);
    }
}
