package com.moxi.bookstore.bean;

/**
 * Created by Administrator on 2016/10/13.
 */
public class SubmitOrder {
    OrderResultData data;
    Status status;
    Long systemDate;

    public OrderResultData getData() {
        return data;
    }

    public void setData(OrderResultData data) {
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
