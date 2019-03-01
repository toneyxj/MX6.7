package com.dangdang.reader.dread.service;

import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.TextUtils;

import com.dangdang.reader.dread.data.FontDomain;
import com.dangdang.reader.dread.font.DownloadDb;
import com.dangdang.reader.dread.font.FontDownLoadRequest;
import com.dangdang.reader.dread.font.FontDownload;
import com.dangdang.reader.dread.font.FontListHandle;
import com.dangdang.reader.request.MultiGetMyFontListRequest;
import com.dangdang.reader.utils.AccountManager;
import com.dangdang.reader.R;
import com.dangdang.zframework.network.command.Request;
import com.dangdang.zframework.network.download.DownloadConstant;
import com.dangdang.zframework.network.download.DownloadConstant.Status;
import com.dangdang.zframework.network.download.DownloadManagerFactory;
import com.dangdang.zframework.network.download.DownloadManagerFactory.DownloadModule;
import com.dangdang.zframework.network.download.IDownloadManager;
import com.dangdang.zframework.network.download.IDownloadManager.DownloadInfo;
import com.dangdang.zframework.network.download.IDownloadManager.IDownloadListener;
import com.dangdang.zframework.plugin.AppUtil;
import com.dangdang.zframework.utils.NetUtil;
import com.dangdang.zframework.utils.UiUtil;

