package com.example.takeaway.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * Redis 配置类
 * 
 * 配置 RedisTemplate 的序列化方式，解决默认序列化导致的乱码问题
 * 使用 Jackson2JsonRedisSerializer 实现对象的 JSON 序列化和反序列化
 */
@Configuration
public class RedisConfig {

    /**
     * 配置 RedisTemplate Bean
     * 
     * @param factory Redis 连接工厂，由 Spring Boot 自动配置注入
     * @return 配置好的 RedisTemplate 实例
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        // 设置 Redis 连接工厂
        template.setConnectionFactory(factory);
        
        // 配置 ObjectMapper，用于 JSON 序列化
        ObjectMapper objectMapper = new ObjectMapper();
        // 设置所有属性可见
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        // 注册 Java 8 时间模块，支持 LocalDateTime 等类型
        objectMapper.registerModule(new JavaTimeModule());
        // 禁用日期作为时间戳输出
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        
        // 创建 JSON 序列化器
        Jackson2JsonRedisSerializer<Object> jsonSerializer = new Jackson2JsonRedisSerializer<>(objectMapper, Object.class);
        
        // Key 序列化：使用 StringRedisSerializer
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        
        // Value 序列化：使用 Jackson2JsonRedisSerializer
        template.setValueSerializer(jsonSerializer);
        template.setHashValueSerializer(jsonSerializer);
        
        // 完成配置
        template.afterPropertiesSet();
        return template;
    }
}
