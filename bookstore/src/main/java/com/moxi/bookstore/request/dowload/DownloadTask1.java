package com.moxi.bookstore.request.dowload;

import com.moxi.bookstore.BookstoreApplication;
import com.moxi.bookstore.db.TableConfig;
import com.moxi.bookstore.utils.ToolUtils;
import com.mx.mxbase.netstate.NetWorkUtil;
import com.mx.mxbase.utils.FileUtils;
import com.mx.mxbase.utils.StringUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Administrator on 2018/12/25.
 */

public class DownloadTask1 {
    private String TAG = "DownloadTask";
    private String downloadUrl;// 下载链接地址
    private String filePath;// 保存文件路径地址
    //    private boolean endDownload = false;
    private long fileSize = 0;
    private DownloadTask.DownloadListener listener;
    private boolean stopTask = false;
    private int faileSize = 0;
    private long sendTime = 0;

    public void setEndDownload(boolean endDownload) {
        setStopTask(endDownload);
    }

    public DownloadTask.DownloadListener getListener() {
        return listener;
    }

    public boolean isEndDownload() {
        return stopTask;
    }

    public void setStopTask(boolean stopTask) {
        this.stopTask = stopTask;
    }

    public DownloadTask1(String downloadUrl, String fileptah) {
        this.downloadUrl = downloadUrl;
        this.filePath = fileptah;
    }

    public DownloadTask1(String downloadUrl, String filName, DownloadTask.DownloadListener listener) {
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

    public void setListener(DownloadTask.DownloadListener listener) {
        this.listener = listener;
    }
private Thread myTh;
    public void run() {
        myTh=new Thread() {
            @Override
            public void run() {
                InputStream is = null;
                RandomAccessFile savedFile = null;
                long downloadLength = 0;   //记录已经下载的文件长度
                //得到下载内容的大小
                long contentLength = getContentLength(downloadUrl);
                fileSize = contentLength;
                //获取文件大小失败
                if (contentLength == 0) {
                    //误误网络状态下或者下载地址已不存在
                    onFail();//重新启动
                    return;
                }
                //创建一个文件
                File file = new File(filePath+"mx");
                if (file.exists()) {
                    //文件存在，得到文件的大小
                    downloadLength = file.length();
                }
                if (contentLength == downloadLength)

                {
                    if (getListener() != null) {
                        getListener().onDownloadSucces(filePath);
                    }
                    return;
                }

                OkHttpClient client = new OkHttpClient();
                /**
                 * HTTP请求是有一个Header的，里面有个Range属性是定义下载区域的，它接收的值是一个区间范围，
                 * 比如：Range:bytes=0-10000。这样我们就可以按照一定的规则，将一个大文件拆分为若干很小的部分，
                 * 然后分批次的下载，每个小块下载完成之后，再合并到文件中；这样即使下载中断了，重新下载时，
                 * 也可以通过文件的字节长度来判断下载的起始点，然后重启断点续传的过程，直到最后完成下载过程。
                 */
                Request request = new Request.Builder()
                        .addHeader("RANGE", "bytes=" + downloadLength + "-")  //断点续传要用到的，指示下载的区间
                        .url(downloadUrl)
                        .build();
                Response response = null;
                getListener().onDownloadProgress(filePath, downloadLength,contentLength);
                try {
                    response = client.newCall(request).execute();
                    if (response != null) {
                        is = response.body().byteStream();
                        savedFile = new RandomAccessFile(file, "rw");
                        savedFile.seek(downloadLength);//跳过已经下载的字节
                        byte[] b = new byte[1024];
                        int total = 0;
                        int len;
                        while ((len = is.read(b)) != -1) {
                            savedFile.write(b, 0, len);
                            //计算已经下载的百分比
                            total += len;
                            long cutT = System.currentTimeMillis();
                            if (Math.abs(cutT - sendTime) >= 1000) {
                                sendTime = cutT;
                                downloadLength += total;
                                if (getListener() != null)
                                    getListener().onDownloadProgress(filePath, downloadLength,contentLength);
                                faileSize = 0;
                                total = 0;
                            }
                            if (stopTask) {
                                onFail();
                                return;
                            }
                        }
                        //下载完成修改文件名后缀
                        file.renameTo(new File(filePath));
                        if (getListener() != null) getListener().onDownloadSucces(filePath);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    onFail();
                } finally {
                    try {
                        if (is != null) {
                            is.close();
                        }
                        if (savedFile != null) {
                            savedFile.close();
                        }
                        if (response != null && response.body() != null) {
                            response.body().close();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        myTh.start();
    }
    public boolean isAlive() {
        if (myTh==null)return false;
       return myTh.isAlive();
    }
    private void onFail() {
        if (getListener() == null) return;
        if (stopTask) {
            getListener().onDownloadStop(filePath);
        } else {
            if (isNotDown()) {
                if (faileSize < 5) {
                    faileSize++;
                    run();
                } else {
                    getListener().onDownloadFail(filePath,faileSize);
                    //重复下载均出现失败，删除项目，标记下载项目为错误
                    StringUtils.deleteFile(filePath+"mx");
                }
            } else {
                getListener().onDownloadFail(filePath, fileSize);
            }
        }
    }

    public boolean isNotDown() {
        boolean isf = FileUtils.getInstance().getUseMenoryLong() > 1024 * 1024 * 5;
        //文件剩余大小小于10
        boolean isn = NetWorkUtil.isNetworkConnected(BookstoreApplication.getContext());
        return isf && isn;
    }

    /**
     * 得到下载内容的大小
     *
     * @param downloadUrl 下载地址
     * @return 返回文件下载长度
     */
    private long getContentLength(String downloadUrl) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(downloadUrl).build();
        try {
            Response response = client.newCall(request).execute();
            if (response != null && response.isSuccessful()) {
                long contentLength = response.body().contentLength();
                response.body().close();
                return contentLength;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
}

