package com.moxi.bookstore.http.deal;

import com.moxi.bookstore.http.HttpService;
import com.moxi.bookstore.http.entity.BaseDeal;

import rx.Observable;
import rx.Subscriber;

/**
 * Created by Administrator on 2016/10/16.
 */
public class DoStoreDeal extends BaseDeal {
    Subscriber msub;
    String id;
    String token;

    public DoStoreDeal(Subscriber msub, String id, String token) {
        this.msub = msub;
        this.id = id;
        this.token = token;
    }

    @Override
    public Observable getObservable(HttpService methods) {
        return methods.doStroeBook("media",id,token);
    }

    @Override
    public Subscriber getSubscirber() {
        return msub;
    }
}
