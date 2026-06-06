package com.example.takeaway.service;

import com.example.takeaway.entity.CartItem;

import java.util.List;

public interface CartService {
    
    void addItem(Long userId, CartItem item);
    
    void removeItem(Long userId, Long foodId);
    
    void updateQuantity(Long userId, Long foodId, Integer quantity);
    
    List<CartItem> getCart(Long userId);
    
    void clearCart(Long userId);
    
    Double getTotalPrice(Long userId);
}
