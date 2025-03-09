package com.sh.fbs.basic.user.access;

import com.sh.fbs.basic.commom.ecode.BasicAppErrorCode;
import com.sh.fbs.basic.commom.utils.UserContextUtils;
import com.sh.fbs.basic.user.domain.*;
import com.sh.fbs.commom.result.BizException;
import com.sh.fbs.commom.result.Result;
import com.sh.fbs.commom.result.ResultUtils;
import com.sh.fbs.commom.utils.MD5Utils;
import com.sh.fbs.commom.user.User;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * 用户管理控制器
 * 处理用户注册、登录和验证码相关的请求
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/api/fbs/basic/sso")
public class UserMgmtController {
    
    private final UserMgmtService userMgmtService;
    private final CaptchaService captchaService;
    private final UserSessionMgmtService sessionMgmtService;

    @Autowired
    public UserMgmtController(UserMgmtService userMgmtService, 
                            CaptchaService captchaService,
                            UserSessionMgmtService sessionMgmtService) {
        this.userMgmtService = userMgmtService;
        this.captchaService = captchaService;
        this.sessionMgmtService = sessionMgmtService;
    }

    /**
     * 生成验证码
     */
    @PostMapping("/captcha")
    public Result generateCaptcha(@RequestBody @Valid CaptchaGenRequest request) {
        log.info("Generate captcha for request: {}", request);
        CaptchaGenParam param = CaptchaGenParam.builder()
                .build();
        BeanUtils.copyProperties(request, param);
        captchaService.generateCaptcha(param);
        return ResultUtils.buildSuccessResult();
    }

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public Result register(@RequestBody @Valid UserRegisterRequest request) {
        log.info("Process user registration: {}", request);
        try {
            // 参数校验
            request.isRegisterParamValid();
            
            // 验证码校验
            validateCaptcha(request.getPhone(), request.getCaptcha());
            
            // 创建用户实体并注册
            UserEntity userEntity = createUserEntity(request);
            userMgmtService.registerUser(userEntity);
            
            return ResultUtils.buildSuccessResult();
        } catch (Exception e) {
            log.error("Registration failed for user: {}", request.getPhone(), e);
            throw new BizException(BasicAppErrorCode.REGISTER_FAILED, e.getMessage());
        }
    }

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public Result login(@RequestBody @Valid UserLoginRequest request) {
        log.info("Process user login: {}", request.getUsername());
        try {
            // 验证用户存在性
            UserEntity userEntity = userMgmtService.getUserByUsername(request.getUsername());
            if (userEntity == null) {
                throw new BizException(BasicAppErrorCode.USER_NOT_EXIST);
            }

            // 验证密码
            validatePassword(request.getPassword(), userEntity.getPwd());
            
            // 验证验证码
            validateCaptcha(request.getUsername(), request.getCaptcha());

            // 生成会话
            UserSession userSession = createUserSession(userEntity);
            String sessionToken = sessionMgmtService.generateSession(userSession);
            
            return ResultUtils.buildSuccessResult(sessionToken);
        } catch (BizException e) {
            throw e;
        } catch (Exception e) {
            log.error("Login failed for user: {}", request.getUsername(), e);
            throw new BizException(BasicAppErrorCode.LOGIN_FAILED, e.getMessage());
        }
    }

    /**
     * 修改用户密码
     */
    @PostMapping("/change-password")
    public Result changePassword(@RequestBody @Valid UserChangePasswordRequest request) {
        User currentUser = UserContextUtils.getCurrentUser();
        log.info("Process password change for user: {}", currentUser.getUserId());
        
        try {
            // 验证验证码
            validateCaptcha(currentUser.getPhone(), request.getCaptcha());
            
            // 修改密码
            userMgmtService.changePassword(
                currentUser.getUserId(),
                request.getOldPassword(),
                request.getNewPassword()
            );
            
            return ResultUtils.buildSuccessResult();
        } catch (BizException e) {
            throw e;
        } catch (Exception e) {
            log.error("Password change failed for user: {}", currentUser.getUserId(), e);
            throw new BizException(BasicAppErrorCode.PASSWORD_CHANGE_FAILED, e.getMessage());
        }
    }

    /**
     * 更新用户个人信息
     */
    @PostMapping("/update-profile")
    public Result updateProfile(@RequestBody @Valid UserUpdateProfileRequest request) {
        User currentUser = UserContextUtils.getCurrentUser();
        log.info("Process profile update for user: {}", currentUser.getUserId());
        
        try {
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            userMgmtService.updateUserProfile(
                currentUser.getUserId(),
                request.getNickname(),
                request.getPhone(),
                request.getIcon(),
                request.getSex(),
                dateFormat.parse(request.getBirthDateStr()),
                request.getArea()
            );
            
            return ResultUtils.buildSuccessResult();
        } catch (BizException e) {
            throw e;
        } catch (Exception e) {
            log.error("Profile update failed for user: {}", currentUser.getUserId(), e);
            throw new BizException(BasicAppErrorCode.PROFILE_UPDATE_FAILED, e.getMessage());
        }
    }

    private void validateCaptcha(String uniqKey, String captcha) {
        CaptchaCheckParam param = CaptchaCheckParam.builder()
                .uniqKey(uniqKey)
                .captcha(captcha)
                .build();
        captchaService.validateCaptcha(param);
    }

    private void validatePassword(String inputPassword, String storedPassword) {
        String encryptedPassword = MD5Utils.encrypt(inputPassword.trim());
        if (!encryptedPassword.equals(storedPassword)) {
            throw new BizException(BasicAppErrorCode.PASSWORD_ERROR);
        }
    }

    private UserEntity createUserEntity(UserRegisterRequest request) {
        UserEntity userEntity = UserEntity.builder().build();
        BeanUtils.copyProperties(request, userEntity);
        userEntity.setBirthDate(request.birthDate());
        userEntity.setPwd(MD5Utils.encrypt(request.getPassword().trim()));
        return userEntity;
    }

    private UserSession createUserSession(UserEntity userEntity) {
        UserSession userSession = UserSession.builder().build();
        BeanUtils.copyProperties(userEntity, userSession);
        return userSession;
    }
}
