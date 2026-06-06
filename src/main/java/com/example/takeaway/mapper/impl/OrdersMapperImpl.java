/**
 * Orders数据映射实现类
 * 实现 Orders数据映射 接口定义的方法
 */

package com.example.takeaway.mapper.impl;

import com.example.takeaway.entity.Orders;
import com.example.takeaway.mapper.Mapper.OrdersMapper;
import com.example.takeaway.mapper.OrdersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public class OrdersMapperImpl implements OrdersMapper {

    @Autowired
    private OrdersRepository ordersRepository;

    @Override
    public Orders save(Orders orders) {
        return ordersRepository.save(orders);
    }

    @Override
    public void deleteById(Long id) {
        ordersRepository.deleteById(id);
    }

    @Override
    public Optional<Orders> findById(Long id) {
        return ordersRepository.findById(id);
    }

    @Override
    public Optional<Orders> findByOrderNumber(String orderNumber) {
        return ordersRepository.findByOrderNumber(orderNumber);
    }

    @Override
    public List<Orders> findByUserIdOrderByCreateTimeDesc(Long userId) {
        return ordersRepository.findByUserIdOrderByCreateTimeDesc(userId);
    }

    @Override
    public List<Orders> findByBusinessIdOrderByCreateTimeDesc(Long businessId) {
        return ordersRepository.findByBusinessIdOrderByCreateTimeDesc(businessId);
    }

    @Override
    public List<Orders> findByStatusOrderByCreateTimeDesc(Integer status) {
        return ordersRepository.findByStatusOrderByCreateTimeDesc(status);
    }
    
    @Override
    public List<Orders> findByUserIdAndNotDeletedByUser(Long userId) {
        return ordersRepository.findByUserIdAndNotDeletedByUser(userId);
    }
    
    @Override
    public List<Orders> findByBusinessIdAndNotDeletedByBusiness(Long businessId) {
        return ordersRepository.findByBusinessIdAndNotDeletedByBusiness(businessId);
    }
    
    @Override
    @Transactional
    public void softDeleteByUser(Long orderId) {
        ordersRepository.softDeleteByUser(orderId);
    }
    
    @Override
    @Transactional
    public void softDeleteByBusiness(Long orderId) {
        ordersRepository.softDeleteByBusiness(orderId);
    }
}
