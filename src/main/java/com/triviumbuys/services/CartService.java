package com.triviumbuys.services;

import org.springframework.http.ResponseEntity;
import com.triviumbuys.dto.AddProductInCartDto;
import com.triviumbuys.dto.UpdateCartItemDto;

public interface CartService {
    ResponseEntity<?> addToCart(AddProductInCartDto addProductInCartDto);
    ResponseEntity<?> removeFromCart(Long cartItemId);
    ResponseEntity<?> updateCartQuantity(UpdateCartItemDto updateCartItemDto);
    ResponseEntity<?> getCartItems(Long userId);
}
