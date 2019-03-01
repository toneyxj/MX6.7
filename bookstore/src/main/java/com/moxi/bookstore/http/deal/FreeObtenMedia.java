package com.moxi.bookstore.http.deal;

import com.moxi.bookstore.http.HttpService;
import com.moxi.bookstore.http.entity.BaseDeal;

import rx.Observable;
import rx.Subscriber;

/**
 * Created by Administrator on 2016/10/12.
 */
public class FreeObtenMedia extends BaseDeal {
    Subscriber msubcriber;
    String token;
    long mediaId;

    public FreeObtenMedia(Subscriber msubcriber, String token, long mediaId) {
        this.msubcriber = msubcriber;
        this.token = token;
        this.mediaId = mediaId;
    }

    @Override
    public Observable getObservable(HttpService methods) {
        return methods.getFreeMedia(mediaId,"Android","DDDS-P",token);
    }

    @Override
    public Subscriber getSubscirber() {
        return msubcriber;
    }
}
