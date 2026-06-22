/**
 * Food控制器实现类
 * 实现 Food控制器 接口定义的方法
 */

package com.example.takeaway.controller.impl;

import com.example.takeaway.controller.Controller.FoodController;
import com.example.takeaway.entity.Food;
import com.example.takeaway.service.Service.FoodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/foods")
public class FoodControllerImpl implements FoodController {

    @Autowired
    private FoodService foodService;

    /**
     * 创建商品
     * @param food 商品信息
     * @return 创建结果
     */
    @Override
    @PostMapping
    public ResponseEntity<Map<String, Object>> create(@RequestBody Food food) {
        Map<String, Object> response = new HashMap<>();
        try {
            Food created = foodService.create(food);
            response.put("success", true);
            response.put("data", created);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 根据商家ID获取商品列表
     * @param businessId 商家ID
     * @return 商品列表（只返回状态为1的商品）
     */
    @Override
    @GetMapping("/business/{businessId}")
    public ResponseEntity<Map<String, Object>> getByBusiness(@PathVariable Long businessId) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Food> foods = foodService.findByBusinessIdAndStatus(businessId, 1);
            response.put("success", true);
            response.put("data", foods);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 根据分类ID获取商品列表
     * @param categoryId 分类ID
     * @return 商品列表
     */
    @Override
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<Map<String, Object>> getByCategory(@PathVariable Long categoryId) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Food> foods = foodService.findByCategoryId(categoryId);
            response.put("success", true);
            response.put("data", foods);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 根据ID获取商品详情
     * @param id 商品ID
     * @return 商品详情
     */
    @Override
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getById(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            Food food = foodService.findById(id);
            response.put("success", true);
            response.put("data", food);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 更新商品信息
     * @param id 商品ID
     * @param food 更新后的商品信息
     * @return 更新结果
     */
    @Override
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> update(@PathVariable Long id, @RequestBody Food food) {
        Map<String, Object> response = new HashMap<>();
        try {
            food.setId(id);
            Food updated = foodService.update(food);
            response.put("success", true);
            response.put("data", updated);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 删除商品
     * @param id 商品ID
     * @return 删除结果
     */
    @Override
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> delete(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            foodService.delete(id);
            response.put("success", true);
            response.put("message", "删除成功");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}
