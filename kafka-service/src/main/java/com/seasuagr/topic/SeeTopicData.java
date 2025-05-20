package com.seasuagr.topic;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.util.Collections;
import java.util.Properties;

public class SeeTopicData {
    public static void main(String[] args) {
        // 设置配置属性
        Properties properties = new Properties();
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, "123");
        properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());

        // 创建消费者
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(properties);

        // 订阅主题
        consumer.subscribe(Collections.singletonList("middle_agent_metrics"));

        // 持续拉取数据

//        ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
        while (true) {
            ConsumerRecords<String, String> records = consumer.poll(1000);
            if (!records.isEmpty()) {
                for (ConsumerRecord<String, String> record : records) {
                    System.out.println("Key: " + record.key() + ", Value: " + record.value());
                }
            }
        }
    }
}
