package com.sh.fbs.basic.user.domain;

import com.sh.fbs.commom.user.User;
import lombok.Builder;
import lombok.Data;

import java.sql.Timestamp;

@Builder
@Data
public class UserSession extends User {

    private String sessionId;
    private Long expireTime;
}
