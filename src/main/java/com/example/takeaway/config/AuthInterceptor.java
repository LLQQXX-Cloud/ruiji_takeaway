/**
 * 认证拦截器
 * 验证请求中的 Authorization token，从 Redis 中获取用户/商家信息
 */
package com.example.takeaway.config;

import com.example.takeaway.service.CacheService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Autowired
    private CacheService cacheService;

    private static final String TOKEN_PREFIX = "token:";

    /** 无需认证的路径 */
    private static final String[] PUBLIC_PATHS = {
        "/api/users/login",
        "/api/users/register",
        "/api/businesses/login",
        "/api/businesses/register",
        "/api/categories",
        "/api/image/proxy"
    };

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String path = request.getRequestURI();

        // OPTIONS 预检请求放行
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        // 公开路径放行
        for (String publicPath : PUBLIC_PATHS) {
            if (path.startsWith(publicPath)) {
                return true;
            }
        }

        // 验证 token
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.setStatus(401);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"success\":false,\"message\":\"未登录\"}");
            return false;
        }

        String token = authHeader.substring(7);
        String tokenKey = TOKEN_PREFIX + token;
        Object userInfo = cacheService.get(tokenKey, Object.class);

        if (userInfo == null) {
            response.setStatus(401);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"success\":false,\"message\":\"登录已过期，请重新登录\"}");
            return false;
        }

        // 将用户信息存入 request，供后续使用
        request.setAttribute("currentUser", userInfo);
        return true;
    }
}
