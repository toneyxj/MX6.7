package com.moxi.bookstore.http.deal;

import com.moxi.bookstore.http.HttpService;
import com.moxi.bookstore.http.entity.BaseDeal;

import rx.Observable;
import rx.Subscriber;

/**
 * Created by Administrator on 2016/10/16.
 */
public class StoreListDeal extends BaseDeal {
    Subscriber msubcriber;
    private String storeDateLong;
    String token;

    public StoreListDeal(Subscriber msubcriber, String token,String storeDateLong) {
        this.msubcriber = msubcriber;
        this.token = token;
        this.storeDateLong=storeDateLong;
    }

    @Override
    public Observable getObservable(HttpService methods) {
        return methods.getStoreData(token,storeDateLong);
    }

    @Override
    public Subscriber getSubscirber() {
        return msubcriber;
    }
}
