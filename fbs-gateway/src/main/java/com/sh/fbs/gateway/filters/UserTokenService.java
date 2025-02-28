package com.sh.fbs.gateway.filters;

import com.alibaba.fastjson2.JSON;
import com.sh.fbs.commom.result.BizException;
import com.sh.fbs.commom.result.ErrCode;
import com.sh.fbs.commom.user.User;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserTokenService {
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    public static final String TOKEN_PREFIX = "TOKEN_";
    private static final int TOKEN_UUID_LENGTH =24;
    private static final int TOKEN_EXPIRES_HOUR = 12;

    public String genToken(User user) throws RuntimeException {
         UUID uuid = UUID.randomUUID();
         String token = TOKEN_PREFIX + uuid.toString().replace("-","").substring(TOKEN_UUID_LENGTH);
         redisTemplate.opsForValue().set(token, JSON.toJSONString(user),TOKEN_EXPIRES_HOUR);
         return token;
    }

    public User validateToken(String token) throws RuntimeException {
        if(StringUtils.isBlank(token)) {
            throw new BizException(ErrCode.NO_AUTH);
        }
        String key = TOKEN_PREFIX + token;
        String value = redisTemplate.opsForValue().get(key);
        if(value == null){
            throw new BizException(ErrCode.NO_AUTH);
        }
        return JSON.parseObject(value, User.class);

    }

    public String refreshToken(String token) throws RuntimeException {
        String key = TOKEN_PREFIX + token;
        String value = redisTemplate.opsForValue().get(key);
        redisTemplate.delete(key);
        return value;
    }

}
