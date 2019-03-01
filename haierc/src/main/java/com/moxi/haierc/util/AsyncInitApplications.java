package com.moxi.haierc.util;

import android.content.Context;
import android.os.AsyncTask;

import com.moxi.haierc.model.AppModel;
import com.moxi.haierc.ports.FinishCallBack;
import com.moxi.haierc.service.AppInfoService;

import java.util.List;

/**
 * Created by King on 2017/10/13.
 */

public class AsyncInitApplications extends AsyncTask<FinishCallBack, Void, Boolean> {
    private FinishCallBack finishCallBack;
    private Context context;
    private AppInfoService appInfoService;

    public AsyncInitApplications(Context context) {
        this.context = context;
        appInfoService = new AppInfoService(context);
    }

    @Override
    protected Boolean doInBackground(FinishCallBack... params) {
        this.finishCallBack = params[0];
        try {
            List<AppModel> appInfos = appInfoService.getAppInfos();
            resolveInitApp(appInfos);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        finishCallBack.onFinish(aBoolean);
    }

    /**
     * 解析初始化商务版应用
     */
    private void resolveInitApp(List<AppModel> appInfos) {
        for (int i = 0; i < appInfos.size(); i++) {
            try {
                IndexApplicationUtils.getInstance(context).insertAppLog("", appInfos.get(i));
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }
        }
    }
}
