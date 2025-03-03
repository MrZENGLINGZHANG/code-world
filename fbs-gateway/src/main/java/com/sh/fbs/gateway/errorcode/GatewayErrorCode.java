package com.sh.fbs.gateway.errorcode;
import com.sh.fbs.commom.result.BizErrorCode;
import lombok.Getter;
import java.util.HashMap;
import java.util.Map;

@Getter
public enum GatewayErrorCode implements BizErrorCode {

    TOKEN_EXPIRED(401,"Token expired, please try to refresh token"),
    USER_ALREADY_LOGGED_IN(10001,"User already logged in"),
    UNKNOWN_DEVICE_TYPE(10002,"Unknown device type"),
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
