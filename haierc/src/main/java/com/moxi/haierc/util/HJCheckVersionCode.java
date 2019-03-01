package com.moxi.haierc.util;

import android.content.Context;
import android.view.View;

import com.mx.mxbase.constant.Constant;
import com.mx.mxbase.utils.AppUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Call;

/**
 * Created by Archer on 2016/11/18.
 */
public class HJCheckVersionCode {
    public Context context;
    public static HJCheckVersionCode instance;

    public HJCheckVersionCode(Context context) {
        this.context = context;
    }

    public static HJCheckVersionCode getInstance(Context context) {
        if (instance == null) {
            instance = new HJCheckVersionCode(context);
        }
        return instance;
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
                        view.setVisibility(View.VISIBLE);
                    } else {
                        view.setVisibility(View.GONE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
