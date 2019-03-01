package com.moxi.bookstore.http.deal;

import com.moxi.bookstore.http.HttpService;
import com.moxi.bookstore.http.entity.BaseDeal;

import rx.Observable;
import rx.Subscriber;

/**
 * Created by Administrator on 2016/11/15.
 */

public class RewardDeal extends BaseDeal {
    Subscriber msub;
    String deviceNo,token;

    public RewardDeal(Subscriber msub, String deviceNo, String token) {
        this.msub = msub;
        this.deviceNo = deviceNo;
        this.token = token;
    }

    @Override
    public Observable getObservable(HttpService methods) {
        return methods.getReward("Android",662,deviceNo,"30061",token);
    }

    @Override
    public Subscriber getSubscirber() {
        return msub;
    }


}
