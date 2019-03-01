package com.moxi.bookstore.bean;

/**
 * Created by Administrator on 2016/9/20.
 * 网络请求messagebase
 */
public class BaseMessage {
    private  Status status;
    private  Long systemDate;

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Long getSystemDate() {
        return systemDate;
    }

    public void setSystemDate(Long systemDate) {
        this.systemDate = systemDate;
    }
}
