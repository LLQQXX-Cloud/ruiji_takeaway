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

    @Override
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> delete(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            foodService.delete(id);
            response.put("success", true);
            response.put("message", "鍒犻櫎鎴愬姛");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}
