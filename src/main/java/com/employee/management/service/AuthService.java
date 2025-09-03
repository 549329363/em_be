package com.employee.management.service;

import com.employee.management.dto.LoginRequest;
import com.employee.management.dto.LoginResponse;

public interface AuthService {
    
    /**
     * 用户登录
     */
    LoginResponse login(LoginRequest loginRequest);
    
    /**
     * 验证Token
     */
    boolean validateToken(String token);
    
    /**
     * 从Token中获取用户信息
     */
    LoginResponse getUserInfoFromToken(String token);
}