package com.software.testing.usermodule.controller;

import com.software.testing.common.Result;
import com.software.testing.usermodule.dto.LoginDTO;
import com.software.testing.usermodule.dto.ChangePasswordDTO;
import com.software.testing.usermodule.dto.AdminResetPasswordDTO;
import com.software.testing.usermodule.entity.User;
import com.software.testing.usermodule.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public Result<?> login(@Valid @RequestBody LoginDTO loginDTO) {
        return authService.login(loginDTO);
    }

    @PostMapping("/register")
    public Result<?> register(@RequestBody User user) {
        return authService.register(user);
    }

    @GetMapping("/userInfo")
    public Result<?> getUserInfo(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return authService.getUserInfo(userId);
    }

    /** 修改密码（所有角色通用） */
    @PostMapping("/changePassword")
    public Result<?> changePassword(HttpServletRequest request,
                                     @RequestBody ChangePasswordDTO dto) {
        Long userId = (Long) request.getAttribute("userId");
        return authService.changePassword(userId, dto);
    }

    /** 更新个人信息（教师/学生可修改姓名、性别、邮箱） */
    @PutMapping("/updateProfile")
    public Result<?> updateProfile(HttpServletRequest request,
                                    @RequestBody User user) {
        Long userId = (Long) request.getAttribute("userId");
        return authService.updateProfile(userId, user);
    }

    /** 管理员注册用户 */
    @PostMapping("/admin/register")
    public Result<?> adminRegister(HttpServletRequest request,
                                    @RequestBody User user) {
        Long adminId = (Long) request.getAttribute("userId");
        return authService.adminRegister(user, adminId);
    }

    /** 管理员重置密码 */
    @PostMapping("/admin/resetPassword")
    public Result<?> adminResetPassword(HttpServletRequest request,
                                         @RequestBody AdminResetPasswordDTO dto) {
        Long adminId = (Long) request.getAttribute("userId");
        return authService.adminResetPassword(dto, adminId);
    }

    /** 管理员首页：获取全平台统计数据 */
    @GetMapping("/admin/dashboard-stats")
    public Result<?> getAdminDashboardStats(HttpServletRequest request) {
        Long adminId = (Long) request.getAttribute("userId");
        return authService.getAdminDashboardStats(adminId);
    }

    /** 管理员查看所有用户列表 */
    @GetMapping("/admin/users")
    public Result<?> getAdminUserList(HttpServletRequest request) {
        Long adminId = (Long) request.getAttribute("userId");
        return authService.getAdminUserList(adminId);
    }
}
