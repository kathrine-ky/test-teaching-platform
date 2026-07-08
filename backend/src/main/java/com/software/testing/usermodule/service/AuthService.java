package com.software.testing.usermodule.service;

import com.software.testing.common.Result;
import com.software.testing.usermodule.dto.LoginDTO;
import com.software.testing.usermodule.dto.ChangePasswordDTO;
import com.software.testing.usermodule.dto.AdminResetPasswordDTO;
import com.software.testing.usermodule.entity.User;

public interface AuthService {
    Result<?> login(LoginDTO loginDTO);
    Result<?> register(User user);
    Result<?> getUserInfo(Long userId);
    Result<?> changePassword(Long userId, ChangePasswordDTO dto);
    Result<?> updateProfile(Long userId, User user);
    Result<?> adminRegister(User user, Long adminId);
    Result<?> adminResetPassword(AdminResetPasswordDTO dto, Long adminId);

    /** 管理员首页：获取全平台统计数据 */
    Result<?> getAdminDashboardStats(Long adminId);

    /** 管理员查看所有用户列表 */
    Result<?> getAdminUserList(Long adminId);
}
