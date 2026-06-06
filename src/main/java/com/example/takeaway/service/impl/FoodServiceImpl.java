/**
 * Food服务实现类
 * 实现 Food服务 接口定义的方法
 */

package com.example.takeaway.service.impl;

import com.example.takeaway.entity.Food;
import com.example.takeaway.mapper.Mapper.FoodMapper;
import com.example.takeaway.service.Service.FoodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FoodServiceImpl implements FoodService {

    @Autowired
    private FoodMapper foodMapper;

    @Override
    public Food create(Food food) {
        if (food.getCategoryId() == null || food.getCategoryId() == 0) {
            food.setCategoryId(null);
        }
        if (food.getImage() != null && food.getImage().trim().isEmpty()) {
            food.setImage(null);
        }
        return foodMapper.save(food);
    }

    @Override
    public Food update(Food food) {
        Food existing = findById(food.getId());
        if (food.getName() != null) existing.setName(food.getName());
        if (food.getDescription() != null) existing.setDescription(food.getDescription());
        if (food.getPrice() != null) existing.setPrice(food.getPrice());
        if (food.getImage() != null && !food.getImage().trim().isEmpty()) {
            existing.setImage(food.getImage());
        } else {
            existing.setImage(null);
        }
        if (food.getCategory() != null) existing.setCategory(food.getCategory());
        if (food.getIsHot() != null) existing.setIsHot(food.getIsHot());
        if (food.getIsNew() != null) existing.setIsNew(food.getIsNew());
        if (food.getStatus() != null) existing.setStatus(food.getStatus());
        return foodMapper.save(existing);
    }

    @Override
    public void delete(Long id) {
        foodMapper.deleteById(id);
    }

    @Override
    public Food findById(Long id) {
        Food food = foodMapper.findById(id)
                .orElseThrow(() -> new RuntimeException("食品不存在"));
        handleEmptyImage(food);
        return food;
    }

    @Override
    public List<Food> findByBusinessId(Long businessId) {
        List<Food> foods = foodMapper.findByBusinessId(businessId);
        foods.forEach(this::handleEmptyImage);
        return foods;
    }

    @Override
    public List<Food> findByBusinessIdAndStatus(Long businessId, Integer status) {
        List<Food> foods = foodMapper.findByBusinessIdAndStatus(businessId, status);
        foods.forEach(this::handleEmptyImage);
        return foods;
    }

    private void handleEmptyImage(Food food) {
        if (food.getImage() != null && food.getImage().trim().isEmpty()) {
            food.setImage(null);
        }
        // 从搜索URL中提取真实图片URL
        if (food.getImage() != null && food.getImage().contains("mediaurl=")) {
            String imageUrl = food.getImage();
            int start = imageUrl.indexOf("mediaurl=") + 9;
            int end = imageUrl.indexOf("&", start);
            if (end == -1) end = imageUrl.length();
            String mediaUrl = imageUrl.substring(start, end);
            try {
                food.setImage(java.net.URLDecoder.decode(mediaUrl, "UTF-8"));
            } catch (Exception e) {
                // 解码失败，保持原URL
            }
        }
    }

    @Override
    public List<Food> findByCategoryId(Long categoryId) {
        List<Food> foods = foodMapper.findByCategoryId(categoryId);
        foods.forEach(this::handleEmptyImage);
        return foods;
    }
}
