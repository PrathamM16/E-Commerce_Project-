package com.triviumbuys.controller;

import com.triviumbuys.entity.Order;
import com.triviumbuys.entity.OrderStatus;
import com.triviumbuys.repository.OrderRepository;
import com.triviumbuys.services.DeliveryPersonService;
import com.triviumbuys.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/delivery/dashboard")
public class DeliveryDashboardController {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private DeliveryPersonService deliveryPersonService;

    @Autowired
    private JwtUtil jwtUtil;

    // ✅ 1. Get all assigned orders for logged-in delivery person
    @GetMapping("/assigned-orders")
    public List<Order> getAssignedOrders(@RequestHeader("Authorization") String token) {
        Long deliveryPersonId = getDeliveryPersonIdFromToken(token);

        return orderRepository.findAll()
                .stream()
                .filter(order -> deliveryPersonId.equals(order.getDeliveryPersonId()))
                .collect(Collectors.toList());
    }

    // ✅ 2. Update order status
    @PutMapping("/update-status/{orderId}")
    public String updateOrderStatus(@RequestHeader("Authorization") String token,
                                    @PathVariable Long orderId,
                                    @RequestParam("status") OrderStatus status) {
        Long deliveryPersonId = getDeliveryPersonIdFromToken(token);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));

        // Check if the order is assigned to the logged-in delivery person
        if (!deliveryPersonId.equals(order.getDeliveryPersonId())) {
            throw new RuntimeException("Access denied. This order is not assigned to you.");
        }

        order.setStatus(status);
        orderRepository.save(order);

        return "Order status updated successfully!";
    }

    // --- Helper method to extract delivery person ID from token ---
    private Long getDeliveryPersonIdFromToken(String token) {
        token = token.replace("Bearer ", "").trim();
        Claims claims = jwtUtil.extractAllClaims(token);
        String username = claims.getSubject();

        return deliveryPersonService.getByUsername(username).getId();
    }
}
