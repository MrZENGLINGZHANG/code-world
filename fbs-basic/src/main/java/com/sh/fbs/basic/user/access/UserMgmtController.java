package com.sh.fbs.basic.user.access;

import com.sh.fbs.basic.commom.ecode.BasicAppErrorCode;
import com.sh.fbs.basic.user.domain.*;
import com.sh.fbs.commom.result.BizException;
import com.sh.fbs.commom.result.Result;
import com.sh.fbs.commom.result.ResultUtils;
import com.sh.fbs.commom.utils.MD5Utils;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Validated
@RestController
@RequestMapping ("/api/fbs/basic/sso")
public class UserMgmtController {
    @Autowired
    private UserMgmtService userMgmtService;
    @Autowired
    private CaptchaService captchaService;
    @Autowired
    private UserSessionMgmtService sessionMgmtService;

    @PostMapping("/captcha")
    @ResponseBody
    public Result captcha(@RequestBody @Valid CaptchaGenRequest request) {
        CaptchaGenParam param = CaptchaGenParam.builder().build();
        BeanUtils.copyProperties(request, param);
        captchaService.generateCaptcha(param);
        return ResultUtils.buildSuccessResult();
    }

    @RequestMapping("/register")
    @ResponseBody
    public Result register(@RequestBody @Valid UserRegisterRequest registerRequest) throws Exception {
        CaptchaCheckParam param = CaptchaCheckParam.builder().uniqKey(registerRequest.getPhone()).captcha(registerRequest.getCaptcha()).build();
        captchaService.validateCaptcha(param);
        UserEntity userEntity = UserEntity.builder().build();
        BeanUtils.copyProperties(registerRequest, userEntity);
        userMgmtService.registerUser(userEntity);
        return ResultUtils.buildSuccessResult();
    }

    @RequestMapping("/login")
    @ResponseBody
    public Result login(@RequestBody @Valid UserLoginRequest loginRequest) throws Exception {
        UserEntity userEntity=userMgmtService.getUserByUsername(loginRequest.getUsername());
        if(userEntity==null) {
            throw new BizException(BasicAppErrorCode.USER_NOT_EXIST);
        }
        if(!MD5Utils.Encrypt(loginRequest.getPassword()).equals(userEntity.getPwd())) {
            throw new BizException(BasicAppErrorCode.PASSWORD_ERROR);
        }
        CaptchaCheckParam param = CaptchaCheckParam.builder().uniqKey(loginRequest.getUsername()).captcha(loginRequest.getCaptcha()).build();
        captchaService.validateCaptcha(param);
        UserSession userSession = UserSession.builder().build();
        BeanUtils.copyProperties(userEntity,userSession);
        return ResultUtils.buildSuccessResult(sessionMgmtService.generateSession(userSession));
    }


}
