package com.employee.management.service.impl;

import com.employee.management.dto.LoginRequest;
import com.employee.management.dto.LoginResponse;
import com.employee.management.entity.SysUser;
import com.employee.management.mapper.SysUserMapper;
import com.employee.management.service.AuthService;
import com.employee.management.utils.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public LoginResponse login(LoginRequest loginRequest) {
        try {
            log.info("开始登录验证，用户名: {}", loginRequest.getUsername());
            
            // 查询用户
            SysUser user = sysUserMapper.findByUsername(loginRequest.getUsername());
            if (user == null) {
                log.warn("用户不存在: {}", loginRequest.getUsername());
                throw new RuntimeException("用户名或密码错误");
            }
            
            log.info("找到用户: {}, 状态: {}", user.getUsername(), user.getStatus());

            // 验证密码
            boolean passwordMatch = passwordEncoder.matches(loginRequest.getPassword(), user.getPassword());
            log.info("密码验证结果: {}", passwordMatch);
            
            if (!passwordMatch) {
                log.warn("密码错误，用户: {}", loginRequest.getUsername());
                throw new RuntimeException("用户名或密码错误");
            }

            // 检查用户状态
            if (user.getStatus() != 1) {
                throw new RuntimeException("用户已被禁用");
            }

            // 获取用户角色（添加空值检查）
            List<String> roles = null;
            try {
                roles = sysUserMapper.findRolesByUserId(user.getId());
                if (roles == null) {
                    roles = new ArrayList<>();
                    log.warn("用户 {} 没有分配角色", user.getUsername());
                }
            } catch (Exception e) {
                log.error("查询用户角色失败", e);
                roles = new ArrayList<>(); // 使用空角色列表
            }
            
            String rolesStr = String.join(",", roles);
            log.info("用户角色: {}", rolesStr);

            // 生成Token（添加空值检查）
            String token;
            try {
                token = jwtUtils.generateToken(user.getUsername(), user.getId(), rolesStr);
                if (token == null || token.isEmpty()) {
                    throw new RuntimeException("Token生成失败");
                }
            } catch (Exception e) {
                log.error("Token生成失败", e);
                throw new RuntimeException("登录失败，请稍后重试");
            }

            // 构建响应
            LoginResponse response = new LoginResponse();
            response.setToken(token);
            response.setUsername(user.getUsername());
            response.setRealName(user.getRealName());
            response.setUserId(user.getId());
            response.setRoles(roles);

            log.info("登录成功，用户: {}", user.getUsername());
            return response;
        } catch (Exception e) {
            log.error("登录失败", e);
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public boolean validateToken(String token) {
        try {
            String username = jwtUtils.getUsernameFromToken(token);
            if (username == null) {
                return false;
            }
            return jwtUtils.validateToken(token, username);
        } catch (Exception e) {
            log.error("Token验证失败", e);
            return false;
        }
    }

    @Override
    public LoginResponse getUserInfoFromToken(String token) {
        try {
            String username = jwtUtils.getUsernameFromToken(token);
            Long userId = jwtUtils.getUserIdFromToken(token);
            String rolesStr = jwtUtils.getRolesFromToken(token);

            if (username == null || userId == null) {
                return null;
            }

            SysUser user = sysUserMapper.findByUsername(username);
            if (user == null) {
                return null;
            }

            LoginResponse response = new LoginResponse();
            response.setToken(token);
            response.setUsername(user.getUsername());
            response.setRealName(user.getRealName());
            response.setUserId(user.getId());
            
            if (rolesStr != null && !rolesStr.isEmpty()) {
                response.setRoles(Arrays.asList(rolesStr.split(",")));
            } else {
                response.setRoles(new ArrayList<>());
            }

            return response;
        } catch (Exception e) {
            log.error("从Token获取用户信息失败", e);
            return null;
        }
    }
}