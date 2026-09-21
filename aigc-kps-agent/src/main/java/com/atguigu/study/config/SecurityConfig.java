package com.atguigu.study.config;

import com.atguigu.study.util.JwtAuthticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.util.AntPathMatcher;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    private static final AntPathMatcher antPathMatcher = new AntPathMatcher();
    private static final String[] PUBLIC_PATHS = {
            "/",
            "/api/test",
            "/api/user/login",
            "/api/user/add",
            "/api/psychological-chat/**"
    };

    public static Boolean isPublicPATH(String requestUri) {
        for (String publicPath : PUBLIC_PATHS) {
            if (antPathMatcher.match(publicPath, requestUri)) {
                return true;
            }
        }
        return false;
    }

    @Bean
    public JwtAuthticationFilter jwtAuthticationFilter() {
        return new JwtAuthticationFilter();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 禁用CSRF保护 （API服务通常不需要）
                .csrf(AbstractHttpConfigurer::disable)
                // 配置会话管理为无状态（JWT需要）
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // ✅ 全部放行！认证逻辑完全交给 JwtFilter
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                // 添加JWT认证过滤器
                .addFilterBefore(jwtAuthticationFilter(), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}