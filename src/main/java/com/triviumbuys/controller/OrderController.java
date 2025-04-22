package com.triviumbuys.controller;

import com.triviumbuys.dto.CreateOrderRequestDto;
import com.triviumbuys.dto.CustomerOrderDto;
import com.triviumbuys.dto.OrderResponseDto;
import com.triviumbuys.dto.RecentOrderDto;
import com.triviumbuys.entity.Order;
import com.triviumbuys.entity.OrderStatus;
import com.triviumbuys.entity.User;
import com.triviumbuys.repository.UserRepository;
import com.triviumbuys.services.OrderService;
import com.triviumbuys.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepository userRepository;

    // ✅ 1. Create Order
    @PostMapping("/create")
    public ResponseEntity<?> createOrder(
            @RequestHeader("Authorization") String token,
            @RequestBody CreateOrderRequestDto request
    ) {
        Long userId = getUserIdFromToken(token);
        System.out.println("✅ Creating Order for User ID: " + userId);
        OrderResponseDto response = orderService.createOrder(userId, request);
        return ResponseEntity.ok(response);
    }

    // ✅ 2. Get logged-in Customer's all Orders
    @GetMapping("/customer")
    public ResponseEntity<List<CustomerOrderDto>> getOrdersOfLoggedInCustomer(@RequestHeader("Authorization") String token) {
        Long userId = getUserIdFromToken(token);
        System.out.println("✅ Fetching Orders for User ID: " + userId);

        List<CustomerOrderDto> customerOrders = orderService.getOrdersByCustomer(userId);
        return ResponseEntity.ok(customerOrders);
    }

    // ✅ 3. Get Specific Order Details
    @GetMapping("/details/{orderId}")
    public ResponseEntity<Order> getOrderDetails(@PathVariable Long orderId) {
        System.out.println("✅ Fetching Order Details for Order ID: " + orderId);
        return ResponseEntity.ok(orderService.getOrderDetails(orderId));
    }

    // ✅ 4. Admin - View All Orders
    @GetMapping("/all")
    public ResponseEntity<List<Order>> getAllOrders() {
        System.out.println("✅ Fetching ALL Orders for Admin View");
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    // ✅ 5. Admin - Update Order Status
    @PutMapping("/admin/orders/{orderId}/status")
    public ResponseEntity<String> updateOrderStatus(
            @PathVariable Long orderId,
            @RequestParam("status") OrderStatus status) {
        String message = orderService.updateOrderStatus(orderId, status);
        return ResponseEntity.ok(message);
    }

    // --- Helper Method to Extract userId from JWT ---
    private Long getUserIdFromToken(String token) {
        token = token.replace("Bearer ", "").trim();
        try {
            Claims claims = jwtUtil.extractAllClaims(token);
            String username = claims.getSubject(); // Username from token

            System.out.println("✅ Extracted Username from Token: " + username);

            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found for username: " + username));

            System.out.println("✅ Found User ID for username " + username + ": " + user.getId());

            return user.getId();
        } catch (Exception e) {
            System.out.println("❌ Error extracting user ID from token: " + e.getMessage());
            throw new RuntimeException("Invalid Token: " + e.getMessage());
        }
    }
}
