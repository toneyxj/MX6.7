package com.moxi.bookstore.bean;

import java.util.List;

/**
 * Created by Administrator on 2016/11/20.
 * 虚拟支付data
 */

public class VirtualPayMentData extends Data {

    private int costOfBindAuth;

    private int costOfConsume;

    private int costOfSaveBought;

    private int costOfbeforeOrderForm;

    private int costOfgetMediaInfo;

    private String orderNo;

    private List<Long> productIds ;

    private Double totalPrice;

    public int getCostOfBindAuth() {
        return costOfBindAuth;
    }

    public void setCostOfBindAuth(int costOfBindAuth) {
        this.costOfBindAuth = costOfBindAuth;
    }

    public int getCostOfConsume() {
        return costOfConsume;
    }

    public void setCostOfConsume(int costOfConsume) {
        this.costOfConsume = costOfConsume;
    }

    public int getCostOfSaveBought() {
        return costOfSaveBought;
    }

    public void setCostOfSaveBought(int costOfSaveBought) {
        this.costOfSaveBought = costOfSaveBought;
    }

    public int getCostOfbeforeOrderForm() {
        return costOfbeforeOrderForm;
    }

    public void setCostOfbeforeOrderForm(int costOfbeforeOrderForm) {
        this.costOfbeforeOrderForm = costOfbeforeOrderForm;
    }

    public int getCostOfgetMediaInfo() {
        return costOfgetMediaInfo;
    }

    public void setCostOfgetMediaInfo(int costOfgetMediaInfo) {
        this.costOfgetMediaInfo = costOfgetMediaInfo;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public List<Long> getProductIds() {
        return productIds;
    }

    public void setProductIds(List<Long> productIds) {
        this.productIds = productIds;
    }

    public Double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }
}
