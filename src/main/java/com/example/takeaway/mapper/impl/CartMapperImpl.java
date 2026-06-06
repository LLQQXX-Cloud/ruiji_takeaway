/**
 * Cart数据映射实现类
 * 实现 Cart数据映射 接口定义的方法
 */

package com.example.takeaway.mapper.impl;

import com.example.takeaway.entity.Cart;
import com.example.takeaway.mapper.Mapper.CartMapper;
import com.example.takeaway.mapper.CartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class CartMapperImpl implements CartMapper {

    @Autowired
    private CartRepository cartRepository;

    @Override
    public Cart save(Cart cart) {
        return cartRepository.save(cart);
    }

    @Override
    public void deleteById(Long id) {
        cartRepository.deleteById(id);
    }

    @Override
    public List<Cart> findByUserId(Long userId) {
        return cartRepository.findByUserId(userId);
    }

    @Override
    public List<Cart> findByUserIdAndBusinessId(Long userId, Long businessId) {
        return cartRepository.findByUserIdAndBusinessId(userId, businessId);
    }

    @Override
    public Optional<Cart> findByUserIdAndFoodId(Long userId, Long foodId) {
        return cartRepository.findByUserIdAndFoodId(userId, foodId);
    }

    @Override
    public void deleteByUserId(Long userId) {
        cartRepository.deleteByUserId(userId);
    }

    @Override
    public void deleteByUserIdAndFoodId(Long userId, Long foodId) {
        cartRepository.deleteByUserIdAndFoodId(userId, foodId);
    }
}
