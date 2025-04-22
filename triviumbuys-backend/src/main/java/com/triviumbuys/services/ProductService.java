package com.triviumbuys.services;

import com.triviumbuys.entity.Category;
import com.triviumbuys.entity.Product;
import com.triviumbuys.repository.CategoryRepository;
import com.triviumbuys.repository.OrderItemRepository;
import com.triviumbuys.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class ProductService {
    
    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private CategoryRepository categoryRepository;
    
    @Autowired
    private OrderItemRepository orderItemRepository;

    // ✅ Add New Product
    public Product addProduct(Product product) {
        product.setCreatedDate(LocalDate.now());
        return productRepository.save(product);
    }

    // ✅ Update Product
    public Product updateProduct(Product product) {
        return productRepository.save(product);
    }

    // ✅ Delete Product - Corrected to handle foreign key constraints
    @Transactional
    public boolean deleteProduct(Long id) {
        try {
            // Check if product exists
            if (!productRepository.existsById(id)) {
                return false;
            }
            
            // Remove references to this product in order items
            orderItemRepository.nullifyProductReferences(id);
            
            // Now delete the product
            productRepository.deleteById(id);
            return true;
        } catch (Exception e) {
            // Log the error
            e.printStackTrace();
            return false;
        }
    }

    // ✅ Get All Products
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    // ✅ Get Single Product
    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    // ✅ Set Category to Product
    public void setCategoryToProduct(Product product, Long categoryId) {
        Optional<Category> categoryOptional = categoryRepository.findById(categoryId);
        categoryOptional.ifPresent(product::setCategory);
    }

    // ✅ Save Image for Product
    public void saveImage(Product product, MultipartFile imageFile) {
        try {
            byte[] imageData = imageFile.getBytes();
            product.setImg(imageData);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save image", e);
        }
    }

    // ✅ Update Existing Product Fields
    public Optional<Product> updateExistingProduct(Long id, Product updatedProduct) {
        return productRepository.findById(id)
                .map(existing -> {
                    existing.setName(updatedProduct.getName());
                    existing.setDescription(updatedProduct.getDescription());
                    existing.setPrice(updatedProduct.getPrice());
                    existing.setStock(updatedProduct.getStock());
                    existing.setImageUrl(updatedProduct.getImageUrl());
                    existing.setBrand(updatedProduct.getBrand());
                    existing.setDiscount(updatedProduct.getDiscount());
                    existing.setTaxRate(updatedProduct.getTaxRate());
                    if (updatedProduct.getCategory() != null) {
                        existing.setCategory(updatedProduct.getCategory());
                    }
                    return productRepository.save(existing);
                });
    }
}