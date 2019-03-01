package com.moxi.bookstore.http.exception;

/**
 * 自定义错误信息，统一处理返回处理
 * Created by cl on 2016/9/16.
 */
public class HttpTimeException extends RuntimeException {

    public static final int NO_DATA = 0x2;
    public static final int WITHOUT_LOGIN=10003;

    public HttpTimeException(int resultCode) {
        this(getApiExceptionMessage(resultCode));
    }

    public HttpTimeException(String detailMessage) {
        super(detailMessage);
    }

    /**
     * 转换错误数据
     *
     * @param code
     * @return
     */
    private static String getApiExceptionMessage(int code) {
        String message = "";
        switch (code) {
            case NO_DATA:
                message = "无数据";
                break;
            case WITHOUT_LOGIN:
                message="10003";
                break;
            default:
                message = "error";
                break;

        }
        return message;
    }
}

