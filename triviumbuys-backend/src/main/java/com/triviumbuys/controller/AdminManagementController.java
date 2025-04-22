package com.triviumbuys.controller;

import com.triviumbuys.dto.AdminCreationRequest;
import com.triviumbuys.entity.User;
import com.triviumbuys.entity.Role;
import com.triviumbuys.repository.UserRepository;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin-management")
public class AdminManagementController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/add-admin")
    public ResponseEntity<String> addAdmin(@RequestBody AdminCreationRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Username already exists!");
        }

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Email already exists!");
        }

        if (userRepository.findByPhone(request.getPhone()).isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Phone number already exists!");
        }

        User newAdmin = new User();
        newAdmin.setName(request.getName());
        newAdmin.setUsername(request.getUsername());
        newAdmin.setEmail(request.getEmail());
        newAdmin.setPhone(request.getPhone());
        newAdmin.setPassword(passwordEncoder.encode(request.getPassword()));
        newAdmin.setAddress(request.getAddress());
        newAdmin.setRole(Role.ADMIN);

        if (request.getUsername().equalsIgnoreCase("Pratham")) {
            // ✅ If it's Pratham, force full access
            newAdmin.setAccessRights(Set.of(
                com.triviumbuys.entity.AdminAccess.PRODUCTS,
                com.triviumbuys.entity.AdminAccess.CUSTOMERS,
                com.triviumbuys.entity.AdminAccess.ORDERS,
                com.triviumbuys.entity.AdminAccess.REPORTS,
                com.triviumbuys.entity.AdminAccess.SETTINGS
            ));
        } else {
            newAdmin.setAccessRights(request.getAccessRights());
        }

        userRepository.save(newAdmin);
        return ResponseEntity.status(HttpStatus.CREATED).body("New Admin Created Successfully!");
    }

}
