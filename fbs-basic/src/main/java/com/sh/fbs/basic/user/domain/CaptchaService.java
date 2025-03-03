package com.sh.fbs.basic.user.domain;


import com.sh.fbs.commom.result.BizException;
import jakarta.validation.Valid;

public interface CaptchaService {

    void generateCaptcha(@Valid CaptchaGenParam param) throws BizException;

    boolean validateCaptcha (@Valid CaptchaCheckParam param) throws BizException;



}
