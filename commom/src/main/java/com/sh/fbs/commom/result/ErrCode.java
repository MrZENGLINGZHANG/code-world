package com.sh.fbs.commom.result;

import lombok.Getter;

@Getter
public enum ErrCode {

    SUCCESS(200,"SUCCESS"),
    NO_AUTH(401,"NO_AUTH"),
    FAILED(500,"FAILED");

    private final int code;
    private final String desc;

    ErrCode(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public ErrCode codeOf(int code) {
        for (ErrCode errCode : ErrCode.values()) {
            if (errCode.code == code) {
                return errCode;
            }
        }
        return null;
    }

}
