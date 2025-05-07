package com.sh.fbs.basic.user.access;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Builder
@Data
public class UserRegisterRequest {
    @NotBlank
    @Length(min = 6, max = 20)
    private String username;
    @NotBlank
    @Length(min = 8, max = 20)
    private String password;
    @NotBlank
    @Length(min = 8, max = 20)
    private String phone;
    @NotBlank
    @Length(min = 4, max = 8)
    private String captcha;
    @NotBlank
    @Length(min = 6, max = 20)
    private String nickname;
    @NotBlank
    private String icon;

    @Range(min = 0, max = 1)
    private int sex;
    @NotBlank
    private String birthDateStr;
    @NotBlank
    private String area;

    public Date birthDate() throws ParseException {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return  dateFormat.parse(this.birthDateStr);
    }
    public boolean isRegisterParamValid() {
        // 校验参数
        // 校验验证码

        return true;
    }

}
