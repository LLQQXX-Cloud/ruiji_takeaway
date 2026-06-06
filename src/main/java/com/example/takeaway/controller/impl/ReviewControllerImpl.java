/**
 * Review控制器实现类
 * 实现 Review控制器 接口定义的方法
 */

package com.example.takeaway.controller.impl;

import com.example.takeaway.controller.Controller.ReviewController;
import com.example.takeaway.entity.Review;
import com.example.takeaway.service.Service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class ReviewControllerImpl implements ReviewController {
    
    private final ReviewService reviewService;
    
    @Override
    public ResponseEntity<List<Review>> getReviewsByBusiness(Long businessId) {
        return ResponseEntity.ok(reviewService.findByBusinessId(businessId));
    }
    
    @Override
    public ResponseEntity<Review> getReviewByOrder(Long orderId) {
        Review review = reviewService.findByOrderId(orderId);
        return review != null ? ResponseEntity.ok(review) : ResponseEntity.notFound().build();
    }
    
    @Override
    public ResponseEntity<List<Review>> getReviewsByUser(Long userId) {
        return ResponseEntity.ok(reviewService.findByUserId(userId));
    }
    
    @Override
    public ResponseEntity<Map<String, Object>> getBusinessRating(Long businessId) {
        return ResponseEntity.ok(reviewService.getBusinessRating(businessId));
    }
    
    @Override
    public ResponseEntity<Review> addReview(Review review) {
        return ResponseEntity.ok(reviewService.addReview(review));
    }
    
    @Override
    public ResponseEntity<Review> updateReview(Long id, Review review) {
        review.setId(id);
        return ResponseEntity.ok(reviewService.updateReview(review));
    }
    
    @Override
    public ResponseEntity<Void> deleteReview(Long id) {
        reviewService.deleteReview(id);
        return ResponseEntity.noContent().build();
    }
}