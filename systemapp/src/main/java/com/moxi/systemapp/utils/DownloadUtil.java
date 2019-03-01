package com.moxi.systemapp.utils;

import com.mx.mxbase.constant.APPLog;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by xj on 2018/5/21.
 */

public class DownloadUtil {
    private final OkHttpClient okHttpClient;
    private long timec=0;
    public boolean isfinish=false;


    public DownloadUtil() {
        okHttpClient = new OkHttpClient();
    }

    /**
     * @param url 下载连接 * @param saveDir 储存下载文件的SDCard目录 * @param listener 下载监听
     */
    public void download(final String url, final String savepath, final OnDownloadListener listener) {
        Request request = new Request.Builder().url(url).build();

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) { // 下载失败
                listener.onDownloadFailed(savepath);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                InputStream is = null;
                byte[] buf = new byte[2048];
                int len = 0;
                FileOutputStream fos = null; // 储存下载文件的目录
                try {
                    is = response.body().byteStream();
                    long total = response.body().contentLength();
                    File file = new File(savepath);
                    fos = new FileOutputStream(file);
                    long sum = 0;
                    long downloadSum=0;
                    int   cusum=0;
                    while ((len = is.read(buf)) != -1) {
                        if (isfinish)return;
                        fos.write(buf, 0, len);
                        sum += len;
                        cusum+=len;
                        int progress = (int) (sum * 1.0f / total * 100); // 下载中
                        if(System.currentTimeMillis()-timec>1000) {
                            timec = System.currentTimeMillis();
                            listener.onDownloading(progress, total, cusum);
                            cusum=0;
                        }
                    }
                    fos.flush(); // 下载完成
                    listener.onDownloadSuccess(savepath);
                } catch (Exception e) {
                    APPLog.e("错误情况",e.getMessage());
                    listener.onDownloadFailed(savepath);
                } finally {
                    try {
                        if (is != null) is.close();
                    } catch (IOException e) {
                    }
                    try {
                        if (fos != null) fos.close();
                    } catch (IOException e) {
                    }
                }
            }
        });
    }

    public interface OnDownloadListener {
        /**
         * 下载成功
         */
        void onDownloadSuccess(String path);

        /**
         * @param progress * 下载进度
         */
        void onDownloading(int progress,long count ,long currentsize);

        /**
         * 下载失败
         */
        void onDownloadFailed(String path);
    }
}

