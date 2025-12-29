package com.example.library.utils;

import cn.hutool.core.date.DateUtil;
import cn.hutool.jwt.JWT;
import cn.hutool.jwt.JWTUtil;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT 工具类
 */
public class JwtUtils {
    /**
     * JWT 签名密钥（生产环境应从配置文件读取）
     */
    private static final String SECRET_KEY = "library-system-secret-key-2024";

    /**
     * Token 过期时间：7天（毫秒）
     */
    private static final long EXPIRE_TIME = 7 * 24 * 60 * 60 * 1000;

    /**
     * 生成 Token
     *
     * @param userId 用户ID
     * @param username 用户名
     * @param role 用户角色
     * @return JWT Token
     */
    public static String createToken(Long userId, String username, String role) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("userId", userId);
        payload.put("username", username);
        payload.put("role", role);
        payload.put("exp", DateUtil.offsetMillisecond(new Date(), (int) EXPIRE_TIME));

        return JWTUtil.createToken(payload, SECRET_KEY.getBytes());
    }

    /**
     * 验证 Token 是否有效
     *
     * @param token JWT Token
     * @return true-有效，false-无效
     */
    public static boolean verify(String token) {
        try {
            return JWTUtil.verify(token, SECRET_KEY.getBytes());
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 解析 Token，获取载荷
     *
     * @param token JWT Token
     * @return JWT 对象
     */
    public static JWT parseToken(String token) {
        return JWTUtil.parseToken(token);
    }

    /**
     * 从 Token 中获取用户ID
     *
     * @param token JWT Token
     * @return 用户ID
     */
    public static Long getUserId(String token) {
        JWT jwt = parseToken(token);
        return jwt.getPayload("userId").toString() != null ? 
               Long.valueOf(jwt.getPayload("userId").toString()) : null;
    }

    /**
     * 从 Token 中获取用户名
     *
     * @param token JWT Token
     * @return 用户名
     */
    public static String getUsername(String token) {
        JWT jwt = parseToken(token);
        return (String) jwt.getPayload("username");
    }

    /**
     * 从 Token 中获取用户角色
     *
     * @param token JWT Token
     * @return 用户角色
     */
    public static String getRole(String token) {
        JWT jwt = parseToken(token);
        return (String) jwt.getPayload("role");
    }
}
