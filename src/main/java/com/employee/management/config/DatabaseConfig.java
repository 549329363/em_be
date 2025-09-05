package com.employee.management.config;

import org.springframework.context.annotation.Configuration;

@Configuration
public class DatabaseConfig {

    // DataSourceInitializer已禁用，避免每次启动重复执行SQL脚本
    // 数据库初始化应该手动执行，或通过专门的数据库迁移工具
    
    /*
    @Bean
    public DataSourceInitializer dataSourceInitializer(DataSource dataSource) {
        DataSourceInitializer initializer = new DataSourceInitializer();
        initializer.setDataSource(dataSource);
        
        // 创建表的SQL脚本
        ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
        populator.addScript(new ClassPathResource("sql/schema.sql"));
        populator.addScript(new ClassPathResource("sql/data.sql"));
        populator.setContinueOnError(true);
        
        initializer.setDatabasePopulator(populator);
        return initializer;
    }
    */
}