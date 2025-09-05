package com.employee.management.config;

import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.exception.NacosException;
import org.junit.jupiter.api.Test;

import java.util.Properties;

/**
 * Nacos连接验证工具
 * 用于验证Nacos配置是否正常工作
 */
public class DirectNacosTest {
    
    @Test
    public void testWithEnvironmentVariables() {
        System.out.println("=== 使用环境变量测试 ===");
        
        // 从环境变量获取配置
        String serverAddr = System.getenv("NACOS_SERVER_ADDR");
        if (serverAddr == null || serverAddr.isEmpty()) {
            serverAddr = "114.55.135.154:8848";
        }
        
        String username = System.getenv("NACOS_USERNAME");
        String password = System.getenv("NACOS_PASSWORD");
        String activeProfile = System.getenv("SPRING_PROFILES_ACTIVE");
        
        if (activeProfile == null || activeProfile.isEmpty()) {
            activeProfile = "test";
        }
        
        System.out.println("环境变量信息:");
        System.out.println("NACOS_SERVER_ADDR: " + serverAddr);
        System.out.println("NACOS_USERNAME: " + (username != null ? username : "未设置"));
        System.out.println("NACOS_PASSWORD: " + (password != null ? "已设置" : "未设置"));
        System.out.println("SPRING_PROFILES_ACTIVE: " + activeProfile);
        System.out.println();
        
        if (username == null || password == null) {
            System.out.println("⚠️ 环境变量未设置，请设置以下环境变量:");
            System.out.println("set NACOS_USERNAME=你的用户名");
            System.out.println("set NACOS_PASSWORD=你的密码");
            System.out.println("set SPRING_PROFILES_ACTIVE=test");
            return;
        }
        
        try {
            // 创建Nacos配置服务
            Properties properties = new Properties();
            properties.put("serverAddr", serverAddr);
            properties.put("username", username);
            properties.put("password", password);
            
            ConfigService configService = NacosFactory.createConfigService(properties);
            
            // 获取指定环境的配置文件
            String dataId = "application-" + activeProfile + ".yml";
            String group = "DEFAULT_GROUP";
            
            System.out.println("=== 获取环境配置 ===");
            System.out.println("Data ID: " + dataId);
            System.out.println("Group: " + group);
            
            String config = configService.getConfig(dataId, group, 5000);
            
            if (config != null && !config.isEmpty()) {
                System.out.println("✅ 成功获取配置!");
                System.out.println("配置内容:");
                System.out.println("--- 开始 ---");
                System.out.println(config);
                System.out.println("--- 结束 ---");
            } else {
                System.out.println("❌ 未获取到配置内容");
                System.out.println("请检查Nacos控制台中是否存在配置文件: " + dataId);
            }
            
        } catch (Exception e) {
            System.err.println("❌ 连接失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
}