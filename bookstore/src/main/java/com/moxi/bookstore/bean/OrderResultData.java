package com.moxi.bookstore.bean;

/**
 * Created by Administrator on 2016/10/13.
 */
public class OrderResultData extends Data {
    String key;
    OrderResult result;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public OrderResult getResult() {
        return result;
    }

    public void setResult(OrderResult result) {
        this.result = result;
    }
}
