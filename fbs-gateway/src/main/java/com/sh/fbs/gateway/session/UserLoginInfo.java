package com.sh.fbs.gateway.session;

import lombok.Builder;
import lombok.Data;

import java.sql.Timestamp;

@Builder
@Data
public class UserLoginInfo {
    private String sessionId;
    private Timestamp loginTime;
    private Timestamp expireTime;

    private int deviceType;
    private String deviceInfo;

    private String ip;
    private String ipLocation;

}
