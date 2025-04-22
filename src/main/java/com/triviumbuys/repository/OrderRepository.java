package com.triviumbuys.repository;

import com.triviumbuys.entity.Order;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    // Sum of all order amounts
    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o")
    Long sumTotalAmount();
    
   
    List<Order> findByCustomerId(Long customerId);
    List<Order> findTop5ByOrderByOrderDateDesc(); 
    
    @Query("SELECT SUM(o.totalAmount) FROM Order o WHERE MONTH(o.orderDate) = :month")
    Double findTotalRevenueByMonth(@Param("month") int month);
    
    List<Order> findByDeliveryPersonId(Long deliveryPersonId);
    
    List<Order> findByOrderDateBetween(LocalDateTime startDateTime, LocalDateTime endDateTime);

    
    

}
