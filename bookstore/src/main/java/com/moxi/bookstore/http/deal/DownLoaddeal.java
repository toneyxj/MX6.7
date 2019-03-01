package com.moxi.bookstore.http.deal;

import com.moxi.bookstore.http.HttpService;
import com.mx.mxbase.constant.APPLog;

import rx.Observable;
import rx.Subscriber;

/**
 * Created by Administrator on 2016/9/27.
 */
public class DownLoaddeal  {
    private Subscriber mSubscriber;
    private long id;
    private String token;
    private int isFull;
    public DownLoaddeal(Subscriber mSubscriber,long id,String token,int isFull){
        this.mSubscriber=mSubscriber;
        this.id=id;
        this.token=token;
        this.isFull=isFull;
    }




    public Observable getObservable(HttpService methods) {
        APPLog.e("DownLoaddeal-id",id);//1900700676
        APPLog.e("DownLoaddeal-isFull",isFull);//1
        APPLog.e("DownLoaddeal-token",token);//e_f4b0b50436451cdff55e5eee05667d24bdb08fff2721842f7ab467032529c104
        return methods.downloadSDMedia("downloadMediaWhole",id,isFull,"Android","DDDS-P",token);
    }


    public Subscriber getSubscirer() {
        return mSubscriber;
    }
}
