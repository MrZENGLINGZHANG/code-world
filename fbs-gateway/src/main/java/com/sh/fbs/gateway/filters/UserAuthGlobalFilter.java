package com.sh.fbs.gateway.filters;

import com.sh.fbs.commom.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;


@Configuration
@Order(Ordered.HIGHEST_PRECEDENCE)
public class UserAuthGlobalFilter implements GlobalFilter{

    @Autowired
    private UserTokenService userTokenService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String authorization = request.getHeaders().getFirst("Authorization");
        User user = userTokenService.validateToken(authorization);
        exchange.getAttributes().put("user", user);
        return chain.filter(exchange);
    }

}
