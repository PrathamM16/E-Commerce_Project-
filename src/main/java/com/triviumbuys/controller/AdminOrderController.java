package com.triviumbuys.controller;

import com.triviumbuys.dto.RecentOrderDto;
import com.triviumbuys.entity.OrderStatus;
import com.triviumbuys.services.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/orders")
public class AdminOrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping
    public List<RecentOrderDto> getAllOrders() {
        return orderService.getAllOrdersForAdmin();
    }

    @PutMapping("/{orderId}/status")
    public String updateOrderStatus(@PathVariable Long orderId, @RequestParam("status") OrderStatus status) {
        return orderService.updateOrderStatus(orderId, status);
    }
}
