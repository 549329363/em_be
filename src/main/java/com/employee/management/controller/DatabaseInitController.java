package com.employee.management.controller;

import com.employee.management.dto.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

/**
 * 数据库初始化控制器
 * 用于手动执行数据库初始化操作，避免每次启动都重复执行
 */
@RestController
@RequestMapping("/public/database")
public class DatabaseInitController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 初始化数据库表结构和基础数据
     * 只在第一次部署或需要重置数据时调用
     */
    @PostMapping("/init")
    public Result<String> initDatabase() {
        try {
            // 1. 设置数据库字符集
            jdbcTemplate.execute("SET NAMES utf8mb4 COLLATE utf8mb4_unicode_ci");
            
            // 2. 创建表结构
            createTables();
            
            // 3. 插入基础数据
            insertInitialData();
            
            return Result.success("数据库初始化完成！表结构和基础数据已创建。");
        } catch (Exception e) {
            return Result.error("数据库初始化失败：" + e.getMessage());
        }
    }

    /**
     * 只重置数据，不重建表结构
     */
    @PostMapping("/reset-data")
    public Result<String> resetData() {
        try {
            // 设置字符集
            jdbcTemplate.execute("SET NAMES utf8mb4 COLLATE utf8mb4_unicode_ci");
            
            // 清空数据并重新插入
            clearAndInsertData();
            
            return Result.success("数据重置完成！所有中文数据已正确插入。");
        } catch (Exception e) {
            return Result.error("数据重置失败：" + e.getMessage());
        }
    }

    /**
     * 检查数据库状态
     */
    @GetMapping("/status")
    public Result<String> checkDatabaseStatus() {
        try {
            // 检查表是否存在
            int employeeCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = 'employee_management' AND table_name = 'employee'",
                Integer.class
            );
            
            int userCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = 'employee_management' AND table_name = 'sys_user'",
                Integer.class
            );
            
            if (employeeCount > 0 && userCount > 0) {
                // 检查数据量
                int empDataCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM employee", Integer.class);
                int userDataCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM sys_user", Integer.class);
                
                return Result.success(String.format("数据库状态正常！员工数据：%d条，用户数据：%d条", empDataCount, userDataCount));
            } else {
                return Result.error("数据库表不存在，需要执行初始化操作");
            }
        } catch (Exception e) {
            return Result.error("数据库状态检查失败：" + e.getMessage());
        }
    }

    private void createTables() {
        // 创建员工表
        jdbcTemplate.execute("""
            CREATE TABLE IF NOT EXISTS employee (
                id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '员工ID',
                employee_number VARCHAR(20) NOT NULL UNIQUE COMMENT '员工编号',
                name VARCHAR(50) NOT NULL COMMENT '姓名',
                gender TINYINT NOT NULL DEFAULT 1 COMMENT '性别：1-男，2-女',
                age INT COMMENT '年龄',
                email VARCHAR(100) COMMENT '邮箱',
                phone VARCHAR(20) COMMENT '电话',
                department VARCHAR(50) COMMENT '部门',
                position VARCHAR(50) COMMENT '职位',
                salary DECIMAL(10,2) COMMENT '薪资',
                hire_date DATE COMMENT '入职日期',
                status TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1-在职，2-离职',
                deleted TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
                create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='员工表'
            """);

        // 创建用户表
        jdbcTemplate.execute("""
            CREATE TABLE IF NOT EXISTS sys_user (
                id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '用户ID',
                username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
                password VARCHAR(255) NOT NULL COMMENT '密码（加密）',
                real_name VARCHAR(50) COMMENT '真实姓名',
                email VARCHAR(100) COMMENT '邮箱',
                phone VARCHAR(20) COMMENT '手机号',
                avatar VARCHAR(255) COMMENT '头像URL',
                status TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1-正常，0-禁用',
                deleted TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
                create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统用户表'
            """);

        // 创建角色表
        jdbcTemplate.execute("""
            CREATE TABLE IF NOT EXISTS sys_role (
                id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '角色ID',
                role_name VARCHAR(50) NOT NULL UNIQUE COMMENT '角色名称',
                role_key VARCHAR(50) NOT NULL UNIQUE COMMENT '角色权限字符串',
                description VARCHAR(255) COMMENT '角色描述',
                status TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1-正常，0-禁用',
                deleted TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
                create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统角色表'
            """);

        // 创建用户角色关联表
        jdbcTemplate.execute("""
            CREATE TABLE IF NOT EXISTS sys_user_role (
                id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'ID',
                user_id BIGINT NOT NULL COMMENT '用户ID',
                role_id BIGINT NOT NULL COMMENT '角色ID',
                create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                UNIQUE KEY uk_user_role (user_id, role_id),
                FOREIGN KEY (user_id) REFERENCES sys_user(id) ON DELETE CASCADE,
                FOREIGN KEY (role_id) REFERENCES sys_role(id) ON DELETE CASCADE
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户角色关联表'
            """);
    }

    private void insertInitialData() {
        // 插入员工测试数据
        jdbcTemplate.execute("""
            INSERT IGNORE INTO employee (employee_number, name, gender, age, email, phone, department, position, salary, hire_date, status) VALUES
            ('EMP001', '张三', 1, 28, 'zhangsan@company.com', '13800138001', '技术部', 'Java开发工程师', 12000.00, '2023-01-15', 1),
            ('EMP002', '李四', 2, 25, 'lisi@company.com', '13800138002', '技术部', '前端开发工程师', 10000.00, '2023-03-01', 1),
            ('EMP003', '王五', 1, 30, 'wangwu@company.com', '13800138003', '产品部', '产品经理', 15000.00, '2023-02-10', 1),
            ('EMP004', '赵六', 2, 26, 'zhaoliu@company.com', '13800138004', '设计部', 'UI设计师', 9000.00, '2023-04-20', 1),
            ('EMP005', '钱七', 1, 32, 'qianqi@company.com', '13800138005', '技术部', '架构师', 18000.00, '2022-12-01', 1)
            """);

        // 插入角色数据
        jdbcTemplate.execute("""
            INSERT IGNORE INTO sys_role (id, role_name, role_key, description, status) VALUES
            (1, '管理员', 'ADMIN', '系统管理员，拥有所有权限', 1),
            (2, '人事经理', 'HR_MANAGER', '人事部经理，可以管理员工信息', 1),
            (3, '普通用户', 'USER', '普通用户，只能查看员工信息', 1)
            """);

        // 插入用户数据（密码：hello）
        jdbcTemplate.execute("""
            INSERT IGNORE INTO sys_user (id, username, password, real_name, email, phone, status) VALUES
            (1, 'admin', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', '系统管理员', 'admin@company.com', '13900139001', 1),
            (2, 'hrmanager', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', '人事经理', 'hr@company.com', '13900139002', 1),
            (3, 'user', '$2a$10$92IXUNpkjO0rOQ5byMmiXeobqm5aRfnFINVdpC5PCyxLgFPyfqzvi', '普通用户', 'user@company.com', '13900139003', 1)
            """);

        // 插入用户角色关联
        jdbcTemplate.execute("""
            INSERT IGNORE INTO sys_user_role (user_id, role_id) VALUES
            (1, 1), (2, 2), (3, 3)
            """);
    }

    private void clearAndInsertData() {
        // 清空关联表
        jdbcTemplate.execute("DELETE FROM sys_user_role");
        jdbcTemplate.execute("DELETE FROM sys_user");
        jdbcTemplate.execute("DELETE FROM sys_role");
        jdbcTemplate.execute("DELETE FROM employee WHERE employee_number LIKE 'EMP%'");

        // 重置自增ID
        jdbcTemplate.execute("ALTER TABLE sys_role AUTO_INCREMENT = 1");
        jdbcTemplate.execute("ALTER TABLE sys_user AUTO_INCREMENT = 1");
        jdbcTemplate.execute("ALTER TABLE sys_user_role AUTO_INCREMENT = 1");

        // 重新插入数据
        insertInitialData();
    }
}