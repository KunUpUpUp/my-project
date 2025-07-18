package com.seasuagr.producer;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;

public class KafkaMessageProducer {
    public static void main(String[] args) {
        send("mysql");
//        send("redis");
//        while (true) {
//            send("kafka");
//            try {
//                Thread.sleep(25000L);
//            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
//            }
//        }
    }

    private static void send(String type) {
        long time = System.currentTimeMillis();
        String mysql = "{\n" +
                "    \"Value\": 4194304,\n" +
                "    \"area\": \"cn\",\n" +
                "    \"clusterName\": \"clouddbm8\",\n" +
                "    \"counterType\": \"Gauge\",\n" +
                "    \"endpoint\": \"10.215.194.228\",\n" +
                "    \"idc\": \"xy\",\n" +
                "    \"instanceIp\": \"10.220.20.34\",\n" +
                "    \"metricType\": \"mysql\",\n" +
                "    \"monitorType\": \"global_variables\",\n" +
                "    \"name\": \"mysql_global_variables_sort_buffer_size\",\n" +
                "    \"roomid\": \"\",\n" +
                "    \"serviceName\": \"cloud_partdb_mysql\",\n" +
                "    \"timestamp\":" + time + "\n" +
                "}";
        String redis = "{\n" +
                "    \"Value\": 5508107600,\n" +
                "    \"area\": \"cn\",\n" +
                "    \"counterType\": \"Gauge\",\n" +
                "    \"endpoint\": \"10.220.50.178\",\n" +
                "    \"idc\": \"xyidc\",\n" +
                "    \"instanceIp\": \"10.220.50.178\",\n" +
                "    \"metricType\": \"redis\",\n" +
                "    \"monitorType\": \"memory_used_peak_bytes\",\n" +
                "    \"name\": \"redis_memory_used_peak_bytes\",\n" +
                "    \"roomid\": \"\",\n" +
                "    \"serviceName\": \"device_basic_redis\",\n" +
                "    \"timestamp\":" + time + "\n" +
                "}";
        String kafka = "{\n" +
                "    \"metricType\": \"kafka\",\n" +
                "    \"monitorType\": \"kafka.server\",\n" +
                "    \"endpoint\": \"10.219.22.27:22222\",\n" +
                "    \"name\": \"BrokerState\",\n" +
                "    \"Value\": 5,\n" +
                "    \"serviceName\": \"perf_kafka\",\n" +
                "    \"type\": \"KafkaServer\",\n" +
                "    \"timestamp\":" + time + ",\n" +
                "    \"instanceIp\": \"10.219.22.27\"\n" +
                "}";
        switch (type) {
            case "mysql":
                sendMessage("middle_agent_metrics", mysql);
                break;
            case "redis":
                sendMessage("middle_agent_metrics", redis);
                break;
            case "kafka":
                sendMessage("middle_agent_metrics", kafka);
                break;
        }
    }

    public static void sendMessage(String topic, String message) {
        KafkaProducer<String, String> producer = null;

        try {
            // 配置 Kafka 生产者属性
            Properties props = new Properties();
            props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
            props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
            props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

            // 创建生产者实例
            producer = new KafkaProducer<>(props);

            // 发送消息
//            String key = "pod_event";
            ProducerRecord<String, String> record = new ProducerRecord<>(topic, null, message);
            producer.send(record);
        } finally {
            // 关闭生产者
            if (producer != null) {
                producer.close();
            }
        }

    }
}
