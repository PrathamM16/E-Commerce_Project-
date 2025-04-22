package com.triviumbuys.controller;

import com.triviumbuys.entity.User;
import com.triviumbuys.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/customers")
public class AdminCustomerController {

    @Autowired
    private UserService userService;

    // ✅ 1. Get All Customers
    @GetMapping("/all")
    public ResponseEntity<List<User>> getAllCustomers() {
        List<User> customers = userService.findAllCustomers();
        return ResponseEntity.ok(customers);
    }

    // ✅ 2. Search Customer by ID
    @GetMapping("/search/id/{id}")
    public ResponseEntity<User> getCustomerById(@PathVariable Long id) {
        return userService.findCustomerById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ✅ 3. Search Customer by Name
    @GetMapping("/search/name/{name}")
    public ResponseEntity<List<User>> getCustomersByName(@PathVariable String name) {
        List<User> customers = userService.findCustomersByName(name);
        return ResponseEntity.ok(customers);
    }
}
