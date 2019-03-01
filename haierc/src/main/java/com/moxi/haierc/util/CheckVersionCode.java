package com.moxi.haierc.util;

import android.content.Context;
import android.content.pm.PackageInfo;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.moxi.haierc.callback.UpdateCallBack;
import com.moxi.updateapp.MXAppInfo;
import com.mx.mxbase.constant.APPLog;
import com.mx.mxbase.constant.Constant;
import com.mx.mxbase.interfaces.YesOrNo;
import com.mx.mxbase.utils.AppUtil;
import com.mx.mxbase.utils.DeviceUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

import static com.mx.mxbase.utils.AppUtil.getPackageInfo;


/**
 * Created by Archer on 2016/11/18.
 */
public class CheckVersionCode {


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
    public void checkframework(final YesOrNo yesOrNo) {
        OkHttpUtils.post().url(Constant.CHECK_LOWER_UPDATE).addParams("versionName", getPackageInfo(context).
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
                        if (yesOrNo!=null)yesOrNo.onYesOrNo(true);
                    } else {
                        if (yesOrNo!=null)yesOrNo.onYesOrNo(false);
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
                        checkUpdate(mxAppInfos, yesOrNo);
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
    public void checkframework(final UpdateCallBack updateCallBack) {
        PackageInfo packageInfo= AppUtil.getPackageInfo(context);
        if (packageInfo==null)return;
        OkHttpUtils.post().url(Constant.CHECK_LOWER_UPDATE).addParams("versionName", packageInfo.
                versionName).addParams("devNo", DeviceUtil.getDeviceSerial()).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
            }

            @Override
            public void onResponse(String response, int id) {
                try {
                    APPLog.e("底层版本数据信息="+response);
                    JSONObject jsonObject = new JSONObject(response);
                    JSONObject aa = new JSONObject(jsonObject.getString("result"));
                    int lowerVersion = aa.getInt("baseCode");
                    if (lowerVersion > Constant.LOCAL_VIR_VERSION) {
                        update = true;
                        updateCallBack.needUpdate(true);
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
                        checkUpdate(mxAppInfos, updateCallBack);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

//    /**
//     * 检测底层是否有更新
//     *
//     * @return
//     */
//    public void checkframework(final View view, final View nullView) {
//        OkHttpUtils.post().url(Constant.CHECK_LOWER_UPDATE).addParams("versionName", AppUtil.getPackageInfo(context).
//                versionName).build().execute(new StringCallback() {
//            @Override
//            public void onError(Call call, Exception e, int id) {
//            }
//
//            @Override
//            public void onResponse(String response, int id) {
//                try {
//                    JSONObject jsonObject = new JSONObject(response);
//                    JSONObject aa = new JSONObject(jsonObject.getString("result"));
//                    int lowerVersion = aa.getInt("baseCode");
//                    if (lowerVersion > Constant.LOCAL_VIR_VERSION) {
//                        update = true;
//                        view.setVisibility(View.VISIBLE);
//                        nullView.setVisibility(View.GONE);
//                    } else {
//                        List<PackageInfo> packages = context.getPackageManager().getInstalledPackages(0);
//                        List<MXAppInfo> mxAppInfos = new ArrayList<>();
//                        MXAppInfo mxAppInfo;
//                        for (int i = 0; i < packages.size(); i++) {
//                            PackageInfo packageInfo = packages.get(i);
//                            String packageName = packageInfo.packageName;
//                            if (packageName.startsWith("com.moxi")) {
//                                mxAppInfo = new MXAppInfo();
//                                mxAppInfo.setVersionName(packageInfo.versionName);
//                                mxAppInfo.setVersionCode(packageInfo.versionCode);
//                                mxAppInfo.setPackageName(packageInfo.packageName);
//                                mxAppInfos.add(mxAppInfo);
//                            }
//                        }
//                        checkUpdate(mxAppInfos, view, nullView);
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//    }

//
//    public void checkUpdate(List<MXAppInfo> params, final View view,YesOrNo yesOrNo) {
//        try {
//            JsonArray jsonEle = new JsonArray();
//            JsonObject jsonObject;
//            for (int i = 0; i < params.size(); i++) {
//                jsonObject = new JsonObject();
//                String packageName = params.get(i).getPackageName();
//                int versionCode = params.get(i).getVersionCode();
//                String versionName = params.get(i).getVersionName();
//                jsonObject.addProperty("packageName", packageName);
//                jsonObject.addProperty("versionCode", versionCode);
//                jsonObject.addProperty("versionName", versionName);
//                jsonEle.add(jsonObject);
//            }
//            final OkHttpUtils okHttpUtils = OkHttpUtils.getInstance();
//            okHttpUtils.post().url(Constant.CHECK_VERSION).addParams("update", jsonEle.toString())
//                    .build().execute(new StringCallback() {
//                @Override
//                public void onError(Call call, Exception e, int id) {
//                }
//
//                @Override
//                public void onResponse(String response, int id) {
//                    try {
//                        JSONObject reObj = new JSONObject(response);
//                        int code = reObj.optInt("code", -1);
//                        if (code == 0) {
//                            JSONArray jsonArray = reObj.optJSONArray("result");
//                            if (jsonArray.length() > 0) {
//                                // TODO: 2016/10/14 有更新
//                                view.setVisibility(View.VISIBLE);
//                                nullView.setVisibility(View.GONE);
//                            }
//                        }
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//            });
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    public void checkUpdate(List<MXAppInfo> params, final UpdateCallBack updateCallBack) {
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
                                updateCallBack.needUpdate(true);
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

    public void checkUpdate(List<MXAppInfo> params, final YesOrNo yesOrNo) {
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
                                if (yesOrNo!=null)yesOrNo.onYesOrNo(true);
                            } else {
                                if (yesOrNo!=null)yesOrNo.onYesOrNo(false);
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
}
