package com.triviumbuys.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.triviumbuys.entity.CartItem;
import com.triviumbuys.entity.User;
import com.triviumbuys.repository.CartItemRepository;
import com.triviumbuys.repository.ProductRepository;
import com.triviumbuys.repository.UserRepository;
import com.triviumbuys.dto.AddProductInCartDto;
import com.triviumbuys.dto.UpdateCartItemDto;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private CartItemRepository cartItemRepository;
    
    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private UserRepository userRepository;  // 🔥 Needed to validate user

    @Override
    public ResponseEntity<?> addToCart(AddProductInCartDto dto) {
        // 🔥 Check if user exists before inserting cart item
        Optional<User> optionalUser = userRepository.findById(dto.getUserId());
        if (!optionalUser.isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("User not found with ID: " + dto.getUserId());
        }

        CartItem cartItem = cartItemRepository.findByUserIdAndProductId(dto.getUserId(), dto.getProductId());

        int requestedQuantity = dto.getQuantity();
        int availableStock = productRepository.findById(dto.getProductId())
                                               .map(product -> product.getStock())
                                               .orElse(0);

        if (availableStock == 0) {
            return ResponseEntity.badRequest().body("Product out of stock");
        }

        int finalQuantity = requestedQuantity;

        if (cartItem != null) {
            finalQuantity = cartItem.getQuantity() + requestedQuantity;
        }

        if (finalQuantity > availableStock) {
            finalQuantity = availableStock;
        }

        if (cartItem == null) {
            cartItem = new CartItem();
            cartItem.setProductId(dto.getProductId());
            cartItem.setUserId(dto.getUserId());
        }

        cartItem.setQuantity(finalQuantity);
        cartItemRepository.save(cartItem);

        return ResponseEntity.ok("Added to cart successfully with quantity: " + finalQuantity);
    }

    @Override
    public ResponseEntity<?> removeFromCart(Long cartItemId) {
        if (!cartItemRepository.existsById(cartItemId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Cart item not found");
        }
        cartItemRepository.deleteById(cartItemId);
        return ResponseEntity.ok("Removed from cart.");
    }

    @Override
    public ResponseEntity<?> updateCartQuantity(UpdateCartItemDto dto) {
        Optional<CartItem> optionalCartItem = cartItemRepository.findById(dto.getCartItemId());
        if (!optionalCartItem.isPresent()) {
            return ResponseEntity.badRequest().body("Cart item not found");
        }
        CartItem cartItem = optionalCartItem.get();
        cartItem.setQuantity(dto.getQuantity());
        cartItemRepository.save(cartItem);
        return ResponseEntity.ok("Quantity updated successfully.");
    }

    @Override
    public ResponseEntity<?> getCartItems(Long userId) {
        // 🔥 Check if user exists before fetching cart
        Optional<User> optionalUser = userRepository.findById(userId);
        if (!optionalUser.isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("User not found with ID: " + userId);
        }

        List<CartItem> cartItems = cartItemRepository.findByUserId(userId);

        List<Map<String, Object>> cartItemsWithProduct = new ArrayList<>();

        for (CartItem cartItem : cartItems) {
            Map<String, Object> cartItemMap = new HashMap<>();
            cartItemMap.put("id", cartItem.getId());
            cartItemMap.put("quantity", cartItem.getQuantity());

            productRepository.findById(cartItem.getProductId()).ifPresent(product -> {
                cartItemMap.put("product", product);  // attach full product details
            });

            cartItemsWithProduct.add(cartItemMap);
        }

        return ResponseEntity.ok(cartItemsWithProduct);
    }
}
