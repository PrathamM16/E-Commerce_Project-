package com.triviumbuys.controller;

import com.triviumbuys.entity.Review;
import com.triviumbuys.services.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@CrossOrigin(origins = "http://localhost:4200")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    // Add a new review
    @PostMapping("/add")
    public Review addReview(@RequestBody Review review) {
        return reviewService.addReview(review);
    }

    // Get all reviews for a product
    @GetMapping("/product/{productId}")
    public List<Review> getReviewsByProduct(@PathVariable Long productId) {
        return reviewService.getReviewsByProductId(productId);
    }
}
