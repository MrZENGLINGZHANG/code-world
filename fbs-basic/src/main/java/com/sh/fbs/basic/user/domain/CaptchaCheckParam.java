package com.sh.fbs.basic.user.domain;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.Length;


@Builder
@Data
public class CaptchaCheckParam {
    @NotNull
    private String uniqKey;
    @NotBlank
    @Length(min = 4, max = 8)
    private String captcha;
}
