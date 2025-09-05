package com.employee.management.config;

import org.junit.jupiter.api.Test;

/**
 * 简单环境变量测试
 * 不依赖Spring上下文，直接测试环境变量读取
 */
public class SimpleEnvironmentTest {

    @Test
    public void testSystemEnvironmentVariables() {
        System.out.println("=== 系统环境变量直接读取测试 ===");
        
        String nacosUsername = System.getenv("NACOS_USERNAME");
        String nacosPassword = System.getenv("NACOS_PASSWORD");
        String springProfilesActive = System.getenv("SPRING_PROFILES_ACTIVE");
        String nacosServerAddr = System.getenv("NACOS_SERVER_ADDR");
        
        System.out.println("NACOS_USERNAME: " + nacosUsername);
        System.out.println("NACOS_PASSWORD: " + (nacosPassword != null ? "已设置" : "未设置"));
        System.out.println("SPRING_PROFILES_ACTIVE: " + springProfilesActive);
        System.out.println("NACOS_SERVER_ADDR: " + nacosServerAddr);
        
        System.out.println("\n=== 系统属性测试 ===");
        System.out.println("spring.profiles.active: " + System.getProperty("spring.profiles.active"));
        
        System.out.println("\n=== 环境变量占位符测试 ===");
        // 模拟Spring Boot的占位符解析
        String serverAddr = nacosServerAddr != null ? nacosServerAddr : "114.55.135.154:8848";
        String profileActive = springProfilesActive != null ? springProfilesActive : "default";
        
        System.out.println("解析后的服务器地址: " + serverAddr);
        System.out.println("解析后的激活配置: " + profileActive);
        
        if (nacosUsername == null || nacosPassword == null) {
            System.out.println("\n❌ 关键环境变量未设置！");
            System.out.println("请确保设置了以下环境变量:");
            System.out.println("- NACOS_USERNAME");
            System.out.println("- NACOS_PASSWORD");
            System.out.println("- SPRING_PROFILES_ACTIVE");
        } else {
            System.out.println("\n✅ 环境变量设置正常");
        }
    }
}