/**
 * Address服务实现类
 * 实现 Address服务 接口定义的方法
 */

package com.example.takeaway.service.impl;

import com.example.takeaway.entity.Address;
import com.example.takeaway.mapper.impl.AddressRepository;
import com.example.takeaway.service.Service.AddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {
    
    private final AddressRepository addressMapper;
    
    @Override
    public Address findById(Long id) {
        return addressMapper.findById(id).orElse(null);
    }
    
    @Override
    public List<Address> findByUserId(Long userId) {
        return addressMapper.findByUserId(userId);
    }
    
    @Override
    public Address findDefaultByUserId(Long userId) {
        return addressMapper.findByUserIdAndIsDefaultTrue(userId);
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
        return addressMapper.save(address);
    }
    
    @Override
    public Address updateAddress(Address address) {
        Address existing = addressMapper.findById(address.getId()).orElse(null);
        if (existing != null && !existing.getUserId().equals(address.getUserId())) {
            throw new RuntimeException("无权修改此地址");
        }
        if (address.getIsDefault() != null && address.getIsDefault()) {
            clearDefault(address.getUserId());
        }
        return addressMapper.save(address);
    }
    
    @Override
    public void deleteAddress(Long id) {
        addressMapper.deleteById(id);
    }
    
    private void clearDefault(Long userId) {
        Address defaultAddr = addressMapper.findByUserIdAndIsDefaultTrue(userId);
        if (defaultAddr != null) {
            defaultAddr.setIsDefault(false);
            addressMapper.save(defaultAddr);
        }
    }
}