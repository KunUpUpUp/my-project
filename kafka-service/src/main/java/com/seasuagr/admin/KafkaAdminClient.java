package com.seasuagr.admin;

import org.apache.kafka.clients.admin.*;
import org.apache.kafka.common.config.ConfigResource;

import java.util.*;
import java.util.concurrent.ExecutionException;

public class KafkaAdminClient {

    private static final String BOOTSTRAP_SERVERS = "localhost:9092";

    public static void main(String[] args) {
        // 创建 AdminClient
        Properties props = new Properties();
        props.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        AdminClient adminClient = AdminClient.create(props);

        try {
            // 1. 新增 Topic
//            createTopic(adminClient, "new-topic", 3, (short) 1);

            // 2. 扩容分区
//            increasePartitions(adminClient, "new-topic", 5);

            // 3. 修改 Topic 的保存时间
            updateTopicRetention(adminClient, "new-topic", "6666666"); // 24小时

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            adminClient.close();
        }
    }

    private static void createTopic(AdminClient adminClient, String topicName, int numPartitions, short replicationFactor) throws ExecutionException, InterruptedException {
        NewTopic newTopic = new NewTopic(topicName, numPartitions, replicationFactor);
        CreateTopicsResult result = adminClient.createTopics(Collections.singleton(newTopic));
        result.all().get();
        System.out.println("Topic " + topicName + " created successfully.");
    }

    private static void increasePartitions(AdminClient adminClient, String topicName, int newPartitions) throws ExecutionException, InterruptedException {
        Map<String, NewPartitions> partitionsMap = new HashMap<>();
        partitionsMap.put(topicName, NewPartitions.increaseTo(newPartitions));
        adminClient.createPartitions(partitionsMap).all().get();
        System.out.println("Partitions for " + topicName + " increased to " + newPartitions + ".");
    }

    private static void updateTopicRetention(AdminClient adminClient, String topicName, String retentionMs) throws ExecutionException, InterruptedException {
        ConfigResource resource = new ConfigResource(ConfigResource.Type.TOPIC, topicName);
        ConfigEntry retentionEntry = new ConfigEntry("retention.ms", retentionMs);
        Config config = new Config(Collections.singleton(retentionEntry));
        Map<ConfigResource, Config> configEntries = Collections.singletonMap(resource, config);
        AlterConfigsResult alterConfigsResult = adminClient.alterConfigs(configEntries);
        System.out.println("Retention time for " + topicName + " updated to " + retentionMs + " ms.");
        System.out.println(alterConfigsResult.all().get());
    }
}