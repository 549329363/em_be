package com.employee.management.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("sys_role")
public class SysRole {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    @TableField("role_name")
    private String roleName;
    
    @TableField("role_key")
    private String roleKey;
    
    private String description;
    
    private Integer status;
    
    @TableLogic
    private Integer deleted;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}