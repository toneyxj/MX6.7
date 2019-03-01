package com.moxi.bookstore.http.deal;

import com.moxi.bookstore.http.HttpService;
import com.moxi.bookstore.http.entity.BaseDeal;

import rx.Observable;
import rx.Subscriber;

/**
 * Created by Administrator on 2016/10/16.
 */
public class CancelStoreDeal extends BaseDeal {
    Subscriber msub;
    String ids;
    String token;

    public CancelStoreDeal(Subscriber msub, String ids, String token) {
        this.msub = msub;
        this.ids = ids;
        this.token = token;
    }

    @Override
    public Observable getObservable(HttpService methods) {
        return methods.cancleStore(ids,"DDDS-P",token);
    }

    @Override
    public Subscriber getSubscirber() {
        return msub;
    }
}
