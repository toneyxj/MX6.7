package com.moxi.bookstore.http.deal;

import com.moxi.bookstore.http.HttpService;
import com.moxi.bookstore.http.entity.BaseDeal;

import rx.Observable;
import rx.Subscriber;

/**
 * Created by kele on 2017/1/9.
 */

public class AliyPayDeal extends BaseDeal {
    Subscriber msub;
    String orderId,totalPrice,productIds,permentId,token;

    public AliyPayDeal(Subscriber msub, String orderId, String totalPrice, String productIds, String permentId, String token) {
        this.msub = msub;
        this.orderId = orderId;
        this.totalPrice = totalPrice;
        this.productIds = productIds;
        this.permentId = permentId;
        this.token = token;
    }

    @Override
    public Observable getObservable(HttpService methods) {
        return methods.getPayHtmlWithAliy(orderId,totalPrice,false,productIds,permentId,"DDDS-P","401","Android",token);
    }

    @Override
    public Subscriber getSubscirber() {
        return msub;
    }
}
