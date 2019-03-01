package com.moxi.bookstore.http.deal;

import com.moxi.bookstore.http.HttpService;
import com.moxi.bookstore.http.entity.BaseDeal;

import rx.Observable;
import rx.Subscriber;

/**
 * Created by Administrator on 2016/12/19.
 */

public class OrderFlowDeal extends BaseDeal {
    Subscriber msub;
    String deviceNO,token,productsId;

    public OrderFlowDeal(Subscriber msub, String deviceNO, String token, String productsId) {
        this.msub = msub;
        this.deviceNO = deviceNO;
        this.token = token;
        this.productsId = productsId;
    }

    @Override
    public Observable getObservable(HttpService methods) {
        return methods.getOFlow(deviceNO,"30061","json","DDDS-P","Android",productsId,token,"","","401");
    }

    @Override
    public Subscriber getSubscirber() {
        return msub;
    }
}
