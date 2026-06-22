/**
 * Review服务实现类
 * 实现 Review服务 接口定义的方法
 */

package com.example.takeaway.service.impl;

import com.example.takeaway.entity.Business;
import com.example.takeaway.entity.Orders;
import com.example.takeaway.entity.Review;
import com.example.takeaway.mapper.impl.BusinessRepository;
import com.example.takeaway.mapper.impl.OrdersRepository;
import com.example.takeaway.mapper.impl.ReviewRepository;
import com.example.takeaway.service.CacheService;
import com.example.takeaway.service.Service.ReviewService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewMapper;
    private final OrdersRepository ordersMapper;
    private final BusinessRepository businessMapper;
    private final CacheService cacheService;

    private static final String BUSINESS_KEY_PREFIX = "business:info:";
    private static final String BUSINESS_LIST_KEY = "business:list:";

    /**
     * 启动时根据所有已有评价重新计算每个商家的评分
     * 修复历史数据中评分不同步的问题
     */
    @PostConstruct
    @Transactional
    public void initSyncAllBusinessRatings() {
        log.info("开始同步所有商家评分...");
        List<Business> allBusinesses = businessMapper.findAll();
        for (Business business : allBusinesses) {
            Long businessId = business.getId();
            Double avgRating = reviewMapper.findAvgRatingByBusinessId(businessId);
            BigDecimal rating = avgRating != null
                    ? BigDecimal.valueOf(avgRating).setScale(1, RoundingMode.HALF_UP)
                    : BigDecimal.valueOf(5.0);
            if (!rating.equals(business.getRating())) {
                log.info("商家 [{}] 评分更新: {} → {}", business.getName(), business.getRating(), rating);
                business.setRating(rating);
                businessMapper.save(business);
            }
        }
        // 清除所有商家相关缓存
        cacheService.deleteByPattern(BUSINESS_KEY_PREFIX + "*");
        cacheService.deleteByPattern(BUSINESS_LIST_KEY + "*");
        log.info("商家评分同步完成");
    }
    
    @Override
    public Review findById(Long id) {
        return reviewMapper.findById(id).orElse(null);
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
        result.put("avgRating", reviewMapper.findAvgRatingByBusinessId(businessId));
        result.put("reviewCount", reviewMapper.countByBusinessId(businessId));
        return result;
    }
    
    @Override
    @Transactional
    public Review addReview(Review review) {
        // 检查该订单是否已评价
        Review existing = reviewMapper.findByOrderId(review.getOrderId());
        if (existing != null) {
            throw new RuntimeException("该订单已评价过");
        }

        Review saved = reviewMapper.save(review);

        // 更新订单评价状态
        Orders order = ordersMapper.findById(review.getOrderId()).orElse(null);
        if (order != null) {
            order.setReviewed(1);
            ordersMapper.save(order);
        }

        // 更新商家评分
        updateBusinessRating(review.getBusinessId());

        return saved;
    }

    @Override
    @Transactional
    public Review updateReview(Review review) {
        Review existing = findById(review.getId());
        if (existing == null) {
            throw new RuntimeException("评价不存在");
        }
        Review updated = reviewMapper.save(review);

        // 如果评分有变化，更新商家评分
        if (review.getRating() != null && !review.getRating().equals(existing.getRating())) {
            updateBusinessRating(existing.getBusinessId());
        }

        return updated;
    }

    @Override
    @Transactional
    public void deleteReview(Long id) {
        Review review = findById(id);
        if (review != null) {
            Long businessId = review.getBusinessId();
            reviewMapper.deleteById(id);
            // 删除后更新商家评分
            updateBusinessRating(businessId);
        }
    }

    /**
     * 根据该商家所有评价的平均分更新商家评分，并清除 Redis 缓存
     */
    private void updateBusinessRating(Long businessId) {
        Double avgRating = reviewMapper.findAvgRatingByBusinessId(businessId);
        BigDecimal rating = avgRating != null
                ? BigDecimal.valueOf(avgRating).setScale(1, RoundingMode.HALF_UP)
                : BigDecimal.valueOf(5.0);

        Business business = businessMapper.findById(businessId).orElse(null);
        if (business != null) {
            business.setRating(rating);
            businessMapper.save(business);
            // 清除 Redis 缓存，确保前端读到最新评分
            cacheService.delete(BUSINESS_KEY_PREFIX + businessId);
            cacheService.deleteByPattern(BUSINESS_LIST_KEY + "*");
        }
    }
    
    @Override
    @Transactional
    public void deleteAllReviews() {
        // 收集所有被评价的商家ID
        List<Long> businessIds = reviewMapper.findAll().stream()
                .map(Review::getBusinessId)
                .distinct()
                .toList();

        // 删除所有评价
        reviewMapper.deleteAll();

        // 重置所有订单的评价状态
        List<Orders> orders = ordersMapper.findAll();
        for (Orders order : orders) {
            order.setReviewed(0);
            ordersMapper.save(order);
        }

        // 重置所有受影响商家的评分
        for (Long businessId : businessIds) {
            updateBusinessRating(businessId);
        }
    }
}