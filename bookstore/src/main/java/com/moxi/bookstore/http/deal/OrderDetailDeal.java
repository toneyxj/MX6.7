package com.moxi.bookstore.http.deal;

import com.moxi.bookstore.http.HttpService;
import com.moxi.bookstore.http.entity.BaseDeal;

import rx.Observable;
import rx.Subscriber;

/**
 * Created by Administrator on 2016/10/19.
 */
public class OrderDetailDeal extends BaseDeal {
    Subscriber msub;
    String orderId,token;

    public OrderDetailDeal(Subscriber msub, String orderId, String token) {
        this.msub = msub;
        this.orderId = orderId;
        this.token = token;
    }

    @Override
    public Observable getObservable(HttpService methods) {
        return methods.getOrderDetail(orderId,token);
    }

    @Override
    public Subscriber getSubscirber() {
        return msub;
    }
}
