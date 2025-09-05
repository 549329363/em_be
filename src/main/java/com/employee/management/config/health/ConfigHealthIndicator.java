package com.employee.management.config.health;

import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.exception.NacosException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Nacos配置健康检查指示器
 */
@Slf4j
@Component("nacos-config")
public class ConfigHealthIndicator implements HealthIndicator {

    @Autowired(required = false)
    private ConfigService configService;

    @Value("${spring.cloud.nacos.config.server-addr:${NACOS_SERVER_ADDR:114.55.135.154:8848}}")
    private String serverAddr;

    @Value("${spring.profiles.active:default}")
    private String activeProfile;

    @Value("${spring.cloud.nacos.config.group:DEFAULT_GROUP}")
    private String group;

    @Override
    public Health health() {
        try {
            Health.Builder builder = new Health.Builder();
            
            if (configService == null) {
                return builder
                    .down()
                    .withDetail("error", "ConfigService not available")
                    .withDetail("nacosServer", serverAddr)
                    .withDetail("activeProfile", activeProfile)
                    .withDetail("checkTime", getCurrentTime())
                    .build();
            }

            // 尝试获取配置以验证连接
            String dataId = "application-" + activeProfile + ".yml";
            try {
                String config = configService.getConfig(dataId, group, 3000);
                boolean hasConfig = config != null && !config.trim().isEmpty();
                
                return builder
                    .status(hasConfig ? "UP" : "WARNING")
                    .withDetail("nacosServer", serverAddr)
                    .withDetail("configStatus", hasConfig ? "CONNECTED" : "NO_CONFIG")
                    .withDetail("activeProfile", activeProfile)
                    .withDetail("dataId", dataId)
                    .withDetail("group", group)
                    .withDetail("hasConfig", hasConfig)
                    .withDetail("lastCheck", getCurrentTime())
                    .build();
                    
            } catch (NacosException e) {
                log.warn("Failed to get config from Nacos: {}", e.getMessage());
                return builder
                    .down()
                    .withDetail("error", "Failed to get config: " + e.getMessage())
                    .withDetail("nacosServer", serverAddr)
                    .withDetail("activeProfile", activeProfile)
                    .withDetail("dataId", dataId)
                    .withDetail("group", group)
                    .withDetail("lastCheck", getCurrentTime())
                    .build();
            }
            
        } catch (Exception e) {
            log.error("Health check failed", e);
            return Health.down()
                .withDetail("error", "Health check failed: " + e.getMessage())
                .withDetail("nacosServer", serverAddr)
                .withDetail("activeProfile", activeProfile)
                .withDetail("lastCheck", getCurrentTime())
                .build();
        }
    }

    private String getCurrentTime() {
        return LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }
}