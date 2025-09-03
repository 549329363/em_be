package com.employee.management.controller;

import com.employee.management.dto.LoginRequest;
import com.employee.management.dto.LoginResponse;
import com.employee.management.dto.Result;
import com.employee.management.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/auth")
@CrossOrigin
public class AuthController {

    @Autowired
    private AuthService authService;

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public Result<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        try {
            LoginResponse response = authService.login(loginRequest);
            return Result.success("登录成功", response);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取当前用户信息
     */
    @GetMapping("/userinfo")
    public Result<LoginResponse> getUserInfo(HttpServletRequest request) {
        try {
            String token = getTokenFromRequest(request);
            if (token == null) {
                return Result.error(401, "未找到Token");
            }

            LoginResponse userInfo = authService.getUserInfoFromToken(token);
            if (userInfo == null) {
                return Result.error(401, "Token无效");
            }

            return Result.success(userInfo);
        } catch (Exception e) {
            return Result.error("获取用户信息失败");
        }
    }

    /**
     * 用户登出
     */
    @PostMapping("/logout")
    public Result<String> logout() {
        // 由于使用JWT，登出只需要前端删除Token即可
        return Result.success("登出成功");
    }

    /**
     * 验证Token
     */
    @PostMapping("/validate")
    public Result<Boolean> validateToken(HttpServletRequest request) {
        try {
            String token = getTokenFromRequest(request);
            if (token == null) {
                return Result.success(false);
            }

            boolean isValid = authService.validateToken(token);
            return Result.success(isValid);
        } catch (Exception e) {
            return Result.success(false);
        }
    }

    /**
     * 从请求中获取Token
     */
    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}