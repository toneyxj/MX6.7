package com.moxi.bookstore.http.deal;

import com.moxi.bookstore.http.HttpService;
import com.moxi.bookstore.http.entity.BaseDeal;

import rx.Observable;
import rx.Subscriber;

/**
 * Created by Administrator on 2016/9/30.
 */
public class SubChannelByPrice extends BaseDeal {
    Subscriber msubscriber;
    String type;
    int order,start,end;

    public SubChannelByPrice(Subscriber subscriber,String type,int order,int start,int end){
        this.msubscriber=subscriber;
        this.type=type;
        this.order=order;
        this.start=start;
        this.end=end;
    }
    @Override
    public Observable getObservable(HttpService methods) {
        return methods.getChanelDataByPrice("mediaCategoryLeaf",type,"price",order,start,end);
    }

    @Override
    public Subscriber getSubscirber() {
        return msubscriber;
    }
}
