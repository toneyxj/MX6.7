package com.moxi.bookstore.http.deal;

import com.moxi.bookstore.http.HttpService;
import com.moxi.bookstore.http.entity.BaseDeal;

import rx.Observable;
import rx.Subscriber;

/**
 * Created by Administrator on 2016/11/20.
 */

public class VirtualPayDeal extends BaseDeal {

    Subscriber msub;
    String productArray,sign,timestamp,token,deviceSerialNo;

    public VirtualPayDeal(Subscriber msub, String productArray, String sign, String timestamp, String token, String deviceSerialNo) {
        this.msub = msub;
        this.productArray = productArray;
        this.sign = sign;
        this.timestamp = timestamp;
        this.token = token;
        this.deviceSerialNo = deviceSerialNo;
    }

    @Override
    public Observable getObservable(HttpService methods) {
        return methods.VirtualPay(productArray,sign,timestamp,401,false,"DDDS-P","Android",token
        ,0,0,"buy",30061,deviceSerialNo);
    }

    @Override
    public Subscriber getSubscirber() {
        return msub;
    }
}
