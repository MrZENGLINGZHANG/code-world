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
    @Length(min = 6, max = 6)
    private String captcha;
}
