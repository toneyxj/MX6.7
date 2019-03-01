package com.moxi.updateapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.os.Bundle;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mx.mxbase.constant.APPLog;
import com.mx.mxbase.constant.Constant;
import com.mx.mxbase.utils.ToastUtils;
import com.mx.mxbase.utils.Toastor;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

/**
 * Created by zhengdelong on 2016/10/19.
 */

public class UpdateUtil {

    WeakReference<Activity> activity;
    Context context;

    public UpdateUtil(Activity activity, Context context) {
        this.activity =new WeakReference<Activity>(activity);
        this.context = context;
    }
    public Activity getActivity(){
        return activity.get();
    }

    public void startUpdate(boolean showHitn) {
        APPLog.d("app", "click update...");
        List<PackageInfo> packages = context.getPackageManager().getInstalledPackages(0);
        List<MXAppInfo> mxAppInfos = new ArrayList<>();
        MXAppInfo mxAppInfo;
        for (int i = 0; i < packages.size(); i++) {
            PackageInfo packageInfo = packages.get(i);
            String packageName = packageInfo.packageName;
            if (packageName.startsWith("com.moxi")) {
                mxAppInfo = new MXAppInfo();
                mxAppInfo.setVersionName(packageInfo.versionName);
                mxAppInfo.setVersionCode(packageInfo.versionCode);
                mxAppInfo.setPackageName(packageInfo.packageName);
                mxAppInfos.add(mxAppInfo);
            }
        }
        checkUpdate(mxAppInfos,showHitn);
    }
    public void startUpdate() {
        startUpdate(false);
    }

    private void checkUpdate(List<MXAppInfo> params, final boolean showHitn) {
        try {
            JsonArray jsonEle = new JsonArray();
            JsonObject jsonObject;
            for (int i = 0; i < params.size(); i++) {
                jsonObject = new JsonObject();
                String packageName = params.get(i).getPackageName();
                int versionCode = params.get(i).getVersionCode();
                String versionName = params.get(i).getVersionName();
                jsonObject.addProperty("packageName", packageName);
//                if (packageName.equals("com.moxi.bookstore")){
//                    jsonObject.addProperty("versionCode", 220);
//                    jsonObject.addProperty("versionName", "ZXTopsirH68_2.2.0");
//                }else if (packageName.equals("com.moxi.filemanager")){
//                    jsonObject.addProperty("versionCode", 70);
//                    jsonObject.addProperty("versionName", "ZXTopsirH68_1.7.0");
//                }else {
//                    jsonObject.addProperty("versionCode", versionCode);
//                    jsonObject.addProperty("versionName", versionName);
//                }
                jsonObject.addProperty("versionCode", versionCode);
                jsonObject.addProperty("versionName", versionName);
                jsonEle.add(jsonObject);
            }

            final OkHttpUtils okHttpUtils = OkHttpUtils.getInstance();
            APPLog.d("update", "param===>" + jsonEle.toString());
            okHttpUtils.post().url(Constant.CHECK_VERSION).addParams("update", jsonEle.toString())
                    .build().execute(new StringCallback() {
                @Override
                public void onError(Call call, Exception e, int id) {
                    APPLog.d("update", "data==>" + e.getMessage());
                }

                @Override
                public void onResponse(String response, int id) {
//                    if (! ActivityUtils.isContextExisted(activity))return;
                    if (getActivity()==null)return;
                    APPLog.d("update", "data==>" + response);
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
                                Intent intent = new Intent(getActivity(), MXUpdateActivity.class);
                                Bundle bundle = new Bundle();
                                bundle.putSerializable("down", (Serializable) mxUpdateModel);
                                intent.putExtras(bundle);
                                getActivity().startActivity(intent);
                            } else {
                                //请求看是否需要系统更新
//                                DownLoadSystemActivity.startDownLoadSystem(activity,new SystemOtaModel());
                                if (showHitn)
                                ToastUtils.getInstance().showToastShort("无系统更新");
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        if (showHitn)
                        ToastUtils.getInstance().showToastShort("获取更新数据出错");
                    }
                }
            });
        } catch (Exception e) {
            APPLog.d("update", e.getMessage());
        }
    }

    public void checkInstall(String packageName) {
        List<MXAppInfo> params = new ArrayList<>();
        MXAppInfo mxAppInfo = new MXAppInfo();
        mxAppInfo.setPackageName(packageName);
        mxAppInfo.setVersionCode(0);
        mxAppInfo.setVersionName("");
        params.add(mxAppInfo);
        checkUpdate(params, 4);
    }

    /**
     * @param params
     * @param flag
     */
    private void checkUpdate(List<MXAppInfo> params, final int flag) {
        try {
            JsonArray jsonEle = new JsonArray();
            JsonObject jsonObject;
            for (int i = 0; i < params.size(); i++) {
                jsonObject = new JsonObject();
                String packageName = params.get(i).getPackageName();
                int versionCode = params.get(i).getVersionCode();
                String versionName = params.get(i).getVersionName();
                jsonObject.addProperty("packageName", packageName);
                if (packageName.equals("com.moxi.bookstore")){
                    jsonObject.addProperty("versionCode", "220");
                    jsonObject.addProperty("versionName", "ZXTopsirH68_2.2.0");
                }else if (packageName.equals("com.moxi.filemanager")){
                    jsonObject.addProperty("versionCode", "70");
                    jsonObject.addProperty("versionName", "ZXTopsirH68_1.7.0");
                }else {
                    jsonObject.addProperty("versionCode", versionCode);
                    jsonObject.addProperty("versionName", versionName);
                }
                jsonEle.add(jsonObject);
            }

            final OkHttpUtils okHttpUtils = OkHttpUtils.getInstance();
            APPLog.d("update", "param===>" + jsonEle.toString());
            okHttpUtils.post().url(Constant.CHECK_VERSION).addParams("update", jsonEle.toString())
                    .build().execute(new StringCallback() {
                @Override
                public void onError(Call call, Exception e, int id) {
                    APPLog.d("update", "data==>" + e.getMessage());
                }

                @Override
                public void onResponse(String response, int id) {
                    if (getActivity()==null)return;
//                    if (! ActivityUtils.isContextExisted(activity))return;
                    APPLog.d("update", "data==>" + response);
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
                                Intent intent = new Intent();
                                intent.setClass(getActivity(), MXDownloadActivity.class);
                                intent.putExtra("install_flag", flag);
                                Bundle bundle = new Bundle();
                                bundle.putSerializable("down", (Serializable) mxUpdateModel);
                                intent.putExtras(bundle);
                                getActivity().startActivity(intent);
                            } else {
                                Toastor.showToast(getActivity(), "下载失败，请链接网络后重试");
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            APPLog.d("update", e.getMessage());
        }
    }
}
