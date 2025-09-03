package com.employee.management.controller;

import com.employee.management.dto.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 密码重置控制器
 * 解决BCrypt密码验证问题
 */
@RestController
@RequestMapping("/public/password")
public class PasswordResetController {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * 重置所有用户密码为hello
     */
    @PostMapping("/reset-all")
    public Result<String> resetAllPasswords() {
        try {
            // 生成新的BCrypt密码哈希
            String encodedPassword = passwordEncoder.encode("hello");
            
            // 更新所有用户的密码
            String sql = "UPDATE sys_user SET password = ? WHERE username IN ('admin', 'hrmanager', 'user')";
            int updatedRows = jdbcTemplate.update(sql, encodedPassword);
            
            return Result.success("密码重置完成！已更新 " + updatedRows + " 个用户的密码为 'hello'");
        } catch (Exception e) {
            return Result.error("密码重置失败：" + e.getMessage());
        }
    }
    
    /**
     * 测试密码验证
     */
    @PostMapping("/test-verify")
    public Result<String> testPasswordVerify() {
        try {
            // 获取数据库中的密码哈希
            String dbPassword = jdbcTemplate.queryForObject(
                "SELECT password FROM sys_user WHERE username = 'admin'", 
                String.class
            );
            
            // 测试密码验证
            boolean matches = passwordEncoder.matches("hello", dbPassword);
            
            return Result.success("密码验证结果: " + (matches ? "成功" : "失败") + 
                    "，数据库密码哈希: " + dbPassword.substring(0, 20) + "...");
        } catch (Exception e) {
            return Result.error("密码验证测试失败：" + e.getMessage());
        }
    }
}