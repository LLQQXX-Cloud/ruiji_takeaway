package com.example.takeaway.mapper;

import com.example.takeaway.entity.Food;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FoodRepository extends JpaRepository<Food, Long> {
    List<Food> findByBusinessIdAndStatus(Long businessId, Integer status);
    List<Food> findByCategoryIdAndStatus(Long categoryId, Integer status);
    List<Food> findByBusinessId(Long businessId);
    void deleteByBusinessId(Long businessId);
}
