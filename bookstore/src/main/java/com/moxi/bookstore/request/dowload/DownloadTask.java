package com.moxi.bookstore.request.dowload;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.moxi.bookstore.db.TableConfig;
import com.moxi.bookstore.utils.ToolUtils;
import com.mx.mxbase.constant.APPLog;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by xj on 2017/11/17.
 */

public class DownloadTask extends Thread {
    private String TAG = "DownloadTask";
    private String downloadUrl;// 下载链接地址
    private int threadNum = 3;// 开启的线程数
    private String filePath;// 保存文件路径地址
    private boolean endDownload = false;
    private long fileSize = 0;
    private DownloadListener listener;
    private boolean stopTask=false;

    public void setEndDownload(boolean endDownload) {
        setStopTask(true);
        this.endDownload = endDownload;
        handler.removeCallbacksAndMessages(null);
        listener=null;
    }

    public boolean isEndDownload() {
        return endDownload;
    }

    public void setStopTask(boolean stopTask) {
        this.stopTask = stopTask;
    }

    public DownloadTask(String downloadUrl, int threadNum, String fileptah) {
        this.downloadUrl = downloadUrl;
        this.threadNum = threadNum;
        this.filePath = fileptah;
    }

    public DownloadTask(String downloadUrl, String filName, DownloadListener listener) {
        //下载文件处理
        filName = ToolUtils.getIntence().downloadPathSpil(filName);

        String tempFileName = File.separator + filName + ".epub";
        File temp = new File(TableConfig.E_DOWNLOAD_DIR);
        if (!temp.exists()) {
            temp.mkdirs();
        }

        this.filePath = temp.getAbsolutePath() + tempFileName;
        this.downloadUrl = downloadUrl;
        this.listener = listener;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setListener(DownloadListener listener) {
        this.listener = listener;
    }

    @Override
    public void run() {

        FileDownloadThread[] threads = new FileDownloadThread[threadNum];
        try {
            URL url = new URL(downloadUrl);
            APPLog.d(TAG, "download file http path:" + downloadUrl);
            URLConnection conn = url.openConnection();
            // 读取下载文件总大小
            fileSize = conn.getContentLength();
            if (fileSize <= 0) {
//                System.out.println("读取文件失败");
                handler.sendEmptyMessage(1);
                return;
            }
            // 设置ProgressBar最大的长度为文件Size

            // 计算每条线程下载的数据长度
            long blockSize = (fileSize % threadNum) == 0 ? fileSize / threadNum
                    : fileSize / threadNum + 1;

            APPLog.d(TAG, "fileSize:" + fileSize + "  blockSize:");

            File file = new File(filePath);
            for (int i = 0; i < threads.length; i++) {
                // 启动线程，分别下载每个线程需要下载的部分
                threads[i] = new FileDownloadThread(url, file, blockSize,
                        (i + 1), handler,fileSize);
                threads[i].setName("Thread:" + i);
                threads[i].start();
            }

            boolean isfinished = false;
            long downloadedAllSize = 0;
            while (!isfinished) {
                if (stopTask) {
                    for (int i = 0; i < threads.length; i++) {
                        if (threads[i]!=null)threads[i].setStop(true);
                    }
                }
                if (endDownload)break;
                isfinished = true;
                // 当前所有线程下载总量
                downloadedAllSize = 0;
                for (int i = 0; i < threads.length; i++) {
                    downloadedAllSize += threads[i].getDownloadLength();
                    if (!threads[i].isCompleted()) {
                        isfinished = false;
                    }
                }
                Message message = new Message();
                message.what = 2;
                message.obj = downloadedAllSize;
                handler.sendMessage(message);

                APPLog.d(TAG, " current of downloadSize:" + downloadedAllSize);
                Thread.sleep(1000);// 休息1秒后再读取下载进度
            }
            if (downloadedAllSize == fileSize) {
                handler.sendEmptyMessage(3);
            }else {
                handler.sendEmptyMessage(1);

            }
            Log.d(TAG, " all of downloadSize:" + downloadedAllSize);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            handler.sendEmptyMessage(1);
        } catch (IOException e) {
            e.printStackTrace();
            handler.sendEmptyMessage(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
            handler.sendEmptyMessage(1);
        }

    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (listener == null) return;
            switch (msg.what) {
                case 1:
                    if (endDownload) return;//已经设置结束不重复设置
                    handler.removeCallbacksAndMessages(null);
                    listener.onDownloadFail(filePath, fileSize);
                    setEndDownload(true);
                    break;
                case 2:
                    listener.onDownloadProgress(filePath, (Long) msg.obj, fileSize);
                    break;
                case 3:
                    listener.onDownloadSucces(filePath);
                    break;
                default:
                    break;
            }
        }
    };

    public interface DownloadListener {
        void onDownloadFail(String filePath, long totalSize);

        void onDownloadSucces(String filePath);
        void onDownloadStop(String filePath);

        void onDownloadProgress(String filePath, long currentSize, long totalSize);
    }


}
