package com.triviumbuys.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import com.triviumbuys.entity.CartItem;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    List<CartItem> findByUserId(Long userId);
    CartItem findByUserIdAndProductId(Long userId, Long productId);
}
