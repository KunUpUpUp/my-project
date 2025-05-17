package com.seasuagr.producer;

import java.util.Random;

import static com.seasuagr.producer.KafkaMessageProducer.sendMessage;

public class ProducerBatch {
    public static void main(String[] args) throws InterruptedException {
        int i = 0;
        Random random = new Random();

        while (true) {
            if (i < 100000) {
                int value = 0;
                if (i % 5 == 0)
                    value = random.nextInt(10);
                int count = 0;
                String endpoint = "10.97.45." + i;
                String serviceName = "newstore_api", namespace = "newstore_name";
                if (count++ % 50 == 0) {
                    count = 0;
                    serviceName = "newstore_api" + i;
                    namespace = "newstore_name" + i;
                    Thread.sleep(500);
                }
                i++;
                String s = "{\n" +
                        "    \"value\": \"" + value + "\",\n" +
                        "    \"nodeip\": \"10.97.168.215\",\n" +
                        "    \"serviceName\": \"" + serviceName + "\",\n" +
                        "    \"metricName\": \"podstatus\",\n" +
                        "    \"metric\": \"Throughput\",\n" +
                        "    \"ts\": 1731208710018,\n" +
                        "    \"step\": 30,\n" +
                        "    \"cluster\": \"xyidc\",\n" +
                        "    \"counterType\": \"COUNTER\",\n" +
                        "    \"uuid\": \"457f8b8c-4825-428d-b77b-30127d57197e\",\n" +
                        "    \"monitorType\": \"zxc\",\n" +
                        "    \"namespace\": \"" + namespace + "\",\n" +
                        "    \"endpoint\": \"" + endpoint + "\",\n" +
                        "    \"monitor_env\": \"online\"\n" +
                        "}";
                sendMessage("pod_event", s);
            } else {
                Thread.sleep(5000000);
                i = 0;
            }
        }
    }
}
