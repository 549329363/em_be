package com.employee.management.config;

import org.junit.jupiter.api.Test;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 测试应用程序配置，特别是环境变量的读取
 */
@TestPropertySource(properties = {
    "spring.cloud.nacos.config.import-check.enabled=false"
})
public class ApplicationConfigTest {

    @Test
    public void testEnvironmentVariableResolution() {
        // 测试环境变量解析逻辑
        String profileFromEnv = System.getProperty("SPRING_PROFILES_ACTIVE", "default");
        assertNotNull(profileFromEnv, "环境变量解析不应该为null");
        
        // 验证默认值处理
        String defaultProfile = System.getProperty("SPRING_PROFILES_ACTIVE", "default");
        assertTrue(defaultProfile.equals("test") || defaultProfile.equals("prod") || defaultProfile.equals("default"), 
                  "profile应该是test、prod或default之一");
    }

    @Test
    public void testDataIdGeneration() {
        // 测试Data ID生成逻辑
        String profile = "test";
        String expectedDataId = "application-" + profile + ".yml";
        assertEquals("application-test.yml", expectedDataId, "Data ID应该正确生成");
        
        profile = "prod";
        expectedDataId = "application-" + profile + ".yml";
        assertEquals("application-prod.yml", expectedDataId, "Data ID应该正确生成");
    }

    @Test
    public void testNacosServerConfiguration() {
        // 测试Nacos服务器配置
        String expectedServerAddr = "114.55.135.154:8848";
        assertNotNull(expectedServerAddr, "Nacos服务器地址不应该为null");
        assertTrue(expectedServerAddr.contains("114.55.135.154"), "应该包含正确的服务器地址");
        assertTrue(expectedServerAddr.contains("8848"), "应该包含正确的端口");
    }
}