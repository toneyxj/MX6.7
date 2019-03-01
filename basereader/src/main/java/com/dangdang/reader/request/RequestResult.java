package com.dangdang.reader.request;


/**
 * 接口请求结果数据结构
 * Created by xiaruri on 2015/5/26.
 */
public class RequestResult {

    private String action;
    private Object result;
    private ResultExpCode expCode;

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    public ResultExpCode getExpCode() {
        return expCode;
    }

    public void setExpCode(ResultExpCode expCode) {
        this.expCode = expCode;
    }
}
