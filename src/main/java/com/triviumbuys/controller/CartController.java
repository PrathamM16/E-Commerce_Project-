package com.triviumbuys.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.triviumbuys.dto.AddProductInCartDto;
import com.triviumbuys.dto.UpdateCartItemDto;
import com.triviumbuys.entity.User;
import com.triviumbuys.repository.UserRepository;
import com.triviumbuys.services.CartService;
import com.triviumbuys.utils.JwtUtil;

@RestController
@RequestMapping("/api/customer/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @Autowired
    private JwtUtil jwtUtil;  

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/add")
    public ResponseEntity<?> addToCart(@RequestBody AddProductInCartDto dto,
                                       @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        String username = jwtUtil.getUsernameFromToken(token);
        User user = userRepository.findByUsername(username)
                       .orElseThrow(() -> new RuntimeException("User not found"));

        dto.setUserId(user.getId());
        return cartService.addToCart(dto);
    }

    @DeleteMapping("/remove/{cartItemId}")
    public ResponseEntity<?> removeFromCart(@PathVariable Long cartItemId) {
        return cartService.removeFromCart(cartItemId);
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateCartQuantity(@RequestBody UpdateCartItemDto dto) {
        return cartService.updateCartQuantity(dto);
    }

    // ✅ Fixed this method: NO userId param, only use token
    @GetMapping("/view")
    public ResponseEntity<?> getCartItems(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        String username = jwtUtil.getUsernameFromToken(token);
        User user = userRepository.findByUsername(username)
                       .orElseThrow(() -> new RuntimeException("User not found"));

        return cartService.getCartItems(user.getId());
    }
}
