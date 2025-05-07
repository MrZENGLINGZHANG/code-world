package com.sh.fbs.basic.user.domain;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

@Builder
@Data
public class CaptchaGenParam {
    @NotNull
    private String uniqKey; // 唯一标识，如userId,username,email等
    @NotNull
    @Range(min = 0, max = 1)
    private int channelType; // 0-sms, 1-email
    @NotBlank
    @Length(min = 8, max = 22)
    private String channelCode; // phone or email
}
