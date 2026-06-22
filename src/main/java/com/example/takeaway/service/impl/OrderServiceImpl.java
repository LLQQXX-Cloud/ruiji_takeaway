/**
 * Order服务实现类
 * 实现 Order服务 接口定义的方法
 */

package com.example.takeaway.service.impl;

import com.example.takeaway.entity.OrderItem;
import com.example.takeaway.entity.Orders;
import com.example.takeaway.mapper.impl.OrderItemRepository;
import com.example.takeaway.mapper.impl.OrdersRepository;
import com.example.takeaway.service.Service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrdersRepository ordersMapper;

    @Autowired
    private OrderItemRepository orderItemMapper;

    @Override
    public Orders create(Orders order) {
        // 先清空订单商品列表，保存订单获取ID
        List<OrderItem> items = order.getOrderItems();
        order.setOrderItems(null);
        
        // 保存订单
        Orders savedOrder = ordersMapper.save(order);
        
        // 保存订单商品
        if (items != null && !items.isEmpty()) {
            for (OrderItem item : items) {
                item.setOrder(savedOrder);
                orderItemMapper.save(item);
            }
            savedOrder.setOrderItems(items);
        }
        
        return savedOrder;
    }

    @Override
    public Orders update(Orders order) {
        Orders existing = findById(order.getId());
        if (order.getAddress() != null) existing.setAddress(order.getAddress());
        if (order.getContact() != null) existing.setContact(order.getContact());
        if (order.getPhone() != null) existing.setPhone(order.getPhone());
        if (order.getRemark() != null) existing.setRemark(order.getRemark());
        if (order.getStatus() != null) existing.setStatus(order.getStatus());
        if (order.getReviewed() != null) existing.setReviewed(order.getReviewed());
        return ordersMapper.save(existing);
    }

    @Override
    public Orders findById(Long id) {
        return ordersMapper.findById(id)
                .orElseThrow(() -> new RuntimeException("订单不存在"));
    }

    @Override
    public Orders findByOrderNumber(String orderNumber) {
        return ordersMapper.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new RuntimeException("订单不存在"));
    }

    @Override
    public List<Orders> findByUserId(Long userId) {
        return ordersMapper.findByUserIdOrderByCreateTimeDesc(userId);
    }

    @Override
    public List<Orders> findByBusinessId(Long businessId) {
        return ordersMapper.findByBusinessIdOrderByCreateTimeDesc(businessId);
    }

    @Override
    public List<Orders> findByStatus(Integer status) {
        return ordersMapper.findByStatusOrderByCreateTimeDesc(status);
    }

    @Override
    public Orders updateStatus(Long id, Integer status) {
        Orders order = findById(id);
        order.setStatus(status);
        // 如果状态变为已取消(4)，但还没有记录取消者，则设置为用户取消(默认)
        if (status == 4 && order.getCancelledBy() == null) {
            order.setCancelledBy(1); // 默认设为用户取消
        }
        return ordersMapper.save(order);
    }
    
    /**
     * 用户申请取消订单
     * 用户在待接单期间取消订单需要商家同意
     * @param id 订单ID
     * @return 更新后的订单
     */
    public Orders applyCancelByUser(Long id) {
        Orders order = findById(id);
        // 只有待接单状态(0)才能申请取消
        if (order.getStatus() != 0) {
            throw new RuntimeException("只有待接单的订单才能申请取消");
        }
        order.setStatus(5); // 5表示用户申请取消中
        order.setCancelledBy(1); // 1表示用户发起的取消申请
        return ordersMapper.save(order);
    }
    
    /**
     * 商家同意用户取消订单
     * @param id 订单ID
     * @return 更新后的订单
     */
    public Orders approveCancelByBusiness(Long id) {
        Orders order = findById(id);
        // 只有用户申请取消中状态(5)才能同意取消
        if (order.getStatus() != 5) {
            throw new RuntimeException("订单状态不允许此操作");
        }
        order.setStatus(4); // 4表示已取消
        order.setCancelledBy(1); // 1表示用户取消（商家同意）
        return ordersMapper.save(order);
    }
    
    /**
     * 商家拒绝用户取消订单
     * @param id 订单ID
     * @return 更新后的订单
     */
    public Orders rejectCancelByBusiness(Long id) {
        Orders order = findById(id);
        // 只有用户申请取消中状态(5)才能拒绝取消
        if (order.getStatus() != 5) {
            throw new RuntimeException("订单状态不允许此操作");
        }
        order.setStatus(0); // 恢复为待接单状态
        order.setCancelledBy(0); // 重置取消者
        return ordersMapper.save(order);
    }
    
    /**
     * 商家取消订单
     * @param id 订单ID
     * @return 更新后的订单
     */
    public Orders cancelByBusiness(Long id) {
        Orders order = findById(id);
        order.setStatus(4);
        order.setCancelledBy(2); // 2表示商家取消
        return ordersMapper.save(order);
    }

    @Override
    public void delete(Long id) {
        Orders order = findById(id);
        ordersMapper.deleteById(id);
    }
    
    @Override
    public List<Orders> findByUserIdActive(Long userId) {
        return ordersMapper.findByUserIdAndNotDeletedByUser(userId);
    }
    
    @Override
    public List<Orders> findByBusinessIdActive(Long businessId) {
        return ordersMapper.findByBusinessIdAndNotDeletedByBusiness(businessId);
    }
    
    @Override
    public void deleteByUser(Long orderId) {
        ordersMapper.softDeleteByUser(orderId);
    }
    
    @Override
    public void deleteByBusiness(Long orderId) {
        ordersMapper.softDeleteByBusiness(orderId);
    }
}
