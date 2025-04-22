package com.triviumbuys.controller;

import com.triviumbuys.dto.DeliveryOrderDto;
import com.triviumbuys.entity.DeliveryPerson;
import com.triviumbuys.repository.DeliveryPersonRepository;
import com.triviumbuys.services.OrderService;
import com.triviumbuys.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/delivery")
public class DeliveryOrderController {

    @Autowired
    private DeliveryPersonRepository deliveryPersonRepository;

    @Autowired
    private OrderService orderService;

    @Autowired
    private JwtUtil jwtUtil;

    // ✅ API for delivery person to get their assigned orders
    @GetMapping("/assigned")
    public ResponseEntity<List<DeliveryOrderDto>> getAssignedOrders(@RequestHeader("Authorization") String token) {
        token = token.replace("Bearer ", "").trim();
        String username = jwtUtil.getUsernameFromToken(token);

        DeliveryPerson deliveryPerson = deliveryPersonRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Delivery person not found"));

        Long deliveryPersonId = deliveryPerson.getId();

        List<DeliveryOrderDto> assignedOrders = orderService.getAssignedOrdersForDelivery(deliveryPersonId);

        return ResponseEntity.ok(assignedOrders);
    }
}
