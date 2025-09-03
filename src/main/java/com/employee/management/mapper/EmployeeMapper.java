package com.employee.management.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.employee.management.entity.Employee;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface EmployeeMapper extends BaseMapper<Employee> {
}