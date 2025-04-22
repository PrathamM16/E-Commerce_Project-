package com.triviumbuys.services;

import com.triviumbuys.entity.Category;
import com.triviumbuys.repository.CategoryRepository;
import com.triviumbuys.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductRepository productRepository;

    public Category addCategory(Category category) {
        return categoryRepository.save(category);
    }

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public Category updateCategory(Long id, Category updatedCategory) {
        Optional<Category> optionalCategory = categoryRepository.findById(id);
        if (optionalCategory.isPresent()) {
            Category existingCategory = optionalCategory.get();
            existingCategory.setName(updatedCategory.getName());
            existingCategory.setDescription(updatedCategory.getDescription());
            return categoryRepository.save(existingCategory);
        } else {
            throw new RuntimeException("Category not found with id: " + id);
        }
    }

    public ResponseEntity<?> deleteCategory(Long categoryId) {
        if (!categoryRepository.existsById(categoryId)) {
            return ResponseEntity.status(404).body(Map.of("message", "Category not found"));
        }

        if (productRepository.existsByCategoryId(categoryId)) {
            return ResponseEntity.status(400)
                    .body(Map.of("message", "Cannot delete category. Products are still associated with it."));
        }

        categoryRepository.deleteById(categoryId);
        return ResponseEntity.ok(Map.of("message", "Category deleted successfully"));
    }
}
