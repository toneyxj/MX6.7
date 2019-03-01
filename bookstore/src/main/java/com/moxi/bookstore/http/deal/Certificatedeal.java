package com.moxi.bookstore.http.deal;

import com.moxi.bookstore.http.HttpService;
import com.moxi.bookstore.http.entity.BaseDeal;

import rx.Observable;
import rx.Subscriber;

/**
 * Created by Administrator on 2016/9/28.
 */
public class Certificatedeal extends BaseDeal {
    Subscriber msubsriber;
    String key,deviceNo;
    Long mediaId;
    Integer isFull;
    String token;
    public Certificatedeal(Subscriber subscriber,String key,String deviceNo,Long mediaId,Integer isFull,String token){
        this.msubsriber=subscriber;
        this.mediaId=mediaId;
        this.key=key;
        this.deviceNo=deviceNo;
        this.isFull=isFull;
        this.token=token;
    }



    @Override
    public Observable getObservable(HttpService methods) {
        return methods.getCertificate("getPublishedCertificate",mediaId,key,deviceNo,isFull,
                "browse","json","Android",token);
    }

    @Override
    public Subscriber getSubscirber() {
        return msubsriber;
    }
}
