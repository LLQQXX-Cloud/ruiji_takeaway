package com.example.takeaway.controller.impl;

import com.example.takeaway.entity.CartItem;
import com.example.takeaway.entity.Food;
import com.example.takeaway.mapper.impl.FoodRepository;
import com.example.takeaway.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 购物车控制器
 * 处理购物车相关的 CRUD 操作
 */
@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartControllerImpl {

    private final CartService cartService;
    private final FoodRepository foodRepository;

    /**
     * 获取用户购物车
     * @param userId 用户ID
     * @return 购物车商品列表和总价
     */
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

    /**
     * 添加商品到购物车
     * @param data 包含 userId、foodId、businessId、quantity 的数据
     * @return 添加结果
     */
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

    /**
     * 更新购物车商品数量
     * @param data 包含 userId、foodId、quantity 的数据
     * @return 更新结果
     */
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

    /**
     * 从购物车移除商品
     * @param userId 用户ID
     * @param foodId 商品ID
     * @return 移除结果
     */
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

    /**
     * 清空购物车
     * @param userId 用户ID
     * @return 清空结果
     */
    @DeleteMapping("/clear")
    public ResponseEntity<Map<String, Object>> clearCart(@RequestParam Long userId) {
        cartService.clearCart(userId);
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "清空成功");
        
        return ResponseEntity.ok(result);
    }
}
