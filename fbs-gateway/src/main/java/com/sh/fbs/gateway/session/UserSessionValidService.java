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

import java.util.concurrent.TimeUnit;

import static java.util.Objects.hash;

@Slf4j
@Service
@Validated
public class UserSessionValidService {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    /**
     * 验证用户会话
     * @param userId 用户ID
     * @param sessionId 会话ID
     * @return 用户信息
     * @throws BizException 业务异常
     */
    public User validateUserSession(Long userId, String sessionId) throws BizException {
        // 1. 参数验证
        validateParams(userId, sessionId);

        // 2. 获取会话信息
        String sessionKey = hashSessionKey(userId);
        String sessionStr = getSessionFromRedis(sessionKey, userId);
        UserSession userSession = parseUserSession(sessionStr);

        // 3. 验证会话
        validateSessionStatus(userSession, sessionId);

        // 4. 检查是否需要续期
        checkAndRenewSession(sessionKey, userId.toString(), userSession);

        return userSession.getUser();
    }

    /**
     * 使会话失效
     * @param userId 用户ID
     */
    public void invalidateSession(Long userId) {
        try {
            String sessionKey = hashSessionKey(userId);
            redisTemplate.opsForHash().delete(sessionKey, userId.toString());
            log.info("Session invalidated for user: {}", userId);
        } catch (Exception e) {
            log.error("Failed to invalidate session for user: {}", userId, e);
            throw new BizException(GatewayErrorCode.SYSTEM_ERROR);
        }
    }

    private void validateParams(Long userId, String sessionId) {
        if (userId == null) {
            throw new BizException(GatewayErrorCode.PARAMS_ERROR.getCode(),
                    String.format(GatewayErrorCode.PARAMS_ERROR.getMessage(), "userId"));
        }
        if (StringUtils.isBlank(sessionId)) {
            throw new BizException(GatewayErrorCode.PARAMS_ERROR.getCode(),
                    String.format(GatewayErrorCode.PARAMS_ERROR.getMessage(), "sessionId"));
        }
    }

    private String getSessionFromRedis(String sessionKey, Long userId) {
        try {
            String sessionStr = (String) redisTemplate.opsForHash().get(sessionKey, userId.toString());
            if (StringUtils.isBlank(sessionStr)) {
                log.warn("Session not found for user: {}", userId);
                throw new BizException(GatewayErrorCode.SESSION_EXPIRED);
            }
            return sessionStr;
        } catch (BizException e) {
            throw e;
        } catch (Exception e) {
            log.error("Failed to get session from Redis for user: {}", userId, e);
            throw new BizException(GatewayErrorCode.SYSTEM_ERROR);
        }
    }

    private UserSession parseUserSession(String sessionStr) {
        try {
            return JSON.parseObject(sessionStr, UserSession.class);
        } catch (Exception e) {
            log.error("Failed to parse user session: {}", sessionStr, e);
            throw new BizException(GatewayErrorCode.SYSTEM_ERROR);
        }
    }

    private void validateSessionStatus(UserSession userSession, String sessionId) {
        if (userSession.getExpireTime() < System.currentTimeMillis()) {
            log.warn("Session expired at: {}", userSession.getExpireTime());
            throw new BizException(GatewayErrorCode.SESSION_EXPIRED);
        }
        if (!userSession.getSessionId().equals(sessionId)) {
            log.warn("Session ID mismatch. Expected: {}, Actual: {}", sessionId, userSession.getSessionId());
            throw new BizException(GatewayErrorCode.SESSION_EXPIRED);
        }
    }

    private void checkAndRenewSession(String sessionKey, String userId, UserSession userSession) {
        long remainingTime = userSession.getExpireTime() - System.currentTimeMillis();
        if (remainingTime < GatewayConstant.USER_SESSION_RENEWAL_THRESHOLD) {
            try {
                userSession.setExpireTime(System.currentTimeMillis() + GatewayConstant.USER_SESSION_KEY_EXPIRE_TIME);
                redisTemplate.opsForHash().put(sessionKey, userId, JSON.toJSONString(userSession));
                log.debug("Session renewed for user: {}", userId);
            } catch (Exception e) {
                log.error("Failed to renew session for user: {}", userId, e);
                // 续期失败不影响当前请求
            }
        }
    }

    private String hashSessionKey(Long userId) {
        int keyIndex = hash(userId) % GatewayConstant.USER_SESSION_NODE_KEY_NUM;
        return String.format(GatewayConstant.USER_SESSION_HASH_KEY, keyIndex);
    }
}
