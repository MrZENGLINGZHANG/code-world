package com.sh.fbs.gateway.common.ecode;
import com.sh.fbs.commom.result.BizErrorCode;
import lombok.Getter;
import java.util.HashMap;
import java.util.Map;

@Getter
public enum GatewayErrorCode implements BizErrorCode {

    SYSTEM_ERROR(500, "System error, please try again later"),
    SESSION_EXPIRED(401,"session expired, please try to refresh token"),
    PARAMS_ERROR(10000,"%s params error or missing"),
    USER_ALREADY_LOGGED_IN(10001,"User already logged in"),
    UNKNOWN_DEVICE_TYPE(10002,"Unknown device type"),
    USER_NOT_EXIST(10003,"user not exist"),
    USERNAME_OR_PASSWORD_ERROR(10004,"username or password error"),
    CAPTCHA_ERROR(10005,"captcha error"),
    USERNAME_ALREADY_EXIST(10006,"username already exist"),
    USERNAME_NOT_EXIST(10007,"username not exist"),
    PASSWORD_ERROR(10008,"password error"),
    USER_NOT_LOGGED_IN(10009,"user not logged in"),
    USER_NOT_ACTIVATED(10010,"user not activated"),
    USER_ALREADY_ACTIVATED(10011,"user already activated"),
    USER_ALREADY_EXIST(10012,"user already exist"),
    USER_ALREADY_DISABLED(10014,"user already disabled"),
    USER_ALREADY_ENABLED(10015,"user already enabled"),
    USER_ALREADY_DELETED(10016,"user already deleted"),
    USER_NOT_DELETED(10017,"user not deleted"),
    USER_ALREADY_EXPIRED(10018,"user already expired"),
    USER_ALREADY_NOT_EXPIRED(10019,"user already not expired"),
    USER_ALREADY_LOCKED(10020,"user already locked"),
    USER_ALREADY_UNLOCKED(10021,"user already unlocked"),

    ;

    private final int code;
    private final String message;

    GatewayErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    private static final Map<Integer, GatewayErrorCode> codeMap = new HashMap<>();
    static {
        for (GatewayErrorCode gatewayErrorCode : values()) {
            codeMap.put(gatewayErrorCode.code, gatewayErrorCode);
        }
    }

    public static GatewayErrorCode codeOf(int code) {
        GatewayErrorCode result = codeMap.get(code);
        if (result == null) {
            throw new IllegalArgumentException("无效的错误码: " + code);
        }
        return result;
    }


}
