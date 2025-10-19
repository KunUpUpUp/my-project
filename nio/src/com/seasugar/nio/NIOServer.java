package com.seasugar.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;

/**
 * <h3>NIO实现<h3/>
 * <p>由于NIO是非阻塞的，所以这段代码可以同时处理多个客户端</p>
 */
public class NIOServer {
    public static void main(String[] args) throws IOException {
        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.bind(new InetSocketAddress(9999));
        System.out.println("服务端已启动，占用端口9999");
        // 设置服务端channel为非阻塞模式
        ssc.configureBlocking(false);
        List<SocketChannel> channels = new ArrayList<>();
        ByteBuffer buffer = ByteBuffer.allocate(3);
        while (true) {
            // 设置为非阻塞模式后，accept方法会立即返回，如果此时没有客户端连接，则返回null
            SocketChannel sc = ssc.accept();
            if (sc != null) {
                System.out.println("客户端已连接");
                channels.add(sc);
                // 把与客户端通信的channel设置为非阻塞模式
                sc.configureBlocking(false);
            }

            // 对channel进行一一处理
            for (SocketChannel channel : channels) {
                while (true) {
                    // 应该为每个channel都分配一个buffer，不然存在线程安全问题
//                    buffer.put(new byte[3]);
                    int len = channel.read(buffer);
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
        }
    }
}
