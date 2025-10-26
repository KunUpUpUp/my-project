package com.seasugar.socket;

import javax.management.*;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Kafka JMX监控工具类
 * 用于获取Kafka的JMX指标数据
 */
public class KafkaJMXMonitor {
    private JMXConnector jmxConnector;
    private MBeanServerConnection mBeanServer;
    private String kafkaHost;
    private int kafkaJmxPort;
    private boolean connected = false;

    // Kafka重要的JMX指标
    private static final Map<String, String> KAFKA_METRICS = new HashMap<>();
    static {
        // Broker指标
        KAFKA_METRICS.put("kafka.server:type=BrokerTopicMetrics,name=MessagesInPerSec", "messages_in_per_sec");
        KAFKA_METRICS.put("kafka.server:type=BrokerTopicMetrics,name=BytesInPerSec", "bytes_in_per_sec");
        KAFKA_METRICS.put("kafka.server:type=BrokerTopicMetrics,name=BytesOutPerSec", "bytes_out_per_sec");
        KAFKA_METRICS.put("kafka.server:type=BrokerTopicMetrics,name=FailedFetchRequestsPerSec", "failed_fetch_requests_per_sec");
        KAFKA_METRICS.put("kafka.server:type=BrokerTopicMetrics,name=FailedProduceRequestsPerSec", "failed_produce_requests_per_sec");
        
        // Controller指标
        KAFKA_METRICS.put("kafka.controller:type=KafkaController,name=ActiveControllerCount", "active_controller_count");
        KAFKA_METRICS.put("kafka.controller:type=KafkaController,name=OfflinePartitionsCount", "offline_partitions_count");
        
        // Replica指标
        KAFKA_METRICS.put("kafka.server:type=ReplicaManager,name=PartitionCount", "partition_count");
        KAFKA_METRICS.put("kafka.server:type=ReplicaManager,name=LeaderCount", "leader_count");
        
        // Log指标
        KAFKA_METRICS.put("kafka.log:type=LogFlushStats,name=LogFlushRateAndTimeMs", "log_flush_rate_and_time_ms");
        
        // Network指标
        KAFKA_METRICS.put("kafka.network:type=RequestMetrics,name=TotalTimeMs,request=Produce", "produce_request_total_time_ms");
        KAFKA_METRICS.put("kafka.network:type=RequestMetrics,name=TotalTimeMs,request=FetchConsumer", "fetch_consumer_request_total_time_ms");
        KAFKA_METRICS.put("kafka.network:type=RequestMetrics,name=RequestsPerSec,request=Produce", "produce_requests_per_sec");
        KAFKA_METRICS.put("kafka.network:type=RequestMetrics,name=RequestsPerSec,request=FetchConsumer", "fetch_consumer_requests_per_sec");
    }

    public KafkaJMXMonitor(String host, int port) {
        this.kafkaHost = host;
        this.kafkaJmxPort = port;
    }

    /**
     * 连接到Kafka JMX
     */
    public boolean connect() {
        try {
            String jmxUrl = String.format("service:jmx:rmi:///jndi/rmi://%s:%d/jmxrmi", kafkaHost, kafkaJmxPort);
            JMXServiceURL serviceURL = new JMXServiceURL(jmxUrl);
            
            System.out.println("正在连接到Kafka JMX: " + jmxUrl);
            jmxConnector = JMXConnectorFactory.connect(serviceURL);
            mBeanServer = jmxConnector.getMBeanServerConnection();
            
            connected = true;
            System.out.println("Kafka JMX连接成功！");
            return true;
        } catch (IOException e) {
            System.err.println("连接Kafka JMX失败: " + e.getMessage());
            connected = false;
            return false;
        }
    }

