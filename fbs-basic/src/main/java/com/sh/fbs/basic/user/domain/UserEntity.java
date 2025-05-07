package com.sh.fbs.basic.user.domain;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Builder
@Data
public class UserEntity {
    private Long userId;
    private String username;
    private String pwd;
    private String nickname;
    private String phone;
    private String icon;
    private int sex;
    private Date birthDate;
    private String area;
    private Short status;
    private Date regTime;
    private Date updateTime;

}
