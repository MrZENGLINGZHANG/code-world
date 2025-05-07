package com.sh.fbs.basic.user.infra;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;


@Data
@TableName("user_tab")
public class UserDO {
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