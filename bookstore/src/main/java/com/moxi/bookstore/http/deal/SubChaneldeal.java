package com.moxi.bookstore.http.deal;

import com.moxi.bookstore.http.HttpService;
import com.moxi.bookstore.http.entity.BaseDeal;

import rx.Observable;
import rx.Subscriber;

/**
 * Created by Administrator on 2016/9/21.
 * chanel数据
 */
public class SubChaneldeal extends BaseDeal {
    private Subscriber mSubscriber;
    private String type;
    private String dimension;
    int start,end;
    private String token;

    public SubChaneldeal(Subscriber getData,String type,String dimension,int start,int end,String token) {
        this.mSubscriber = getData;
        this.type=type;
        this.dimension=dimension;
        this.start=start;
        this.end=end;
        this.token=token;
    }
    //media/api.go?action=mediaCategoryLeaf&category=XS2&dimension=dd_sale&start=0&end=19
    @Override
    public Observable getObservable(HttpService methods) {
        return methods.getChanelData("mediaCategoryLeaf",type,dimension,start,end, token);
    }

    @Override
    public Subscriber getSubscirber() {
        return mSubscriber;
    }
}
