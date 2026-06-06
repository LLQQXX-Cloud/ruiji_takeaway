/**
 * 外卖系统启动类
 * Spring Boot 应用入口
 */
package com.example.takeaway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.example.takeaway.mapper")
public class TakeawayApplication {

    /**
     * 主方法，启动 Spring Boot 应用
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        SpringApplication.run(TakeawayApplication.class, args);
    }
}
