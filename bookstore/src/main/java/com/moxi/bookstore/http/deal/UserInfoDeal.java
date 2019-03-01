package com.moxi.bookstore.http.deal;

import com.moxi.bookstore.http.HttpService;
import com.moxi.bookstore.http.entity.BaseDeal;

import rx.Observable;
import rx.Subscriber;

/**
 * Created by Administrator on 2016/11/14.
 */

public class UserInfoDeal extends BaseDeal {
    Subscriber msub;
    String token;

    public UserInfoDeal(Subscriber msub, String token) {
        this.msub = msub;
        this.token = token;
    }

    @Override
    public Observable getObservable(HttpService methods) {
        return methods.getAcountData(token);
    }

    @Override
    public Subscriber getSubscirber() {
        return msub;
    }
}
