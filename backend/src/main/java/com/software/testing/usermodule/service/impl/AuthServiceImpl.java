package com.software.testing.usermodule.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.software.testing.common.Result;
import com.software.testing.usermodule.dto.LoginDTO;
import com.software.testing.usermodule.entity.User;
import com.software.testing.usermodule.mapper.UserMapper;
import com.software.testing.usermodule.service.AuthService;
import com.software.testing.util.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class AuthServiceImpl implements AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthServiceImpl.class);

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private JwtUtil jwtUtil;

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
}
