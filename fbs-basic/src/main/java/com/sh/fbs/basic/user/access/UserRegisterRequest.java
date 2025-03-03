package com.sh.fbs.basic.user.access;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Builder
@Data
public class UserRegisterRequest {
    @NotBlank
    private String username;
    @NotBlank
    private String password;
    @NotBlank
    private String phone;
    @NotBlank
    private String captcha;
    @NotBlank
    private String nickName;
    @NotBlank
    private String iconUri;
    @NotBlank
    private Boolean sex;
    @NotBlank
    private Date birthDate;
    @NotBlank
    private String area;

}
