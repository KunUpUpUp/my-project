package com.seasugar.socket;

import java.io.*;
import java.net.Socket;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Java Socket客户端
 * 用于与Go Socket服务器通信
 */
public class SocketClient {
    private Socket socket;
    private BufferedReader reader;
    private PrintWriter writer;
    private String serverHost;
    private int serverPort;
    private Scanner scanner;
    
    // Kafka JMX相关
    private KafkaJMXMonitor kafkaJMXMonitor;
    private ScheduledExecutorService scheduler;
    private boolean jmxEnabled = false;

    public SocketClient(String host, int port) {
        this.serverHost = host;
        this.serverPort = port;
        this.scanner = new Scanner(System.in);
        this.scheduler = Executors.newScheduledThreadPool(1);
    }

    /**
     * 连接到服务器
     */
    public boolean connect() {
        try {
            System.out.println("正在连接到服务器 " + serverHost + ":" + serverPort + "...");
            socket = new Socket(serverHost, serverPort);
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new PrintWriter(socket.getOutputStream(), true);
            
            System.out.println("连接成功！");
            
            // 读取服务器欢迎消息
            String welcomeMessage = reader.readLine();
            System.out.println("服务器: " + welcomeMessage);
            
            return true;
        } catch (IOException e) {
            System.err.println("连接失败: " + e.getMessage());
            return false;
        }
    }

    /**
     * 发送消息到服务器
     */
    public void sendMessage(String message) {
        if (writer != null) {
            writer.println(message);
            System.out.println("发送: " + message);
        }
    }

    /**
     * 接收服务器消息
     */
    public String receiveMessage() {
        try {
            if (reader != null) {
                return reader.readLine();
            }
        } catch (IOException e) {
            System.err.println("接收消息失败: " + e.getMessage());
        }
        return null;
    }

    /**
     * 初始化Kafka JMX监控
     */
    public boolean initKafkaJMX(String kafkaHost, int kafkaJmxPort) {
        try {
            kafkaJMXMonitor = new KafkaJMXMonitor(kafkaHost, kafkaJmxPort);
            boolean connected = kafkaJMXMonitor.connect();
            if (connected) {
                jmxEnabled = true;
                System.out.println("Kafka JMX监控已启用");
            }
            return connected;
        } catch (Exception e) {
            System.err.println("初始化Kafka JMX失败: " + e.getMessage());
            return false;
        }
    }

    /**
     * 发送Kafka JMX指标到服务器
     */
    public void sendKafkaMetrics() {
        if (!jmxEnabled || kafkaJMXMonitor == null) {
            System.out.println("Kafka JMX未启用");
            return;
        }

        try {
            Map<String, Object> metrics = kafkaJMXMonitor.getKafkaSummary();
            String jsonMetrics = kafkaJMXMonitor.formatMetricsAsJson(metrics);
            
            // 发送JMX数据到服务器
            sendMessage("JMX_DATA:" + jsonMetrics);
            
        } catch (Exception e) {
            System.err.println("发送Kafka指标失败: " + e.getMessage());
        }
    }

    /**
     * 启动定期发送Kafka指标
     */
    public void startKafkaMetricsScheduler(int intervalSeconds) {
        if (!jmxEnabled) {
            System.out.println("Kafka JMX未启用，无法启动定期发送");
            return;
        }

        scheduler.scheduleAtFixedRate(() -> {
            try {
                sendKafkaMetrics();
            } catch (Exception e) {
                System.err.println("定期发送Kafka指标失败: " + e.getMessage());
            }
        }, 0, intervalSeconds, TimeUnit.SECONDS);

        System.out.println("Kafka指标定期发送已启动，间隔: " + intervalSeconds + "秒");
    }

