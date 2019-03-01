package com.moxi.bookstore.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.moxi.bookstore.R;
import com.moxi.bookstore.base.BookStoreBaseActivity;
import com.moxi.bookstore.bean.DeleteShoppingCart;
import com.moxi.bookstore.bean.VirtualPayMentData;
import com.moxi.bookstore.http.HttpManager;
import com.moxi.bookstore.http.HttpService;
import com.moxi.bookstore.http.deal.ParamsMap;
import com.moxi.bookstore.http.deal.VirtualPayDeal;
import com.moxi.bookstore.http.entity.BaseDeal;
import com.moxi.bookstore.http.listener.HttpOnNextListener;
import com.moxi.bookstore.http.subscribers.ProgressSubscriber;
import com.mx.mxbase.constant.APPLog;

import java.util.HashMap;

import butterknife.Bind;
import rx.Observable;
import rx.Subscriber;

public class BalencePayActivity extends BookStoreBaseActivity {
    @Bind(R.id.book_title_tv)
    TextView book_title_tv;
    @Bind(R.id.paybalence_tv)
    TextView paybalence_tv;
    @Bind(R.id.mybalence_tv)
    TextView mybalence_tv;
    @Bind(R.id.gold_tv)
    TextView gold_tv;
    @Bind(R.id.silver_tv)
    TextView silver_tv;
    @Bind(R.id.paywithbalence)
    Button pay;

    String productArray,productIds,key,token,deviceNo,title;
    long subbalance,mainbalance,payable;
    @Override
    protected int getMainContentViewId() {
        return R.layout.activity_balence_pay;
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        productArray=getIntent().getStringExtra("productArray");
        productIds=getIntent().getStringExtra("productIds");
        cartId=getIntent().getStringExtra("cartId");
        key=getIntent().getStringExtra("key");
        token=getIntent().getStringExtra("token");
        deviceNo=getIntent().getStringExtra("deviceNo");
        title=getIntent().getStringExtra("title");
        payable=getIntent().getLongExtra("payable",-1);
        subbalance=getIntent().getLongExtra("subbalance",-1);
        mainbalance=getIntent().getLongExtra("mainbalance",-1);
    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {
        book_title_tv.setText(title);
        paybalence_tv.setText("支付铃铛："+payable);
        mybalence_tv.setText("我的铃铛："+(subbalance+mainbalance));
        gold_tv.setText("金铃铛："+mainbalance);
        silver_tv.setText("银铃铛："+subbalance);
    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {

    }

    public void goBack(View v){finish();}
    public void cartList(View v) {
        Intent cartIt=new Intent();
        cartIt.setClass(this,CartActivity.class);
        startActivity(cartIt);
        finish();
    }

    @Override
    public void onActivitySaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {

    }

    @Override
    public void onActivityRestoreInstanceState(Bundle savedInstanceState) {

    }
    public void paywithbalence(View v){
        balencePay();
        v.setClickable(false);
    }

    private void balencePay(){
        long time=System.currentTimeMillis();
        VirtualPayDeal vpd=new VirtualPayDeal(new ProgressSubscriber(virtalpayListener,this),
                productArray,key,String.valueOf(time),token,deviceNo);
        HttpManager manager=HttpManager.getInstance();
        manager.doHttpDeal(vpd);
        showDialog("铃铛支付...");
        showToast("铃铛支付");
    }

    HttpOnNextListener virtalpayListener=new HttpOnNextListener<VirtualPayMentData>() {
        @Override
        public void onNext(VirtualPayMentData obj) {
            if (isfinish)return;
            hideDialog();
            APPLog.e("铃铛支付成功");
            showToast("支付成功");
//            delFormCatlist();
            finish();
        }

        @Override
        public void onError() {
            if (isfinish)return;
            hideDialog();
            pay.setClickable(true);
            showToast("支付失败");
            APPLog.e("铃铛支付失败");

        }
    };
    String cartId="";

    private void delFormCatlist() {
        final HashMap<String, Object> params = new ParamsMap(this);
        //params.put("action", "deleteShoppingCart");
        //params.put("cartId", "1609271123042346");
        APPLog.e("cartId:"+cartId);
        params.put("cartId", cartId);
        params.put("productIds", productIds.replace("\"",""));
        params.put("token",token);
        HttpManager.getInstance().doHttpDeal(new BaseDeal() {
            @Override
            public Observable getObservable(HttpService methods) {
                return methods.deleteShoppingCart(params);
            }

            @Override
            public Subscriber getSubscirber() {
                return new ProgressSubscriber(
                        new HttpOnNextListener<DeleteShoppingCart>() {
                            @Override
                            public void onError() {
                                if (isFinishing())return;
                                ToastUtil("更新购物车失败");
                            }

                            @Override
                            public void onNext(DeleteShoppingCart o) {
                                if (isFinishing())return;
                                APPLog.e("删除购物车成功!");

                            }

                        }, BalencePayActivity.this);
            }

        });

    }
}
