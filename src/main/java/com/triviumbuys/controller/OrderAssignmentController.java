package com.triviumbuys.controller;

import com.triviumbuys.services.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders/assignment")
public class OrderAssignmentController {

    @Autowired
    private OrderService orderService;

    @PostMapping("/assign/{orderId}")
    public ResponseEntity<String> assignDeliveryPerson(@PathVariable Long orderId) {
        orderService.assignDeliveryPerson(orderId);
        return ResponseEntity.ok("Order assigned to delivery person successfully");
    }
    
    @PostMapping("/assign-all-unassigned")
    public ResponseEntity<String> assignAllUnassignedOrders() {
        orderService.assignAllUnassignedOrders();
        return ResponseEntity.ok("All unassigned orders have been assigned to delivery people");
    }
}