package com.seasuagr.test;

import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.admin.AdminClient;

import java.util.Properties;

public class KafkaConnectTest {
    public static void main(String[] args) {
        test();
    }

    private static AdminClient createAdminClient(String brokerUrl) {
        Properties props = new Properties();
        props.put(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, brokerUrl);
        AdminClient adminClient = AdminClient.create(props);
        System.out.println("创建成功");
        return adminClient;
    }

    private static void test() {
        AdminClient adminClient = createAdminClient("localhost:9092");
    }
}
