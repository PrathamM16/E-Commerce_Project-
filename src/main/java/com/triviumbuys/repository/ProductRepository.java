package com.triviumbuys.repository;

import com.triviumbuys.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
	boolean existsByCategoryId(Long categoryId);

}
