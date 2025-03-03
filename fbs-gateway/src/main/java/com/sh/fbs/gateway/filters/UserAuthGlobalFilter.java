package com.sh.fbs.gateway.filters;

import com.sh.fbs.commom.user.User;
import com.sh.fbs.gateway.session.USMgmtValidateParam;
import com.sh.fbs.gateway.session.UserSession;
import com.sh.fbs.gateway.session.UserSessionMgmtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.Objects;


@Configuration
@Order(Ordered.HIGHEST_PRECEDENCE)
public class UserAuthGlobalFilter implements GlobalFilter{
    @Autowired
    private UserSessionMgmtService userSessionMgmtService;

    private static final String USER_SESSION_KEY = "user";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        Long userId = Long.parseLong(Objects.requireNonNull(request.getHeaders().getFirst("X-User-Id")));
        int deviceType = Integer.parseInt(Objects.requireNonNull(request.getHeaders().getFirst("X-Device-Type")));
        String sessionId = Objects.requireNonNull(request.getCookies().getFirst("sessionId")).getValue();
        try {
            UserSession userSimpleDeviceSession = userSessionMgmtService.validateUserSession(USMgmtValidateParam.builder().userId(userId).deviceType(deviceType).sessionId(sessionId).build());
            exchange.getAttributes().put(USER_SESSION_KEY, userSimpleDeviceSession);
            return chain.filter(exchange);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