    /**
     * 获取所有Kafka JMX指标
     */
    public Map<String, Object> getAllMetrics() {
        Map<String, Object> metrics = new ConcurrentHashMap<>();
        
        if (!connected || mBeanServer == null) {
            System.err.println("JMX未连接，无法获取指标");
            return metrics;
        }

        try {
            // 获取所有MBean
            Set<ObjectName> objectNames = mBeanServer.queryNames(null, null);
            
            for (ObjectName objectName : objectNames) {
                String objectNameStr = objectName.toString();
                
                // 只处理Kafka相关的MBean
                if (objectNameStr.contains("kafka")) {
                    try {
                        MBeanInfo mBeanInfo = mBeanServer.getMBeanInfo(objectName);
                        MBeanAttributeInfo[] attributes = mBeanInfo.getAttributes();
                        
                        for (MBeanAttributeInfo attribute : attributes) {
                            if (attribute.isReadable()) {
                                try {
                                    Object value = mBeanServer.getAttribute(objectName, attribute.getName());
                                    if (value != null) {
                                        String metricName = objectNameStr + "." + attribute.getName();
                                        metrics.put(metricName, value);
                                    }
                                } catch (Exception e) {
                                    // 忽略无法读取的属性
                                }
                            }
                        }
                    } catch (Exception e) {
                        // 忽略无法处理的MBean
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("获取JMX指标失败: " + e.getMessage());
        }

        return metrics;
    }

    /**
     * 获取重要的Kafka指标
     */
    public Map<String, Object> getImportantMetrics() {
        Map<String, Object> metrics = new ConcurrentHashMap<>();
        
        if (!connected || mBeanServer == null) {
            System.err.println("JMX未连接，无法获取指标");
            return metrics;
        }

        try {
            for (Map.Entry<String, String> entry : KAFKA_METRICS.entrySet()) {
                String objectNameStr = entry.getKey();
                String metricKey = entry.getValue();
                
                try {
                    ObjectName objectName = new ObjectName(objectNameStr);
                    
                    // 获取所有实例
                    Set<ObjectName> instances = mBeanServer.queryNames(objectName, null);
                    
                    for (ObjectName instance : instances) {
                        try {
                            MBeanInfo mBeanInfo = mBeanServer.getMBeanInfo(instance);
                            MBeanAttributeInfo[] attributes = mBeanInfo.getAttributes();
                            
                            for (MBeanAttributeInfo attribute : attributes) {
                                if (attribute.isReadable()) {
                                    try {
                                        Object value = mBeanServer.getAttribute(instance, attribute.getName());
                                        if (value != null) {
                                            String fullMetricName = metricKey + "_" + attribute.getName();
                                            metrics.put(fullMetricName, value);
                                        }
                                    } catch (Exception e) {
                                        // 忽略无法读取的属性
                                    }
                                }
                            }
                        } catch (Exception e) {
                            // 忽略无法处理的实例
                        }
                    }
                } catch (Exception e) {
                    // 忽略无法处理的ObjectName
                }
            }
        } catch (Exception e) {
            System.err.println("获取重要JMX指标失败: " + e.getMessage());
        }

        return metrics;
    }

    /**
     * 获取简化的Kafka指标摘要
     */
    public Map<String, Object> getKafkaSummary() {
        Map<String, Object> summary = new ConcurrentHashMap<>();
        
        if (!connected || mBeanServer == null) {
            summary.put("status", "disconnected");
            summary.put("error", "JMX未连接");
            return summary;
        }

        try {
            summary.put("status", "connected");
            summary.put("timestamp", System.currentTimeMillis());
            summary.put("kafka_host", kafkaHost);
            summary.put("jmx_port", kafkaJmxPort);

            // 获取一些关键指标
            Map<String, Object> importantMetrics = getImportantMetrics();
            summary.putAll(importantMetrics);

            // 添加一些统计信息
            summary.put("total_metrics_count", importantMetrics.size());
            
        } catch (Exception e) {
            summary.put("status", "error");
            summary.put("error", e.getMessage());
        }

        return summary;
    }

    /**
     * 格式化指标数据为JSON字符串
     */
    public String formatMetricsAsJson(Map<String, Object> metrics) {
        StringBuilder json = new StringBuilder();
        json.append("{\n");
        
        boolean first = true;
        for (Map.Entry<String, Object> entry : metrics.entrySet()) {
            if (!first) {
                json.append(",\n");
            }
            json.append("  \"").append(entry.getKey()).append("\": ");
            
            Object value = entry.getValue();
            if (value instanceof String) {
                json.append("\"").append(value).append("\"");
            } else if (value instanceof Number) {
                json.append(value);
            } else {
                json.append("\"").append(value.toString()).append("\"");
            }
            first = false;
        }
        
        json.append("\n}");
        return json.toString();
    }

    /**
     * 关闭JMX连接
     */
    public void close() {
        try {
            if (jmxConnector != null) {
                jmxConnector.close();
            }
            connected = false;
            System.out.println("Kafka JMX连接已关闭");
        } catch (IOException e) {
            System.err.println("关闭JMX连接时出错: " + e.getMessage());
        }
    }

    /**
     * 检查连接状态
     */
    public boolean isConnected() {
        return connected;
    }
}
