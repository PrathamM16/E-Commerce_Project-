package com.triviumbuys.kafka;

import com.triviumbuys.entity.DeliveryPerson;
import com.triviumbuys.entity.Order;
import com.triviumbuys.entity.OrderStatus;
import com.triviumbuys.repository.DeliveryPersonRepository;
import com.triviumbuys.repository.OrderRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class OrderKafkaConsumer {

    @Autowired
    private DeliveryPersonRepository deliveryPersonRepository;

    @Autowired
    private OrderRepository orderRepository;

    private final AtomicInteger counter = new AtomicInteger(0); // for round-robin assignment

    @KafkaListener(topics = "${app.kafka.order-topic}", groupId = "order-group")
    @Transactional
    public void consumeOrder(String message) {
        System.out.println("✅ Received Order from Kafka: " + message);

        try {
            // Extract orderId from message
            Long orderId = extractOrderId(message);

            if (orderId == null) {
                System.out.println("❌ Invalid order message: " + message);
                return;
            }

            System.out.println("✅ Processing Order ID: " + orderId);

            // Fetch all delivery persons
            List<DeliveryPerson> deliveryPeople = deliveryPersonRepository.findAll();

            if (deliveryPeople.isEmpty()) {
                System.out.println("❌ No delivery persons available. Please register at least one delivery person.");
                return;
            }

            System.out.println("✅ Found " + deliveryPeople.size() + " delivery persons available");

            // Find the order in the database
            Order order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));
            
            System.out.println("✅ Found order in database with ID: " + order.getId());

            // Assign a delivery person using round-robin
            int index = counter.getAndIncrement() % deliveryPeople.size();
            DeliveryPerson deliveryPerson = deliveryPeople.get(index);
            
            System.out.println("✅ Selected delivery person ID: " + deliveryPerson.getId() + ", Name: " + deliveryPerson.getName());
            
            // Set the delivery person ID on the order
            order.setDeliveryPersonId(deliveryPerson.getId());
            
            // Save the updated order back to the database
            orderRepository.save(order);
            
            System.out.println("✅ Successfully assigned Order ID: " + order.getId() + 
                             " to Delivery Person ID: " + deliveryPerson.getId());

        } catch (Exception e) {
            System.out.println("❌ Error processing Kafka message: " + e.getMessage());
            e.printStackTrace(); // Print full stack trace for debugging
        }
    }

    // Helper method to extract orderId from message
    private Long extractOrderId(String message) {
        try {
            System.out.println("Parsing message: " + message);
            String[] parts = message.split(",");
            for (String part : parts) {
                String trimmed = part.trim();
                System.out.println("Checking part: " + trimmed);
                if (trimmed.startsWith("OrderId:")) {
                    String orderIdStr = trimmed.substring("OrderId:".length()).trim();
                    System.out.println("Found OrderId: " + orderIdStr);
                    return Long.parseLong(orderIdStr);
                }
            }
            System.out.println("❌ OrderId not found in message");
        } catch (Exception e) {
            System.out.println("❌ Error extracting OrderId: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
}