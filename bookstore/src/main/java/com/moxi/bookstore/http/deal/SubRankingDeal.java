package com.moxi.bookstore.http.deal;

import com.moxi.bookstore.http.HttpService;
import com.moxi.bookstore.http.entity.BaseDeal;
import com.moxi.bookstore.requestModel.RankingListModel;

import rx.Observable;
import rx.Subscriber;

/**
 * Created by xj on 2017/10/16.
 */

public class SubRankingDeal extends BaseDeal {
    private Subscriber mSubscriber;
    private RankingListModel model;

    public SubRankingDeal(Subscriber getData, RankingListModel model) {
        this.mSubscriber = getData;
        this.model=model;
    }
    //media/api.go?action=mediaCategoryLeaf&category=XS2&dimension=dd_sale&start=0&end=19
    @Override
    public Observable getObservable(HttpService methods) {
        return methods.getRankData(model.action,model.category,model.timeDimension,model.rankType,model.payType,model.getStart(),model.getEnd());
    }

    @Override
    public Subscriber getSubscirber() {
        return mSubscriber;
    }
}