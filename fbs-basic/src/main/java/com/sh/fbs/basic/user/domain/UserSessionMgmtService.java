package com.sh.fbs.basic.user.domain;

import com.alibaba.fastjson2.JSON;
import com.sh.fbs.basic.commom.constant.BasicAppConstant;
import com.sh.fbs.commom.user.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static java.util.Objects.hash;

@Slf4j
@Service
public class UserSessionMgmtService {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    public UserSession generateSession(UserSession userSession) {
        String sessionId = UUID.randomUUID().toString();
        userSession.setSessionId(sessionId);
        userSession.setExpireTime(System.currentTimeMillis()+BasicAppConstant.USER_SESSION_KEY_EXPIRE_TIME);
        redisTemplate.opsForHash().put(hashSessionKey(userSession.getUserId()), userSession.getUserId().toString(), JSON.toJSONString(userSession));
        return userSession;
    }

    private String hashSessionKey(Long userId) {
        int keyIndex =hash(userId)%BasicAppConstant.USER_SESSION_NODE_KEY_NUM;
        return String.format(BasicAppConstant.USER_SESSION_HASH_KEY,keyIndex);
    }
}
