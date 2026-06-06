package com.example.takeaway.service;

import java.util.concurrent.TimeUnit;

public interface CacheService {
    
    void set(String key, Object value);
    
    void set(String key, Object value, long timeout, TimeUnit unit);
    
    <T> T get(String key, Class<T> clazz);
    
    boolean exists(String key);
    
    void delete(String key);
    
    void deleteByPattern(String pattern);
    
    void expire(String key, long timeout, TimeUnit unit);
}
