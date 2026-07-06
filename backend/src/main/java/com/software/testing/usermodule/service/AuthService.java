package com.software.testing.usermodule.service;

import com.software.testing.common.Result;
import com.software.testing.usermodule.dto.LoginDTO;
import com.software.testing.usermodule.entity.User;

public interface AuthService {
    Result<?> login(LoginDTO loginDTO);
    Result<?> register(User user);
    Result<?> getUserInfo(Long userId);
}
