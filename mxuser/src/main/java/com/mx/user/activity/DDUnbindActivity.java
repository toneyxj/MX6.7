package com.mx.user.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mx.mxbase.base.BaseActivity;
import com.mx.mxbase.constant.Constant;
import com.mx.mxbase.utils.ActivitysManager;
import com.mx.mxbase.utils.GsonTools;
import com.mx.mxbase.utils.MXUamManager;
import com.mx.mxbase.utils.Toastor;
import com.mx.mxbase.view.AlertDialog;
import com.mx.user.R;
import com.mx.user.model.UserInfoModel;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.Bind;
import okhttp3.Call;

/**
 * Created by King on 2017/12/6.
 */

public class DDUnbindActivity extends BaseActivity implements View.OnClickListener {
    @Bind(R.id.tv_dd_username)
    TextView tvDDUser;
    @Bind(R.id.tv_base_mid_title)
    TextView tvMidTitle;
    @Bind(R.id.ll_base_back)
    LinearLayout llBack;
    @Bind(R.id.rl_dd_unbind)
    RelativeLayout rlUnBind;

    private UserInfoModel userInfoModel;

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        MXLoginActivity.isBack=getIntent().getBooleanExtra("is_back",false);
        String temp = share.getString("v3_user_info_json");
        userInfoModel = GsonTools.getPerson(temp, UserInfoModel.class);
        llBack.setVisibility(View.VISIBLE);
        tvMidTitle.setText("解绑账号");
        llBack.setOnClickListener(this);
        rlUnBind.setOnClickListener(this);
        tvDDUser.setText(userInfoModel.getResult().getDdUser().getData().getUser().getUserName());
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

    @Override
    public void onActivityDestroyed(Activity activity) {

    }

    @Override
    protected int getMainContentViewId() {
        return R.layout.mx_activity_unbind_dd_user;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_base_back:
                this.finish();
                break;
            case R.id.rl_dd_unbind:
                new AlertDialog(DDUnbindActivity.this).builder().setTitle("提示").setCancelable(false).setMsg("" +
                        "是否解绑当前当当账号？").setNegativeButton("确定", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (userInfoModel == null) {
                            Toastor.showToast(DDUnbindActivity.this, "请先登录之后再操作！");
                            return;
                        }
                        OkHttpUtils.post().url(Constant.unBindUrl).addParams("appSession", userInfoModel.getResult().getAppSession()).build().execute(new StringCallback() {
                            @Override
                            public void onError(Call call, Exception e, int id) {
                                Toastor.showToast(DDUnbindActivity.this, "请求失败！");
                            }

                            @Override
                            public void onResponse(String response, int id) {
                                if (isfinish)return;
                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    int code = jsonObject.getInt("code");
                                    if (code == 0) {
                                        UserInfoModel userModel = GsonTools.getPerson(response, UserInfoModel.class);
                                        share.setCache("v3_user_info_json", response);
                                        if (userModel != null) {
                                            MXUamManager.insertUam(DDUnbindActivity.this, Constant.MAIN_PACKAGE, userModel.getResult().getAppSession(), userModel.getResult().getDdToken(), share.getString("v3_user_info_json"));
                                            if (!MXLoginActivity.isBack) {
                                                Intent unbind = new Intent("com.moxi.bind.dd.user.ACTION");
                                                unbind.putExtra("action_type", "unbind");
                                                LocalBroadcastManager.getInstance(DDUnbindActivity.this).sendBroadcast(unbind);
                                            }else {
                                                ActivitysManager.getAppManager().finishAllActivity();
                                                MXLoginActivity.isBack=false;
                                            }
                                            DDUnbindActivity.this.finish();
                                        }
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                }).setPositiveButton("取消", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                }).show();
                break;
            default:
                break;
        }
    }
}
