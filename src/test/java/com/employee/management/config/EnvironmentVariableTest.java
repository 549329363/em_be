package com.employee.management.config;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.beans.factory.annotation.Value;

/**
 * 环境变量读取测试
 * 诊断Spring Boot是否能正确读取环境变量
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@TestPropertySource(properties = {
    "spring.cloud.nacos.config.import-check.enabled=false",
    "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration,org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration"
})
public class EnvironmentVariableTest {

    @Value("${NACOS_USERNAME:未设置}")
    private String nacosUsername;
    
    @Value("${NACOS_PASSWORD:未设置}")
    private String nacosPassword;
    
    @Value("${SPRING_PROFILES_ACTIVE:未设置}")
    private String activeProfile;
    
    @Value("${spring.cloud.nacos.config.username:未设置}")
    private String springNacosUsername;
    
    @Value("${spring.cloud.nacos.config.password:未设置}")
    private String springNacosPassword;

    @Test
    public void testEnvironmentVariables() {
        System.out.println("=== Spring Boot环境变量读取测试 ===");
        
        // 直接从系统环境变量读取
        System.out.println("系统环境变量:");
        System.out.println("NACOS_USERNAME: " + System.getenv("NACOS_USERNAME"));
        System.out.println("NACOS_PASSWORD: " + (System.getenv("NACOS_PASSWORD") != null ? "已设置" : "未设置"));
        System.out.println("SPRING_PROFILES_ACTIVE: " + System.getenv("SPRING_PROFILES_ACTIVE"));
        
        System.out.println("\nSpring Boot @Value注解读取:");
        System.out.println("NACOS_USERNAME: " + nacosUsername);
        System.out.println("NACOS_PASSWORD: " + (!"未设置".equals(nacosPassword) ? "已设置" : "未设置"));
        System.out.println("SPRING_PROFILES_ACTIVE: " + activeProfile);
        
        System.out.println("\nSpring配置属性读取:");
        System.out.println("spring.cloud.nacos.config.username: " + springNacosUsername);
        System.out.println("spring.cloud.nacos.config.password: " + (!"未设置".equals(springNacosPassword) ? "已设置" : "未设置"));
        
        // 检查系统属性
        System.out.println("\n系统属性:");
        System.out.println("spring.profiles.active: " + System.getProperty("spring.profiles.active"));
        
        // 验证环境变量是否正确传递
        if (System.getenv("NACOS_USERNAME") != null && !"未设置".equals(nacosUsername)) {
            System.out.println("\n✅ 环境变量读取正常");
        } else {
            System.out.println("\n❌ 环境变量读取异常");
            System.out.println("可能的问题:");
            System.out.println("1. 环境变量未正确设置");
            System.out.println("2. Spring Boot配置文件中的占位符语法有问题");
            System.out.println("3. 配置文件加载顺序问题");
        }
    }
}