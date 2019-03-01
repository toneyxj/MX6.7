package com.moxi.bookstore.http.deal;

import com.moxi.bookstore.http.HttpService;
import com.moxi.bookstore.http.entity.BaseDeal;

import rx.Observable;
import rx.Subscriber;

/**
 * Created by Administrator on 2016/9/26.
 */
public class SubSearchMediadeal extends BaseDeal {
    Subscriber subscriber;
    String keyword;

    public  SubSearchMediadeal(Subscriber subscriber,String str){
        this.subscriber=subscriber;
        this.keyword=str;
    }

    @Override
    public Observable getObservable(HttpService methods) {
        return methods.getSearchMediaData("searchMedia",keyword,0,500);
    }

    @Override
    public Subscriber getSubscirber() {
        return subscriber;
    }
}
