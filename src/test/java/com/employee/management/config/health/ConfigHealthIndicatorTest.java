package com.employee.management.config.health;

import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.exception.NacosException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * ConfigHealthIndicator测试类
 */
@ExtendWith(MockitoExtension.class)
public class ConfigHealthIndicatorTest {

    @Mock
    private ConfigService configService;

    @InjectMocks
    private ConfigHealthIndicator healthIndicator;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(healthIndicator, "serverAddr", "114.55.135.154:8848");
        ReflectionTestUtils.setField(healthIndicator, "activeProfile", "test");
        ReflectionTestUtils.setField(healthIndicator, "group", "DEFAULT_GROUP");
    }

    @Test
    void testHealthCheckWhenConfigServiceIsNull() {
        // 设置configService为null
        ReflectionTestUtils.setField(healthIndicator, "configService", null);
        
        Health health = healthIndicator.health();
        
        assertEquals(Status.DOWN, health.getStatus());
        assertEquals("ConfigService not available", health.getDetails().get("error"));
        assertEquals("114.55.135.154:8848", health.getDetails().get("nacosServer"));
        assertEquals("test", health.getDetails().get("activeProfile"));
    }

    @Test
    void testHealthCheckWhenConfigExists() throws NacosException {
        // 模拟配置存在
        String mockConfig = "spring:\n  datasource:\n    url: jdbc:mysql://localhost:3306/test";
        when(configService.getConfig("application-test.yml", "DEFAULT_GROUP", 3000))
            .thenReturn(mockConfig);
        
        Health health = healthIndicator.health();
        
        assertEquals(Status.UP, health.getStatus());
        assertEquals("CONNECTED", health.getDetails().get("configStatus"));
        assertEquals("114.55.135.154:8848", health.getDetails().get("nacosServer"));
        assertEquals("test", health.getDetails().get("activeProfile"));
        assertEquals("application-test.yml", health.getDetails().get("dataId"));
        assertEquals("DEFAULT_GROUP", health.getDetails().get("group"));
        assertEquals(true, health.getDetails().get("hasConfig"));
    }

    @Test
    void testHealthCheckWhenConfigIsEmpty() throws NacosException {
        // 模拟配置为空
        when(configService.getConfig("application-test.yml", "DEFAULT_GROUP", 3000))
            .thenReturn("");
        
        Health health = healthIndicator.health();
        
        assertEquals("WARNING", health.getStatus().getCode());
        assertEquals("NO_CONFIG", health.getDetails().get("configStatus"));
        assertEquals(false, health.getDetails().get("hasConfig"));
    }

    @Test
    void testHealthCheckWhenNacosException() throws NacosException {
        // 模拟Nacos异常
        NacosException nacosException = new NacosException(500, "Server error");
        when(configService.getConfig("application-test.yml", "DEFAULT_GROUP", 3000))
            .thenThrow(nacosException);
        
        Health health = healthIndicator.health();
        
        assertEquals(Status.DOWN, health.getStatus());
        assertTrue(health.getDetails().get("error").toString().contains("Failed to get config"));
        assertEquals("application-test.yml", health.getDetails().get("dataId"));
    }

    @Test
    void testHealthCheckWhenGeneralException() throws NacosException {
        // 模拟一般异常
        when(configService.getConfig(anyString(), anyString(), anyInt()))
            .thenThrow(new RuntimeException("Unexpected error"));
        
        Health health = healthIndicator.health();
        
        assertEquals(Status.DOWN, health.getStatus());
        assertTrue(health.getDetails().get("error").toString().contains("Health check failed"));
    }

    @Test
    void testHealthCheckWithDifferentProfile() throws NacosException {
        // 测试不同的profile
        ReflectionTestUtils.setField(healthIndicator, "activeProfile", "prod");
        
        String mockConfig = "spring:\n  datasource:\n    url: jdbc:mysql://prod:3306/prod";
        when(configService.getConfig("application-prod.yml", "DEFAULT_GROUP", 3000))
            .thenReturn(mockConfig);
        
        Health health = healthIndicator.health();
        
        assertEquals(Status.UP, health.getStatus());
        assertEquals("prod", health.getDetails().get("activeProfile"));
        assertEquals("application-prod.yml", health.getDetails().get("dataId"));
    }
}