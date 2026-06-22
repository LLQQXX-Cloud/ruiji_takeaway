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

    /**
     * 创建订单
     * @param order 订单信息
     * @return 创建结果
     */
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

    /**
     * 根据ID获取订单详情
     * @param id 订单ID
     * @return 订单详情
     */
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

    /**
     * 根据订单号获取订单
     * @param orderNumber 订单号
     * @return 订单详情
     */
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

    /**
     * 获取用户订单列表
     * @param userId 用户ID
     * @return 用户订单列表（排除已被用户删除的）
     */
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

    /**
     * 获取商家订单列表
     * @param businessId 商家ID
     * @return 商家订单列表（排除已被商家删除的）
     */
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

    /**
     * 更新订单状态
     * @param id 订单ID
     * @param data 包含状态的 Map
     * @return 更新后的订单
     */
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
    
    /**
     * 用户申请取消订单
     * 用户在待接单期间取消订单需要商家同意
     * @param id 订单ID
     * @return 更新后的订单
     */
    @PutMapping("/{id}/cancel/apply")
    public ResponseEntity<Map<String, Object>> applyCancelByUser(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            Orders order = orderService.applyCancelByUser(id);
            response.put("success", true);
            response.put("data", order);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * 商家同意用户取消订单
     * @param id 订单ID
     * @return 更新后的订单
     */
    @PutMapping("/{id}/cancel/approve")
    public ResponseEntity<Map<String, Object>> approveCancelByBusiness(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            Orders order = orderService.approveCancelByBusiness(id);
            response.put("success", true);
            response.put("data", order);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * 商家拒绝用户取消订单
     * @param id 订单ID
     * @return 更新后的订单
     */
    @PutMapping("/{id}/cancel/reject")
    public ResponseEntity<Map<String, Object>> rejectCancelByBusiness(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            Orders order = orderService.rejectCancelByBusiness(id);
            response.put("success", true);
            response.put("data", order);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * 商家取消订单
     * @param id 订单ID
     * @return 更新后的订单
     */
    @PutMapping("/{id}/cancel/business")
    public ResponseEntity<Map<String, Object>> cancelByBusiness(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            Orders order = orderService.cancelByBusiness(id);
            response.put("success", true);
            response.put("data", order);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 更新订单信息
     * @param id 订单ID
     * @param order 更新后的订单信息
     * @return 更新结果
     */
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

    /**
     * 删除订单（物理删除）
     * @param id 订单ID
     * @return 删除结果
     */
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
    
    /**
     * 用户软删除订单（从用户订单列表中移除）
     * @param orderId 订单ID
     * @return 删除结果
     */
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
    
    /**
     * 商家软删除订单（从商家订单列表中移除）
     * @param orderId 订单ID
     * @return 删除结果
     */
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
