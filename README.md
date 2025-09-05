# 员工管理系统后端服务

## 项目简介

员工管理系统后端服务，基于Spring Boot 3.1.5开发，集成MyBatis Plus、JWT认证、Nacos配置管理等功能。

## 技术栈

- Spring Boot 3.1.5
- MyBatis Plus 3.5.3.2
- MySQL 8.0
- JWT认证
- Nacos配置管理
- Lombok

## 环境要求

- Java 17+
- Maven 3.6+
- MySQL 8.0
- Nacos 2.0+

## 项目配置

### 1. Nacos配置

本项目使用Nacos进行配置管理，支持多环境配置。需要在阿里云Nacos服务中添加对应环境的数据库配置：

**Group**: `DEFAULT_GROUP`
**配置格式**: `YAML`
**Nacos地址**: `http://114.55.135.154:8848/`

根据环境变量 `SPRING_PROFILES_ACTIVE` 的值加载对应的配置文件：
- `SPRING_PROFILES_ACTIVE=test` 时，加载 **Data ID**: `application-test.yaml`
- `SPRING_PROFILES_ACTIVE=prod` 时，加载 **Data ID**: `application-prod.yaml`
- 未设置环境变量时，默认加载 **Data ID**: `application-default.yaml`

> **注意**：如果Nacos服务启用了认证，请确保应用能够提供正确的用户名和密码才能获取配置。

配置内容示例（以test环境为例）：
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/employee_management_test?createDatabaseIfNotExist=true&useUnicode=true&characterEncoding=UTF-8&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true&autoReconnect=true&zeroDateTimeBehavior=convertToNull&transformedBitIsBoolean=true
    username: test_user
    password: test_password
    driver-class-name: com.mysql.cj.jdbc.Driver
    hikari:
      connection-init-sql: "SET NAMES utf8mb4 COLLATE utf8mb4_unicode_ci"
      connection-test-query: "SELECT 1"
      minimum-idle: 5
      maximum-pool-size: 20
      connection-timeout: 30000
      idle-timeout: 600000
      max-lifetime: 1800000
```

### 2. Nacos服务配置

Nacos服务运行在阿里云服务器 `114.55.135.154:8848`，配置已在 `bootstrap.yml` 中设置。

如果Nacos服务启用了认证，需要提供用户名和密码：

1. 通过环境变量设置：
   ```bash
   export NACOS_USERNAME=your_username
   export NACOS_PASSWORD=your_password
   export SPRING_PROFILES_ACTIVE=test  # 或 prod
   ```

2. 或在启动应用时通过JVM参数传递：
   ```bash
   java -DNACOS_USERNAME=your_username -DNACOS_PASSWORD=your_password -Dspring.profiles.active=test -jar target/employee-management-1.0.0.jar
   ```

3. 也可以直接修改 `bootstrap.yml` 文件中的用户名和密码配置。

## 项目启动

### 1. 启动Nacos服务

确保Nacos服务已启动并可访问。

### 2. 添加Nacos配置

在Nacos控制台中添加上述配置。

### 3. 启动应用

如果Nacos服务未启用认证：
```bash
# 设置环境变量指定运行环境
export SPRING_PROFILES_ACTIVE=test  # 或 prod
mvn spring-boot:run
```

如果Nacos服务启用了认证，需要提供用户名、密码和环境标识：
```bash
# 通过环境变量
export NACOS_USERNAME=your_nacos_username
export NACOS_PASSWORD=your_nacos_password
export SPRING_PROFILES_ACTIVE=test  # 或 prod
mvn spring-boot:run
```

或

```bash
mvn clean package
# 通过JVM参数传递认证信息和环境标识
java -DNACOS_USERNAME=your_nacos_username -DNACOS_PASSWORD=your_nacos_password -Dspring.profiles.active=test -jar target/employee-management-1.0.0.jar
```

## API文档

应用启动后，可通过以下地址访问：

- API基础路径: `http://localhost:8083/api`
- Swagger文档: `http://localhost:8083/api/swagger-ui.html`

## 数据库初始化

数据库表结构和初始数据需要手动执行，SQL文件位于 `src/main/resources/sql/` 目录下：

1. `schema.sql` - 数据库表结构
2. `data.sql` - 初始数据

## 安全配置

系统使用JWT进行认证，相关配置可通过Nacos进行管理。