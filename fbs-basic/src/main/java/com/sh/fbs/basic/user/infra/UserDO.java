package com.sh.fbs.basic.user.infra;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;


@Data
@TableName("user_tab")
public class UserDO {
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