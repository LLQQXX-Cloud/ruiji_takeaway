/**
 * Order控制器实现类
 * 实现 Order控制器 接口定义的方法
 */

package com.example.takeaway.controller.impl;

import com.example.takeaway.controller.Controller.OrderController;
import com.example.takeaway.entity.Orders;
import com.example.takeaway.service.Service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
public class OrderControllerImpl implements OrderController {

    @Autowired
    private OrderService orderService;

    @Override
    @PostMapping
    public ResponseEntity<Map<String, Object>> create(@RequestBody Orders order) {
        Map<String, Object> response = new HashMap<>();
        try {
            Orders created = orderService.create(order);
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
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getById(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            Orders order = orderService.findById(id);
            response.put("success", true);
            response.put("data", order);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @Override
    @GetMapping("/number/{orderNumber}")
    public ResponseEntity<Map<String, Object>> getByOrderNumber(@PathVariable String orderNumber) {
        Map<String, Object> response = new HashMap<>();
        try {
            Orders order = orderService.findByOrderNumber(orderNumber);
            response.put("success", true);
            response.put("data", order);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @Override
    @GetMapping("/user/{userId}")
    public ResponseEntity<Map<String, Object>> getByUser(@PathVariable Long userId) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Orders> orders = orderService.findByUserIdActive(userId);
            response.put("success", true);
            response.put("data", orders);
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
            List<Orders> orders = orderService.findByBusinessIdActive(businessId);
            response.put("success", true);
            response.put("data", orders);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @Override
    @PutMapping("/{id}/status")
    public ResponseEntity<Map<String, Object>> updateStatus(@PathVariable Long id, @RequestBody Map<String, Integer> data) {
        Map<String, Object> response = new HashMap<>();
        try {
            Orders order = orderService.updateStatus(id, data.get("status"));
            response.put("success", true);
            response.put("data", order);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @Override
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> update(@PathVariable Long id, @RequestBody Orders order) {
        Map<String, Object> response = new HashMap<>();
        try {
            order.setId(id);
            Orders updated = orderService.update(order);
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
            orderService.delete(id);
            response.put("success", true);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @Override
    @DeleteMapping("/{orderId}/user")
    public ResponseEntity<Map<String, Object>> deleteByUser(@PathVariable Long orderId) {
        Map<String, Object> response = new HashMap<>();
        try {
            orderService.deleteByUser(orderId);
            response.put("success", true);
            response.put("message", "订单已从您的订单列表中删除");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @Override
    @DeleteMapping("/{orderId}/business")
    public ResponseEntity<Map<String, Object>> deleteByBusiness(@PathVariable Long orderId) {
        Map<String, Object> response = new HashMap<>();
        try {
            orderService.deleteByBusiness(orderId);
            response.put("success", true);
            response.put("message", "订单已从您的订单列表中删除");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}
