package com.moxi.bookreader.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.cooler.download.DownloadListener;
import com.cooler.download.DownloadManager;
import com.cooler.download.DownloadRequest;
import com.mx.mxbase.utils.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DownDbService extends Service {
    public static String localDBPath = StringUtils.getSDCardPath()+"/downBooks/";
    private DownloadManager downloadManager;

    private DownDbCallback calback;
    private List<Integer> bookIds;
    private boolean isColock = false;
    private int index = 0;

    private List<Integer> failureBookIds;
    private List<Integer> successIds;

    public class MyBinder extends Binder {
        public DownDbService getService() {
            return DownDbService.this;
        }
    }

    public interface DownDbCallback {
        void onEnd(List<Integer> successIds, List<Integer> failureBookIds);

        void onStart(String error);

        void onProgress(int i, long l, long l1, int index, int count);
    }

    public void setCallBack(DownDbCallback calback) {
        this.calback = calback;
    }

    public DownDbService() {

    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new MyBinder();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        File destDir = new File(localDBPath);
        if (!destDir.exists()) {
            destDir.mkdirs();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    public void downBookFileWithIds(List<Integer> bookIds, String url) {
        if (isColock) {
            if (calback != null)
                calback.onStart("已经开启");
            return;
        }
        isColock = true;
        this.bookIds = bookIds;
        if (bookIds == null || bookIds.size() == 0) {
            if (calback != null)
                calback.onStart("书本id为空");
            return;
        }

        index = 0;
        if (successIds != null) {
            successIds.clear();
        }
        if (failureBookIds != null) {
            failureBookIds.clear();
        }
        if (calback != null)
            calback.onStart("开始下载");
        myDownloadManager(url);

    }

    private void myDownloadManager(final String url) {
        DownloadManager downloadManager = getDownloadManager();
        DownloadRequest downloadRequest = new DownloadRequest();
        downloadRequest.setRetryTime(5);
        downloadRequest.setDownloadListener(new DownloadListener() {
            @Override
            public void onStart(int i, long l) {
                Log.d("TAG", "onStart");
                if (calback != null)
                    calback.onStart("开始下载");
            }

            @Override
            public void onRetry(int i) {
                Log.d("TAG", "onRetry");
                if (calback != null)
                    calback.onStart("尝试连接服务……" + i);
            }

            @Override
            public void onProgress(int i, long l, long l1) {
                if (calback != null) {
                    calback.onProgress(i, l, l1, index, bookIds.size());
                }
            }

            @Override
            public void onSuccess(int i, String s) {
                if (successIds == null) {
                    successIds = new ArrayList<Integer>();
                }
                successIds.add(bookIds.get(index));
                index++;
                if (index < bookIds.size()) {
                    myDownloadManager(url);
                } else {
                    isColock = false;
                    if (calback != null) {
                        calback.onEnd(successIds, failureBookIds);
                    }
                }
                Log.d("TAG", "onSuccess");
            }

            @Override
            public void onFailure(int i, int code, String s) {
                if (failureBookIds == null) {
                    failureBookIds = new ArrayList<Integer>();
                }
                failureBookIds.add(bookIds.get(index));
                index++;
                if (index < bookIds.size()) {
                    myDownloadManager(url);
                } else {
                    isColock = false;
                    if (calback != null) {
                        calback.onEnd(successIds, failureBookIds);
                    }
                }
                Log.d("TAG", "onFailure==>" + s);
            }
        });
        downloadRequest.setUrl(url);
        downloadRequest.setDestinationPath(getFilePathWithId(bookIds.get(index)));
        downloadManager.add(downloadRequest);
    }

    public static String getFilePathWithId(int bookId) {
        return localDBPath + File.separator + bookId + ".pdf";
    }

    public DownloadManager getDownloadManager() {
        if (downloadManager == null) {
            synchronized (DownloadManager.class) {
                downloadManager = new DownloadManager();
            }
        }
        return downloadManager;
    }
}
