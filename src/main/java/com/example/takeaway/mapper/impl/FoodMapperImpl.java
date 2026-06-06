/**
 * Food数据映射实现类
 * 实现 Food数据映射 接口定义的方法
 */

package com.example.takeaway.mapper.impl;

import com.example.takeaway.entity.Food;
import com.example.takeaway.mapper.Mapper.FoodMapper;
import com.example.takeaway.mapper.FoodRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class FoodMapperImpl implements FoodMapper {

    @Autowired
    private FoodRepository foodRepository;

    @Override
    public Food save(Food food) {
        return foodRepository.save(food);
    }

    @Override
    public void deleteById(Long id) {
        foodRepository.deleteById(id);
    }

    @Override
    public Optional<Food> findById(Long id) {
        return foodRepository.findById(id);
    }

    @Override
    public List<Food> findByBusinessIdAndStatus(Long businessId, Integer status) {
        return foodRepository.findByBusinessIdAndStatus(businessId, status);
    }

    @Override
    public List<Food> findByCategoryId(Long categoryId) {
        return foodRepository.findByCategoryIdAndStatus(categoryId, 1);
    }

    @Override
    public List<Food> findByBusinessId(Long businessId) {
        return foodRepository.findByBusinessId(businessId);
    }

    @Override
    public void deleteByBusinessId(Long businessId) {
        foodRepository.deleteByBusinessId(businessId);
    }
}
