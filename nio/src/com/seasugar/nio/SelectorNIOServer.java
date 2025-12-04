package com.seasugar.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;

public class SelectorNIOServer {
    public static void main(String[] args) throws IOException {
        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.bind(new InetSocketAddress(9999));
        // 非阻塞
        ssc.configureBlocking(false);
        // Selector
        Selector selector = Selector.open();
        // ssc 关注 accept 事件
        ssc.register(selector, SelectionKey.OP_ACCEPT);
        while (true) {
            // 多路复用，无事件阻塞
            selector.select();
            // 有事件，获取事件集合
            Set<SelectionKey> keys = selector.selectedKeys();
            // 后续需要移除key，所以需要迭代器
            Iterator<SelectionKey> iterator = keys.iterator();
            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                // SelectedKeys 不会主动移除 key
                iterator.remove();
                if (key.isAcceptable()) {
                    handleAccept(key, selector);
                } else if (key.isReadable()) {
                    handleRead(key);
                } else if (key.isWritable()) {
                    // TODO
                }
            }
        }
    }

    private static void handleRead(SelectionKey key) throws IOException {
        while (true) {
            // 如果是大数据，还是会出现阻塞
            SocketChannel sc = (SocketChannel) key.channel();
            System.out.println("客户端发送数据");
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            int len = sc.read(buffer);
            // 只要len不为-1，就一直在while循环中，所以buffer无空间时（返回len为0）也不会跳出while
            if (len == -1) break;
            if (len > 0) {
                buffer.flip();
                byte[] bytes = new byte[len];
                buffer.get(bytes);
                System.out.print(new String(bytes));
                buffer.clear();
            }
        }
    }

    private static void handleAccept(SelectionKey key, Selector selector) throws IOException {
        // key 的 channel 是 SelectableChannel，抽象类需要自己强转
        ServerSocketChannel ssc = (ServerSocketChannel) key.channel();
        // Server进行accept
        SocketChannel sc = ssc.accept();
        System.out.println("客户端已连接");
        // 设置 SocketChannel 非阻塞，不然后续的read和write会阻塞
        // 出现客户端或者服务端只能一端发送数据的情况，不是全双工了
        sc.configureBlocking(false);
        // 让 sc 关注 read 和 write 事件
        sc.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);
    }
}
