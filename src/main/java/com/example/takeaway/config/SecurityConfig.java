/**
 * 安全配置类
 * 提供密码加密等安全相关的 Bean 配置
 */
package com.example.takeaway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
public class SecurityConfig {

    /**
     * 密码加密器
     * 使用 BCrypt 算法对用户密码进行加密存储
     * @return BCryptPasswordEncoder 实例
     */
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
