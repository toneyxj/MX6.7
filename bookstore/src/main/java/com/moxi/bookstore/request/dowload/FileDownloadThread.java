package com.moxi.bookstore.request.dowload;

import android.os.Handler;

import com.mx.mxbase.constant.APPLog;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by xj on 2017/11/17.
 */

public class FileDownloadThread extends Thread {

    private static final String TAG = FileDownloadThread.class.getSimpleName();

    /**
     * 当前下载是否完成
     */
    private boolean isCompleted = false;
    /**
     * 当前下载文件长度
     */
    private int downloadLength = 0;
    /**
     * 文件保存路径
     */
    private File file;
    /**
     * 文件下载路径
     */
    private URL downloadUrl;
    /**
     * 当前下载线程ID
     */
    private int threadId;
    /**
     * 线程下载数据长度
     */
    private long blockSize;
    private Handler handler;
    private boolean isStop=false;
    private long totalSize;

    public void setStop(boolean stop) {
        isStop = stop;
    }

    /**
     * @param downloadUrl:文件下载地址
     * @param file:文件保存路径
     * @param blocksize:下载数据长度
     * @param threadId:线程ID
     */
    public FileDownloadThread(URL downloadUrl, File file, long blocksize,
                              int threadId, Handler handler,long totalSize) {
        this.downloadUrl = downloadUrl;
        this.file = file;
        this.threadId = threadId;
        this.blockSize = blocksize;
        this.handler=handler;
        this.totalSize=totalSize;
    }

    @Override
    public void run() {

        BufferedInputStream bis = null;
        RandomAccessFile raf = null;

        try {
            URLConnection conn = downloadUrl.openConnection();
            conn.setAllowUserInteraction(true);

            long startPos = blockSize * (threadId - 1);//开始位置
            long endPos = blockSize * threadId - 1;//结束位置
//            if (endPos>=totalSize)endPos=totalSize;
            //设置当前线程下载的起点、终点
            conn.setRequestProperty("Range", "bytes=" + startPos + "-" + endPos);
            APPLog.d(TAG, Thread.currentThread().getName() + "  bytes="
                    + startPos + "-" + endPos);

            byte[] buffer = new byte[1024];
            bis = new BufferedInputStream(conn.getInputStream());

            raf = new RandomAccessFile(file, "rwd");
            raf.seek(startPos);
            int len;
            while ((len = bis.read(buffer, 0, 1024)) != -1) {
                if (isStop){
                    try {
                        file.delete();
                    }catch (Exception e){}
                    break;
                }
                raf.write(buffer, 0, len);
                downloadLength += len;
            }
            isCompleted = true;
            APPLog.d(TAG, "current thread task has finished,all size:"
                    + downloadLength);

        } catch (IOException e) {
            e.printStackTrace();
            handler.sendEmptyMessage(1);
        } finally {
            try {
                if (bis != null)
                    bis.close();
                if (raf != null)
                    raf.close();
            } catch (IOException e) {
                e.printStackTrace();
                handler.sendEmptyMessage(1);
            }
        }
    }

    /**
     * 线程文件是否下载完毕
     */
    public boolean isCompleted() {
        return isCompleted;
    }

    /**
     * 线程下载文件长度
     */
    public int getDownloadLength() {
        return downloadLength;
    }

}
