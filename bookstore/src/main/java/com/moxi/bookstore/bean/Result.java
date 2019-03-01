package com.moxi.bookstore.bean;

import com.moxi.bookstore.modle.GetEbookOrderFlowV2;

/**
 * Created by Administrator on 2016/10/13.
 */
public class Result {
    SubmitOrder submitOrder;
    Status status;
    public GetEbookOrderFlowV2 getEbookOrderFlowV2;

    public SubmitOrder getSubmitOrder() {
        return submitOrder;
    }

    public void setSubmitOrder(SubmitOrder submitOrder) {
        this.submitOrder = submitOrder;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
