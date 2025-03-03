package com.sh.fbs.gateway.session;

import lombok.Data;
import java.util.Map;

@Data
public class UserSession {
    private UserInfo userInfo;
    private Map<Integer,UserLoginInfo> loginInfoMap;

    public boolean isLoggedIn(DeviceType deviceType) {
        return loginInfoMap.containsKey(deviceType.getCode());
    }

    public boolean isLoggedIn(DeviceType deviceType,String sessionId){
        UserLoginInfo loginInfo = loginInfoMap.get(deviceType.getCode());
        return loginInfo != null && loginInfo.getSessionId().equals(sessionId);
    }

    public void logout(DeviceType deviceType){
        loginInfoMap.remove(deviceType.getCode());
    }

    public void logout(String sessionId){
        for (UserLoginInfo userLoginInfo : loginInfoMap.values()) {
            if(userLoginInfo.getSessionId().equals(sessionId)){
                loginInfoMap.remove(sessionId);
                break;
            }
        }
    }

    public UserSession toSimpleDeviceTypeSession(DeviceType deviceType){
        UserSession userSession = new UserSession();
        userSession.setUserInfo(userInfo);
        userSession.setLoginInfoMap(Map.of(deviceType.getCode(),loginInfoMap.get(deviceType.getCode())));
        return userSession;
    }





}
