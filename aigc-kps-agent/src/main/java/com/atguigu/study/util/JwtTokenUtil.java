package com.atguigu.study.util;

import com.atguigu.study.config.JwtConfig;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class JwtTokenUtil implements ApplicationContextAware {
    private static final String ISSUER = "mental-health-assistant";

    // Token 黑名单：key=token, value=该 token 的过期时间戳（毫秒）
    private static final Map<String, Long> TOKEN_BLACKLIST = new ConcurrentHashMap<>();

    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        JwtTokenUtil.applicationContext = applicationContext;
    }

    private static JwtConfig getJwtConfig() {
        return applicationContext.getBean(JwtConfig.class);
    }

    /** 将 token 加入黑名单 */
    public static void addToBlacklist(String token) {
        cleanExpiredBlacklist();
        try {
            DecodedJWT jwt = verifyToken(token);
            long expireAt = jwt.getExpiresAt().getTime();
            TOKEN_BLACKLIST.put(token, expireAt);
        } catch (Exception ignored) {
            // token 本身已无效，直接忽略
        }
    }

    /** 判断 token 是否已被注销（在黑名单中） */
    public static boolean isTokenBlacklisted(String token) {
        cleanExpiredBlacklist();
        return TOKEN_BLACKLIST.containsKey(token);
    }

    /** 清理黑名单中已过期的条目，避免内存泄漏 */
    private static void cleanExpiredBlacklist() {
        long now = System.currentTimeMillis();
        Iterator<Map.Entry<String, Long>> it = TOKEN_BLACKLIST.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, Long> entry = it.next();
            if (entry.getValue() < now) {
                it.remove();
            }
        }
    }

    // 生成token的方法
    public static String generateToken(Long userId, String username, Integer roleType) {
        try {
            JwtConfig jwtConfig = getJwtConfig();
            Algorithm algorithm = Algorithm.HMAC256(jwtConfig.getSecret());
            Date expiration = new Date(System.currentTimeMillis() + jwtConfig.getExpiration());

            String token = JWT.create()
                    .withClaim("userId", userId)
                    .withClaim("username", username)
                    .withClaim("roleType", roleType)
                    .withExpiresAt(expiration)
                    .withIssuedAt(new Date())
                    .withIssuer(ISSUER)
                    .sign(algorithm);
            return token;
        } catch (Exception e) {
            throw new RuntimeException("生成token 失败: " + e);
        }
    }

    // 提取token
    public static String extractTokenFromRequest(HttpServletRequest request) {
        if (request == null) {
            return null;
        }
        String tokenHeader = request.getHeader("token");
        if (StringUtils.hasText(tokenHeader)) {
            return tokenHeader;
        }
        return null;
    }

    // 获取当前token
    public static String getCurrentToken() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            String token = (String) request.getAttribute("jwtToken");
            if (token != null) {
                return token;
            }
            String headerToken = extractTokenFromRequest(request);
            return headerToken;
        }
        return null;
    }

    // 验证token
    public static TokenVerificationResult validateToken(String token) {
        DecodedJWT jwt = verifyToken(token);
        Long userId = jwt.getClaim("userId").asLong();
        String username = jwt.getClaim("username").asString();

        Integer roleType = null;
        try {
            roleType = jwt.getClaim("roleType").asInt();
        } catch (Exception e) {
            String roleTypeStr = jwt.getClaim("roleType").asString();
            if (StringUtils.hasText(roleTypeStr)) {
                roleType = Integer.valueOf(roleTypeStr);
            }
        }
        if (userId != null && StringUtils.hasText(username) && roleType != null) {
            return new TokenVerificationResult(userId, username, roleType, true);
        }
        return null;
    }

    // 验证token有效性
    public static DecodedJWT verifyToken(String token) {
        if (!StringUtils.hasText(token)) {
            throw new JWTVerificationException("Token不能为空");
        }
        JwtConfig jwtConfig = getJwtConfig();
        Algorithm algorithm = Algorithm.HMAC256(jwtConfig.getSecret());
        JWTVerifier verifier = JWT.require(algorithm)
                .withIssuer(ISSUER)
                .build();
        return verifier.verify(token);
    }

    @Getter
    public static class TokenVerificationResult {
        private final Long userId;
        private final String username;
        private final Integer roleType;
        private final boolean valid;

        public TokenVerificationResult(Long userId, String username, Integer roleType, boolean valid) {
            this.userId = userId;
            this.username = username;
            this.roleType = roleType;
            this.valid = valid;
        }
    }
}