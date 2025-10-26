package com.seasugar.socket;

/**
 * Kafka JMX Socket客户端测试类
 */
public class KafkaJMXSocketTest {
    
    public static void main(String[] args) {
        System.out.println("=== Kafka JMX Socket客户端测试 ===");
        
        // 测试连接到Go服务器
        SocketClient client = new SocketClient("localhost", 8080);
        
        try {
            if (client.connect()) {
                System.out.println("连接成功，开始测试Kafka JMX功能...");
                
                // 初始化Kafka JMX (假设Kafka运行在localhost:9999)
                boolean jmxConnected = client.initKafkaJMX("localhost", 9999);
                
                if (jmxConnected) {
                    System.out.println("Kafka JMX连接成功！");
                    
                    // 发送一次JMX数据
                    System.out.println("发送JMX数据...");
                    client.sendKafkaMetrics();
                    
                    // 等待服务器响应
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                    
                    // 启动定期发送
                    System.out.println("启动定期发送JMX数据 (每5秒)...");
                    client.startKafkaMetricsScheduler(5);
                    
                    // 运行30秒
                    try {
                        Thread.sleep(30000);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                    
                    // 停止定期发送
                    client.stopKafkaMetricsScheduler();
                    
                } else {
                    System.out.println("Kafka JMX连接失败，发送模拟数据...");
                    
                    // 发送模拟JMX数据
                    String mockJMXData = "{\n" +
                        "  \"status\": \"connected\",\n" +
                        "  \"timestamp\": " + System.currentTimeMillis() + ",\n" +
                        "  \"kafka_host\": \"localhost\",\n" +
                        "  \"jmx_port\": 9999,\n" +
                        "  \"total_metrics_count\": 5,\n" +
                        "  \"messages_in_per_sec_Count\": 1000,\n" +
                        "  \"bytes_in_per_sec_Count\": 50000,\n" +
                        "  \"bytes_out_per_sec_Count\": 45000,\n" +
                        "  \"produce_requests_per_sec_Count\": 200,\n" +
                        "  \"fetch_consumer_requests_per_sec_Count\": 150\n" +
                        "}";
                    
                    client.sendMessage("JMX_DATA:" + mockJMXData);
                }
                
                // 发送退出命令
                client.sendMessage("quit");
                
            } else {
                System.err.println("无法连接到服务器，请确保Go服务器正在运行");
            }
        } finally {
            client.close();
        }
    }
}
