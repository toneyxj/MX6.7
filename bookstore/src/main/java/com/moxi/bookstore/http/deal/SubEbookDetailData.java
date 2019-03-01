package com.moxi.bookstore.http.deal;

import com.moxi.bookstore.http.HttpService;
import com.moxi.bookstore.http.entity.BaseDeal;
import com.mx.mxbase.constant.APPLog;

import rx.Observable;
import rx.Subscriber;

/**
 * Created by Administrator on 2016/9/22.
 * 详情数据
 */
public class SubEbookDetailData extends BaseDeal {
    private Subscriber mSubscriber;
    private long id;
    private String token;
    public SubEbookDetailData(Subscriber getData,long id,String token) {
        this.mSubscriber = getData;
        this.id=id;
        this.token=token;
        APPLog.e("SubEbookDetailData-token",token);
    }

    //media/api.go?action=getMedia&saleId=1900423761&deviceType=Android
    @Override
    public Observable getObservable(HttpService methods) {
        return methods.getEbookDetailData("getMedia",id,"Android",token,"6.0");
    }

    @Override
    public Subscriber getSubscirber() {
        return mSubscriber;
    }
}
