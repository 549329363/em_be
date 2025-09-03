package com.employee.management.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.employee.management.dto.Result;
import com.employee.management.entity.Employee;
import com.employee.management.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/employee")
@CrossOrigin
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    /**
     * 分页查询员工列表
     */
    @GetMapping("/page")
    public Result<IPage<Employee>> getEmployeePage(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword) {
        IPage<Employee> page = employeeService.getEmployeePage(current, size, keyword);
        return Result.success(page);
    }

    /**
     * 查询所有员工
     */
    @GetMapping("/list")
    public Result<List<Employee>> getAllEmployees() {
        List<Employee> employees = employeeService.list();
        return Result.success(employees);
    }

    /**
     * 根据ID查询员工详情
     */
    @GetMapping("/{id}")
    public Result<Employee> getEmployeeById(@PathVariable Long id) {
        Employee employee = employeeService.getById(id);
        if (employee == null) {
            return Result.error("员工不存在");
        }
        return Result.success(employee);
    }

    /**
     * 新增员工
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('HR_MANAGER')")
    public Result<String> addEmployee(@RequestBody Employee employee) {
        // 检查员工编号是否已存在
        if (employeeService.checkEmployeeNumberExists(employee.getEmployeeNumber(), null)) {
            return Result.error("员工编号已存在");
        }
        
        boolean success = employeeService.save(employee);
        if (success) {
            return Result.success("员工添加成功");
        } else {
            return Result.error("员工添加失败");
        }
    }

    /**
     * 更新员工信息
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('HR_MANAGER')")
    public Result<String> updateEmployee(@PathVariable Long id, @RequestBody Employee employee) {
        Employee existingEmployee = employeeService.getById(id);
        if (existingEmployee == null) {
            return Result.error("员工不存在");
        }
        
        // 检查员工编号是否已存在（排除当前员工）
        if (employeeService.checkEmployeeNumberExists(employee.getEmployeeNumber(), id)) {
            return Result.error("员工编号已存在");
        }
        
        employee.setId(id);
        boolean success = employeeService.updateById(employee);
        if (success) {
            return Result.success("员工信息更新成功");
        } else {
            return Result.error("员工信息更新失败");
        }
    }

    /**
     * 删除员工（逻辑删除）
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('HR_MANAGER')")
    public Result<String> deleteEmployee(@PathVariable Long id) {
        Employee employee = employeeService.getById(id);
        if (employee == null) {
            return Result.error("员工不存在");
        }
        
        boolean success = employeeService.removeById(id);
        if (success) {
            return Result.success("员工删除成功");
        } else {
            return Result.error("员工删除失败");
        }
    }

    /**
     * 批量删除员工
     */
    @DeleteMapping("/batch")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<String> batchDeleteEmployees(@RequestBody List<Long> ids) {
        boolean success = employeeService.removeByIds(ids);
        if (success) {
            return Result.success("批量删除成功");
        } else {
            return Result.error("批量删除失败");
        }
    }

    /**
     * 导出员工CSV数据
     */
    @GetMapping("/export")
    public Result<List<Employee>> exportEmployees(
            @RequestParam(required = false) String keyword) {
        List<Employee> employees = employeeService.getEmployeesForExport(keyword);
        return Result.success(employees);
    }
}