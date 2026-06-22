/**
 * Orders数据映射实现类
 * 提供订单的数据访问操作
 */

package com.example.takeaway.mapper.impl;

import com.example.takeaway.entity.Orders;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrdersRepository extends JpaRepository<Orders, Long> {

    /**
     * 根据订单号查询订单
     * @param orderNumber 订单号
     * @return 订单对象
     */
    Optional<Orders> findByOrderNumber(String orderNumber);

    /**
     * 根据用户ID查询订单列表，按创建时间降序排列
     * @param userId 用户ID
     * @return 订单列表
     */
    @Query("SELECT DISTINCT o FROM Orders o LEFT JOIN FETCH o.orderItems WHERE o.userId = :userId ORDER BY o.createTime DESC")
    List<Orders> findByUserIdOrderByCreateTimeDesc(@Param("userId") Long userId);

    /**
     * 根据商家ID查询订单列表，按创建时间降序排列
     * @param businessId 商家ID
     * @return 订单列表
     */
    @Query("SELECT DISTINCT o FROM Orders o LEFT JOIN FETCH o.orderItems WHERE o.businessId = :businessId ORDER BY o.createTime DESC")
    List<Orders> findByBusinessIdOrderByCreateTimeDesc(@Param("businessId") Long businessId);

    /**
     * 根据状态查询订单列表，按创建时间降序排列
     * @param status 订单状态
     * @return 订单列表
     */
    @Query("SELECT DISTINCT o FROM Orders o LEFT JOIN FETCH o.orderItems WHERE o.status = :status ORDER BY o.createTime DESC")
    List<Orders> findByStatusOrderByCreateTimeDesc(@Param("status") Integer status);

    /**
     * 根据用户ID查询未删除的订单列表（用户视角）
     * @param userId 用户ID
     * @return 订单列表
     */
    @Query("SELECT DISTINCT o FROM Orders o LEFT JOIN FETCH o.orderItems WHERE o.userId = :userId AND (o.userDeleted IS NULL OR o.userDeleted = false) ORDER BY o.createTime DESC")
    List<Orders> findByUserIdAndNotDeletedByUser(@Param("userId") Long userId);

    /**
     * 根据商家ID查询未删除的订单列表（商家视角）
     * @param businessId 商家ID
     * @return 订单列表
     */
    @Query("SELECT DISTINCT o FROM Orders o LEFT JOIN FETCH o.orderItems WHERE o.businessId = :businessId AND (o.businessDeleted IS NULL OR o.businessDeleted = false) ORDER BY o.createTime DESC")
    List<Orders> findByBusinessIdAndNotDeletedByBusiness(@Param("businessId") Long businessId);

    /**
     * 用户软删除订单（标记userDeleted为true）
     * @param orderId 订单ID
     */
    @Transactional
    @Modifying
    @Query("UPDATE Orders o SET o.userDeleted = true WHERE o.id = :orderId")
    void softDeleteByUser(@Param("orderId") Long orderId);

    /**
     * 商家软删除订单（标记businessDeleted为true）
     * @param orderId 订单ID
     */
    @Transactional
    @Modifying
    @Query("UPDATE Orders o SET o.businessDeleted = true WHERE o.id = :orderId")
    void softDeleteByBusiness(@Param("orderId") Long orderId);
}
