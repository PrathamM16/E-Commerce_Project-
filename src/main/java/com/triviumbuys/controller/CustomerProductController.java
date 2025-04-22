package com.triviumbuys.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.triviumbuys.entity.Product;
import com.triviumbuys.repository.ProductRepository;

@RestController
@RequestMapping("/api/customer/products")
public class CustomerProductController {

    @Autowired
    private ProductRepository productRepository; // or use your Service layer if you have

    // ✅ GET /api/customer/products/{id}
    @GetMapping("/{id}")
    public ResponseEntity<?> getProductById(@PathVariable Long id) {
        Optional<Product> optionalProduct = productRepository.findById(id);

        if (optionalProduct.isPresent()) {
            return ResponseEntity.ok(optionalProduct.get());
        } else {
            return ResponseEntity.status(404).body("Product not found");
        }
    }
}
