package com.moxi.bookstore.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.widget.TextView;

import com.dangdang.zframework.utils.ConfigManager;
import com.moxi.bookstore.BookstoreApplication;
import com.moxi.bookstore.R;
import com.moxi.bookstore.base.BookStoreBaseActivity;
import com.moxi.bookstore.bean.UserInfo;
import com.moxi.bookstore.bean.UserInfoData;
import com.moxi.bookstore.http.HttpManager;
import com.moxi.bookstore.http.deal.RewardDeal;
import com.moxi.bookstore.http.deal.UserInfoDeal;
import com.moxi.bookstore.http.listener.HttpOnNextListener;
import com.moxi.bookstore.http.subscribers.ProgressSubscriber;
import com.moxi.bookstore.utils.ToolUtils;
import com.mx.mxbase.constant.APPLog;

import butterknife.Bind;

public class AcountActivity extends BookStoreBaseActivity {

    @Bind(R.id.mainbalence_tv)
    TextView mainbalence;
    @Bind(R.id.subbalence_tv)
    TextView subbalence;

    String token,deviceNo;
    UserInfo user;
    @Override
    protected int getMainContentViewId() {
        return R.layout.activity_acount;
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        token=getIntent().getStringExtra("token");
        ConfigManager cm=new ConfigManager(this);
        deviceNo = BookstoreApplication.getDeviceNO();
    }

    @Override
    public void onActivityStarted(Activity activity) {
        getAcountData();
    }

    private void getAcountData() {
        UserInfoDeal deal=new UserInfoDeal(new ProgressSubscriber(acountListener,this),token);
        HttpManager manger=HttpManager.getInstance();
        manger.doHttpDeal(deal);
        showDialog("加载中...");
    }

    HttpOnNextListener acountListener=new HttpOnNextListener<UserInfoData>() {
        @Override
        public void onNext(UserInfoData o) {
            hideDialog();
            user=o.getUserInfo();
            updataInfo();
        }

        @Override
        public void onError() {
            hideDialog();

        }
    };
    HttpOnNextListener rewardListener=new HttpOnNextListener() {
        @Override
        public void onNext(Object o) {
            hideDialog();
            showToast("获取成功");
            APPLog.e("get Reward success");
            getAcountData();
        }

        @Override
        public void onError() {
            hideDialog();
            APPLog.e("get Reward failed");
        }
    };

    public void getRward(View v){
        APPLog.e("deviceNo:"+deviceNo);
        APPLog.e("toke:"+token);
        RewardDeal deal=new RewardDeal(new ProgressSubscriber(rewardListener,this),deviceNo,token);
        HttpManager manager=HttpManager.getInstance();
        manager.doHttpDeal(deal);
        showDialog("获取中...");
    }

    public void goCharge(View v){
        if (ToolUtils.getIntence().showBindingDDUser(this)) {
            startActivityForResult(new Intent(this, ChargeActivity.class), ChargeActivity.CHARGE);
        }
    }
    private void updataInfo() {
        mainbalence.setText("金铃铛 : "+user.getMainBalance());
        subbalence.setText("银铃铛 : "+user.getSubBalance());
    }

    @Override
    public void onActivityResumed(Activity activity) {

    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {

    }

    @Override
    public void onActivityRestoreInstanceState(Bundle savedInstanceState) {

    }
    public void goBack(View v){
        finish();
    }
    @Override
    public void onActivityDestroyed(Activity activity) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        getAcountData();
        super.onActivityResult(requestCode, resultCode, data);
    }
}
