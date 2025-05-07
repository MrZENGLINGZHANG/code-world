package com.sh.fbs.basic.commom.constant;

public class BasicAppConstant {


    // USER
    public static final String USER_CAPTCHA_HASH_KEY = "fbs:user:captcha:%s";
    public static final int USER_CAPTCHA_NODE_KEY_NUM = 16 ;
    public static final long USER_CAPTCHA_KEY_EXPIRE_TIME_MS = 60 * 1000 * 5 ;
    public static final int USER_CAPTCHA_MAX_TIMES = 5 ;
    // SESSION
    public static final String USER_SESSION_HASH_KEY = "fbs:user:session:%s";
    public static final int USER_SESSION_NODE_KEY_NUM = 16 ;
    public static final long USER_SESSION_KEY_EXPIRE_TIME = 60 * 1000 * 60 * 24 ;


}
