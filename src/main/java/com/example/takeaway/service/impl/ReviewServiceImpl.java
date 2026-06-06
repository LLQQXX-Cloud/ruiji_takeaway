/**
 * Review服务实现类
 * 实现 Review服务 接口定义的方法
 */

package com.example.takeaway.service.impl;

import com.example.takeaway.entity.Review;
import com.example.takeaway.mapper.Mapper.ReviewMapper;
import com.example.takeaway.service.Service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {
    
    private final ReviewMapper reviewMapper;
    
    @Override
    public Review findById(Long id) {
        return reviewMapper.findById(id);
    }
    
    @Override
    public List<Review> findByBusinessId(Long businessId) {
        return reviewMapper.findByBusinessId(businessId);
    }
    
    @Override
    public Review findByOrderId(Long orderId) {
        return reviewMapper.findByOrderId(orderId);
    }
    
    @Override
    public List<Review> findByUserId(Long userId) {
        return reviewMapper.findByUserId(userId);
    }
    
    @Override
    public Map<String, Object> getBusinessRating(Long businessId) {
        Map<String, Object> result = new HashMap<>();
        result.put("avgRating", reviewMapper.findAvgRating(businessId));
        result.put("reviewCount", reviewMapper.countByBusinessId(businessId));
        return result;
    }
    
    @Override
    public Review addReview(Review review) {
        reviewMapper.insert(review);
        return review;
    }
    
    @Override
    public Review updateReview(Review review) {
        reviewMapper.update(review);
        return review;
    }
    
    @Override
    public void deleteReview(Long id) {
        reviewMapper.deleteById(id);
    }
}