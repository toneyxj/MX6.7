package com.moxi.bookstore.http.deal;

import com.moxi.bookstore.http.HttpService;
import com.moxi.bookstore.http.entity.BaseDeal;

import rx.Observable;
import rx.Subscriber;

/**
 * Created by Administrator on 2016/9/20.
 * 分类数据
 */
public class Subcatetorydeal extends BaseDeal {
    private Subscriber mSubscriber;


    public Subcatetorydeal(Subscriber getData) {
        this.mSubscriber = getData;
    }

    @Override
    public Observable getObservable(HttpService methods) {
        //return methods.getCatetoryData("mediaCategory","dddsonly",0,5,4);
        return methods.getCatetoryData();
    }

    @Override
    public Subscriber getSubscirber() {
        return mSubscriber;
    }
}
