package com.triviumbuys.kafka;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class OrderKafkaProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;

    @Value("${app.kafka.order-topic}")
    private String orderTopic;

    public OrderKafkaProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendOrder(String orderMessage) {
        kafkaTemplate.send(orderTopic, orderMessage);
        System.out.println("✅ Order sent to Kafka Topic: " + orderMessage);
    }
}
