/**
 * OrderItem数据映射实现类
 * 提供订单项的数据访问操作
 */

package com.example.takeaway.mapper.impl;

import com.example.takeaway.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    /**
     * 根据订单ID查询订单项列表
     * @param orderId 订单ID
     * @return 订单项列表
     */
    List<OrderItem> findByOrderId(Long orderId);

    /**
     * 根据订单ID删除订单项
     * @param orderId 订单ID
     */
    void deleteByOrderId(Long orderId);
}
