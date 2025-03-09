package com.sh.fbs.commom.user;

import lombok.Builder;
import lombok.Data;

import java.util.Date;


@Data
public class User {
    private Long userId;
    private String username;
    private String nickname;
    private String phone;
    private int sex;
}
