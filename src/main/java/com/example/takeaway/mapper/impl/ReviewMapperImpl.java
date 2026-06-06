/**
 * Review数据映射实现类
 * 实现 Review数据映射 接口定义的方法
 */

package com.example.takeaway.mapper.impl;

import com.example.takeaway.entity.Review;
import com.example.takeaway.mapper.Mapper.ReviewMapper;
import com.example.takeaway.mapper.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ReviewMapperImpl implements ReviewMapper {
    
    private final ReviewRepository reviewRepository;
    
    @Override
    public Review findById(Long id) {
        return reviewRepository.findById(id).orElse(null);
    }
    
    @Override
    public List<Review> findByBusinessId(Long businessId) {
        return reviewRepository.findByBusinessIdOrderByCreatedAtDesc(businessId);
    }
    
    @Override
    public Review findByOrderId(Long orderId) {
        return reviewRepository.findByOrderId(orderId);
    }
    
    @Override
    public List<Review> findByUserId(Long userId) {
        return reviewRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }
    
    @Override
    public Double findAvgRating(Long businessId) {
        return reviewRepository.findAvgRatingByBusinessId(businessId);
    }
    
    @Override
    public int countByBusinessId(Long businessId) {
        return reviewRepository.countByBusinessId(businessId);
    }
    
    @Override
    public void insert(Review review) {
        reviewRepository.save(review);
    }
    
    @Override
    public void update(Review review) {
        reviewRepository.save(review);
    }
    
    @Override
    public void deleteById(Long id) {
        reviewRepository.deleteById(id);
    }
}
