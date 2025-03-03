package com.sh.fbs.gateway.session;

import com.alibaba.fastjson2.JSON;
import com.sh.fbs.commom.result.BizException;
import com.sh.fbs.gateway.errorcode.GatewayErrorCode;
import com.sh.fbs.gateway.utils.LZ4Utils;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.*;

@Slf4j
@Service
@Validated
public class UserSessionMgmtService {

    private static final String USER_SESSION_PREFIX = "user_session_";
    private static final int USER_SESSION_KEY_SHARDING = 24;
    private static final int USER_SESSION_KEY_EXPIRATION = 3600*24*30;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    public String buildUserSession(@Valid USMgmtBuildParam param) throws Exception{
        String hashTableKey = buildUserSessionKey(param.getUserInfo().getUserId());
        String usCompressStr = (String) redisTemplate.opsForHash().get(hashTableKey,param.getUserInfo().getUserId().toString());
        UserSession userSession = null;
        if (StringUtils.isBlank(usCompressStr)) {
            userSession = new UserSession();
            userSession.setUserInfo(param.getUserInfo());
            Map<Integer,UserLoginInfo> userLoginInfoMap = new HashMap<>();
            UserLoginInfo userLoginInfo=buildUserLoginInfoByParam(param);
            userLoginInfoMap.put(userLoginInfo.getDeviceType(),userLoginInfo);
            userSession.setLoginInfoMap(userLoginInfoMap);
        }else {
            String usDecompressStr =LZ4Utils.decompress(usCompressStr);
            userSession = JSON.parseObject(usDecompressStr,UserSession.class);
            DeviceType deviceType = DeviceType.valueOf(param.getDeviceType());
            if(deviceType==null){
                throw new BizException(GatewayErrorCode.UNKNOWN_DEVICE_TYPE);
            }
            if(userSession.isLoggedIn(deviceType)){
                throw new BizException(GatewayErrorCode.USER_ALREADY_LOGGED_IN);
            }
            UserLoginInfo userLoginInfo=buildUserLoginInfoByParam(param);
            userSession.getLoginInfoMap().put(userLoginInfo.getDeviceType(),userLoginInfo);
        }
        usCompressStr= LZ4Utils.compress(JSON.toJSONString(userSession));
        redisTemplate.opsForHash().put(hashTableKey,userSession.getUserInfo().getUserId().toString(), usCompressStr);
        return userSession.getLoginInfoMap().get(param.getDeviceType()).getSessionId();
    }

    public UserSession validateUserSession(@Valid USMgmtValidateParam param) throws Exception {
        UserSession userSession = getUserSession(param.getUserId());
        DeviceType deviceType = DeviceType.valueOf(param.getDeviceType());
        if(deviceType==null){
            throw new BizException(GatewayErrorCode.UNKNOWN_DEVICE_TYPE);
        }
        if(!userSession.isLoggedIn(deviceType,param.getSessionId())){
            throw new BizException(GatewayErrorCode.TOKEN_EXPIRED);
        }
        return userSession.toSimpleDeviceTypeSession(deviceType);
    }

    public String refreshUserSession(@Valid USMgmtRefreshParam param) throws Exception {
        UserSession userSession = getUserSession(param.getUserId());
        String SessionId = UUID.randomUUID().toString();
        userSession.getLoginInfoMap().get(param.getDeviceType()).setSessionId(SessionId);
        userSession.getLoginInfoMap().get(param.getDeviceType()).setLoginTime(new Timestamp(System.currentTimeMillis()));
        userSession.getLoginInfoMap().get(param.getDeviceType()).setExpireTime(new Timestamp(System.currentTimeMillis() + USER_SESSION_KEY_EXPIRATION));
        redisTemplate.opsForHash().put(buildUserSessionKey(param.getUserId()),param.getUserId().toString(),LZ4Utils.compress(JSON.toJSONString(userSession)));
        return SessionId;
    }

    private UserSession getUserSession(Long userId ) throws IOException {
        String hashTableKey = buildUserSessionKey(userId);
        String usCompressStr = (String) redisTemplate.opsForHash().get(hashTableKey,userId.toString());
        if (StringUtils.isBlank(usCompressStr)) {
            throw new BizException(GatewayErrorCode.TOKEN_EXPIRED);
        }
        return JSON.parseObject(LZ4Utils.decompress(usCompressStr),UserSession.class);
    }

    private String buildUserSessionKey(long userId) {
        return USER_SESSION_PREFIX + userId%USER_SESSION_KEY_SHARDING;
    }
    private UserLoginInfo buildUserLoginInfoByParam(USMgmtBuildParam param) {
        UserLoginInfo userLoginInfo =UserLoginInfo.builder().build();
        BeanUtils.copyProperties(param,userLoginInfo);
        userLoginInfo.setSessionId(UUID.randomUUID().toString());
        userLoginInfo.setLoginTime(new Timestamp(System.currentTimeMillis()));
        userLoginInfo.setExpireTime(new Timestamp(System.currentTimeMillis() + USER_SESSION_KEY_EXPIRATION));
        return userLoginInfo;
    }

}
