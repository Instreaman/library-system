package com.example.library.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.library.common.Result;
import com.example.library.dto.LoginDTO;
import com.example.library.entity.User;
import com.example.library.mapper.UserMapper;
import com.example.library.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 认证控制器
 */
@RestController
@RequestMapping("/api")
public class AuthController {

    @Autowired
    private UserMapper userMapper;

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody LoginDTO loginDTO) {
        // 1. 查询用户
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, loginDTO.getUsername());
        User user = userMapper.selectOne(wrapper);

        // 2. 验证用户是否存在
        if (user == null) {
            return Result.error("用户不存在");
        }

        // 3. 验证密码
        if (!user.getPassword().equals(loginDTO.getPassword())) {
            return Result.error("密码错误");
        }

        // 4. 验证用户状态
        if (!"ACTIVE".equals(user.getStatus())) {
            return Result.error("账号已被禁用");
        }

        // 5. 生成 Token
        String token = JwtUtils.createToken(user.getId(), user.getUsername(), user.getRole());

        // 6. 返回登录信息
        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("role", user.getRole());
        data.put("username", user.getUsername());
        data.put("realName", user.getRealName());

        return Result.success("登录成功", data);
    }
}
