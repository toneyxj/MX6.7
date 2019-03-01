package com.moxi.bookstore.http.deal;

import com.moxi.bookstore.http.HttpService;
import com.moxi.bookstore.http.entity.BaseDeal;

import rx.Observable;
import rx.Subscriber;

/**
 * Created by Administrator on 2016/10/13.
 */
public class StoreUpDeal extends BaseDeal {
    Subscriber msubscriber;
    String token;
    String pubId;
    String date;
    String pagesize;

    public StoreUpDeal(Subscriber msubscriber, String token, String pubId, String date, String pagesize) {
        this.msubscriber = msubscriber;
        this.token = token;
        this.pubId = pubId;
        this.date = date;
        this.pagesize = pagesize;
    }

    @Override
    public Observable getObservable(HttpService methods) {
        return methods.getStoreUpData("0","media",token,pubId,date,pagesize);
    }

    @Override
    public Subscriber getSubscirber() {
        return msubscriber;
    }
}
