package com.moxi.bookstore.http.entity;

import com.moxi.bookstore.bean.Status;

/**
 * Created by Administrator on 2016/9/20.
 */
public class BaseEntity <T>{
    private T data;
    private Status status;
    private  Long systemDate;

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

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
