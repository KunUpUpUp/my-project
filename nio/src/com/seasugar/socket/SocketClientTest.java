package com.seasugar.socket;

/**
 * Socket客户端测试类
 */
public class SocketClientTest {
    
    public static void main(String[] args) {
        System.out.println("=== Java Socket客户端测试 ===");
        
        // 测试连接到Go服务器
        SocketClient client = new SocketClient("localhost", 8080);
        
        try {
            if (client.connect()) {
                System.out.println("连接成功，开始测试...");
                
                // 发送测试消息
                String[] testMessages = {
                    "Hello from Java client!",
                    "ping",
                    "time",
                    "测试中文消息",
                    "quit"
                };
                
                for (String message : testMessages) {
                    try {
                        Thread.sleep(1000);
                        client.sendMessage(message);
                        
                        String response = client.receiveMessage();
                        if (response != null) {
                            System.out.println("服务器响应: " + response);
                        }
                        
                        if ("quit".equalsIgnoreCase(message)) {
                            break;
                        }
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            } else {
                System.err.println("无法连接到服务器，请确保Go服务器正在运行");
            }
        } finally {
            client.close();
        }
    }
}
