package com.seasugar.kafkaspring.service;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@Service
@ManagedResource(objectName = "com.seasugar.kafkaspring:type=Consumer,name=QPSMonitor")
public class Consumer {
    private static final Logger log = LoggerFactory.getLogger(Consumer.class);

    @Value("${sleep.time}")
    private long sleepTime;

    // JMX监控变量
    private final AtomicLong messagesInCurrentSecond = new AtomicLong(0);
    private volatile double currentQPS = 0.0;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    // 初始化QPS统计定时器
    public Consumer() {
        scheduler.scheduleAtFixedRate(() -> {
            // 每秒更新一次QPS
            currentQPS = messagesInCurrentSecond.get();
            messagesInCurrentSecond.set(0); // 重置计数器
        }, 1, 1, java.util.concurrent.TimeUnit.SECONDS);
    }

    @KafkaListener(topics = "${kafka.topic}", concurrency = "${kafka.consumer.concurrency}")
    public void batchListen(List<ConsumerRecord<String, String>> records) {
        // 更新统计信息
        messagesInCurrentSecond.addAndGet(records.size()); // 累加当前秒的消息数
        
        try {
            Thread.sleep(sleepTime);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    // JMX暴露的属性
    @ManagedAttribute
    public double getCurrentQPS() {
        return currentQPS;
    }
}
