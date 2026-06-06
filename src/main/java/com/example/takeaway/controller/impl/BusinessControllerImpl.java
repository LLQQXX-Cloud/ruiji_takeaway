/**
 * Business控制器实现类
 * 实现 Business控制器 接口定义的方法
 */

package com.example.takeaway.controller.impl;

import com.example.takeaway.controller.Controller.BusinessController;
import com.example.takeaway.entity.Business;
import com.example.takeaway.service.Service.BusinessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/businesses")
public class BusinessControllerImpl implements BusinessController {

    @Autowired
    private BusinessService businessService;

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

    @Override
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> update(@PathVariable Long id, @RequestBody Business business) {
        Map<String, Object> response = new HashMap<>();
        try {
            business.setId(id);
            System.out.println("鎺ユ敹鍒扮殑鍟嗗鏁版嵁: " + business.toString());
            System.out.println("categoryId: " + business.getCategoryId());
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

    @Override
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> delete(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            businessService.delete(id);
            response.put("success", true);
            response.put("message", "鍒犻櫎鎴愬姛");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @Override
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> loginData) {
        Map<String, Object> response = new HashMap<>();
        try {
            String username = loginData.get("username");
            String password = loginData.get("password");
            Business business = businessService.login(username, password);
            response.put("success", true);
            response.put("data", business);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

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
