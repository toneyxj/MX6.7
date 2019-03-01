package com.moxi.bookstore.bean;

/**
 * Created by Administrator on 2016/10/13.
 */
public class OrderResult {
    String order_id;
    String payable;
    String total;

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public String getPayable() {
        return payable;
    }

    public void setPayable(String payable) {
        this.payable = payable;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }
}
