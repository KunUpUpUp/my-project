package com.seasuagr.producer;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;

public class KafkaMessageProducer {
    public static void main(String[] args) {
        String mysql = "{\n" +
                "    \"Value\": 4194304,\n" +
                "    \"area\": \"cn\",\n" +
                "    \"clusterName\": \"clouddbm8\",\n" +
                "    \"counterType\": \"Gauge\",\n" +
                "    \"endpoint\": \"10.97.166.61\",\n" +
                "    \"idc\": \"xy\",\n" +
                "    \"instanceIp\": \"10.97.166.61\",\n" +
                "    \"metricType\": \"mysql\",\n" +
                "    \"monitorType\": \"global_variables\",\n" +
                "    \"name\": \"mysql_global_variables_sort_buffer_size\",\n" +
                "    \"roomid\": \"\",\n" +
                "    \"serviceName\": \"cloud_partdb_mysql\",\n" +
                "    \"timestamp\": 1747722607000\n" +
                "}";
        String redis = "{\n" +
                "    \"Value\": 0,\n" +
                "    \"area\": \"cn\",\n" +
                "    \"counterType\": \"Gauge\",\n" +
                "    \"endpoint\": \"10.215.13.95\",\n" +
                "    \"idc\": \"xyidc\",\n" +
                "    \"instanceIp\": \"10.215.13.95\",\n" +
                "    \"metricType\": \"redis\",\n" +
                "    \"monitorType\": \"aof_rewrite_in_progress\",\n" +
                "    \"name\": \"redis_aof_rewrite_in_progress\",\n" +
                "    \"roomid\": \"\",\n" +
                "    \"serviceName\": \"valueadded_redis\",\n" +
                "    \"timestamp\": 1747986660000\n" +
                "}";
        String kafka = "{\n" +
                "    \"RateUnit\": \"SECONDS\",\n" +
                "    \"EventType\": \"bytes\",\n" +
                "    \"Count\": 16526320334,\n" +
                "    \"serviceName\": \"rtc_common_kafka\",\n" +
                "    \"type\": \"BrokerTopicMetrics\",\n" +
                "    \"FiveMinuteRate\": 116.29131242848267,\n" +
                "    \"MeanRate\": 190.8541086185517,\n" +
                "    \"metricType\": \"kafka\",\n" +
                "    \"monitorType\": \"kafka.server\",\n" +
                "    \"endpoint\": \"rtc-common2.sq.kafka:22222\",\n" +
                "    \"OneMinuteRate\": 140.7057362788054,\n" +
                "    \"name\": \"BytesOutPerSec\",\n" +
                "    \"topic\": \"cloudstorage_netdisk_conference_record_topic\",\n" +
                "    \"FifteenMinuteRate\": 124.85242221545927,\n" +
                "    \"timestamp\": 1747922280000,\n" +
                "    \"instanceIp\": \"10.215.18.24\"\n" +
                "}";
        sendMessage("middle_agent_metrics", redis);
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
            String key = "pod_event";
            ProducerRecord<String, String> record = new ProducerRecord<>(topic, key, message);
            producer.send(record);
        } finally {
            // 关闭生产者
            if (producer != null) {
                producer.close();
            }
        }

    }
}
