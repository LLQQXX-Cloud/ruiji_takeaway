package com.example.takeaway.mapper;

import com.example.takeaway.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByBusinessIdOrderByCreatedAtDesc(Long businessId);
    Review findByOrderId(Long orderId);
    List<Review> findByUserIdOrderByCreatedAtDesc(Long userId);
    Double findAvgRatingByBusinessId(Long businessId);
    int countByBusinessId(Long businessId);
}
