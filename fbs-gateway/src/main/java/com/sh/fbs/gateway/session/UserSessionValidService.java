package com.sh.fbs.gateway.session;

import com.alibaba.fastjson2.JSON;
import com.sh.fbs.commom.result.BizException;
import com.sh.fbs.commom.user.User;
import com.sh.fbs.gateway.common.constant.GatewayConstant;
import com.sh.fbs.gateway.common.ecode.GatewayErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import static java.util.Objects.hash;

@Slf4j
@Service
@Validated
public class UserSessionValidService {

    private static final String USER_SESSION_PREFIX = "user_session_";
    private static final int USER_SESSION_KEY_SHARDING = 24;
    private static final int USER_SESSION_KEY_EXPIRATION = 3600*24*30;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    public User validateUserSession(Long userId,String sessionId) throws Exception {
        if (StringUtils.isBlank(sessionId)) {
            throw new BizException(GatewayErrorCode.PARAMS_ERROR.getCode(),String.format(GatewayErrorCode.PARAMS_ERROR.getMessage(),"sessionId"));
        }
        if (userId == null) {
            throw new BizException(GatewayErrorCode.PARAMS_ERROR.getCode(),String.format(GatewayErrorCode.PARAMS_ERROR.getMessage(),"userId"));
        }
        String sessionKey = hashSessionKey(userId);
        String sessionStr =(String) redisTemplate.opsForHash().get(sessionKey,userId.toString());
        if (StringUtils.isBlank(sessionStr)) {
            throw new BizException(GatewayErrorCode.SESSION_EXPIRED);
        }
        UserSession userSession = JSON.parseObject(sessionStr,UserSession.class);
        if (userSession.getExpireTime() < System.currentTimeMillis()) {
            throw new BizException(GatewayErrorCode.SESSION_EXPIRED);
        }
        if (!userSession.getSessionId().equals(sessionId)) {
            throw new BizException(GatewayErrorCode.SESSION_EXPIRED);
        }
        return userSession.getUser();
    }

    private String hashSessionKey(Long userId) {
        int keyIndex =hash(userId)% GatewayConstant.USER_SESSION_NODE_KEY_NUM;
        return String.format(GatewayConstant.USER_SESSION_HASH_KEY,keyIndex);
    }

}
