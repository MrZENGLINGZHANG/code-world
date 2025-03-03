package com.sh.fbs.commom.result;

public class ResultUtils {

    public static Result buildSuccessResult() {
       return new Result(BizBaseErrorCode.SUCCESS.getCode(), BizBaseErrorCode.SUCCESS.getMessage(),null);
    }
    public static Result buildSuccessResult(Object data) {
        return new Result(BizBaseErrorCode.SUCCESS.getCode(), BizBaseErrorCode.SUCCESS.getMessage(),data);
    }
    public static Result buildResult(BizBaseErrorCode bizBaseErrorCode, Object data) {
        return new Result(bizBaseErrorCode.getCode(), bizBaseErrorCode.getMessage(),data);
    }
    public static Result buildResult(int code,String msg,Object data) {
        return new Result(code,msg,data);
    }


}
