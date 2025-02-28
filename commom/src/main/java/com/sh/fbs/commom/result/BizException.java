package com.sh.fbs.commom.result;

public class BizException extends RuntimeException {

    private int eCode;

    public BizException(int eCode, String message) {
        super(message);
        this.eCode = eCode;
    }

    public BizException(String message, Throwable cause, int eCode) {
        super(message, cause);
        this.eCode = eCode;
    }

    public BizException(ErrCode errCode) {
        super(errCode.getDesc());
        this.eCode = errCode.getCode();
    }

    public int getECode() {
        return eCode;
    }

    public void setECode(int eCode) {
        this.eCode = eCode;
    }
}
