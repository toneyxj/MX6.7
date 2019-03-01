package com.moxi.updateapp;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.moxi.updateapp.model.SystemOtaModel;
import com.mx.mxbase.base.BaseActivity;
import com.mx.mxbase.constant.Constant;
import com.mx.mxbase.utils.AppUtil;
import com.mx.mxbase.utils.ToastUtils;
import com.mx.mxbase.view.AlertDialog;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import soft.com.updateapp.R;

/**
 * Created by Archer on 16/10/20.
 */
public class MXUpdateActivity extends BaseActivity implements View.OnClickListener {

    private LinearLayout llBack;
    private TextView tvMidTitle;
    private TextView tvBack;
    private ImageView imgUpdate;
    List<MXUpdateModel> mxUpdateModelList;

    @Override
    protected int getMainContentViewId() {
        return R.layout.mx_activity_update;
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        init();
    }

    /**
     * 初始化视图
     */
    private void init() {

        llBack = (LinearLayout) findViewById(R.id.ll_base_back);
        tvMidTitle = (TextView) findViewById(R.id.tv_base_mid_title);
        tvBack = (TextView) findViewById(R.id.tv_base_back);
        imgUpdate = (ImageView) findViewById(R.id.img_update_from_net);

        llBack.setVisibility(View.VISIBLE);
        tvMidTitle.setText("更新");
        tvBack.setText("设置");

        llBack.setOnClickListener(this);
        imgUpdate.setOnClickListener(this);

        mxUpdateModelList = (List<MXUpdateModel>) getIntent().getSerializableExtra("down");
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
    public void onClick(View view) {
        if (R.id.ll_base_back == view.getId()) {
            this.finish();
        } else if (R.id.img_update_from_net == view.getId()) {
            checkSsytemUpdate();
        }
    }

    private void checkSsytemUpdate() {
        OkHttpUtils.post().url(Constant.CHECK_LOWER_UPDATE).addParams("versionName", AppUtil.getPackageInfo(this).versionName).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                if (isfinish)return;
                ToastUtils.getInstance().showToastShort("网络请求失败，请检查网络");
            }

            @Override
            public void onResponse(String response, int id) {
                if (isfinish)return;
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONObject result = new JSONObject(jsonObject.getString("result"));
                    int lowerVersion = result.getInt("baseCode");
                    if (lowerVersion > Constant.LOCAL_VIR_VERSION) {
                        String url = result.getString("storeAdr");
                        String MD5 = result.getString("code");//暂时没有使用上
                        String describe = result.getString("desc");
                        DownLoadSystemActivity.startDownLoadSystem(MXUpdateActivity.this, new SystemOtaModel(url, MD5, describe));
                        return;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                checkUpdate();
            }
        });
    }

    private void checkUpdate() {
        List<PackageInfo> packages = this.getPackageManager().getInstalledPackages(0);
        List<MXAppInfo> params = new ArrayList<>();
        MXAppInfo mxAppInfo;
        for (int i = 0; i < packages.size(); i++) {
            PackageInfo packageInfo = packages.get(i);
            String packageName = packageInfo.packageName;
            if (packageName.startsWith("com.moxi")) {
                mxAppInfo = new MXAppInfo();
                mxAppInfo.setVersionName(packageInfo.versionName);
                mxAppInfo.setVersionCode(packageInfo.versionCode);
                mxAppInfo.setPackageName(packageInfo.packageName);
                params.add(mxAppInfo);
            }
        }
        try {
            JsonArray jsonEle = new JsonArray();
            JsonObject jsonObject;
            for (int i = 0; i < params.size(); i++) {
                jsonObject = new JsonObject();
                String packageName = params.get(i).getPackageName();
                int versionCode = params.get(i).getVersionCode();
                String versionName = params.get(i).getVersionName();
                jsonObject.addProperty("packageName", packageName);
                jsonObject.addProperty("versionCode", versionCode);
                jsonObject.addProperty("versionName", versionName);
                jsonEle.add(jsonObject);
            }

            final OkHttpUtils okHttpUtils = OkHttpUtils.getInstance();
            okHttpUtils.post().url(Constant.CHECK_VERSION).addParams("update", jsonEle.toString())
                    .build().execute(new StringCallback() {
                @Override
                public void onError(Call call, Exception e, int id) {
                    Log.d("update", "data==>" + e.getMessage());
                }

                @Override
                public void onResponse(String response, int id) {
                    Log.d("update", "data==>" + response);
                    if (isfinish)return;
                    try {
                        JSONObject reObj = new JSONObject(response);
                        int code = reObj.optInt("code", -1);
                        if (code == 0) {
                            JSONArray jsonArray = reObj.optJSONArray("result");
                            final List<MXUpdateModel> mxUpdateModel = new ArrayList<MXUpdateModel>();
                            MXUpdateModel mxUpdateModel1;
                            final StringBuffer stringBuffer = new StringBuffer();
                            for (int i = 0; i < jsonArray.length(); i++) {
                                mxUpdateModel1 = new MXUpdateModel();
                                JSONObject jsonObject1 = jsonArray.optJSONObject(i);
                                mxUpdateModel1.setAppDesc(jsonObject1.optString("appDesc"));
                                mxUpdateModel1.setDownloadUrl(jsonObject1.optString("downloadUrl"));
                                mxUpdateModel1.setPackageName(jsonObject1.optString("packageName"));
                                mxUpdateModel1.setVersionName(jsonObject1.optString("versionName"));
                                mxUpdateModel1.setMd5Str(jsonObject1.optString("md5"));
                                mxUpdateModel1.setIsLancher(jsonObject1.optInt("isLuncher"));
                                mxUpdateModel1.setVersionCode(jsonObject1.optInt("versionCode"));
                                mxUpdateModel1.setUpdateType(jsonObject1.optInt("updateType"));
                                mxUpdateModel.add(mxUpdateModel1);
                                stringBuffer.append(jsonObject1.optString("appDesc") + "\n");
                            }
                            if (mxUpdateModel.size() > 0) {
                                // TODO: 2016/10/14 有更新
                                new AlertDialog(MXUpdateActivity.this).builder().
                                        setTitle("更新提示").setMsg(stringBuffer.toString()).setCancelable(false).setNegativeButton("确定", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        // TODO: 16/9/2
                                        Intent intent = new Intent();
                                        intent.setClass(MXUpdateActivity.this, MXDownloadActivity.class);
                                        Bundle bundle = new Bundle();
                                        intent.putExtra("install_flag", 3);
                                        bundle.putSerializable("down", (Serializable) mxUpdateModel);
                                        intent.putExtras(bundle);
                                        startActivity(intent);
                                    }
                                }).setPositiveButton("取消", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {

                                    }
                                }).show();
                            } else {
                                ToastUtils.getInstance().showToastShort("暂无更新！！");
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            Log.d("update", e.getMessage());
        }
    }
}
