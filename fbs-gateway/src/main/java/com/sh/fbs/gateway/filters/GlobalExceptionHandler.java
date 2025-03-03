package com.sh.fbs.gateway.filters;

import com.alibaba.fastjson.JSON;
import com.sh.fbs.commom.result.BizException;
import com.sh.fbs.commom.result.BizBaseErrorCode;
import com.sh.fbs.commom.result.Result;
import com.sh.fbs.commom.result.ResultUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import java.nio.charset.StandardCharsets;

@Slf4j
@Configuration
@Order(-1)
public class GlobalExceptionHandler implements ErrorWebExceptionHandler {

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        ServerHttpResponse response = exchange.getResponse();
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        if (response.isCommitted()) {
            return Mono.error(ex);
        }
        log.error(JSON.toJSONString(ex));
        //构建错误结果
        Result result = ResultUtils.buildResult(BizBaseErrorCode.FAILED, null);
        if (ex instanceof BizException) {
            result = ResultUtils.buildResult(((BizException) ex).getECode(), ex.getMessage(), null);
        }
        // 构建错误响应信息 响应码统一由业务结果code定义，取消HTTP的状态码定义
        response.setStatusCode(HttpStatus.OK);
        String errorMessage = JSON.toJSONString(result);
        byte[] bytes = errorMessage.getBytes(StandardCharsets.UTF_8);
        DataBuffer buffer = response.bufferFactory().wrap(bytes);
        // 直接返回响应
        return response.writeWith(Mono.just(buffer));
    }
}