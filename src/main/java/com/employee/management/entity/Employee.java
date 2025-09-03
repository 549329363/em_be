package com.employee.management.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("employee")
public class Employee {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    @TableField("employee_number")
    private String employeeNumber;
    
    private String name;
    
    private Integer gender;
    
    private Integer age;
    
    private String email;
    
    private String phone;
    
    private String department;
    
    private String position;
    
    private BigDecimal salary;
    
    @TableField("hire_date")
    private LocalDate hireDate;
    
    private Integer status;
    
    @TableLogic
    private Integer deleted;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}