    /**
     * 停止Kafka指标定期发送
     */
    public void stopKafkaMetricsScheduler() {
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdown();
            System.out.println("Kafka指标定期发送已停止");
        }
    }

    /**
     * 交互式聊天模式
     */
    public void startInteractiveMode() {
        System.out.println("\n=== 交互模式开始 ===");
        System.out.println("输入 'quit' 或 'exit' 退出");
        System.out.println("输入 'time' 获取服务器时间");
        System.out.println("输入 'ping' 测试连接");
        System.out.println("输入 'jmx' 发送Kafka JMX指标");
        System.out.println("输入 'jmx_start' 启动定期发送JMX指标");
        System.out.println("输入 'jmx_stop' 停止定期发送JMX指标");
        System.out.println("输入其他任意消息进行对话\n");

        // 启动消息接收线程
        Thread receiveThread = new Thread(() -> {
            try {
                String message;
                while ((message = receiveMessage()) != null) {
                    System.out.println("服务器: " + message);
                }
            } catch (Exception e) {
                System.err.println("接收线程异常: " + e.getMessage());
            }
        });
        receiveThread.setDaemon(true);
        receiveThread.start();

        // 主线程处理用户输入
        while (true) {
            System.out.print("客户端> ");
            String input = scanner.nextLine().trim();
            
            if (input.isEmpty()) {
                continue;
            }
            
            // 处理特殊命令
            if ("jmx".equalsIgnoreCase(input)) {
                sendKafkaMetrics();
            } else if ("jmx_start".equalsIgnoreCase(input)) {
                startKafkaMetricsScheduler(5); // 每5秒发送一次
            } else if ("jmx_stop".equalsIgnoreCase(input)) {
                stopKafkaMetricsScheduler();
            } else {
                sendMessage(input);
            }
            
            if ("quit".equalsIgnoreCase(input) || "exit".equalsIgnoreCase(input)) {
                break;
            }
        }
    }

    /**
     * 自动测试模式
     */
    public void startTestMode() {
        System.out.println("\n=== 自动测试模式开始 ===");
        
        String[] testMessages = {
            "Hello, Go Server!",
            "ping",
            "time",
            "这是一条中文消息",
            "测试特殊字符: !@#$%^&*()",
            "quit"
        };

        for (String message : testMessages) {
            try {
                Thread.sleep(1000); // 等待1秒
                sendMessage(message);
                
                String response = receiveMessage();
                if (response != null) {
                    System.out.println("服务器: " + response);
                }
                
                if ("quit".equalsIgnoreCase(message)) {
                    break;
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    /**
     * 关闭连接
     */
    public void close() {
        try {
            // 停止Kafka指标定期发送
            stopKafkaMetricsScheduler();
            
            // 关闭Kafka JMX连接
            if (kafkaJMXMonitor != null) {
                kafkaJMXMonitor.close();
            }
            
            if (writer != null) {
                writer.close();
            }
            if (reader != null) {
                reader.close();
            }
            if (socket != null) {
                socket.close();
            }
            if (scanner != null) {
                scanner.close();
            }
            System.out.println("连接已关闭");
        } catch (IOException e) {
            System.err.println("关闭连接时出错: " + e.getMessage());
        }
    }

    /**
     * 主方法
     */
    public static void main(String[] args) {
        String host = "localhost";
        int port = 8080;
        
        // 解析命令行参数
        if (args.length >= 1) {
            host = args[0];
        }
        if (args.length >= 2) {
            try {
                port = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                System.err.println("端口号格式错误，使用默认端口 8080");
            }
        }

        SocketClient client = new SocketClient(host, port);
        
        try {
            if (client.connect()) {
                // 询问是否启用Kafka JMX
                System.out.println("\n是否启用Kafka JMX监控? (y/n): ");
                Scanner modeScanner = new Scanner(System.in);
                String jmxChoice = modeScanner.nextLine().trim();
                
                if ("y".equalsIgnoreCase(jmxChoice) || "yes".equalsIgnoreCase(jmxChoice)) {
                    System.out.print("请输入Kafka JMX主机 (默认: localhost): ");
                    String kafkaHost = modeScanner.nextLine().trim();
                    if (kafkaHost.isEmpty()) {
                        kafkaHost = "localhost";
                    }
                    
                    System.out.print("请输入Kafka JMX端口 (默认: 9999): ");
                    String portStr = modeScanner.nextLine().trim();
                    int kafkaPort = 9999;
                    if (!portStr.isEmpty()) {
                        try {
                            kafkaPort = Integer.parseInt(portStr);
                        } catch (NumberFormatException e) {
                            System.out.println("端口格式错误，使用默认端口 9999");
                        }
                    }
                    
                    boolean jmxConnected = client.initKafkaJMX(kafkaHost, kafkaPort);
                    if (!jmxConnected) {
                        System.out.println("Kafka JMX连接失败，继续使用普通模式");
                    }
                }
                
                // 选择模式
                System.out.println("\n请选择模式:");
                System.out.println("1. 交互模式 (手动输入消息)");
                System.out.println("2. 测试模式 (自动发送测试消息)");
                System.out.print("请输入选择 (1 或 2): ");
                
                String choice = modeScanner.nextLine().trim();
                
                if ("2".equals(choice)) {
                    client.startTestMode();
                } else {
                    client.startInteractiveMode();
                }
            }
        } finally {
            client.close();
        }
    }
}
