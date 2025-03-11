package com.sh.fbs.gateway.filters;

import com.sh.fbs.gateway.common.constant.GatewayConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@Configuration
@Order(-1)
public class GlobalCSRFFilter implements GlobalFilter {
    
    // 需要CSRF验证的HTTP方法
    private static final Set<HttpMethod> CSRF_METHODS = new HashSet<>(Arrays.asList(
        HttpMethod.POST,
        HttpMethod.PUT,
        HttpMethod.DELETE,
        HttpMethod.PATCH
    ));

    // 不需要CSRF验证的路径
    private static final Set<String> CSRF_EXCLUDE_PATHS = new HashSet<>(Arrays.asList(
        GatewayConstant.CSRF_EXCLUDE_LOGIN,
        GatewayConstant.CSRF_EXCLUDE_REGISTER,
        GatewayConstant.CSRF_EXCLUDE_CAPTCHA
    ));

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();

        String path = request.getPath().value();
        HttpMethod method = request.getMethod();

        // 1. 检查是否需要CSRF验证
        if (isPathExcluded(path) || !CSRF_METHODS.contains(method)) {
            return chain.filter(exchange);
        }

        // 2. 获取请求头中的CSRF Token
        String headerToken = request.getHeaders().getFirst(GatewayConstant.CSRF_TOKEN_HEADER);
        if (headerToken == null || headerToken.isEmpty()) {
            log.warn("CSRF token missing in request header for path: {}", path);
            response.setStatusCode(HttpStatus.FORBIDDEN);
            return response.setComplete();
        }

        // 3. 获取Cookie中的CSRF Token
        String cookieToken = getCsrfTokenFromCookie(request);
        if (cookieToken == null) {
            log.warn("CSRF token missing in cookie for path: {}", path);
            response.setStatusCode(HttpStatus.FORBIDDEN);
            return response.setComplete();
        }

        // 4. 验证Token是否匹配
        if (!headerToken.equals(cookieToken)) {
            log.warn("CSRF token mismatch for path: {}", path);
            response.setStatusCode(HttpStatus.FORBIDDEN);
            return response.setComplete();
        }

        return chain.filter(exchange);
    }

    private boolean isPathExcluded(String path) {
        return CSRF_EXCLUDE_PATHS.stream().anyMatch(path::startsWith);
    }

    private String getCsrfTokenFromCookie(ServerHttpRequest request) {
        var cookie = request.getCookies().getFirst(GatewayConstant.CSRF_TOKEN_COOKIE);
        return cookie != null ? cookie.getValue() : null;
    }
}
