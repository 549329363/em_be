package com.employee.management.controller;

import com.employee.management.entity.SysUser;
import com.employee.management.mapper.SysUserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/test")
public class TestController {

    @Autowired
    private SysUserMapper sysUserMapper;

    @GetMapping("/users")
    public List<SysUser> getAllUsers() {
        return sysUserMapper.selectList(null);
    }

    @GetMapping("/admin")
    public SysUser getAdmin() {
        return sysUserMapper.findByUsername("admin");
    }
}