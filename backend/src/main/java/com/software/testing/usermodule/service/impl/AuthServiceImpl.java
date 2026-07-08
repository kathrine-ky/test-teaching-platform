package com.software.testing.usermodule.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.software.testing.common.Result;
import com.software.testing.usermodule.dto.LoginDTO;
import com.software.testing.usermodule.dto.ChangePasswordDTO;
import com.software.testing.usermodule.dto.AdminResetPasswordDTO;
import com.software.testing.usermodule.entity.User;
import com.software.testing.usermodule.mapper.UserMapper;
import com.software.testing.usermodule.service.AuthService;
import com.software.testing.util.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AuthServiceImpl implements AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthServiceImpl.class);

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public Result<?> login(LoginDTO loginDTO) {
        log.info("[登录请求] username={}", loginDTO.getUsername());
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, loginDTO.getUsername()));
        if (user == null) {
            log.warn("[登录失败] 用户不存在: {}", loginDTO.getUsername());
            return Result.error("用户名或密码错误");
        }
        if (!user.getPassword().equals(loginDTO.getPassword())) {
            log.warn("[登录失败] 密码错误: {}", loginDTO.getUsername());
            return Result.error("用户名或密码错误");
        }
        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());
        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("userId", user.getId());
        data.put("username", user.getUsername());
        data.put("realName", user.getRealName());
        data.put("role", user.getRole());
        data.put("className", user.getClassName());
        data.put("studentNo", user.getStudentNo());
        data.put("email", user.getEmail());
        data.put("gender", user.getGender());
        log.info("[登录成功] userId={}, username={}, role={}", user.getId(), user.getUsername(), user.getRole());
        return Result.success("登录成功", data);
    }

    @Override
    public Result<?> register(User user) {
        log.info("[注册请求] username={}", user.getUsername());
        User exist = userMapper.selectOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, user.getUsername()));
        if (exist != null) {
            log.warn("[注册失败] 用户名已存在: {}", user.getUsername());
            return Result.error("用户名已存在");
        }
        userMapper.insert(user);
        log.info("[注册成功] userId={}, username={}", user.getId(), user.getUsername());
        return Result.success("注册成功", null);
    }

    @Override
    public Result<?> getUserInfo(Long userId) {
        log.info("[查询用户信息] userId={}", userId);
        User user = userMapper.selectById(userId);
        if (user != null) {
            user.setPassword(null);
        }
        return Result.success(user);
    }

    @Override
    public Result<?> changePassword(Long userId, ChangePasswordDTO dto) {
        log.info("[修改密码] userId={}", userId);
        User user = userMapper.selectById(userId);
        if (user == null) {
            return Result.error("用户不存在");
        }
        // 教师和学生需要通过旧密码验证；管理员不需要
        if (!"ADMIN".equals(user.getRole())) {
            if (dto.getOldPassword() == null || dto.getOldPassword().isEmpty()) {
                return Result.error("请输入旧密码");
            }
            if (!user.getPassword().equals(dto.getOldPassword())) {
                log.warn("[修改密码失败] 旧密码错误: userId={}", userId);
                return Result.error("旧密码错误");
            }
        }
        if (dto.getNewPassword() == null || dto.getNewPassword().length() < 6) {
            return Result.error("新密码至少需要6位");
        }
        user.setPassword(dto.getNewPassword());
        userMapper.updateById(user);
        log.info("[修改密码成功] userId={}", userId);
        return Result.success("密码修改成功", null);
    }

    @Override
    public Result<?> updateProfile(Long userId, User updatedUser) {
        log.info("[更新个人信息] userId={}", userId);
        User user = userMapper.selectById(userId);
        if (user == null) {
            return Result.error("用户不存在");
        }
        // 只允许修改用户名、性别、邮箱
        if (updatedUser.getUsername() != null && !updatedUser.getUsername().isEmpty()) {
            // 检查新用户名是否已被他人占用
            User exist = userMapper.selectOne(new LambdaQueryWrapper<User>()
                    .eq(User::getUsername, updatedUser.getUsername())
                    .ne(User::getId, userId));
            if (exist != null) {
                return Result.error("用户名已被占用");
            }
            user.setUsername(updatedUser.getUsername());
        }
        if (updatedUser.getGender() != null && !updatedUser.getGender().isEmpty()) {
            user.setGender(updatedUser.getGender());
        }
        if (updatedUser.getEmail() != null && !updatedUser.getEmail().isEmpty()) {
            user.setEmail(updatedUser.getEmail());
        }
        userMapper.updateById(user);
        // 返回更新后的用户信息（隐藏密码）
        user.setPassword(null);
        log.info("[更新个人信息成功] userId={}", userId);
        return Result.success("个人信息更新成功", user);
    }

    @Override
    public Result<?> adminRegister(User user, Long adminId) {
        log.info("[管理员注册用户] adminId={}, username={}, role={}", adminId, user.getUsername(), user.getRole());
        // 验证操作者是管理员
        User admin = userMapper.selectById(adminId);
        if (admin == null || !"ADMIN".equals(admin.getRole())) {
            return Result.error(403, "无权限操作");
        }
        // 管理员不能创建管理员
        if ("ADMIN".equals(user.getRole())) {
            return Result.error("不能创建管理员账号");
        }
        // 角色必须是 TEACHER 或 STUDENT
        if (!"TEACHER".equals(user.getRole()) && !"STUDENT".equals(user.getRole())) {
            return Result.error("角色只能是 TEACHER 或 STUDENT");
        }
        // 检查用户名是否已存在
        User exist = userMapper.selectOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, user.getUsername()));
        if (exist != null) {
            return Result.error("用户名已存在");
        }
        // 检查学号是否已存在
        if (user.getStudentNo() != null && !user.getStudentNo().isEmpty()) {
            User existNo = userMapper.selectOne(new LambdaQueryWrapper<User>()
                    .eq(User::getStudentNo, user.getStudentNo()));
            if (existNo != null) {
                return Result.error("学号已存在");
            }
        }
        userMapper.insert(user);
        log.info("[管理员注册成功] userId={}, username={}, role={}", user.getId(), user.getUsername(), user.getRole());
        return Result.success("注册成功", null);
    }

    @Override
    public Result<?> adminResetPassword(AdminResetPasswordDTO dto, Long adminId) {
        log.info("[管理员重置密码] adminId={}, studentNo={}", adminId, dto.getStudentNo());
        // 验证操作者是管理员
        User admin = userMapper.selectById(adminId);
        if (admin == null || !"ADMIN".equals(admin.getRole())) {
            return Result.error(403, "无权限操作");
        }
        if (dto.getNewPassword() == null || dto.getNewPassword().length() < 6) {
            return Result.error("新密码至少需要6位");
        }
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>()
                .eq(User::getStudentNo, dto.getStudentNo()));
        if (user == null) {
            return Result.error("未找到该学号的用户");
        }
        user.setPassword(dto.getNewPassword());
        userMapper.updateById(user);
        log.info("[管理员重置密码成功] targetUserId={}, studentNo={}", user.getId(), dto.getStudentNo());
        return Result.success("密码重置成功", null);
    }

    @Override
    public Result<?> getAdminDashboardStats(Long adminId) {
        log.info("[管理员首页统计] adminId={}", adminId);
        // 验证操作者是管理员
        User admin = userMapper.selectById(adminId);
        if (admin == null || !"ADMIN".equals(admin.getRole())) {
            return Result.error(403, "无权限操作");
        }

        Map<String, Object> stats = new HashMap<>();

        // 总用户数
        Long totalUsers = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM t_user", Long.class);
        // 教师数
        Long teacherCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM t_user WHERE role = 'TEACHER'", Long.class);
        // 学生数
        Long studentCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM t_user WHERE role = 'STUDENT'", Long.class);

        // 总课程数
        Long totalCourses = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM t_course", Long.class);
        // 总任务数（已发布的）
        Long totalTasks = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM t_experiment_task WHERE status = 1", Long.class);
        // 总提交数
        Long totalSubmissions = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM t_submission WHERE status IN (1, 2)", Long.class);

        stats.put("totalUsers", totalUsers != null ? totalUsers : 0);
        stats.put("teacherCount", teacherCount != null ? teacherCount : 0);
        stats.put("studentCount", studentCount != null ? studentCount : 0);
        stats.put("totalCourses", totalCourses != null ? totalCourses : 0);
        stats.put("totalTasks", totalTasks != null ? totalTasks : 0);
        stats.put("totalSubmissions", totalSubmissions != null ? totalSubmissions : 0);

        log.info("[管理员首页统计结果] {}", stats);
        return Result.success(stats);
    }

    @Override
    public Result<?> getAdminUserList(Long adminId) {
        log.info("[管理员查看用户列表] adminId={}", adminId);
        // 验证操作者是管理员
        User admin = userMapper.selectById(adminId);
        if (admin == null || !"ADMIN".equals(admin.getRole())) {
            return Result.error(403, "无权限操作");
        }
        // 查询所有非管理员用户（教师和学生）
        List<User> users = userMapper.selectList(
                new LambdaQueryWrapper<User>()
                        .ne(User::getRole, "ADMIN")
                        .orderByAsc(User::getRole)
                        .orderByAsc(User::getCreateTime)
        );
        // 隐藏密码
        users.forEach(u -> u.setPassword(null));
        log.info("[管理员查看用户列表] 共{}条记录", users.size());
        return Result.success(users);
    }
}