import java.io.File;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class FontDownloadService extends BaseDownloadService {

    private final Class<?> moduleKey = getClass();

    private DownloadDb mDownService;
    private DownloadModule mDModule;

    private FontListHandle mFontHandle;
    private List<FontDomain> mFreeFonts;
    private AtomicInteger mFreeFontSize = new AtomicInteger(0);

    private Handler mHandler;

    @Override
    protected DownloadModule initDownloadModule() {
        mDModule = new DownloadManagerFactory.DownloadModule("font");
        return mDModule;
    }

    @Override
    public void createImpl() {
        printLog(" onCreate() ");
        mDownService = new DownloadDb(getApplicationContext());
        mFontHandle = FontListHandle.getHandle(getApplicationContext());
        mHandler = new MyHandler(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        printLog(" onStart() ");

        if (mFreeFonts == null) {
            getDataByFree();
        }
        registerDownloadListener(moduleKey, mDownloadListener);
        return START_STICKY;
    }

    private void getDataByFree() {
        AccountManager am = new AccountManager(this);
        Request request = new MultiGetMyFontListRequest(mHandler, am.getToken(), mFontHandle.getDefaultFontName());
        AppUtil.getInstance(this).getRequestQueueManager().sendRequest(request, FontDownloadService.class);
    }

    @Override
    protected void onSuccess(Message msg) {
        mFreeFonts = (List<FontDomain>) msg.obj;
        downloadFreeFont();
    }

    @Override
    protected void onFail(Message msg) {

    }

    protected void downloadFreeFont() {
        if (mFreeFonts == null)
            return;
        for (FontDomain freeFont : mFreeFonts) {
            if (handleAutoDownloadOfFreeFont(freeFont)) {
                incrementFreeFontDownCount();
            }
        }
        printLog(" init FreeFontDownCount = " + getFreeFontDownCount());
    }

    private int incrementFreeFontDownCount() {
        return mFreeFontSize.incrementAndGet();
    }

    private int decrementFreeFontDownCount() {
        return mFreeFontSize.decrementAndGet();
    }

    private int getFreeFontDownCount() {
        return mFreeFontSize.get();
    }

    /**
     * @param freeFont
     * @return true: 加到下载队列，false:未加到下载队列
     */
    protected boolean handleAutoDownloadOfFreeFont(FontDomain freeFont) {
        final String indentityId = freeFont.productId;
        String url = freeFont.getDownloadURL();
        Status status = DownloadConstant.Status.UNSTART;
        long start = mFontHandle.getFontDownloadSize(indentityId);
        File dest = mFontHandle.getFontSaveFile(indentityId);
        long totalSize = 0;
        FontDownload sDownload = mDownService.getDownload(indentityId);
        if (sDownload != null) {
            totalSize = sDownload.totalSize;
            status = Status.convert(sDownload.status);
        }
        //TODO free download
        fontDownload(indentityId, status, url, dest, start, totalSize);
        freeFont.status = Status.PENDING;
        if (sDownload == null) {
            freeFont.fontZipPath = dest.getAbsolutePath();
            saveDownloadRecord(freeFont, DownloadDb.DType.FONT_FREE);
        } else if (status == Status.PAUSE || status == Status.FAILED) {//自动下载时改变状态为PENDING
            mDownService.updateStatusById(indentityId, Status.PENDING.getStatus());
        }
        return status == Status.UNSTART || status == Status.FAILED || status == Status.PAUSE;
    }

    private void saveDownloadRecord(FontDomain freeFont, DownloadDb.DType type) {
        String indentity = freeFont.productId;
        String url = freeFont.getDownloadURL();
        String fontZipPath = freeFont.fontZipPath;
        long progress = freeFont.progress;
        long totalSize = freeFont.totalSize;
        String status = freeFont.status.getStatus();
        String data = freeFont.jsonStr;
        String user = mFontHandle.getUserName();//AccountManager.getUsername();
        DownloadDb.DType tp = type;
        mDownService.saveDownload(indentity, url, fontZipPath,
                progress, totalSize, status, data, user, tp);
    }

    private void fontDownload(String indentityId, Status status, String url,
                              File dest, long start, long totalSize) {
        switch (status) {
            case UNSTART:
            case FAILED:
            case PAUSE:
                startDownload(indentityId, url, start, totalSize, dest);
                break;
            case DOWNLOADING:
            case RESUME:
            case PENDING:
                pauseDownload(indentityId, url, start, totalSize, dest);
                break;
            default:
                break;
        }
        return;
    }

    private void startDownload(String indentityId, String url, long start, long totalSize, File dest) {
        FontDownLoadRequest request = new FontDownLoadRequest(mDModule);
        request.setParams(indentityId, start, totalSize, url, dest);
        mDownloadManager.startDownload(request);
        return;
    }

    private void pauseDownload(String indentityId, String url, long start, long totalSize, File dest) {
        FontDownLoadRequest request = new FontDownLoadRequest(mDModule);
        request.setParams(indentityId, start, totalSize, url, dest);
        mDownloadManager.pauseDownload(request);
        return;
    }

    private Status getDownStatus(String indentityId) {
        Status status = Status.UNSTART;
        String strTatus = mDownService.getStatusByIndentityId(indentityId);
        if (!TextUtils.isEmpty(strTatus)) {
            status = Status.convert(strTatus);
        }
        return status;
    }

    /**
     * 免费字体列表是否下载完成
     *
     * @return
     */
    private boolean checkAllDownloadFinish() {
        final int deget = decrementFreeFontDownCount();
        printLog(" check FreeFontDownCount = " + deget);
        return deget == 0;
    }

    final IDownloadListener mDownloadListener = new IDownloadListener() {
        @Override
        public void onDownloading(IDownloadManager.DownloadInfo info) {
            if (hasOtherDownload()) {
                return;
            }
            String indentityId = (String) info.download.getTag();
            Status status = Status.DOWNLOADING;
            if (status != getDownStatus(indentityId)) {
                mDownService.updateStatusById(indentityId, status.getStatus());
            }
        }

        @Override
        public void onPauseDownload(DownloadInfo info) {//
            if (hasOtherDownload()) {
                return;
            }
            String indentityId = (String) info.download.getTag();
            mDownService.updateStatusById(indentityId, Status.PAUSE.getStatus());
        }

        @Override
        public void onDownloadFinish(DownloadInfo info) {
            if (checkAllDownloadFinish()) {
                mFontHandle.setFreeFontDownFinish();
                toastFreeFontDownFinish();
                printLog(" onDownloadFinish setFreeFontDownFinish ");
            }
            if (!NetUtil.isWifiConnected(getApplicationContext())) {//如果不是wifi,那么取消下载
                printLog(" onDownloadFinish not wifi");
                pauseAll();
            }
            if (hasOtherDownload()) {
                return;
            }
            String sourceFile = info.file.getAbsolutePath();
            String indentityId = (String) info.download.getTag();
            mFontHandle.addUnZip(sourceFile, indentityId);
            mDownService.updateStatusById(indentityId, Status.FINISH.getStatus());
        }

        @Override
        public void onFileTotalSize(DownloadInfo info) {
            printLog("onFileTotalSize[" + info.download.getTag()
                    + "]{progress=" + info.progress.progress
                    + ",Total=" + info.progress.total + "}");

            if (hasOtherDownload()) {
                return;
            }
            String url = info.url;
            long totalSize = info.progress.total;
            mDownService.updateTotalSize(url, totalSize);
        }

        @Override
        public void onDownloadFailed(DownloadInfo info, IDownloadManager.DownloadExp exp) {
            if (hasOtherDownload()) {
                return;
            }
            String indentityId = (String) info.download.getTag();
            mDownService.updateStatusById(indentityId, Status.FAILED.getStatus());
        }
    };

    protected void toastFreeFontDownFinish() {
        Message msg = mHandler.obtainMessage();
        msg.what = MSG_SHOW_TOAST;
        msg.arg1 = R.string.freefont_downfinish_tip;
        mHandler.sendMessage(msg);
    }

    private boolean hasOtherDownload() {
        final int listenerSize = getListenerSize();
        return listenerSize > 1;
    }

    @Override
    public void onDestroyImpl() {
        printLog(" onDestroyImpl() ");
        unRegisterDownloadListener(moduleKey);
    }

	/*
     * @Override public void onTaskRemoved(Intent rootIntent) {
	 * super.onTaskRemoved(rootIntent); }
	 * 
	 * @Override public void onTrimMemory(int level) {
	 * super.onTrimMemory(level); }
	 */

    @Override
    public boolean onUnbindImpl(Intent intent) {
        printLog(" onUnbindImpl() ");

        return true;
    }

    @Override
    public IBinder onBindImpl(Intent arg0) {
        printLog(" onBindImpl() ");
        return null;
    }

    public void showToast(int resId) {
        UiUtil.showToast(this, resId);
    }

    private final static int MSG_SHOW_TOAST = 0x1;
}
