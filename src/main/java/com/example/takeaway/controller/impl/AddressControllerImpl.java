/**
 * Address控制器实现类
 * 实现 Address控制器 接口定义的方法
 */

package com.example.takeaway.controller.impl;

import com.example.takeaway.controller.Controller.AddressController;
import com.example.takeaway.entity.Address;
import com.example.takeaway.service.Service.AddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class AddressControllerImpl implements AddressController {
    
    private final AddressService addressService;
    
    /**
     * 根据用户ID获取地址列表
     * @param userId 用户ID
     * @return 地址列表
     */
    @Override
    public ResponseEntity<Map<String, Object>> getAddresses(Long userId) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Address> addresses = addressService.findByUserId(userId);
            response.put("success", true);
            response.put("data", addresses);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * 获取用户默认地址
     * @param userId 用户ID
     * @return 默认地址
     */
    @Override
    public ResponseEntity<Map<String, Object>> getDefaultAddress(Long userId) {
        Map<String, Object> response = new HashMap<>();
        try {
            Address address = addressService.findDefaultByUserId(userId);
            if (address != null) {
                response.put("success", true);
                response.put("data", address);
            } else {
                response.put("success", false);
                response.put("message", "默认地址不存在");
            }
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * 根据ID获取单个地址
     * @param id 地址ID
     * @return 地址信息
     */
    @Override
    public ResponseEntity<Map<String, Object>> getAddress(Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            Address address = addressService.findById(id);
            if (address != null) {
                response.put("success", true);
                response.put("data", address);
            } else {
                response.put("success", false);
                response.put("message", "地址不存在");
            }
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * 添加新地址
     * @param address 地址信息
     * @return 创建成功的地址（含ID）
     */
    @Override
    public ResponseEntity<Map<String, Object>> addAddress(Address address) {
        Map<String, Object> response = new HashMap<>();
        try {
            Address created = addressService.addAddress(address);
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
     * 更新地址信息
     * @param id 地址ID
     * @param address 更新后的地址信息
     * @return 更新后的地址
     */
    @Override
    public ResponseEntity<Map<String, Object>> updateAddress(Long id, Address address) {
        Map<String, Object> response = new HashMap<>();
        try {
            address.setId(id);
            Address updated = addressService.updateAddress(address);
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
     * 删除地址
     * @param id 地址ID
     * @return 删除结果
     */
    @Override
    public ResponseEntity<Map<String, Object>> deleteAddress(Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            addressService.deleteAddress(id);
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