package com.moxi.systemapp.ok;

import android.content.Context;
import android.util.Log;
import android.widget.TextView;

import com.google.gson.Gson;
import com.mx.mxbase.constant.APPLog;
import com.mx.mxbase.utils.SharePreferceUtil;

import java.io.File;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by xj on 2018/8/7.
 */

public class OkDownlaodUtils implements DownloadListener {

    private Context context;
    private DownloadTask downloadTask;
    private TextView view;
    private String url;
    private String savePath;

    public OkDownlaodUtils(Context context) {
        this.context = context;
    }

    public int errorIndex = 0;
    private DownloadListener listener;

    public void setDownlaod(String url, String savePath) {

        setDownlaod(url, savePath, null);
    }

    public void setDownlaod(String url, String savePath, DownloadListener listener) {
        if (url == null || savePath == null || url.isEmpty() || savePath.isEmpty()) {
            Log.e("startDownlaod", "传入参数不能为空");
            return;
        }
        try {
            String path = SharePreferceUtil.getInstance(context).getString("OkDownlaodUtils-down");
            Gson gson = new Gson();
            Map<String, String> map =gson.fromJson(path, Map.class);
            if (map==null)map=new HashMap<>();

            String getsave=null;
            getsave = map.get(url);

            if (getsave == null || !getsave.equals(savePath)) {
                deleteFile(savePath);
                // 第三种：推荐，尤其是容量大时
                String deKey="";
                for (Map.Entry<String, String> entry : map.entrySet()) {
                    if (entry.getValue().equals(savePath)){
                        deKey=entry.getKey();
                        break;
                    }
                }
                if (!deKey.isEmpty())
                    map.remove(deKey);
            }
            map.put(url, savePath);
            String str=gson.toJson(map);
            APPLog.e("下载数据保存",str);
            SharePreferceUtil.getInstance(context).setCache("OkDownlaodUtils-down",str);

        } catch (Exception e) {
            e.printStackTrace();
        }


        this.listener = listener;
        this.url = url;
        this.savePath = savePath;
    }

    public void setView(TextView view) {
        this.view = view;
    }

    public void startDownlaod() {
        downloadTask = new DownloadTask(url, savePath, this);
        downloadTask.execute("");
    }

    @Override
    public void onProgress(int progress, long total, long hasDownlaod) {
        if (errorIndex>0)errorIndex--;
        if (listener != null) {
            listener.onProgress(progress, total, hasDownlaod);
        }
    }

    @Override
    public void onSuccess() {
        if (listener != null) {
            listener.onSuccess();
        }
    }

    @Override
    public void onFailed(Exception e) {
        errorIndex++;
        if (errorIndex > 5) {
            if (!(e instanceof SocketTimeoutException)) {
                deleteFile(savePath);
            }
            if (listener != null) {
                listener.onFailed(e);
            }
        } else {
            startDownlaod();
        }
    }

    @Override
    public void onPaused() {
        if (listener != null) {
            listener.onPaused();
        }
    }

    @Override
    public void onCanceled() {
        if (listener != null) {
            listener.onCanceled();
        }
    }

    public void onDestory() {
        if (downloadTask != null) downloadTask.pauseDownload();
    }

    /**
     * 将SD卡文件删除
     *
     * @param file 删除路径
     */
    private boolean deleteFile(File file) {
        if (file.exists()) {
            if (file.isFile()) {
                file.delete();
            }
            // 如果它是一个目录
            else if (file.isDirectory()) {
                // 声明目录下所有的文件 files[];
                File files[] = file.listFiles();
                for (int i = 0; i < files.length; i++) { // 遍历目录下所有的文件
                    deleteFile(files[i]); // 把每个文件 用这个方法进行迭代
                }
            }
            file.delete();
            return true;
        }
        return false;
    }

    /**
     * 将SD卡文件删除
     *
     * @param file 删除路径
     */
    private void deleteFile(String file) {
        deleteFile(new File(file));
    }
}
