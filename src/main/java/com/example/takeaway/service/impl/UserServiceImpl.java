/**
 * User服务实现类
 * 实现 User服务 接口定义的方法
 */

package com.example.takeaway.service.impl;

import com.example.takeaway.entity.User;
import com.example.takeaway.mapper.Mapper.UserMapper;
import com.example.takeaway.service.CacheService;
import com.example.takeaway.service.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class UserServiceImpl implements UserService {

    private static final String USER_KEY_PREFIX = "user:info:";

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private CacheService cacheService;

    @Override
    public User register(User user) {
        if (userMapper.existsByUsername(user.getUsername())) {
            throw new RuntimeException("用户名已存在");
        }
        if (userMapper.existsByPhone(user.getPhone())) {
            throw new RuntimeException("手机号已存在");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userMapper.save(user);
    }

    @Override
    public User login(String username, String password) {
        User user = userMapper.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("密码错误");
        }
        return user;
    }

    @Override
    public User findById(Long id) {
        String key = USER_KEY_PREFIX + id;
        User cached = cacheService.get(key, User.class);
        if (cached != null) {
            return cached;
        }
        
        User user = userMapper.findById(id)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        
        cacheService.set(key, user, 30, TimeUnit.MINUTES);
        
        return user;
    }

    @Override
    public User findByUsername(String username) {
        return userMapper.findByUsername(username).orElse(null);
    }

    @Override
    public User update(User user) {
        User existing = findById(user.getId());
        if (user.getUsername() != null) existing.setUsername(user.getUsername());
        if (user.getPassword() != null) existing.setPassword(passwordEncoder.encode(user.getPassword()));
        if (user.getPhone() != null) existing.setPhone(user.getPhone());
        if (user.getNickname() != null) existing.setNickname(user.getNickname());
        if (user.getAddress() != null) existing.setAddress(user.getAddress());
        
        User result = userMapper.save(existing);
        
        cacheService.delete(USER_KEY_PREFIX + user.getId());
        
        return result;
    }

    @Override
    public void delete(Long id) {
        userMapper.deleteById(id);
        
        cacheService.delete(USER_KEY_PREFIX + id);
    }
}
