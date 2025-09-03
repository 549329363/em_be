-- 插入测试数据
INSERT IGNORE INTO employee (employee_number, name, gender, age, email, phone, department, position, salary, hire_date, status) VALUES
('EMP001', '张三', 1, 28, 'zhangsan@company.com', '13800138001', '技术部', 'Java开发工程师', 12000.00, '2023-01-15', 1),
('EMP002', '李四', 2, 25, 'lisi@company.com', '13800138002', '技术部', '前端开发工程师', 10000.00, '2023-03-01', 1),
('EMP003', '王五', 1, 30, 'wangwu@company.com', '13800138003', '产品部', '产品经理', 15000.00, '2023-02-10', 1),
('EMP004', '赵六', 2, 26, 'zhaoliu@company.com', '13800138004', '设计部', 'UI设计师', 9000.00, '2023-04-20', 1),
('EMP005', '钱七', 1, 32, 'qianqi@company.com', '13800138005', '技术部', '架构师', 18000.00, '2022-12-01', 1);

-- 插入系统用户（密码都是hello，使用BCrypt加密）
-- 使用DELETE和INSERT来确保数据更新
DELETE FROM sys_user_role;
DELETE FROM sys_user;
DELETE FROM sys_role;

-- 重置AUTO_INCREMENT
ALTER TABLE sys_role AUTO_INCREMENT = 1;
ALTER TABLE sys_user AUTO_INCREMENT = 1;
ALTER TABLE sys_user_role AUTO_INCREMENT = 1;

-- 重新插入角色（指定ID）
INSERT INTO sys_role (id, role_name, role_key, description, status) VALUES
(1, '管理员', 'ADMIN', '系统管理员，拥有所有权限', 1),
(2, '人事经理', 'HR_MANAGER', '人事部经理，可以管理员工信息', 1),
(3, '普通用户', 'USER', '普通用户，只能查看员工信息', 1);

-- 重新插入用户（指定ID）
INSERT INTO sys_user (id, username, password, real_name, email, phone, status) VALUES
(1, 'admin', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', '系统管理员', 'admin@company.com', '13900139001', 1),
(2, 'hrmanager', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', '人事经理', 'hr@company.com', '13900139002', 1),
(3, 'user', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', '普通用户', 'user@company.com', '13900139003', 1);

-- 重新插入用户角色关联
INSERT INTO sys_user_role (user_id, role_id) VALUES
(1, 1), -- admin 用户对应管理员角色
(2, 2), -- hrmanager 用户对应人事经理角色
(3, 3); -- user 用户对应普通用户角色