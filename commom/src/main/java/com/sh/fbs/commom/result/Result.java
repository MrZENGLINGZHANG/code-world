package com.sh.fbs.commom.result;


import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Result {

    private int code;
    private String msg;
    private Object data;

    public Result(int code, String msg, Object data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

}
