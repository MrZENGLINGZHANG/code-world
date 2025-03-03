package com.sh.fbs.commom.user;

import lombok.Builder;
import lombok.Data;


@Data
public class User {
    private Long userId;
    private String username;
    private String realName;
    private String phone;
    private int sex;
    private String area;
}
