package com.example.takeaway.service.impl;

import com.example.takeaway.entity.Cart;
import com.example.takeaway.entity.CartItem;
import com.example.takeaway.entity.Food;
import com.example.takeaway.mapper.CartRepository;
import com.example.takeaway.mapper.FoodRepository;
import com.example.takeaway.service.CartService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private static final String CART_KEY_PREFIX = "cart:user:";
    private static final long CART_EXPIRE_DAYS = 7;

    private final StringRedisTemplate stringRedisTemplate;
    private final ObjectMapper objectMapper;
    private final CartRepository cartRepository;
    private final FoodRepository foodRepository;

    @Override
    @Transactional
    public void addItem(Long userId, CartItem item) {
        String key = CART_KEY_PREFIX + userId;
        
        try {
            // 1. 更新MySQL数据库
            Optional<Cart> existingCart = cartRepository.findByUserIdAndFoodId(userId, item.getFoodId());
            if (existingCart.isPresent()) {
                Cart cart = existingCart.get();
                cart.setQuantity(cart.getQuantity() + item.getQuantity());
                cartRepository.save(cart);
            } else {
                Cart cart = new Cart();
                cart.setUserId(userId);
                cart.setFoodId(item.getFoodId());
                cart.setBusinessId(item.getBusinessId());
                cart.setQuantity(item.getQuantity());
                cartRepository.save(cart);
            }
            
            // 2. 更新Redis缓存
            List<CartItem> cartItems = getCartFromDatabase(userId);
            String json = objectMapper.writeValueAsString(cartItems);
            stringRedisTemplate.opsForValue().set(key, json, CART_EXPIRE_DAYS, TimeUnit.DAYS);
            
        } catch (Exception e) {
            log.error("Failed to add item to cart: userId={}, foodId={}", userId, item.getFoodId(), e);
            throw new RuntimeException("添加购物车失败");
        }
    }

    @Override
    @Transactional
    public void removeItem(Long userId, Long foodId) {
        String key = CART_KEY_PREFIX + userId;
        
        try {
            // 1. 从MySQL删除
            cartRepository.deleteByUserIdAndFoodId(userId, foodId);
            
            // 2. 从Redis删除
            List<CartItem> cartItems = getCartFromDatabase(userId);
            if (cartItems.isEmpty()) {
                stringRedisTemplate.delete(key);
            } else {
                String json = objectMapper.writeValueAsString(cartItems);
                stringRedisTemplate.opsForValue().set(key, json, CART_EXPIRE_DAYS, TimeUnit.DAYS);
            }
            
        } catch (Exception e) {
            log.error("Failed to remove item from cart: userId={}, foodId={}", userId, foodId, e);
            throw new RuntimeException("删除购物车商品失败");
        }
    }

    @Override
    @Transactional
    public void updateQuantity(Long userId, Long foodId, Integer quantity) {
        String key = CART_KEY_PREFIX + userId;
        
        try {
            if (quantity <= 0) {
                removeItem(userId, foodId);
                return;
            }
            
            // 1. 更新MySQL
            Optional<Cart> existingCart = cartRepository.findByUserIdAndFoodId(userId, foodId);
            if (existingCart.isPresent()) {
                Cart cart = existingCart.get();
                cart.setQuantity(quantity);
                cartRepository.save(cart);
            }
            
            // 2. 更新Redis
            List<CartItem> cartItems = getCartFromDatabase(userId);
            String json = objectMapper.writeValueAsString(cartItems);
            stringRedisTemplate.opsForValue().set(key, json, CART_EXPIRE_DAYS, TimeUnit.DAYS);
            
        } catch (Exception e) {
            log.error("Failed to update cart quantity: userId={}, foodId={}", userId, foodId, e);
            throw new RuntimeException("更新购物车数量失败");
        }
    }

    @Override
    public List<CartItem> getCart(Long userId) {
        String key = CART_KEY_PREFIX + userId;
        
        try {
            // 1. 先从Redis获取
            String json = stringRedisTemplate.opsForValue().get(key);
            
            if (json != null && !json.isEmpty()) {
                return objectMapper.readValue(json, new TypeReference<List<CartItem>>() {});
            }
            
            // 2. Redis为空，从数据库加载并同步到Redis
            List<CartItem> cartItems = getCartFromDatabase(userId);
            if (!cartItems.isEmpty()) {
                json = objectMapper.writeValueAsString(cartItems);
                stringRedisTemplate.opsForValue().set(key, json, CART_EXPIRE_DAYS, TimeUnit.DAYS);
            }
            
            return cartItems;
            
        } catch (Exception e) {
            log.error("Failed to get cart: userId={}", userId, e);
            // 如果Redis出错，直接从数据库获取
            return getCartFromDatabase(userId);
        }
    }

    @Override
    @Transactional
    public void clearCart(Long userId) {
        String key = CART_KEY_PREFIX + userId;
        
        try {
            // 1. 清空MySQL
            cartRepository.deleteByUserId(userId);
            
            // 2. 清空Redis
            stringRedisTemplate.delete(key);
            
        } catch (Exception e) {
            log.error("Failed to clear cart: userId={}", userId, e);
            throw new RuntimeException("清空购物车失败");
        }
    }

    @Override
    public Double getTotalPrice(Long userId) {
        List<CartItem> cart = getCart(userId);
        
        return cart.stream()
                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum();
    }
    
    /**
     * 从数据库加载购物车数据并转换为CartItem列表
     */
    private List<CartItem> getCartFromDatabase(Long userId) {
        List<Cart> carts = cartRepository.findByUserId(userId);
        
        return carts.stream()
                .map(cart -> {
                    CartItem item = new CartItem();
                    item.setId(cart.getId());
                    item.setUserId(cart.getUserId());
                    item.setFoodId(cart.getFoodId());
                    item.setBusinessId(cart.getBusinessId());
                    item.setQuantity(cart.getQuantity());
                    
                    // 从数据库获取商品详情
                    Optional<Food> food = foodRepository.findById(cart.getFoodId());
                    if (food.isPresent()) {
                        item.setFoodName(food.get().getName());
                        item.setPrice(food.get().getPrice().doubleValue());
                        item.setImage(food.get().getImage());
                    }
                    
                    return item;
                })
                .collect(Collectors.toList());
    }
    
    /**
     * 登录时从数据库加载购物车到Redis
     */
    public void loadCartFromDatabaseToRedis(Long userId) {
        String key = CART_KEY_PREFIX + userId;
        
        try {
            List<CartItem> cartItems = getCartFromDatabase(userId);
            if (!cartItems.isEmpty()) {
                String json = objectMapper.writeValueAsString(cartItems);
                stringRedisTemplate.opsForValue().set(key, json, CART_EXPIRE_DAYS, TimeUnit.DAYS);
                log.info("Loaded cart from database to Redis for userId={}", userId);
            }
        } catch (Exception e) {
            log.error("Failed to load cart from database to Redis: userId={}", userId, e);
        }
    }
}