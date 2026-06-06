/**
 * Address数据映射实现类
 * 实现 Address数据映射 接口定义的方法
 */

package com.example.takeaway.mapper.impl;

import com.example.takeaway.entity.Address;
import com.example.takeaway.mapper.Mapper.AddressMapper;
import com.example.takeaway.mapper.AddressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class AddressMapperImpl implements AddressMapper {
    
    private final AddressRepository addressRepository;
    
    @Override
    public Address findById(Long id) {
        return addressRepository.findById(id).orElse(null);
    }
    
    @Override
    public List<Address> findByUserId(Long userId) {
        return addressRepository.findByUserIdOrderByIsDefaultDescUpdatedAtDesc(userId);
    }
    
    @Override
    public Address findDefaultByUserId(Long userId) {
        return addressRepository.findByUserIdAndIsDefaultTrue(userId);
    }
    
    @Override
    public void insert(Address address) {
        addressRepository.save(address);
    }
    
    @Override
    public void update(Address address) {
        addressRepository.save(address);
    }
    
    @Override
    public void deleteById(Long id) {
        addressRepository.deleteById(id);
    }
    
    @Override
    public int countByUserId(Long userId) {
        return (int) addressRepository.countByUserId(userId);
    }
}
