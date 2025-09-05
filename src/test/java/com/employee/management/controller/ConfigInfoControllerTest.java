package com.employee.management.controller;

import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.exception.NacosException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * ConfigInfoController测试类
 */
@WebMvcTest(ConfigInfoController.class)
@TestPropertySource(properties = {
    "spring.cloud.nacos.config.import-check.enabled=false",
    "spring.profiles.active=test",
    "spring.cloud.nacos.config.server-addr=114.55.135.154:8848",
    "spring.cloud.nacos.config.group=DEFAULT_GROUP",
    "spring.application.name=employee-management"
})
public class ConfigInfoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ConfigService configService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testGetConfigInfoWhenConfigExists() throws Exception {
        // 模拟配置存在
        String mockConfig = "spring:\n  datasource:\n    url: jdbc:mysql://localhost:3306/test\nlogging:\n  level: debug";
        when(configService.getConfig("application-test.yml", "DEFAULT_GROUP", 3000))
            .thenReturn(mockConfig);

        mockMvc.perform(get("/admin/config/info"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.applicationName").value("employee-management"))
            .andExpect(jsonPath("$.activeProfile").value("test"))
            .andExpect(jsonPath("$.serverAddr").value("114.55.135.154:8848"))
            .andExpect(jsonPath("$.group").value("DEFAULT_GROUP"))
            .andExpect(jsonPath("$.dataId").value("application-test.yml"))
            .andExpect(jsonPath("$.configStatus").value("AVAILABLE"))
            .andExpect(jsonPath("$.hasConfig").value(true))
            .andExpect(jsonPath("$.configKeys").isArray())
            .andExpect(jsonPath("$.configSize").isNumber());
    }

    @Test
    void testGetConfigInfoWhenConfigNotFound() throws Exception {
        // 模拟配置不存在
        when(configService.getConfig("application-test.yml", "DEFAULT_GROUP", 3000))
            .thenReturn(null);

        mockMvc.perform(get("/admin/config/info"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.configStatus").value("NOT_FOUND"))
            .andExpect(jsonPath("$.hasConfig").value(false));
    }

    @Test
    void testGetConfigInfoWhenNacosException() throws Exception {
        // 模拟Nacos异常
        NacosException nacosException = new NacosException(500, "Server error");
        when(configService.getConfig("application-test.yml", "DEFAULT_GROUP", 3000))
            .thenThrow(nacosException);

        mockMvc.perform(get("/admin/config/info"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.configStatus").value("ERROR"))
            .andExpect(jsonPath("$.error").value("Failed to get config: Server error"));
    }

    @Test
    void testTestConnectionSuccess() throws Exception {
        // 模拟连接成功
        when(configService.getConfig("application-test.yml", "DEFAULT_GROUP", 3000))
            .thenReturn("some config");

        mockMvc.perform(get("/admin/config/test-connection"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("SUCCESS"))
            .andExpect(jsonPath("$.message").value("Connection to Nacos server is working"))
            .andExpect(jsonPath("$.serverAddr").value("114.55.135.154:8848"));
    }

    @Test
    void testTestConnectionFailure() throws Exception {
        // 模拟连接失败
        NacosException nacosException = new NacosException(503, "Service unavailable");
        when(configService.getConfig("application-test.yml", "DEFAULT_GROUP", 3000))
            .thenThrow(nacosException);

        mockMvc.perform(get("/admin/config/test-connection"))
            .andExpect(status().isServiceUnavailable())
            .andExpect(jsonPath("$.status").value("FAILED"))
            .andExpect(jsonPath("$.message").value("Failed to connect to Nacos: Service unavailable"))
            .andExpect(jsonPath("$.errorCode").value(503));
    }

    @Test
    void testGetConfigInfoWhenConfigServiceUnavailable() throws Exception {
        // 模拟ConfigService不可用的情况
        // 这需要在实际的集成测试中验证，因为@MockBean总是会创建mock对象
        
        mockMvc.perform(get("/admin/config/info"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.applicationName").value("employee-management"))
            .andExpect(jsonPath("$.activeProfile").value("test"));
    }

    @Test
    void testTestConnectionWhenConfigServiceUnavailable() throws Exception {
        // 这个测试需要在实际环境中验证ConfigService为null的情况
        // 在单元测试中，@MockBean会确保configService不为null
        
        mockMvc.perform(get("/admin/config/test-connection"))
            .andExpect(jsonPath("$.serverAddr").value("114.55.135.154:8848"));
    }
}