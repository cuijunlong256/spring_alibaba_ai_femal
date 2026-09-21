package com.atguigu.study.util;

import cn.hutool.json.JSONUtil;
import com.atguigu.study.common.ResultCode;
import com.atguigu.study.config.SecurityConfig;
import com.atguigu.study.dto.response.UserLoginResponseDTO;
import com.atguigu.study.enumClass.UserStatus;
import com.atguigu.study.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class JwtAuthticationFilter extends OncePerRequestFilter {

    @Autowired
    private UserService userService;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        // 放行所有 OPTIONS 预检请求（CORS 需要）
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }
        String requestUri = request.getRequestURI();
        boolean isPublic = SecurityConfig.isPublicPATH(requestUri);
        System.out.println("═══════════════════════════════════════════");
//        System.out.println("🔍 [JwtFilter.shouldNotFilter]");
        System.out.println("   请求URI    : " + requestUri);
//        System.out.println("   请求方法   : " + request.getMethod());
        System.out.println("   是否公开路径: " + isPublic + (isPublic ? " → 跳过JWT校验 ✅" : " → 需要JWT校验 🛡️"));
        System.out.println("═══════════════════════════════════════════");
        return isPublic;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain) throws ServletException, IOException {

        String token = JwtTokenUtil.extractTokenFromRequest(request);
        System.out.println("🔐 [JwtFilter.doFilterInternal] 提取到的Token: " + (StringUtils.hasText(token) ? token.substring(0, Math.min(20, token.length())) + "..." : "(无Token)"));

        if (StringUtils.hasText(token)) {
            // 检查 token 是否已注销（黑名单校验）
            if (JwtTokenUtil.isTokenBlacklisted(token)) {
                System.out.println("❌ Token在黑名单中");
                clearSecurityContext();
                ResponseUtil.writeError(response, ResultCode.TOKEN_INVALID);
                return;
            }

            // 验证token并获取用户信息
            JwtTokenUtil.TokenVerificationResult validationResult = JwtTokenUtil.validateToken(token);
            if (validationResult != null && validationResult.isValid()) {
                // 查询用户信息验证用户的状态
                UserLoginResponseDTO.UserDetailResponseDTO user = userService.getUserById(validationResult.getUserId());
                System.out.println("✅ Token验证通过, 用户: " + JSONUtil.parseObj(user));
                if (user != null && UserStatus.NORMAL.getCode().equals(user.getStatus())) {
                    List<SimpleGrantedAuthority> authorities = Collections.singletonList(
                            new SimpleGrantedAuthority("ROLE_" + validationResult.getRoleType())
                    );
                    UsernamePasswordAuthenticationToken authcation = new UsernamePasswordAuthenticationToken(
                            validationResult.getUsername(),
                            null,
                            authorities
                    );
                    SecurityContextHolder.getContext().setAuthentication(authcation);
                    request.setAttribute("jwtToken", token);
                    System.out.println("✅ 用户认证成功，放行请求");
                } else {
                    System.out.println("❌ 用户状态异常, Token被禁止访问");
                    clearSecurityContext();
                    ResponseUtil.writeError(response, ResultCode.TOKEN_ACCESS_FORBIDDEN);
                }
            } else {
                System.out.println("❌ Token验证失败, 已过期或无效");
                clearSecurityContext();
                ResponseUtil.writeError(response, ResultCode.TOKEN_INVALID);
            }
        } else {
            System.out.println("❌ 没有Token → ACCESS_UNAUTHORIZED (403)");
            clearSecurityContext();
            ResponseUtil.writeError(response, ResultCode.ACCESS_UNAUTHORIZED);
            return;
        }
        chain.doFilter(request, response);
    }

    private void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }
}