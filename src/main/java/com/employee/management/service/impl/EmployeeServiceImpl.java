package com.employee.management.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.employee.management.entity.Employee;
import com.employee.management.mapper.EmployeeMapper;
import com.employee.management.service.EmployeeService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements EmployeeService {

    @Override
    public IPage<Employee> getEmployeePage(int current, int size, String keyword) {
        Page<Employee> page = new Page<>(current, size);
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(keyword)) {
            queryWrapper.and(wrapper -> wrapper
                .like(Employee::getName, keyword)
                .or()
                .like(Employee::getEmployeeNumber, keyword)
                .or()
                .like(Employee::getDepartment, keyword)
                .or()
                .like(Employee::getPosition, keyword)
            );
        }
        
        queryWrapper.orderByDesc(Employee::getCreateTime);
        return this.page(page, queryWrapper);
    }

    @Override
    public Employee getByEmployeeNumber(String employeeNumber) {
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getEmployeeNumber, employeeNumber);
        return this.getOne(queryWrapper);
    }

    @Override
    public boolean checkEmployeeNumberExists(String employeeNumber, Long excludeId) {
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getEmployeeNumber, employeeNumber);
        if (excludeId != null) {
            queryWrapper.ne(Employee::getId, excludeId);
        }
        return this.count(queryWrapper) > 0;
    }

    @Override
    public List<Employee> getEmployeesForExport(String keyword) {
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(keyword)) {
            queryWrapper.and(wrapper -> wrapper
                .like(Employee::getName, keyword)
                .or()
                .like(Employee::getEmployeeNumber, keyword)
                .or()
                .like(Employee::getDepartment, keyword)
                .or()
                .like(Employee::getPosition, keyword)
            );
        }
        
        queryWrapper.orderByDesc(Employee::getCreateTime);
        return this.list(queryWrapper);
    }
}