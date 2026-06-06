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

import java.util.List;

@RestController
@RequiredArgsConstructor
public class AddressControllerImpl implements AddressController {
    
    private final AddressService addressService;
    
    @Override
    public ResponseEntity<List<Address>> getAddresses(Long userId) {
        return ResponseEntity.ok(addressService.findByUserId(userId));
    }
    
    @Override
    public ResponseEntity<Address> getDefaultAddress(Long userId) {
        Address address = addressService.findDefaultByUserId(userId);
        return address != null ? ResponseEntity.ok(address) : ResponseEntity.notFound().build();
    }
    
    @Override
    public ResponseEntity<Address> getAddress(Long id) {
        Address address = addressService.findById(id);
        return address != null ? ResponseEntity.ok(address) : ResponseEntity.notFound().build();
    }
    
    @Override
    public ResponseEntity<Address> addAddress(Address address) {
        return ResponseEntity.ok(addressService.addAddress(address));
    }
    
    @Override
    public ResponseEntity<Address> updateAddress(Long id, Address address) {
        address.setId(id);
        return ResponseEntity.ok(addressService.updateAddress(address));
    }
    
    @Override
    public ResponseEntity<Void> deleteAddress(Long id) {
        addressService.deleteAddress(id);
        return ResponseEntity.noContent().build();
    }
}