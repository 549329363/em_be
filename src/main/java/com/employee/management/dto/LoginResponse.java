package com.employee.management.dto;

import lombok.Data;
import java.util.List;

@Data
public class LoginResponse {
    private String token;
    private String username;
    private String realName;
    private List<String> roles;
    private Long userId;
}