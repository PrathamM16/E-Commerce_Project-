package com.triviumbuys.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.triviumbuys.entity.OrderItem;
import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    List<OrderItem> findByProductId(Long productId);
    
    @Modifying
    @Transactional
    @Query("UPDATE OrderItem oi SET oi.productId = NULL WHERE oi.productId = :productId")
    void nullifyProductReferences(@Param("productId") Long productId);
}