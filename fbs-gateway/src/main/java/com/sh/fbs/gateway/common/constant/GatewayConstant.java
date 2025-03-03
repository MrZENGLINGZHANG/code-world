package com.sh.fbs.gateway.common.constant;

public class GatewayConstant {

    // SESSION
    public static final String USER_SESSION_HASH_KEY = "fbs:user:session:%s";
    public static final int USER_SESSION_NODE_KEY_NUM = 16 ;
    public static final long USER_SESSION_KEY_EXPIRE_TIME = 60 * 1000 * 60 * 24 ;

    // HTTP REQUEST ATTRIBUTES
    public static final String REQUEST_NO_NEED_AUTH = "/api/fbs/basic/sso";
    public static final String REQUEST_CONTEXT_USER_INFO_ATTR = "user-info";
}
