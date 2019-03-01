package com.dangdang.reader.dread.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

import com.dangdang.reader.request.RequestConstants;
import com.dangdang.zframework.log.LogM;
import com.dangdang.zframework.network.download.DownloadManager;
import com.dangdang.zframework.network.download.DownloadManagerFactory;
import com.dangdang.zframework.network.download.DownloadManagerFactory.DownloadModule;
import com.dangdang.zframework.network.download.IDownloadManager.IDownloadListener;

import java.lang.ref.WeakReference;

/**
 * 此类还需要调整，目前先满足字体下载
 *
 * @author luxu
 * @author liuboyu
 */
public abstract class BaseDownloadService extends Service {

    protected DownloadManager mDownloadManager;

    protected abstract DownloadModule initDownloadModule();

    protected abstract void createImpl();

    protected abstract IBinder onBindImpl(Intent intent);

    protected abstract boolean onUnbindImpl(Intent intent);

    protected abstract void onDestroyImpl();

    @Override
    final public void onCreate() {
        super.onCreate();

        downloadInit();
        createImpl();
    }

    private void downloadInit() {
        DownloadModule module = initDownloadModule();
        if (module == null) {
            throw new NullPointerException(" module not null ");
        }
        module.setTaskingSize(1);
        mDownloadManager = (DownloadManager) DownloadManagerFactory.getFactory().create(module);
    }

    @Override
    final public void onDestroy() {
        super.onDestroy();
        onDestroyImpl();
    }

    @Override
    final public boolean onUnbind(Intent intent) {
        return onUnbindImpl(intent);
    }

    @Override
    final public IBinder onBind(Intent intent) {
        return onBindImpl(intent);
    }

    protected void registerDownloadListener(Class<?> moduleKey, IDownloadListener l) {
        if (l == null) {
            throw new NullPointerException("[IDownloadListener not null]");
        }
        mDownloadManager.registerDownloadListener(moduleKey, l);
    }

    protected void unRegisterDownloadListener(Class<?> moduleKey) {
        mDownloadManager.unRegisterDownloadListener(moduleKey);
    }

    public void pauseAll() {
        mDownloadManager.pauseAll();
    }

    protected int getListenerSize() {
        return mDownloadManager.getListenerSize();
    }

    protected void printLog(String log) {
        LogM.i(getClass().getSimpleName(), log);
    }

    protected void printLogD(String log) {
        LogM.d(getClass().getSimpleName(), log);
    }

    protected void printLogE(String log) {
        LogM.e(getClass().getSimpleName(), log);
    }

    protected void printLogV(String log) {
        LogM.v(getClass().getSimpleName(), log);
    }

    protected void printLogW(String log) {
        LogM.w(getClass().getSimpleName(), log);
    }

    protected static class MyHandler extends Handler {
        private final WeakReference<BaseDownloadService> mService;

        MyHandler(BaseDownloadService service) {
            this.mService = new WeakReference<BaseDownloadService>(service);
        }

        @Override
        public void handleMessage(Message msg) {
            BaseDownloadService service = mService.get();
            if (service != null) {
                super.handleMessage(msg);
                try {
                    switch (msg.what) {
                        case RequestConstants.MSG_WHAT_REQUEST_DATA_SUCCESS:
                            service.onSuccess(msg);
                            break;
                        case RequestConstants.MSG_WHAT_REQUEST_DATA_FAIL:
                            service.onFail(msg);
                            break;
                        default:
                            break;
                    }
                } catch (Exception e) {
                    LogM.e("download", e.toString());
                }
            }
        }
    }

    protected abstract void onSuccess(Message msg);

    protected abstract void onFail(Message msg);
}
