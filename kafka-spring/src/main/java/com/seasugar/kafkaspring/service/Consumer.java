package com.seasugar.kafkaspring.service;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class Consumer {
    private static final Logger log = LoggerFactory.getLogger(Consumer.class);

    @Value("${sleep.time}")
    private long sleepTime;

    // 监听指定topic，启动32个消费者消费
    @KafkaListener(topics = "${kafka.topic}", concurrency = "${kafka.consumer.concurrency}")
    public void batchListen(List<ConsumerRecord<String, String>> records) {
        // 接收kafka消息，累计1000合成大对象发送一个http，多线程异步发送
        int counter = 0;
        for (ConsumerRecord<String, String> record : records) {
//            log.info(record.toString())
            counter++;
        }
        try {
            Thread.sleep(sleepTime);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
