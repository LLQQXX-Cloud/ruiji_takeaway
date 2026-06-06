package com.example.takeaway.controller.impl;

import com.example.takeaway.entity.CartItem;
import com.example.takeaway.entity.Food;
import com.example.takeaway.mapper.FoodRepository;
import com.example.takeaway.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartControllerImpl {

    private final CartService cartService;
    private final FoodRepository foodRepository;

    @GetMapping("/user/{userId}")
    public ResponseEntity<Map<String, Object>> getCart(@PathVariable Long userId) {
        List<CartItem> items = cartService.getCart(userId);
        Double totalPrice = cartService.getTotalPrice(userId);
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("data", items);
        result.put("totalPrice", totalPrice);
        
        return ResponseEntity.ok(result);
    }

    @PostMapping("/add")
    public ResponseEntity<Map<String, Object>> addItem(@RequestBody Map<String, Object> data) {
        Object userIdObj = data.get("userId");
        Object foodIdObj = data.get("foodId");
        Object businessIdObj = data.get("businessId");
        Object quantityObj = data.get("quantity");
        
        if (userIdObj == null || foodIdObj == null || businessIdObj == null) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "参数不完整");
            return ResponseEntity.badRequest().body(result);
        }
        
        Long userId = ((Number) userIdObj).longValue();
        Long foodId = ((Number) foodIdObj).longValue();
        Long businessId = ((Number) businessIdObj).longValue();
        Integer quantity = quantityObj != null ? ((Number) quantityObj).intValue() : 1;
        
        Food food = foodRepository.findById(foodId).orElse(null);
        if (food == null) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "商品不存在");
            return ResponseEntity.badRequest().body(result);
        }
        
        CartItem item = new CartItem();
        item.setFoodId(foodId);
        item.setBusinessId(businessId);
        item.setFoodName(food.getName());
        item.setPrice(food.getPrice().doubleValue());
        item.setQuantity(quantity);
        item.setImage(food.getImage());
        
        cartService.addItem(userId, item);
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "添加成功");
        
        return ResponseEntity.ok(result);
    }

    @PostMapping("/update")
    public ResponseEntity<Map<String, Object>> updateQuantity(@RequestBody Map<String, Object> data) {
        Object userIdObj = data.get("userId");
        Object foodIdObj = data.get("foodId");
        Object quantityObj = data.get("quantity");
        
        if (userIdObj == null || foodIdObj == null || quantityObj == null) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "参数不完整");
            return ResponseEntity.badRequest().body(result);
        }
        
        Long userId = ((Number) userIdObj).longValue();
        Long foodId = ((Number) foodIdObj).longValue();
        Integer quantity = ((Number) quantityObj).intValue();
        
        cartService.updateQuantity(userId, foodId, quantity);
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "更新成功");
        
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/remove")
    public ResponseEntity<Map<String, Object>> removeItem(
            @RequestParam Long userId,
            @RequestParam Long foodId) {
        cartService.removeItem(userId, foodId);
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "删除成功");
        
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/clear")
    public ResponseEntity<Map<String, Object>> clearCart(@RequestParam Long userId) {
        cartService.clearCart(userId);
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "清空成功");
        
        return ResponseEntity.ok(result);
    }
}
