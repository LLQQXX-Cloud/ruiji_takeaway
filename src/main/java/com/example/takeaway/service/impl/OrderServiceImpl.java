/**
 * Order服务实现类
 * 实现 Order服务 接口定义的方法
 */

package com.example.takeaway.service.impl;

import com.example.takeaway.entity.Orders;
import com.example.takeaway.mapper.Mapper.OrdersMapper;
import com.example.takeaway.service.Service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrdersMapper ordersMapper;

    @Override
    public Orders create(Orders order) {
        return ordersMapper.save(order);
    }

    @Override
    public Orders update(Orders order) {
        Orders existing = findById(order.getId());
        if (order.getAddress() != null) existing.setAddress(order.getAddress());
        if (order.getContact() != null) existing.setContact(order.getContact());
        if (order.getPhone() != null) existing.setPhone(order.getPhone());
        if (order.getRemark() != null) existing.setRemark(order.getRemark());
        if (order.getStatus() != null) existing.setStatus(order.getStatus());
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
