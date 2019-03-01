package com.moxi.bookstore.bean;

/**
 * Created by Administrator on 2016/10/13.
 * 支付data
 */
public class PayData extends Data {
    String redirectUrl;

    public String getRedirectUrl() {
        return redirectUrl;
    }

    public void setRedirectUrl(String redirectUrl) {
        this.redirectUrl = redirectUrl;
    }
}
