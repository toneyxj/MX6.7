package com.moxi.bookstore.http.deal;

import com.moxi.bookstore.http.HttpService;
import com.moxi.bookstore.http.entity.BaseDeal;

import rx.Observable;
import rx.Subscriber;

/**
 * Created by Administrator on 2016/10/13.
 */
public class PayDeal extends BaseDeal {
    Subscriber msubscriber;
    String orderId,totalPrice,productIds,permentId,token,payable;

    public PayDeal(Subscriber msubscriber, String orderId, String totalPrice,String payable, String productIds, String permentId, String token) {
        this.msubscriber = msubscriber;
        this.orderId = orderId;
        this.totalPrice = totalPrice;
        this.productIds = productIds;
        this.permentId = permentId;
        this.token = token;
        this.payable=payable;
    }

    @Override
    public Observable getObservable(HttpService methods) {
        return methods.getPayHtml(orderId,totalPrice,payable,false,productIds,permentId,"DDDS-P","401","Android",token);
    }

    @Override
    public Subscriber getSubscirber() {
        return msubscriber;
    }
}
