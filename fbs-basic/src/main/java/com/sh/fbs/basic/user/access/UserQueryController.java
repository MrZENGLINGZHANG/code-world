package com.sh.fbs.basic.user.access;

import com.sh.fbs.basic.commom.ecode.BasicAppErrorCode;
import com.sh.fbs.basic.commom.utils.UserContextUtils;
import com.sh.fbs.basic.user.domain.UserEntity;
import com.sh.fbs.basic.user.domain.UserMgmtService;
import com.sh.fbs.commom.result.BizException;
import com.sh.fbs.commom.result.Result;
import com.sh.fbs.commom.result.ResultUtils;
import com.sh.fbs.commom.user.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * User query controller
 * Handles user information query operations
 */
@Slf4j
@RestController
@RequestMapping("/api/fbs/basic/user/query")
public class UserQueryController {

    private final UserMgmtService userMgmtService;

    @Autowired
    public UserQueryController(UserMgmtService userMgmtService) {
        this.userMgmtService = userMgmtService;
    }

    /**
     * Get current user information
     */
    @GetMapping("/current")
    public Result<UserEntity> getCurrentUser() {
        User currentUser = UserContextUtils.getCurrentUser();
        log.info("Query user information for user: {}", currentUser.getUserId());
        
        try {
            UserEntity userEntity = userMgmtService.getUserById(currentUser.getUserId());
            if (userEntity == null) {
                throw new BizException(BasicAppErrorCode.USER_NOT_EXIST);
            }
            
            // 清除敏感信息
            userEntity.setPwd(null);
            return ResultUtils.buildSuccessResult(userEntity);
        } catch (BizException e) {
            throw e;
        } catch (Exception e) {
            log.error("Failed to get user information for user: {}", currentUser.getUserId(), e);
            throw new BizException(BasicAppErrorCode.USER_QUERY_FAILED, e.getMessage());
        }
    }
} 