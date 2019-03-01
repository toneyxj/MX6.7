package com.mx.main.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.view.View;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.moxi.updateapp.MXAppInfo;
import com.mx.main.view.RoundView;
import com.mx.mxbase.constant.Constant;
import com.mx.mxbase.utils.AppUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

/**
 * Created by Archer on 2016/11/18.
 */
public class CheckVersionCode {

    public String CHECK_VERSION = "http://120.25.193.163/app/appversion/checkNew";

    public boolean update = false;
    public Context context;
    public static CheckVersionCode instance;

    public CheckVersionCode(Context context) {
        this.context = context;
    }

    public static CheckVersionCode getInstance(Context context) {
        if (instance == null) {
            instance = new CheckVersionCode(context);
        }
        return instance;
    }

    /**
     * 检测底层是否有更新
     *
     * @return
     */
    public void checkframework(final RoundView view) {
        OkHttpUtils.post().url(Constant.CHECK_LOWER_UPDATE).addParams("versionName", AppUtil.getPackageInfo(context).
                versionName).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
            }

            @Override
            public void onResponse(String response, int id) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONObject aa = new JSONObject(jsonObject.getString("result"));
                    int lowerVersion = aa.getInt("baseCode");
                    if (lowerVersion > Constant.LOCAL_VIR_VERSION) {
                        update = true;
                        view.setHasUpdate(true);
                    } else {
                        view.setHasUpdate(false);
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
                        checkUpdate(mxAppInfos, view);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 检测底层是否有更新
     *
     * @return
     */
    public void checkframework(final View view, final View nullView) {
        OkHttpUtils.post().url(Constant.CHECK_LOWER_UPDATE).addParams("versionName", AppUtil.getPackageInfo(context).
                versionName).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
            }

            @Override
            public void onResponse(String response, int id) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONObject aa = new JSONObject(jsonObject.getString("result"));
                    int lowerVersion = aa.getInt("baseCode");
                    if (lowerVersion > Constant.LOCAL_VIR_VERSION) {
                        update = true;
                        view.setVisibility(View.VISIBLE);
                        nullView.setVisibility(View.GONE);
                    } else {
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
                        checkUpdate(mxAppInfos, view);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    public void checkUpdate(List<MXAppInfo> params, final View view) {
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
            okHttpUtils.post().url(CHECK_VERSION).addParams("update", jsonEle.toString())
                    .build().execute(new StringCallback() {
                @Override
                public void onError(Call call, Exception e, int id) {
                }

                @Override
                public void onResponse(String response, int id) {
                    try {
                        JSONObject reObj = new JSONObject(response);
                        int code = reObj.optInt("code", -1);
                        if (code == 0) {
                            JSONArray jsonArray = reObj.optJSONArray("result");
                            if (jsonArray.length() > 0) {
                                // TODO: 2016/10/14 有更新
                                view.setVisibility(View.VISIBLE);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 检测更新
     *
     * @param params
     * @param view
     */
    public void checkUpdate(List<MXAppInfo> params, final RoundView view) {
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
            okHttpUtils.post().url(CHECK_VERSION).addParams("update", jsonEle.toString())
                    .build().execute(new StringCallback() {
                @Override
                public void onError(Call call, Exception e, int id) {
                }

                @Override
                public void onResponse(String response, int id) {
                    try {
                        JSONObject reObj = new JSONObject(response);
                        int code = reObj.optInt("code", -1);
                        if (code == 0) {
                            JSONArray jsonArray = reObj.optJSONArray("result");
                            if (jsonArray.length() > 0) {
                                // TODO: 2016/10/14 有更新
                                view.setHasUpdate(true);
                            } else {
                                view.setHasUpdate(false);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 检测底层是否有更新
     *
     * @return
     */
    public void checkframework(final View view) {
        OkHttpUtils.post().url(Constant.CHECK_LOWER_UPDATE).addParams("versionName", AppUtil.getPackageInfo(context).
                versionName).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
            }

            @Override
            public void onResponse(String response, int id) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONObject aa = new JSONObject(jsonObject.getString("result"));
                    int lowerVersion = aa.getInt("baseCode");
                    if (lowerVersion > Constant.LOCAL_VIR_VERSION) {
                        update = true;
                        view.setVisibility(View.VISIBLE);
                    } else {
                        view.setVisibility(View.GONE);
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
                        checkUpdate(mxAppInfos, view);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
