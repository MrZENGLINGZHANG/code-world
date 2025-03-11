package com.sh.fbs.gateway.common.constant;

public class GatewayConstant {

    // SESSION
    public static final String USER_SESSION_HASH_KEY = "fbs:user:session:%s";
    public static final int USER_SESSION_NODE_KEY_NUM = 16;
    public static final long USER_SESSION_KEY_EXPIRE_TIME = 60 * 1000 * 60 * 24;  // 24 hours
    public static final long USER_SESSION_RENEWAL_THRESHOLD = 60 * 1000 * 30;      // 30 minutes
    public static final String USER_ID_HEADER = "X-User-Id";
    public static final String SESSION_ID_COOKIE = "sessionId";

    // HTTP REQUEST ATTRIBUTES
    public static final String REQUEST_NO_NEED_AUTH = "/api/fbs/basic/sso";
    public static final String REQUEST_CONTEXT_USER_INFO_ATTR = "user-info";

    // CSRF CONSTANTS
    public static final String CSRF_TOKEN_HEADER = "X-CSRF-TOKEN";
    public static final String CSRF_TOKEN_COOKIE = "XSRF-TOKEN";
    
    // CSRF EXCLUDE PATHS
    public static final String CSRF_EXCLUDE_LOGIN = "/api/fbs/basic/sso/login";
    public static final String CSRF_EXCLUDE_REGISTER = "/api/fbs/basic/sso/register";
    public static final String CSRF_EXCLUDE_CAPTCHA = "/api/fbs/basic/sso/captcha";
}
