package com.employee.management.controller;

import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.exception.NacosException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * 配置信息查看控制器
 */
@Slf4j
@RestController
@RequestMapping("/admin/config")
public class ConfigInfoController {

    @Autowired(required = false)
    private ConfigService configService;

    @Value("${spring.cloud.nacos.config.server-addr:114.55.135.154:8848}")
    private String serverAddr;

    @Value("${spring.profiles.active:default}")
    private String activeProfile;

    @Value("${spring.cloud.nacos.config.group:DEFAULT_GROUP}")
    private String group;

    @Value("${spring.application.name:employee-management}")
    private String applicationName;

    /**
     * 获取配置信息
     */
    @GetMapping("/info")
    public ResponseEntity<Map<String, Object>> getConfigInfo() {
        Map<String, Object> info = new HashMap<>();
        
        try {
            info.put("applicationName", applicationName);
            info.put("activeProfile", activeProfile);
            info.put("serverAddr", serverAddr);
            info.put("group", group);
            info.put("checkTime", getCurrentTime());
            
            String dataId = "application-" + activeProfile + ".yml";
            info.put("dataId", dataId);
            
            if (configService != null) {
                try {
                    String config = configService.getConfig(dataId, group, 3000);
                    boolean hasConfig = config != null && !config.trim().isEmpty();
                    
                    info.put("configStatus", hasConfig ? "AVAILABLE" : "NOT_FOUND");
                    info.put("hasConfig", hasConfig);
                    
                    if (hasConfig) {
                        // 提取配置键（不暴露敏感值）
                        String[] lines = config.split("\n");
                        java.util.List<String> configKeys = new java.util.ArrayList<>();
                        for (String line : lines) {
                            line = line.trim();
                            if (line.contains(":") && !line.startsWith("#")) {
                                String key = line.split(":")[0].trim();
                                if (!key.isEmpty()) {
                                    configKeys.add(key);
                                }
                            }
                        }
                        info.put("configKeys", configKeys);
                        info.put("configSize", config.length());
                    }
                    
                } catch (NacosException e) {
                    log.warn("Failed to get config from Nacos: {}", e.getMessage());
                    info.put("configStatus", "ERROR");
                    info.put("error", "Failed to get config: " + e.getMessage());
                }
            } else {
                info.put("configStatus", "SERVICE_UNAVAILABLE");
                info.put("error", "ConfigService not available");
            }
            
            return ResponseEntity.ok(info);
            
        } catch (Exception e) {
            log.error("Failed to get config info", e);
            info.put("configStatus", "ERROR");
            info.put("error", "Failed to get config info: " + e.getMessage());
            return ResponseEntity.status(500).body(info);
        }
    }

    /**
     * 测试Nacos连接
     */
    @GetMapping("/test-connection")
    public ResponseEntity<Map<String, Object>> testConnection() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            result.put("serverAddr", serverAddr);
            result.put("testTime", getCurrentTime());
            
            if (configService == null) {
                result.put("status", "FAILED");
                result.put("message", "ConfigService not available");
                return ResponseEntity.status(503).body(result);
            }
            
            // 尝试获取服务器状态
            String dataId = "application-" + activeProfile + ".yml";
            try {
                configService.getConfig(dataId, group, 3000);
                result.put("status", "SUCCESS");
                result.put("message", "Connection to Nacos server is working");
                return ResponseEntity.ok(result);
                
            } catch (NacosException e) {
                result.put("status", "FAILED");
                result.put("message", "Failed to connect to Nacos: " + e.getMessage());
                result.put("errorCode", e.getErrCode());
                return ResponseEntity.status(503).body(result);
            }
            
        } catch (Exception e) {
            log.error("Connection test failed", e);
            result.put("status", "ERROR");
            result.put("message", "Connection test failed: " + e.getMessage());
            return ResponseEntity.status(500).body(result);
        }
    }

    private String getCurrentTime() {
        return LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }
}