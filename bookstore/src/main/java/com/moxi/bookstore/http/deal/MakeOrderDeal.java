package com.moxi.bookstore.http.deal;

import com.moxi.bookstore.http.HttpService;
import com.moxi.bookstore.http.entity.BaseDeal;
import com.mx.mxbase.constant.APPLog;

import rx.Observable;
import rx.Subscriber;

/**
 * Created by Administrator on 2016/10/13.
 */
public class MakeOrderDeal extends BaseDeal {
    Subscriber msubscriber;
    String productIds, token, permentId;
    String field;

    public MakeOrderDeal(Subscriber msubscriber, String productIds, String token, String permentId, boolean isrecharge) {
        this.msubscriber = msubscriber;
        this.productIds = productIds;
        this.token = token;
        this.permentId = permentId;
        if (isrecharge) {
            field = "{\"dependActions\":" +
                    "[{\"action\":\"appendCart\"," +
                    "\"params\":{\"productIds\":" + productIds + ",\"oneKeyBuy\":\"true\",\"referType\":\"personal\",\"fromPlatform\":\"401\",\"orderSource\":\"77000\"}," +
                    "\"parseParams\":{\"cartId\":\"cartId\"}}," +
                    "{\"action\":\"getOrderFlow\"," +
                    "\"params\":{\"productIds\":" + productIds + ",\"fromPlatform\":\"401\",\"orderSource\":\"77000\"}}," +
                    "{\"action\":\"savePayment\"," +
                    "\"params\":{\"fromPlatform\":\"401\",\"orderSource\":\"77000\"}}," +
                    "{\"action\":\"submitOrder\"," +
                    "\"params\":{\"isChargeOrder\":\"1\",\"fromPlatform\":\"401\",\"isPaperBook\":\"false\",\"depositPayment\":\"1018\",\"orderSource\":\"77000\"}}" +
                    "]}";
        } else {
            field = "{\"noDependActions\":[{\"action\":\"getEbookOrderFlowV2\",\"params\":{\"productIds\":\"" + productIds + "\"}}," +
                    "{\"action\":\"getAccount\"}," +
                    "{\"action\":\"getValidCoupon\",\"params\":{\"isPaperBook\":\"false\"}}," +
                    "{\"action\":\"getDdMoneyList\"}]}";
        }
//http://e.dangdang.com/media/api2.go?action=multiAction&field={"noDependActions":[{"action":"getEbookOrderFlowV2","params":{"productIds":"1900699373"}},{"action":"getAccount"},{"action":"getValidCoupon","params":{"isPaperBook":"false"}},{"action":"getDdMoneyList"}]}&permanentId=bd3ade25-8567-477b-990a-0a3712fe097b&productIds=1900699373&platformSource=DDDS-P&deviceType=Android&token=e_c6fa58b2d6c7754844c3043c021196de017324c3b0aa9ca7238bb40fe31c6b88
    }

    @Override
    public Observable getObservable(HttpService methods) {
        APPLog.e("field", field);
        APPLog.e("permanentId", permentId);
        APPLog.e("productIds", productIds);
        APPLog.e("platformSource", "DDDS-P");
        APPLog.e("deviceType", "Android");
        APPLog.e("token", token);
        return methods.MakeOrder(field, permentId, "DDDS-P", "Android", token);
    }

    @Override
    public Subscriber getSubscirber() {
        return msubscriber;
    }
}
