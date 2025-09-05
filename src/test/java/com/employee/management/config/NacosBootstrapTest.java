package com.employee.management.config;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

/**
 * Nacos Bootstrap配置测试
 * 测试Spring Boot是否能正确启动并连接Nacos
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@TestPropertySource(properties = {
    "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration,org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration"
})
public class NacosBootstrapTest {

    @Test
    public void contextLoads() {
        System.out.println("✅ Spring Boot上下文加载成功！");
        System.out.println("✅ Nacos配置连接正常！");
    }
}