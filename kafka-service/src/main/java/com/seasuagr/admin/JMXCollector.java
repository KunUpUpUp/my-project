package com.seasuagr.admin;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import java.util.Set;

/**
 * JMX 收集器，用于从 Kafka broker 获取 JMX 指标
 */
public class JMXCollector {
    
    private String host;
    private int port;
    private JMXConnector connector;
    private MBeanServerConnection mbsc;

    /**
     * 构造函数
     * @param host Kafka broker 主机地址
     * @param port JMX 端口（默认通常是 9999）
     */
    public JMXCollector(String host, int port) {
        this.host = host;
        this.port = port;
    }

    /**
     * 连接到 JMX 服务器
     * @throws Exception 连接失败时抛出异常
     */
    public void connect() throws Exception {
        String jmxUrl = String.format("service:jmx:rmi:///jndi/rmi://%s:%d/jmxrmi", host, port);
        JMXServiceURL url = new JMXServiceURL(jmxUrl);
        connector = JMXConnectorFactory.connect(url, null);
        mbsc = connector.getMBeanServerConnection();
        System.out.println("成功连接到 JMX 服务器: " + host + ":" + port);
    }

    /**
     * 获取 NetworkProcessorAvgIdlePercent 值
     * @return NetworkProcessorAvgIdlePercent 的百分比值（0-100）
     * @throws Exception 获取失败时抛出异常
     */
    public Double getNetworkProcessorAvgIdlePercent() throws Exception {
        if (mbsc == null) {
            throw new IllegalStateException("未连接到 JMX 服务器，请先调用 connect() 方法");
        }

        // Kafka 的 NetworkProcessorAvgIdlePercent MBean 名称
        ObjectName objectName = new ObjectName("kafka.network:type=SocketServer,name=NetworkProcessorAvgIdlePercent");
        
        // 获取属性值
        Object value = mbsc.getAttribute(objectName, "Value");
        
        if (value instanceof Double) {
            return (Double) value;
        } else if (value instanceof Number) {
            return ((Number) value).doubleValue();
        } else {
            throw new ClassCastException("无法将值转换为 Double: " + value.getClass());
        }
    }

    /**
     * 获取所有可用的 Kafka network 相关的 MBean 对象名（用于调试和发现）
     * @return MBean 对象名集合
     * @throws Exception 查询失败时抛出异常
     */
    public Set<ObjectName> listNetworkMBeans() throws Exception {
        if (mbsc == null) {
            throw new IllegalStateException("未连接到 JMX 服务器，请先调用 connect() 方法");
        }

        ObjectName pattern = new ObjectName("kafka.network:*");
        return mbsc.queryNames(pattern, null);
    }

    /**
     * 获取指定 MBean 的所有属性（用于调试）
     * @param objectName MBean 对象名
     * @return 属性值
     * @throws Exception 获取失败时抛出异常
     */
    public Object getAttribute(ObjectName objectName, String attributeName) throws Exception {
        if (mbsc == null) {
            throw new IllegalStateException("未连接到 JMX 服务器，请先调用 connect() 方法");
        }
        return mbsc.getAttribute(objectName, attributeName);
    }

    /**
     * 断开 JMX 连接
     */
    public void disconnect() {
        if (connector != null) {
            try {
                connector.close();
                System.out.println("已断开 JMX 连接");
            } catch (Exception e) {
                System.err.println("断开连接时发生错误: " + e.getMessage());
            }
        }
    }

    /**
     * 测试方法：获取 NetworkProcessorAvgIdlePercent
     */
    public static void main(String[] args) {
        // 默认连接参数
        String host = "localhost";
        int port = 9999;

        // 可以从命令行参数获取
        if (args.length >= 1) {
            host = args[0];
        }
        if (args.length >= 2) {
            port = Integer.parseInt(args[1]);
        }

        JMXCollector collector = null;
        try {
            // 创建收集器并连接
            collector = new JMXCollector(host, port);
            collector.connect();

            // 获取 NetworkProcessorAvgIdlePercent
            Double idlePercent = collector.getNetworkProcessorAvgIdlePercent();
            System.out.println("NetworkProcessorAvgIdlePercent: " + idlePercent + "%");

            // 可选：列出所有 network 相关的 MBean（用于调试）
            System.out.println("\n可用的 Kafka network MBeans:");
            Set<ObjectName> mbeans = collector.listNetworkMBeans();
            for (ObjectName name : mbeans) {
                System.out.println("  - " + name.toString());
            }

        } catch (Exception e) {
            System.err.println("获取 JMX 指标失败: " + e.getMessage());
            e.printStackTrace();
            System.err.println("\n提示：");
            System.err.println("1. 确保 Kafka broker 已启用 JMX（设置 JMX_PORT 环境变量）");
            System.err.println("2. 确保 JMX 端口可访问");
            System.err.println("3. 检查防火墙设置");
        } finally {
            if (collector != null) {
                collector.disconnect();
            }
        }
    }
}
