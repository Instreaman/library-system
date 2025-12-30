package com.example.library.interceptor;

import cn.hutool.core.util.StrUtil;
import com.example.library.utils.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 登录拦截器
 */
@Component
public class LoginInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) throws Exception {
        // 0. 预检请求直接放行，避免 CORS 拦截
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        // 1. 从请求头中获取 Token
        String token = request.getHeader("Authorization");
        
        // 2. 判断 Token 是否为空
        if (StrUtil.isBlank(token)) {
            writeCorsHeaders(response, request);
            response.setStatus(401);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":401,\"message\":\"未登录，请先登录\"}");
            return false;
        }

        // 3. 兼容 Bearer 前缀
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        
        // 4. 验证 Token 是否有效
        if (!JwtUtils.verify(token)) {
            writeCorsHeaders(response, request);
            response.setStatus(401);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":401,\"message\":\"Token无效或已过期\"}");
            return false;
        }

        // 5. 将 Token 中的用户信息放入请求上下文
        Long userId = JwtUtils.getUserId(token);
        String username = JwtUtils.getUsername(token);
        String role = JwtUtils.getRole(token);
        request.setAttribute("userId", userId);
        request.setAttribute("username", username);
        request.setAttribute("role", role);

        // 6. Token 验证通过，放行
        return true;
    }

    private void writeCorsHeaders(HttpServletResponse response, HttpServletRequest request) {
        String origin = request.getHeader("Origin");
        if (StrUtil.isNotBlank(origin)) {
            response.setHeader("Access-Control-Allow-Origin", origin);
        }
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Allow-Methods", "GET,POST,PUT,DELETE,OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type,Authorization");
    }
}
