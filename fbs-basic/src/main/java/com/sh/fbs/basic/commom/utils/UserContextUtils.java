package com.sh.fbs.basic.commom.utils;

import com.sh.fbs.basic.commom.ecode.BasicAppErrorCode;
import com.sh.fbs.commom.result.BizException;
import com.sh.fbs.commom.user.User;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

/**
 * User context utility class
 */
public class UserContextUtils {
    
    private static final String CURRENT_USER_KEY = "current_user";
    
    /**
     * Get current user from request context
     *
     * @return current user
     * @throws BizException if user is not logged in
     */
    public static User getCurrentUser() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes == null) {
            throw new BizException(BasicAppErrorCode.USER_NOT_LOGGED_IN);
        }
        
        User currentUser = (User) requestAttributes.getAttribute(CURRENT_USER_KEY, RequestAttributes.SCOPE_REQUEST);
        if (currentUser == null || currentUser.getUserId() == null) {
            throw new BizException(BasicAppErrorCode.USER_NOT_LOGGED_IN);
        }
        
        return currentUser;
    }
    
    /**
     * Get current user ID from request context
     *
     * @return current user ID
     * @throws BizException if user is not logged in
     */
    public static Long getCurrentUserId() {
        return getCurrentUser().getUserId();
    }
    
    private UserContextUtils() {
        // Private constructor to prevent instantiation
    }
} 