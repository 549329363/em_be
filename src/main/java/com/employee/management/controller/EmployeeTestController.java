package com.employee.management.controller;

import com.employee.management.entity.Employee;
import com.employee.management.mapper.EmployeeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/test")
public class EmployeeTestController {

    @Autowired
    private EmployeeMapper employeeMapper;

    @GetMapping("/employees")
    public List<Employee> getAllEmployees() {
        return employeeMapper.selectList(null);
    }
}