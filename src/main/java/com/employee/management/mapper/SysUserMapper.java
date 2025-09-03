package com.employee.management.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.employee.management.entity.SysUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {
    
    /**
     * 根据用户名查询用户
     */
    @Select("SELECT * FROM sys_user WHERE username = #{username} AND status = 1 AND deleted = 0")
    SysUser findByUsername(String username);
    
    /**
     * 根据用户ID查询角色列表
     */
    @Select("SELECT r.role_key FROM sys_role r " +
            "INNER JOIN sys_user_role ur ON r.id = ur.role_id " +
            "WHERE ur.user_id = #{userId} AND r.status = 1 AND r.deleted = 0")
    List<String> findRolesByUserId(Long userId);
}