/**
 * Food数据映射实现类
 * 提供食品的数据访问操作
 */

package com.example.takeaway.mapper.impl;

import com.example.takeaway.entity.Food;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FoodRepository extends JpaRepository<Food, Long> {

    /**
     * 根据商家ID和状态查询食品列表
     * @param businessId 商家ID
     * @param status 食品状态（0:下架, 1:上架）
     * @return 食品列表
     */
    List<Food> findByBusinessIdAndStatus(Long businessId, Integer status);

    /**
     * 根据分类ID和状态查询食品列表
     * @param categoryId 分类ID
     * @param status 食品状态
     * @return 食品列表
     */
    List<Food> findByCategoryIdAndStatus(Long categoryId, Integer status);

    /**
     * 根据商家ID查询食品列表
     * @param businessId 商家ID
     * @return 食品列表
     */
    List<Food> findByBusinessId(Long businessId);

    /**
     * 根据商家ID删除所有食品
     * @param businessId 商家ID
     */
    void deleteByBusinessId(Long businessId);
}
