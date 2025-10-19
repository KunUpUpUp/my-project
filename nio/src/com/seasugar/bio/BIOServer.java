package com.seasugar.bio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

/**
 * <h3>BIO实现<h3/>
 * <p>由于BIO是阻塞的，所以这段代码在处理两个可以同时连接的客户端时，必须要等一个客户端处理完，才能处理下一个客户端，所以效率很低</p>
 */
public class BIOServer {
    public static void main(String[] args) throws IOException {
        // BIO实现
        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.bind(new InetSocketAddress(9999));
        while (true) {
            SocketChannel sc = ssc.accept();
            System.out.println("客户端已连接");
            ByteBuffer buffer = ByteBuffer.allocate(3);
            while (true) {
                /**
                 * 1 read返回的是实际读取到的字节数，如果返回-1，则表示客户端关闭了连接
                 * 2 read后会将buffer的position移动到读取到字节末尾的下一个位置
                 */
                int bytesRead = sc.read(buffer);
                if (bytesRead == -1) break;
                if (bytesRead > 0) {
                    buffer.flip();
                    // remaining = limit - position
                    int remaining = buffer.remaining();
                    byte[] bytes = new byte[remaining];
                    // get后把内容写到bytes数组中，同时position会移动到读取到的字节末尾的下一个位置
                    buffer.get(bytes);
                    System.out.print(new String(bytes));
                    // clear会将position置为0，limit置为capacity
                    buffer.clear();
                }
            }
        }
    }
}
