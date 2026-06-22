/**
 * Business控制器实现类
 * 实现 Business控制器 接口定义的方法
 */

package com.example.takeaway.controller.impl;

import com.example.takeaway.controller.Controller.BusinessController;
import com.example.takeaway.entity.Business;
import com.example.takeaway.service.CacheService;
import com.example.takeaway.service.Service.BusinessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/businesses")
public class BusinessControllerImpl implements BusinessController {

    @Autowired
    private BusinessService businessService;

    @Autowired
    private CacheService cacheService;

    private static final String TOKEN_PREFIX = "token:";
    private static final long TOKEN_TTL_HOURS = 24;

    /**
     * 创建商家
     * @param business 商家信息
     * @return 创建结果，包含成功状态和商家数据
     */
    @Override
    @PostMapping
    public ResponseEntity<Map<String, Object>> create(@RequestBody Business business) {
        Map<String, Object> response = new HashMap<>();
        try {
            Business created = businessService.create(business);
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
     * 获取商家列表
     * @param name 商家名称（可选，用于模糊搜索）
     * @return 商家列表
     */
    @Override
    @GetMapping
    public ResponseEntity<Map<String, Object>> list(@RequestParam(required = false) String name) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Business> businesses;
            if (name != null && !name.isEmpty()) {
                businesses = businessService.searchByName(name);
            } else {
                businesses = businessService.findAll();
            }
            response.put("success", true);
            response.put("data", businesses);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 高级搜索商家
     * @param keyword 关键词（搜索名称）
     * @param categoryId 分类ID
     * @param status 状态
     * @return 搜索结果列表
     */
    @Override
    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> search(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Integer status) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Business> businesses = businessService.search(keyword, categoryId, status);
            response.put("success", true);
            response.put("data", businesses);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 根据ID获取商家详情
     * @param id 商家ID
     * @return 商家详情
     */
    @Override
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getById(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            Business business = businessService.findById(id);
            response.put("success", true);
            response.put("data", business);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 更新商家信息
     * @param id 商家ID
     * @param business 更新后的商家信息
     * @return 更新结果
     */
    @Override
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> update(@PathVariable Long id, @RequestBody Business business) {
        Map<String, Object> response = new HashMap<>();
        try {
            business.setId(id);
            Business updated = businessService.update(business);
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
     * 删除商家
     * @param id 商家ID
     * @return 删除结果
     */
    @Override
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> delete(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            businessService.delete(id);
            response.put("success", true);
            response.put("message", "删除成功");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 商家登录
     * @param loginData 登录信息（username, password）
     * @return 登录结果，包含商家信息
     */
    @Override
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> loginData) {
        Map<String, Object> response = new HashMap<>();
        try {
            String username = loginData.get("username");
            String password = loginData.get("password");
            Business business = businessService.login(username, password);
            // 生成 token 并存入 Redis
            String token = UUID.randomUUID().toString().replace("-", "");
            Map<String, Object> sessionData = new HashMap<>();
            sessionData.put("businessId", business.getId());
            sessionData.put("username", business.getUsername());
            sessionData.put("role", "business");
            cacheService.set(TOKEN_PREFIX + token, sessionData, TOKEN_TTL_HOURS, TimeUnit.HOURS);

            response.put("success", true);
            response.put("data", business);
            response.put("token", token);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 商家注册
     * @param business 商家注册信息
     * @return 注册结果，包含创建的商家信息
     */
    @Override
    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody Business business) {
        Map<String, Object> response = new HashMap<>();
        try {
            Business registered = businessService.register(business);
            response.put("success", true);
            response.put("data", registered);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}
