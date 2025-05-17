package com.seasuagr.topic;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

public class CreateTopic {
    public static void main(String[] args) {
        Properties properties = new Properties();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        try (AdminClient adminClient = AdminClient.create(properties)) {
            // 重新创建主题
            NewTopic newTopic = new NewTopic("pod_event", 1, (short) 1); // 1个分区，副本因子为1
            adminClient.createTopics(Collections.singletonList(newTopic)).all().get();
            System.out.println("Topic 'test' has been recreated.");
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }
}
