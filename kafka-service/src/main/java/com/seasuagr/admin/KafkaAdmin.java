package main.java.com.seasuagr.admin;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.KafkaAdminClient;
import org.apache.kafka.clients.admin.ListTopicsResult;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

public class KafkaAdmin {
    public static void main(String[] args) {


        Properties props = new Properties();
        props.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(AdminClientConfig.REQUEST_TIMEOUT_MS_CONFIG, "5000");
        AdminClient admin = KafkaAdminClient.create(props);

        ListTopicsResult topics = admin.listTopics();
        try {
            System.out.println("Topics: " + topics.names().get());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

    }
}
