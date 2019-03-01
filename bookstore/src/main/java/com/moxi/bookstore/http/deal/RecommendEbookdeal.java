package com.moxi.bookstore.http.deal;

import com.moxi.bookstore.http.HttpService;
import com.moxi.bookstore.http.entity.BaseDeal;

import rx.Observable;
import rx.Subscriber;

/**
 * Created by Administrator on 2016/10/10.
 */
public class RecommendEbookdeal extends BaseDeal {
    Subscriber msubscriber;
    private String token;

    public RecommendEbookdeal(Subscriber msubscriber,String token) {
        this.msubscriber = msubscriber;
        this.token=token;
    }

    @Override
    public Observable getObservable(HttpService methods) {
        return methods.getRecommendData(token);
    }

    @Override
    public Subscriber getSubscirber() {
        return msubscriber;
    }
}
