package com.sh.fbs.gateway.filters;

import com.sh.fbs.commom.user.User;
import com.sh.fbs.gateway.common.constant.GatewayConstant;
import com.sh.fbs.gateway.common.ecode.GatewayErrorCode;
import com.sh.fbs.gateway.session.UserSessionValidService;
import com.sh.fbs.commom.result.BizException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
@Configuration
@Order(Ordered.HIGHEST_PRECEDENCE)
public class GlobalUserSessionFilter implements GlobalFilter {
    
    @Autowired
    private UserSessionValidService sessionValidService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();
        String path = request.getPath().value();

        // 1. 检查是否需要进行会话验证
        if (isAuthExcludedPath(path)) {
            return chain.filter(exchange);
        }

        try {
            // 2. 获取用户ID和会话ID
            String userId = request.getHeaders().getFirst(GatewayConstant.USER_ID_HEADER);
            var sessionCookie = request.getCookies().getFirst(GatewayConstant.SESSION_ID_COOKIE);
            String sessionId = sessionCookie != null ? sessionCookie.getValue() : null;

            // 3. 验证基本参数
            if (StringUtils.isBlank(userId) || StringUtils.isBlank(sessionId)) {
                log.warn("Missing authentication credentials - userId: {}, sessionId: {}", userId, sessionId);
                response.setStatusCode(HttpStatus.UNAUTHORIZED);
                return response.setComplete();
            }

            // 4. 验证会话
            User user = sessionValidService.validateUserSession(Long.parseLong(userId), sessionId);
            
            // 5. 将用户信息添加到请求上下文
            exchange.getAttributes().put(GatewayConstant.REQUEST_CONTEXT_USER_INFO_ATTR, user);
            
            // 6. 继续处理请求
            return chain.filter(exchange);

        } catch (NumberFormatException e) {
            log.error("Invalid user ID format", e);
            response.setStatusCode(HttpStatus.BAD_REQUEST);
            return response.setComplete();
        } catch (BizException e) {
            log.warn("Session validation failed: {}", e.getMessage());
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.setComplete();
        } catch (Exception e) {
            log.error("Unexpected error during session validation", e);
            response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
            return response.setComplete();
        }
    }

    private boolean isAuthExcludedPath(String path) {
        return path.startsWith(GatewayConstant.REQUEST_NO_NEED_AUTH);
    }
}
