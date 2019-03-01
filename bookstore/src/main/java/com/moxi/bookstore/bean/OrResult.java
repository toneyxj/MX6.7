package com.moxi.bookstore.bean;

/**
 * Created by Administrator on 2016/10/19.
 */
public class OrResult {
    private String errorCode;

    private OrderBean resultObject;

    private String statusCode;

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public OrderBean getResultObject() {
        return resultObject;
    }

    public void setResultObject(OrderBean resultObject) {
        this.resultObject = resultObject;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }
}
