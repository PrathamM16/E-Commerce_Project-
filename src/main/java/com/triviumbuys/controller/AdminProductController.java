package com.triviumbuys.controller;

import com.triviumbuys.entity.Product;
import com.triviumbuys.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.Map;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/admin/products")
public class AdminProductController {

    @Autowired
    private ProductService productService;

    // ✅ Add Product
    @PostMapping("/add")
    public ResponseEntity<Product> addProduct(
            @RequestParam("name") String name,
            @RequestParam("description") String description,
            @RequestParam("brand") String brand,
            @RequestParam("price") Double price,
            @RequestParam("stock") Integer stock,
            @RequestParam(value = "discount", required = false, defaultValue = "0") Double discount,
            @RequestParam(value = "taxRate", required = false, defaultValue = "18.0") Double taxRate,
            @RequestParam("categoryId") Long categoryId,
            @RequestParam("img") MultipartFile imgFile
    ) {
        Product product = new Product();
        product.setName(name);
        product.setDescription(description);
        product.setBrand(brand);
        product.setPrice(price);
        product.setStock(stock);
        product.setDiscount(discount);
        product.setTaxRate(taxRate);
        productService.setCategoryToProduct(product, categoryId);

        if (imgFile != null && !imgFile.isEmpty()) {
            productService.saveImage(product, imgFile);
        }

        return ResponseEntity.ok(productService.addProduct(product));
    }


    // ✅ Update Product
    @PutMapping("/update/{id}")
    public ResponseEntity<Product> updateProduct(
            @PathVariable Long id,
            @RequestParam("name") String name,
            @RequestParam("description") String description,
            @RequestParam("brand") String brand,
            @RequestParam("price") Double price,
            @RequestParam("stock") Integer stock,
            @RequestParam(value = "discount", required = false, defaultValue = "0") Double discount,
            @RequestParam(value = "taxRate", required = false, defaultValue = "18.0") Double taxRate,
            @RequestParam("categoryId") Long categoryId,
            @RequestParam(value = "img", required = false) MultipartFile imgFile
    ) {
        return productService.getProductById(id)
                .map(existingProduct -> {
                    existingProduct.setName(name);
                    existingProduct.setDescription(description);
                    existingProduct.setBrand(brand);
                    existingProduct.setPrice(price);
                    existingProduct.setStock(stock);
                    existingProduct.setDiscount(discount);
                    existingProduct.setTaxRate(taxRate);
                    productService.setCategoryToProduct(existingProduct, categoryId);

                    if (imgFile != null && !imgFile.isEmpty()) {
                        productService.saveImage(existingProduct, imgFile);
                    }

                    Product updatedProduct = productService.updateProduct(existingProduct);
                    return ResponseEntity.ok(updatedProduct);
                })
                .orElse(ResponseEntity.notFound().build());
    }

 // ✅ Delete Product
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id) {
        if (!productService.getProductById(id).isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Product not found"));
        }

        boolean deleted = productService.deleteProduct(id);
        if (deleted) {
            return ResponseEntity.ok(Map.of("message", "Product deleted successfully"));
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Failed to delete product"));
        }
    }

    // ✅ Get All Products
    @GetMapping("/all")
    public ResponseEntity<List<Product>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    // ✅ Get Product by ID
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        return productService.getProductById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
