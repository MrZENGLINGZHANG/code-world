package com.sh.fbs.commom.result;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public enum BizBaseErrorCode implements BizErrorCode {

    SUCCESS(200,"SUCCESS"),
    FAILED(500,"FAILED");

    private final int code;
    private final String message;

    BizBaseErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    private static final Map<Integer, BizBaseErrorCode> codeMap = new HashMap<>();

    static {
        for (BizBaseErrorCode bizBaseErrorCode : values()) {
            codeMap.put(bizBaseErrorCode.code, bizBaseErrorCode);
        }
    }

    public static BizBaseErrorCode codeOf(int code) {
        BizBaseErrorCode result = codeMap.get(code);
        if (result == null) {
            throw new IllegalArgumentException("无效的错误码: " + code);
        }
        return result;
    }


}
