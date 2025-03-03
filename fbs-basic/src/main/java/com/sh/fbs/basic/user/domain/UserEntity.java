package com.sh.fbs.basic.user.domain;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Builder
@Data
public class UserEntity {

    private Long userId;

    private String userName;

    private String phone;

    private String pwd;

    private String nickName;

    private String iconUri;

    private Boolean sex;

    private Date birthDate;

    private String area;

    private Short status;

    private Date regTime;

    private Date updateTime;

}
