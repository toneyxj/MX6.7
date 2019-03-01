package com.moxi.bookstore.http;

import com.moxi.bookstore.http.entity.BaseDeal;

import rx.Observable;
import rx.Subscriber;

/**
 * Created by Administrator on 2016/10/17.
 */
public class BoughtListDeal extends BaseDeal {
    Subscriber msub;
    String token;
    String lastMediaAuthorityId;

    public BoughtListDeal(Subscriber msub, String token,String lastMediaAuthorityId) {
        this.msub = msub;
        this.token = token;
        this.lastMediaAuthorityId=lastMediaAuthorityId;
    }

    @Override
    public Observable getObservable(HttpService methods) {
        return methods.getBoughtBookData("android",token,lastMediaAuthorityId);
    }

    @Override
    public Subscriber getSubscirber() {
        return msub;
    }
}
