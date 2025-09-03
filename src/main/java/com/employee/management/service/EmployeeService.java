package com.employee.management.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.employee.management.entity.Employee;

import java.util.List;

public interface EmployeeService extends IService<Employee> {
    
    /**
     * 分页查询员工
     */
    IPage<Employee> getEmployeePage(int current, int size, String keyword);
    
    /**
     * 根据员工编号查询员工
     */
    Employee getByEmployeeNumber(String employeeNumber);
    
    /**
     * 检查员工编号是否存在
     */
    boolean checkEmployeeNumberExists(String employeeNumber, Long excludeId);
    
    /**
     * 获取员工数据用于导出
     */
    List<Employee> getEmployeesForExport(String keyword);
}