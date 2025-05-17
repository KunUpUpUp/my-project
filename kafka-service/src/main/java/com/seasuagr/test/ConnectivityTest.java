package com.seasuagr.test;

import java.net.Socket;

public class ConnectivityTest {
    public static void main(String[] args) {
        String host = "localhost";  // 服务器IP地址
        int port = 9092;  // 要测试的端口，例如Kafka的端口
        try (Socket socket = new Socket(host, port)) {
            System.out.println("Connection successful to " + host + " on port " + port);
        } catch (Exception e) {
            System.out.println("Unable to connect to " + host + " on port " + port);
            e.printStackTrace();
        }
    }
}