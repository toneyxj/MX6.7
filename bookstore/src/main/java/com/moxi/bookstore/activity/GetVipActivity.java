package com.moxi.bookstore.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.text.Html;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.moxi.bookstore.R;
import com.moxi.bookstore.adapter.VipMealAdapter;
import com.moxi.bookstore.adapter.VipPrivilepeAdapter;
import com.moxi.bookstore.base.BookStoreBaseActivity;
import com.moxi.bookstore.modle.mediaCategory.ChannelMonthlyStrategy;
import com.moxi.bookstore.request.ReuestKeyValues;
import com.moxi.bookstore.request.json.Connector;
import com.moxi.bookstore.request.json.JsonAnalysis;
import com.mx.mxbase.interfaces.InsureOrQuitListener;
import com.mx.mxbase.view.NoListView;
import com.mx.mxbase.view.SlideLinerlayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

/**
 * 购买vip
 */
public class GetVipActivity extends BookStoreBaseActivity implements View.OnClickListener{

    @Override
    protected int getMainContentViewId() {
        return R.layout.activity_get_vip;
    }
    @Bind(R.id.back_rl)
    RelativeLayout back_rl;
    @Bind(R.id.search_rl)
    RelativeLayout search_rl;

    @Bind(R.id.silder_layout)
    SlideLinerlayout silder_layout;//滑动控件
    @Bind(R.id.vip_privilege)
    NoListView vip_privilege;//租阅特权
    @Bind(R.id.vip_meal)
    NoListView vip_meal;//租阅套餐

    //无数据布局
    @Bind(R.id.body_ll)
    LinearLayout body_ll;
    @Bind(R.id.error_body)
    RelativeLayout error_body;
    @Bind(R.id.reflash_tv)
    TextView reflash_tv;

    @Bind(R.id.get_vip)
    Button get_vip;

    private VipPrivilepeAdapter privilepeAdapter;
    private VipMealAdapter mealAdapter;
    private List<ChannelMonthlyStrategy> listData=new ArrayList<>();

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        //获取传递数据完成
        back_rl.setOnClickListener(this);
        search_rl.setOnClickListener(this);
        reflash_tv.setOnClickListener(this);
        get_vip.setOnClickListener(this);

        vip_meal.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mealAdapter.setSelectIndex(position);
            }
        });
        getPrivilepe();
    }

    private void getPrivilepe(){
        body_ll.setVisibility(View.INVISIBLE);
        error_body.setVisibility(View.VISIBLE);
        List<ReuestKeyValues> valuePairs = new ArrayList<>();
        valuePairs.add(new ReuestKeyValues("action", "block"));
        valuePairs.add(new ReuestKeyValues("code", "getVipPrivilege"));
        valuePairs.add(getthisToken());
        getData(valuePairs, Connector.getInstance().block, Connector.getInstance().url, true, "加载中...");
    }
    private void gethannel(){
        List<ReuestKeyValues> valuePairs = new ArrayList<>();
        valuePairs.add(new ReuestKeyValues("action", "channel"));
        valuePairs.add(new ReuestKeyValues("cId", "5425"));
        valuePairs.add(new ReuestKeyValues("deviceType", "Android"));
        valuePairs.add(getthisToken());
        getData(valuePairs, Connector.getInstance().channel, Connector.getInstance().url, true, "加载中...");
    }
    private void getVip(){
        showDialog("VIP...");
        List<ReuestKeyValues> valuePairs = new ArrayList<>();
        valuePairs.add(new ReuestKeyValues("action", "buyMonthlyAuthority"));
        valuePairs.add(new ReuestKeyValues("deviceType", "Android"));
        valuePairs.add(new ReuestKeyValues("platformSource", "DDDS-P"));
        valuePairs.add(new ReuestKeyValues("cId", "5425"));
        valuePairs.add(new ReuestKeyValues("channelMonthlyStrategyId", String.valueOf(mealAdapter.getChannelMonth().id)));
        valuePairs.add(new ReuestKeyValues("isAutomaticallyRenew", "0"));
        valuePairs.add(getthisToken());
        getData(valuePairs, Connector.getInstance().buyMonthlyAuthority, Connector.getInstance().url, false, "加载中...");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_rl://返回
                this.onBackPressed();
                break;
            case R.id.search_rl://搜索
                startActivity(new Intent(this, SearchBookActivity.class));
                break;
            case R.id.reflash_tv:
                getPrivilepe();
                break;
            case R.id.get_vip:
                insureDialog("购买提示", "请确认购买" + mealAdapter.getChannelMonth().name, "购买", "取消", "get_vip", new InsureOrQuitListener() {
                    @Override
                    public void isInsure(Object code, boolean is) {
                        if (is){
                            getVip();
                        }
                    }
                });
                break;
            default:
                break;
        }
    }
    private void initPrivilepeAdapter(String[] value){
        List<String> values=new ArrayList<>();
        for (int i = 0; i < value.length; i++) {
            if (!value[i].equals(" ")&&!value[i].contains("￼")&&!value[i].equals("")&&!value[i].equals("\\n")){
                values.add(value[i]);
            }
        }
        if (privilepeAdapter==null){
            privilepeAdapter=new VipPrivilepeAdapter(this,values);
            vip_privilege.setAdapter(privilepeAdapter);
        }else {
            privilepeAdapter.notifyDataSetChanged();
        }
    }
    private void initmealAdapter(){
        if (mealAdapter==null){
            mealAdapter=new VipMealAdapter(this,listData);
            vip_meal.setAdapter(mealAdapter);
        }else {
            mealAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void Success(String result, String code) {
        if (code.equals( Connector.getInstance().block)){
            String value= JsonAnalysis.getInstance().getBlock(result);
            value=value.substring(1,value.length()-1);
            value= Html.fromHtml(value).toString();
            String[] spils=value.split("\\n");
            initPrivilepeAdapter(spils);
            gethannel();
        }else if (code.equals( Connector.getInstance().channel)){
            body_ll.setVisibility(View.VISIBLE);
            error_body.setVisibility(View.INVISIBLE);
            listData=JsonAnalysis.getInstance().getChannelMonthlyStrategys(result);
            initmealAdapter();
        }else if (code.equals( Connector.getInstance().buyMonthlyAuthority)){
            //购买结果
            showToast("购买成功");
            setResult(RESULT_OK);
            this.finish();
        }
    }

    @Override
    public void onFail(String code, boolean showFail, int failCode, String msg,String result) {
        super.onFail(code, showFail, failCode, msg, result);
        if (code.equals( Connector.getInstance().buyMonthlyAuthority)){
            try {
                //购买结果
                JSONObject object= JSON.parseObject(result);
                JSONObject status=object.getJSONObject("status");
                int _code=status.getIntValue("code");
                switch (_code){
                    case 99999://其它错误
                        String _msg=status.getString("message");
                        if (_msg.contains("账户余额不足")){
                            goCharge();
                        }
                        break;
                    default:
                        showToast(msg);
                        break;
                }
            }catch (Exception e){
                showToast(msg);
            }
        }
    }

    /**
     * 跳转购买铃铛页面
     */
    public void goCharge(){
        showToast("铃铛不够请充值后购买！！");
        startActivity(new Intent(this,ChargeActivity.class));
    }
    @Override
    public void onActivityStarted(Activity activity) {

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

}
