package com.employee.management.controller;

import com.employee.management.dto.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 数据库编码修复控制器
 * 提供一次性解决中文编码问题的接口
 */
@RestController
@RequestMapping("/public/database")
public class DatabaseFixController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 一次性修复所有中文编码问题
     */
    @PostMapping("/fix-encoding")
    public Result<String> fixAllEncoding() {
        try {
            // 1. 修改数据库字符集
            jdbcTemplate.execute("ALTER DATABASE employee_management CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci");
            
            // 2. 修改所有表的字符集
            String[] tables = {"employee", "sys_user", "sys_role", "sys_user_role"};
            for (String table : tables) {
                jdbcTemplate.execute("ALTER TABLE " + table + " CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci");
            }
            
            // 3. 清空并重新插入正确编码的数据
            fixEmployeeData();
            fixUserData();
            fixRoleData();
            
            return Result.success("数据库编码修复完成！所有中文数据已重新插入。");
        } catch (Exception e) {
            return Result.error("编码修复失败：" + e.getMessage());
        }
    }
    
    /**
     * 修复员工数据
     */
    private void fixEmployeeData() {
        // 清空员工表
        jdbcTemplate.execute("DELETE FROM employee");
        jdbcTemplate.execute("ALTER TABLE employee AUTO_INCREMENT = 1");
        
        // 重新插入正确编码的员工数据
        String sql = "INSERT INTO employee (employee_number, name, gender, age, email, phone, department, position, salary, hire_date, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        jdbcTemplate.update(sql, "EMP001", "张三", 1, 28, "zhangsan@company.com", "13800138001", "技术部", "Java开发工程师", 12000.00, "2023-01-15", 1);
        jdbcTemplate.update(sql, "EMP002", "李四", 2, 25, "lisi@company.com", "13800138002", "技术部", "前端开发工程师", 10000.00, "2023-03-01", 1);
        jdbcTemplate.update(sql, "EMP003", "王五", 1, 30, "wangwu@company.com", "13800138003", "产品部", "产品经理", 15000.00, "2023-02-10", 1);
        jdbcTemplate.update(sql, "EMP004", "赵六", 2, 26, "zhaoliu@company.com", "13800138004", "设计部", "UI设计师", 9000.00, "2023-04-20", 1);
        jdbcTemplate.update(sql, "EMP005", "钱七", 1, 32, "qianqi@company.com", "13800138005", "技术部", "架构师", 18000.00, "2022-12-01", 1);
        jdbcTemplate.update(sql, "EMP006", "孙八", 2, 29, "sunba@company.com", "13800138006", "人事部", "人事专员", 8000.00, "2023-05-10", 1);
        jdbcTemplate.update(sql, "EMP007", "周九", 1, 27, "zhoujiu@company.com", "13800138007", "市场部", "市场专员", 9500.00, "2023-06-15", 1);
        jdbcTemplate.update(sql, "EMP008", "吴十", 2, 24, "wushi@company.com", "13800138008", "财务部", "会计", 7500.00, "2023-07-20", 1);
    }
    
    /**
     * 修复用户数据
     */
    private void fixUserData() {
        // 清空用户相关表
        jdbcTemplate.execute("DELETE FROM sys_user_role");
        jdbcTemplate.execute("DELETE FROM sys_user");
        jdbcTemplate.execute("ALTER TABLE sys_user AUTO_INCREMENT = 1");
        
        // 重新插入用户数据（密码都是hello的BCrypt哈希）
        String userSql = "INSERT INTO sys_user (id, username, password, real_name, email, phone, status) VALUES (?, ?, ?, ?, ?, ?, ?)";
        String bcryptPassword = "$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqfcxG33euGCSkVWhDvx.S.";
        
        jdbcTemplate.update(userSql, 1, "admin", bcryptPassword, "系统管理员", "admin@company.com", "13900139001", 1);
        jdbcTemplate.update(userSql, 2, "hrmanager", bcryptPassword, "人事经理", "hr@company.com", "13900139002", 1);
        jdbcTemplate.update(userSql, 3, "user", bcryptPassword, "普通用户", "user@company.com", "13900139003", 1);
    }
    
    /**
     * 修复角色数据
     */
    private void fixRoleData() {
        // 清空角色表
        jdbcTemplate.execute("DELETE FROM sys_role");
        jdbcTemplate.execute("ALTER TABLE sys_role AUTO_INCREMENT = 1");
        
        // 重新插入角色数据
        String roleSql = "INSERT INTO sys_role (id, role_name, role_key, description, status) VALUES (?, ?, ?, ?, ?)";
        
        jdbcTemplate.update(roleSql, 1, "管理员", "ADMIN", "系统管理员，拥有所有权限", 1);
        jdbcTemplate.update(roleSql, 2, "人事经理", "HR_MANAGER", "人事部经理，可以管理员工信息", 1);
        jdbcTemplate.update(roleSql, 3, "普通用户", "USER", "普通用户，只能查看员工信息", 1);
        
        // 重新插入用户角色关联
        jdbcTemplate.execute("DELETE FROM sys_user_role");
        jdbcTemplate.execute("ALTER TABLE sys_user_role AUTO_INCREMENT = 1");
        
        String userRoleSql = "INSERT INTO sys_user_role (user_id, role_id) VALUES (?, ?)";
        jdbcTemplate.update(userRoleSql, 1, 1); // admin -> 管理员
        jdbcTemplate.update(userRoleSql, 2, 2); // hrmanager -> 人事经理
        jdbcTemplate.update(userRoleSql, 3, 3); // user -> 普通用户
    }
    
    /**
     * 检查当前编码状态
     */
    @PostMapping("/check-encoding")
    public Result<Map<String, Object>> checkEncoding() {
        try {
            // 检查数据库字符集
            List<Map<String, Object>> dbCharset = jdbcTemplate.queryForList(
                "SELECT DEFAULT_CHARACTER_SET_NAME, DEFAULT_COLLATION_NAME FROM information_schema.SCHEMATA WHERE SCHEMA_NAME = 'employee_management'"
            );
            
            // 检查表字符集
            List<Map<String, Object>> tableCharsets = jdbcTemplate.queryForList(
                "SELECT TABLE_NAME, TABLE_COLLATION FROM information_schema.TABLES WHERE TABLE_SCHEMA = 'employee_management'"
            );
            
            // 检查样本中文数据
            List<Map<String, Object>> sampleData = jdbcTemplate.queryForList(
                "SELECT name, department, position FROM employee LIMIT 3"
            );
            
            return Result.success(Map.of(
                "数据库字符集", dbCharset,
                "表字符集", tableCharsets,
                "样本数据", sampleData
            ));
        } catch (Exception e) {
            return Result.error("检查编码失败：" + e.getMessage());
        }
    }
}