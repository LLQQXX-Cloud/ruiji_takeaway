/**
 * User控制器实现类
 * 实现 User控制器 接口定义的方法
 */

package com.example.takeaway.controller.impl;

import com.example.takeaway.controller.Controller.UserController;
import com.example.takeaway.entity.User;
import com.example.takeaway.service.CacheService;
import com.example.takeaway.service.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 用户控制器
 * 处理用户相关的 CRUD 操作，包括注册、登录等
 */
@RestController
@RequestMapping("/api/users")
public class UserControllerImpl implements UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private CacheService cacheService;

    private static final String TOKEN_PREFIX = "token:";
    private static final long TOKEN_TTL_HOURS = 24;

    /**
     * 用户注册
     * @param user 用户注册信息
     * @return 注册结果，包含用户信息
     */
    @Override
    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody User user) {
        Map<String, Object> response = new HashMap<>();
        try {
            User created = userService.register(user);
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
     * 用户登录
     * @param loginData 登录信息（username, password）
     * @return 登录结果，包含 token 和用户信息
     */
    @Override
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> loginData) {
        Map<String, Object> response = new HashMap<>();
        try {
            User user = userService.login(loginData.get("username"), loginData.get("password"));
            // 生成 token 并存入 Redis
            String token = UUID.randomUUID().toString().replace("-", "");
            Map<String, Object> sessionData = new HashMap<>();
            sessionData.put("userId", user.getId());
            sessionData.put("username", user.getUsername());
            sessionData.put("role", "user");
            cacheService.set(TOKEN_PREFIX + token, sessionData, TOKEN_TTL_HOURS, TimeUnit.HOURS);

            response.put("success", true);
            response.put("data", user);
            response.put("token", token);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 根据ID获取用户信息
     * @param id 用户ID
     * @return 用户信息
     */
    @Override
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getUser(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            User user = userService.findById(id);
            response.put("success", true);
            response.put("data", user);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 更新用户信息
     * @param id 用户ID
     * @param user 更新后的用户信息
     * @return 更新结果
     */
    @Override
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateUser(@PathVariable Long id, @RequestBody User user) {
        Map<String, Object> response = new HashMap<>();
        try {
            user.setId(id);
            User updated = userService.update(user);
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
     * 删除用户
     * @param id 用户ID
     * @return 删除结果
     */
    @Override
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteUser(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            userService.delete(id);
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
