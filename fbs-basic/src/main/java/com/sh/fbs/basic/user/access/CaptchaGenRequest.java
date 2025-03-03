package com.sh.fbs.basic.user.access;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

@Builder
@Data
public class CaptchaGenRequest {
    @NotNull
    private String uniqKey;
    @NotNull
    @Range(min = 0, max = 1)
    private int channelType; // 0-sms, 1-email
    @NotBlank
    @Length(min = 8, max = 22)
    private String channelCode; // phone or email
}
