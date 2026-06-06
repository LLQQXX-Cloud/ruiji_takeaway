/**
 * Business服务实现类
 * 实现 Business服务 接口定义的方法
 */

package com.example.takeaway.service.impl;

import com.example.takeaway.entity.Business;
import com.example.takeaway.mapper.Mapper.BusinessMapper;
import com.example.takeaway.service.CacheService;
import com.example.takeaway.service.Service.BusinessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class BusinessServiceImpl implements BusinessService {

    private static final String BUSINESS_KEY_PREFIX = "business:info:";
    private static final String BUSINESS_LIST_KEY = "business:list:";

    @Autowired
    private BusinessMapper businessMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private CacheService cacheService;

    @Override
    public Business create(Business business) {
        return businessMapper.save(business);
    }

    @Override
    public Business update(Business business) {
        Business existing = findById(business.getId());
        if (business.getName() != null) existing.setName(business.getName());
        if (business.getAddress() != null) existing.setAddress(business.getAddress());
        if (business.getPhone() != null) existing.setPhone(business.getPhone());
        if (business.getDescription() != null) existing.setDescription(business.getDescription());
        if (business.getImage() != null) existing.setImage(business.getImage());
        if (business.getStartPrice() != null) existing.setStartPrice(business.getStartPrice());
        if (business.getDeliveryFee() != null) existing.setDeliveryFee(business.getDeliveryFee());
        if (business.getRating() != null) existing.setRating(business.getRating());
        if (business.getStatus() != null) existing.setStatus(business.getStatus());
        if (business.getCategoryId() != null) existing.setCategoryId(business.getCategoryId());
        
        Business result = businessMapper.save(existing);
        
        cacheService.delete(BUSINESS_KEY_PREFIX + business.getId());
        cacheService.deleteByPattern(BUSINESS_LIST_KEY + "*");
        
        return result;
    }

    @Override
    public void delete(Long id) {
        businessMapper.deleteById(id);
        
        cacheService.delete(BUSINESS_KEY_PREFIX + id);
        cacheService.deleteByPattern(BUSINESS_LIST_KEY + "*");
    }

    @Override
    public Business findById(Long id) {
        String key = BUSINESS_KEY_PREFIX + id;
        Business cached = cacheService.get(key, Business.class);
        if (cached != null) {
            return cached;
        }
        
        Business business = businessMapper.findById(id)
                .orElseThrow(() -> new RuntimeException("商家不存在"));
        
        cacheService.set(key, business, 30, TimeUnit.MINUTES);
        
        return business;
    }

    @Override
    public List<Business> findAll() {
        String key = BUSINESS_LIST_KEY + "all";
        @SuppressWarnings("unchecked")
        List<Business> cached = (List<Business>) cacheService.get(key, List.class);
        if (cached != null) {
            return cached;
        }
        
        List<Business> businesses = businessMapper.findAll();
        
        cacheService.set(key, businesses, 15, TimeUnit.MINUTES);
        
        return businesses;
    }

    @Override
    public List<Business> findByStatus(Integer status) {
        String key = BUSINESS_LIST_KEY + "status_" + status;
        @SuppressWarnings("unchecked")
        List<Business> cached = (List<Business>) cacheService.get(key, List.class);
        if (cached != null) {
            return cached;
        }
        
        List<Business> businesses = businessMapper.findByStatus(status);
        
        cacheService.set(key, businesses, 15, TimeUnit.MINUTES);
        
        return businesses;
    }

    @Override
    public List<Business> searchByName(String name) {
        String key = BUSINESS_LIST_KEY + "name_" + name;
        @SuppressWarnings("unchecked")
        List<Business> cached = (List<Business>) cacheService.get(key, List.class);
        if (cached != null) {
            return cached;
        }
        
        List<Business> businesses = businessMapper.findByNameContaining(name);
        
        cacheService.set(key, businesses, 10, TimeUnit.MINUTES);
        
        return businesses;
    }

    @Override
    public List<Business> search(String keyword, Long categoryId, Integer status) {
        if (keyword != null && !keyword.isEmpty() && categoryId != null && status != null) {
            return businessMapper.findByStatusAndNameContainingAndCategoryId(status, keyword, categoryId);
        } else if (keyword != null && !keyword.isEmpty() && status != null) {
            return businessMapper.findByStatusAndNameContaining(status, keyword);
        } else if (categoryId != null && status != null) {
            return businessMapper.findByStatusAndCategoryId(status, categoryId);
        } else if (status != null) {
            return businessMapper.findByStatus(status);
        } else if (keyword != null && !keyword.isEmpty() && categoryId != null) {
            return businessMapper.findByNameContainingAndCategoryId(keyword, categoryId);
        } else if (keyword != null && !keyword.isEmpty()) {
            return businessMapper.findByNameContaining(keyword);
        } else if (categoryId != null) {
            return businessMapper.findByCategoryId(categoryId);
        } else {
            return businessMapper.findAll();
        }
    }

    @Override
    public Business login(String username, String password) {
        Business business = businessMapper.findByUsername(username);
        if (business == null) {
            throw new RuntimeException("账号不存在");
        }
        if (!passwordEncoder.matches(password, business.getPassword())) {
            throw new RuntimeException("密码错误");
        }
        return business;
    }

    @Override
    public Business register(Business business) {
        if (businessMapper.findByUsername(business.getUsername()) != null) {
            throw new RuntimeException("账号已存在");
        }
        business.setPassword(passwordEncoder.encode(business.getPassword()));
        return businessMapper.save(business);
    }
}
