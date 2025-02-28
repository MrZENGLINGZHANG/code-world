package com.sh.fbs.commom.result;

public class ResultUtils {

    public static Result buildSuccessResult() {
       return new Result(ErrCode.SUCCESS.getCode(),ErrCode.SUCCESS.getDesc(),null);
    }
    public static Result buildResult(ErrCode errCode,Object data) {
        return new Result(errCode.getCode(),errCode.getDesc(),data);
    }
    public static Result buildResult(int code,String msg,Object data) {
        return new Result(code,msg,data);
    }


}
