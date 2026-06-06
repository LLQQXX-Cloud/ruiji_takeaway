/**
 * Address服务实现类
 * 实现 Address服务 接口定义的方法
 */

package com.example.takeaway.service.impl;

import com.example.takeaway.entity.Address;
import com.example.takeaway.mapper.Mapper.AddressMapper;
import com.example.takeaway.service.Service.AddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {
    
    private final AddressMapper addressMapper;
    
    @Override
    public Address findById(Long id) {
        return addressMapper.findById(id);
    }
    
    @Override
    public List<Address> findByUserId(Long userId) {
        return addressMapper.findByUserId(userId);
    }
    
    @Override
    public Address findDefaultByUserId(Long userId) {
        return addressMapper.findDefaultByUserId(userId);
    }
    
    @Override
    public Address addAddress(Address address) {
        if (address.getIsDefault() == null || !address.getIsDefault()) {
            if (addressMapper.countByUserId(address.getUserId()) == 0) {
                address.setIsDefault(true);
            }
        }
        if (address.getIsDefault()) {
            clearDefault(address.getUserId());
        }
        addressMapper.insert(address);
        return address;
    }
    
    @Override
    public Address updateAddress(Address address) {
        Address existing = addressMapper.findById(address.getId());
        if (existing != null && !existing.getUserId().equals(address.getUserId())) {
            throw new RuntimeException("鏃犳潈淇敼姝ゅ湴鍧€");
        }
        if (address.getIsDefault() != null && address.getIsDefault()) {
            clearDefault(address.getUserId());
        }
        addressMapper.update(address);
        return address;
    }
    
    @Override
    public void deleteAddress(Long id) {
        Address address = addressMapper.findById(id);
        if (address != null) {
            addressMapper.deleteById(id);
        }
    }
    
    private void clearDefault(Long userId) {
        Address defaultAddr = addressMapper.findDefaultByUserId(userId);
        if (defaultAddr != null) {
            defaultAddr.setIsDefault(false);
            addressMapper.update(defaultAddr);
        }
    }
}