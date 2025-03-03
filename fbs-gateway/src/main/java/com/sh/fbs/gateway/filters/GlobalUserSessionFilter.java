package com.sh.fbs.gateway.filters;

import com.sh.fbs.commom.user.User;
import com.sh.fbs.gateway.session.UserSessionValidService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import java.util.Objects;

import static com.sh.fbs.gateway.common.constant.GatewayConstant.REQUEST_CONTEXT_USER_INFO_ATTR;
import static com.sh.fbs.gateway.common.constant.GatewayConstant.REQUEST_NO_NEED_AUTH;


@Slf4j
@Configuration
@Order(Ordered.HIGHEST_PRECEDENCE)
public class GlobalUserSessionFilter implements GlobalFilter{
    @Autowired
    private UserSessionValidService sessionValidService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        return chain.filter(exchange);

//        String userId = request.getHeaders().getFirst("X-User-Id");
//        String sessionId =request.getCookies().getFirst("sessionId").getValue();
//        if(StringUtils.isBlank(userId) || StringUtils.isBlank(sessionId)){
//            throw new RuntimeException("userId or sessionId is blank");
//        }
//        try {
//            User user = sessionValidService.validateUserSession(Long.parseLong(userId), sessionId);
//            exchange.getAttributes().put(REQUEST_CONTEXT_USER_INFO_ATTR, user);
//            return chain.filter(exchange);
//        } catch (Exception e) {
//            log.error(e.getMessage());
//            throw new RuntimeException(e);
//        }
    }

}
