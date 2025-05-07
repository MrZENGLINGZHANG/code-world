package com.sh.fbs.gateway.session;

import com.sh.fbs.commom.user.User;
import lombok.Builder;
import lombok.Data;
import org.springframework.beans.BeanUtils;

@Builder
@Data
public class UserSession extends User {

    private String sessionId;
    private Long expireTime;

    public User getUser() {
        User user = new User();
        BeanUtils.copyProperties(this,user);
        return user;
    }
}
