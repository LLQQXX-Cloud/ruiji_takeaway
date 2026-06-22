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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 评论控制器
 * 处理评论相关的 CRUD 操作
 */
@RestController
@RequiredArgsConstructor
public class ReviewControllerImpl implements ReviewController {
    
    private final ReviewService reviewService;
    
    /**
     * 根据商家ID获取评论列表
     * @param businessId 商家ID
     * @return 评论列表
     */
    @Override
    public ResponseEntity<Map<String, Object>> getReviewsByBusiness(Long businessId) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Review> reviews = reviewService.findByBusinessId(businessId);
            response.put("success", true);
            response.put("data", reviews);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * 根据订单ID获取评论
     * @param orderId 订单ID
     * @return 评论信息
     */
    @Override
    public ResponseEntity<Map<String, Object>> getReviewByOrder(Long orderId) {
        Map<String, Object> response = new HashMap<>();
        try {
            Review review = reviewService.findByOrderId(orderId);
            if (review != null) {
                response.put("success", true);
                response.put("data", review);
            } else {
                response.put("success", false);
                response.put("message", "评论不存在");
            }
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * 根据用户ID获取评论列表
     * @param userId 用户ID
     * @return 用户评论列表
     */
    @Override
    public ResponseEntity<Map<String, Object>> getReviewsByUser(Long userId) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Review> reviews = reviewService.findByUserId(userId);
            response.put("success", true);
            response.put("data", reviews);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * 获取商家评分统计
     * @param businessId 商家ID
     * @return 评分统计信息（平均分、评论数）
     */
    @Override
    public ResponseEntity<Map<String, Object>> getBusinessRating(Long businessId) {
        Map<String, Object> response = new HashMap<>();
        try {
            Map<String, Object> rating = reviewService.getBusinessRating(businessId);
            response.put("success", true);
            response.put("data", rating);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * 添加评论
     * @param review 评论信息
     * @return 创建的评论
     */
    @Override
    public ResponseEntity<Map<String, Object>> addReview(Review review) {
        Map<String, Object> response = new HashMap<>();
        try {
            Review created = reviewService.addReview(review);
            response.put("success", true);
            response.put("data", created);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * 更新评论
     * @param id 评论ID
     * @param review 更新后的评论信息
     * @return 更新后的评论
     */
    @Override
    public ResponseEntity<Map<String, Object>> updateReview(Long id, Review review) {
        Map<String, Object> response = new HashMap<>();
        try {
            review.setId(id);
            Review updated = reviewService.updateReview(review);
            response.put("success", true);
            response.put("data", updated);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * 删除评论
     * @param id 评论ID
     * @return 删除结果
     */
    @Override
    public ResponseEntity<Map<String, Object>> deleteReview(Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            reviewService.deleteReview(id);
            response.put("success", true);
            response.put("message", "删除成功");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * 清空所有评论
     * @return 删除结果
     */
    @Override
    public ResponseEntity<Map<String, Object>> deleteAllReviews() {
        Map<String, Object> response = new HashMap<>();
        try {
            reviewService.deleteAllReviews();
            response.put("success", true);
            response.put("message", "清空成功");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}