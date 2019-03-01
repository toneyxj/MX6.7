package com.moxi.bookstore.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import com.moxi.bookstore.BookstoreApplication;
import com.moxi.bookstore.R;
import com.moxi.bookstore.adapter.ChargeAdapter;
import com.moxi.bookstore.base.BookStoreBaseActivity;
import com.moxi.bookstore.bean.ChargeData;
import com.moxi.bookstore.bean.ChargeInfor;
import com.moxi.bookstore.bean.MakeOrderData;
import com.moxi.bookstore.bean.OrderResult;
import com.moxi.bookstore.http.HttpManager;
import com.moxi.bookstore.http.deal.ChargeDeal;
import com.moxi.bookstore.http.deal.MakeOrderDeal;
import com.moxi.bookstore.http.listener.HttpOnNextListener;
import com.moxi.bookstore.http.subscribers.ProgressSubscriber;
import com.moxi.bookstore.utils.ToolUtils;

import java.util.List;

import butterknife.Bind;

public class ChargeActivity extends BookStoreBaseActivity implements AdapterView.OnItemClickListener {
    @Bind(R.id.charge_gv)
    GridView charge_gv;
    @Bind(R.id.worn_tv)
    TextView worn_tv;

    List<ChargeInfor> list;
    ChargeAdapter adapter;
    String token,deviceNo;
    public static final int CHARGE=10086;
    @Override
    protected int getMainContentViewId() {
        return R.layout.activity_charge;
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        deviceNo= BookstoreApplication.getDeviceNO();
        token= ToolUtils.getIntence().getToken(this).getToken();
        /*deviceNo="df38c322-6d1e-441b-80ac-ea3deb378cca";
        token="e_639eb0356802f11c2d7d75e4886f782b4b13f7e526ec63373dcbd206065dc7fb";*/

        charge_gv.setOnItemClickListener(this);
    }

    @Override
    public void onActivityStarted(Activity activity) {
        getChargeData();
    }

    public void goBack(View v){
        finish();
    }
    private void getChargeData() {
        ChargeDeal deal=new ChargeDeal(new ProgressSubscriber(chargeListener,this),deviceNo,token);
        HttpManager.getInstance().doHttpDeal(deal);
        showDialog("加载中...");
    }
    private void makeOrder(String productId){
        if (ToolUtils.getIntence().showBindingDDUser(this)) {
            MakeOrderDeal makeOrderDeal = new MakeOrderDeal(new ProgressSubscriber(orderListener, this),
                    productId, token, deviceNo, true);
            HttpManager manager = HttpManager.getInstance();
            manager.doHttpDeal(makeOrderDeal);
            showDialog("创建订单...");
        }
    }
    String orderId,total,payable,key;
    private HttpOnNextListener orderListener=new HttpOnNextListener<MakeOrderData>(){

        @Override
        public void onNext(MakeOrderData data) {
            hideDialog();
            OrderResult result = data.getResult().getSubmitOrder().getData().getResult();
            orderId = result.getOrder_id();
            total = result.getTotal();
            payable = result.getPayable();
            key = data.getResult().getSubmitOrder().getData().getKey();
//            getEbookOrderFlowV2_Data result = data.getResult().getEbookOrderFlowV2.data;
//            total = String.valueOf(result.totalPrice/100.0f);
//            payable = String.valueOf(result.payable/100.0f);
//            key = result.key;
            goPay();
        }

        @Override
        public void onError() {
            hideDialog();
            showToast("充值失败，请重试");
        }
    };
    HttpOnNextListener chargeListener=new HttpOnNextListener<ChargeData>() {
        @Override
        public void onNext(ChargeData obj) {
            hideDialog();
            list=obj.getActivityInfos();
            adapter=new ChargeAdapter(ChargeActivity.this,list);
            charge_gv.setAdapter(adapter);
            worn_tv.setText("1.充值比例：1元=100铃铛。\n2.充值赠送的银铃铛有效期为 "+30+"天。");
        }

        @Override
        public void onError() {
            hideDialog();

        }
    };
    @Override
    public void onActivityResumed(Activity activity) {

    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {
        setResult(CHARGE);
    }

    @Override
    public void onActivitySaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {

    }

    @Override
    public void onActivityRestoreInstanceState(Bundle savedInstanceState) {

    }

    private void goPay(){
        if(null!=item) {
            PayActivity.startPayActivity(this,orderId,item.getRelationProductId(),payable,total,key,token,"",deviceNo,item.getActivityName(),"",CHARGE);
        }
    }

    ChargeInfor item;
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            item=list.get(position);
            makeOrder(item.getRelationProductId());
    }
}
