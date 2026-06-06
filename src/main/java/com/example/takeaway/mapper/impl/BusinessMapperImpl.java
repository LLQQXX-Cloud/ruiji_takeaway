/**
 * Business数据映射实现类
 * 实现 Business数据映射 接口定义的方法
 */

package com.example.takeaway.mapper.impl;

import com.example.takeaway.entity.Business;
import com.example.takeaway.mapper.Mapper.BusinessMapper;
import com.example.takeaway.mapper.BusinessRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class BusinessMapperImpl implements BusinessMapper {

    @Autowired
    private BusinessRepository businessRepository;

    @Override
    public Business save(Business business) {
        return businessRepository.save(business);
    }

    @Override
    public void deleteById(Long id) {
        businessRepository.deleteById(id);
    }

    @Override
    public Optional<Business> findById(Long id) {
        return businessRepository.findById(id);
    }

    @Override
    public List<Business> findAll() {
        return businessRepository.findAll();
    }

    @Override
    public List<Business> findByStatus(Integer status) {
        return businessRepository.findByStatus(status);
    }

    @Override
    public List<Business> findByNameContaining(String name) {
        return businessRepository.findByNameContaining(name);
    }

    @Override
    public Business findByUsername(String username) {
        return businessRepository.findByUsername(username);
    }

    @Override
    public List<Business> findByCategoryId(Long categoryId) {
        return businessRepository.findByCategoryId(categoryId);
    }

    @Override
    public List<Business> findByStatusAndNameContaining(Integer status, String name) {
        return businessRepository.findByStatusAndNameContaining(status, name);
    }

    @Override
    public List<Business> findByStatusAndCategoryId(Integer status, Long categoryId) {
        return businessRepository.findByStatusAndCategoryId(status, categoryId);
    }

    @Override
    public List<Business> findByStatusAndNameContainingAndCategoryId(Integer status, String name, Long categoryId) {
        return businessRepository.findByStatusAndNameContainingAndCategoryId(status, name, categoryId);
    }

    @Override
    public List<Business> findByNameContainingAndCategoryId(String name, Long categoryId) {
        return businessRepository.findByNameContainingAndCategoryId(name, categoryId);
    }
}
