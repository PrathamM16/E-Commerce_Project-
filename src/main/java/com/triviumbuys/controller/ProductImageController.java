package com.triviumbuys.controller;

import com.triviumbuys.entity.Product;
import com.triviumbuys.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/uploads")
public class ProductImageController {

    @Autowired
    private ProductRepository productRepository;

    @GetMapping("/{productId}")
    public ResponseEntity<byte[]> getProductImage(@PathVariable Long productId) {
        Optional<Product> optionalProduct = productRepository.findById(productId);

        if (optionalProduct.isPresent() && optionalProduct.get().getImg() != null) {
            Product product = optionalProduct.get();
            byte[] imageBytes = product.getImg();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(getImageMimeType(imageBytes));

            return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    private MediaType getImageMimeType(byte[] imageBytes) {
        if (imageBytes.length > 3) {
            if (imageBytes[0] == (byte) 0xFF && imageBytes[1] == (byte) 0xD8) {
                return MediaType.IMAGE_JPEG;
            } else if (imageBytes[0] == (byte) 0x89 && imageBytes[1] == (byte) 0x50) {
                return MediaType.IMAGE_PNG;
            } else if (imageBytes[0] == (byte) 0x47 && imageBytes[1] == (byte) 0x49) {
                return MediaType.IMAGE_GIF;
            } else if (imageBytes[0] == (byte) 0x42 && imageBytes[1] == (byte) 0x4D) {
                return MediaType.valueOf("image/bmp");
            }
        }
        return MediaType.APPLICATION_OCTET_STREAM;
    }
}
