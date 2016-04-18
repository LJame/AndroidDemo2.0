package com.hardrubic.util.network;

import android.text.TextUtils;

/**
 * HTTP异常类
 */
public class HttpException extends RuntimeException {

    private String code;   //错误码
    private String msg = "";  //错误信息
    private Exception originException; //原始错误Exception

    public HttpException(String code) {
        this(new Exception(), code, "");
    }

    public HttpException(String code, String msg) {
        this(new Exception(msg), code, msg);
    }

    public HttpException(Exception e, String code) {
        this(e, code, e.getMessage());
    }

    public HttpException(Exception e, String code, String msg) {
        this.originException = e;
        this.code = code;
        this.msg = msg;
    }

    public Throwable getOriginException() {
        return originException;
    }

    public String getErrorCode() {
        return code;
    }

    public String getErrorMsg() {
        return msg;
    }

    @Override
    public String getMessage() {
        StringBuilder builder = new StringBuilder();
        if (!TextUtils.isEmpty(msg)) {
            builder.append(msg);
        } else {
            builder.append(originException.getMessage());
        }
        builder.append("-").append(code);
        return builder.toString();
    }
}
