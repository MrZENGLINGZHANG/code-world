package com.sh.fbs.gateway.session;

import com.alibaba.fastjson2.JSON;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserSessionMgmtServiceTest {

    @Autowired
    private UserSessionMgmtService userSessionMgmtService;
    @Test
    void buildUserSession() throws Exception {
        UserInfo userInfo =new UserInfo();
        userInfo.setUserId(101L);
        userInfo.setUsername("test");
        userInfo.setRealName("test");
        userInfo.setPhone("12345678901");
        userInfo.setSex(1);
        userInfo.setArea("test");
        USMgmtBuildParam buildParam = USMgmtBuildParam.builder()
                .userInfo(userInfo)
                .deviceType(DeviceType.PC.getCode())
                .deviceInfo("{'deviceId':'pc-ajsna1','xinghao':'huaweiz','os':'android','osVersion':'Hom','appVersion':'2.0.0'}")
                .ip("218.18.121.20")
                .ipLocation("SHENZHEN,GUANGDONG,CHINA").build();
       String sessionId = userSessionMgmtService.buildUserSession(buildParam);
       System.out.println(sessionId);
    }

    @Test
    void validateUserSession() throws Exception {
        //String sessionId = "c0f1c2a3-a902-484a-a578-f61cd596e152";
        String sessionId="59c2f8d5-99f6-4d74-9f75-3e5d089d75ef";
        USMgmtValidateParam validateParam = USMgmtValidateParam.builder()
                .userId(101L)
                .deviceType(DeviceType.MOBILE.getCode())
                .sessionId(sessionId).build();
        UserSession userSession = userSessionMgmtService.validateUserSession(validateParam);
        System.out.println(JSON.toJSONString(userSession));
    }

    @Test
    void refreshUserSession() throws Exception {
        String sessionId = "c0f1c2a3-a902-484a-a578-f61cd596e152";
        USMgmtRefreshParam refreshParam = USMgmtRefreshParam.builder()
               .userId(101L)
               .deviceType(DeviceType.MOBILE.getCode())
               .sessionId(sessionId).build();
        String newSessionId = userSessionMgmtService.refreshUserSession(refreshParam);
        System.out.println(newSessionId);
    }
}