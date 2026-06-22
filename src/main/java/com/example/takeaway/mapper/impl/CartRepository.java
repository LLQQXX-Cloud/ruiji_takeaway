/**
 * Cart数据映射实现类
 * 提供购物车的数据访问操作
 */

package com.example.takeaway.mapper.impl;

import com.example.takeaway.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {

    /**
     * 根据用户ID查询购物车列表
     * @param userId 用户ID
     * @return 购物车列表
     */
    List<Cart> findByUserId(Long userId);

    /**
     * 根据用户ID和商家ID查询购物车列表
     * @param userId 用户ID
     * @param businessId 商家ID
     * @return 购物车列表
     */
    List<Cart> findByUserIdAndBusinessId(Long userId, Long businessId);

    /**
     * 根据用户ID和食品ID查询购物车项
     * @param userId 用户ID
     * @param foodId 食品ID
     * @return 购物车项
     */
    Optional<Cart> findByUserIdAndFoodId(Long userId, Long foodId);

    /**
     * 根据用户ID清空购物车
     * @param userId 用户ID
     */
    void deleteByUserId(Long userId);

    /**
     * 根据用户ID和食品ID删除购物车项
     * @param userId 用户ID
     * @param foodId 食品ID
     */
    void deleteByUserIdAndFoodId(Long userId, Long foodId);
}
