package com.moxi.bookstore.http.subscribers;

import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.StatFs;
import android.text.format.Formatter;

import com.moxi.bookstore.db.TableConfig;
import com.moxi.bookstore.utils.ToolUtils;
import com.mx.mxbase.constant.APPLog;
import com.mx.mxbase.utils.ToastUtils;
import com.tencent.bugly.crashreport.CrashReport;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.nio.channels.FileChannel;

import okhttp3.ResponseBody;
import rx.Subscriber;

/**
 * Created by Administrator on 2016/9/27.
 */
public class ProgressDownLoad extends Subscriber<ResponseBody> {
    Context ctx;
    String filename;
    private Handler mSubscriberOnNextListener;
    private boolean cancel = false;

    public ProgressDownLoad(Context context, String str, Handler mSubscriberOnNextListener) {
        this.ctx = context;
        this.filename = str;
        this.mSubscriberOnNextListener = mSubscriberOnNextListener;
    }

    public ProgressDownLoad(Context context, String str, Handler mSubscriberOnNextListener, boolean cancel) {
        this.ctx = context;
        this.filename = str;
        this.mSubscriberOnNextListener = mSubscriberOnNextListener;
        this.cancel = cancel;
    }

    @Override
    public void onStart() {
        super.onStart();
        ToolUtils.getIntence().ToastUtil(ctx, "开始下载...");
        File file = new File(TableConfig.E_DOWNLOAD_DIR);
        if (!file.exists()) file.mkdirs();
    }

    @Override
    public void onCompleted() {
    }

    @Override
    public void onError(Throwable e) {
        if (ctx == null) return;
        try {
            if (e instanceof SocketTimeoutException) {
                ToastUtils.getInstance().showToastShort("网络中断，请检查您的网络状态");
            } else if (e instanceof ConnectException) {
                ToastUtils.getInstance().showToastShort("网络中断，请检查您的网络状态");
            } else {
                String msg = e.getMessage();
                if (msg.length() < 30) {
                    ToastUtils.getInstance().showToastShort("错误" + e.getMessage());
                }
                APPLog.e(getClass().getName(), "error----------->" + e.getMessage());
            }
            mSubscriberOnNextListener.sendEmptyMessage(103);
//            ToastUtils.getInstance().showToastShort("下载出小差了，麻烦您重新点击下载！！");

        } catch (Exception e1) {
            e1.printStackTrace();
            mSubscriberOnNextListener.sendEmptyMessage(103);
        }
    }

    @Override
    public void onNext(ResponseBody responseBody) {
        long bodyLenth = responseBody.contentLength();
        File temp = null;
        try {
            //创建文件
            filename = filename.replace(":", "");
            filename = filename.replace("：", "");
            filename = filename.replace("\"", "");
            filename = filename.replace("\'", "");

            String tempFileName = filename + ".epub";
            temp = new File(TableConfig.E_DOWNLOAD_DIR, tempFileName);
            if (!temp.exists()) {
                temp.createNewFile();
                temp.setWritable(true);
            }


            FileOutputStream fos = new FileOutputStream(temp);
            InputStream is = responseBody.byteStream();

            byte[] buf = new byte[1024];
            int len;
            while ((len = is.read(buf)) != -1) {
                fos.write(buf, 0, len);
            }
            fos.flush();
            is.close();
            fos.close();

            APPLog.e("bodylenth:" + bodyLenth + "--fileSize:" + temp.length());
            if (temp.length() == bodyLenth) {
                mSubscriberOnNextListener.sendEmptyMessage(200);
            } else {//验证不通过
                mSubscriberOnNextListener.sendEmptyMessage(103);
            }
            APPLog.e("bookfile:" + temp.exists() + temp.getName());
        } catch (IOException e) {
//            e.printStackTrace();
            CrashReport.postCatchedException(e);
            if (temp != null) temp.delete();
            if (ctx == null || cancel) return;
            long romsize = getAvailSpace(Environment.getExternalStorageDirectory().getPath());
            APPLog.e("bodylenth:" + bodyLenth + "--romsize:" + romsize);
            if (bodyLenth> romsize) {
                ToastUtils.getInstance().showToastShort("内存空间不足，下载书籍需要：" + Formatter.formatFileSize(ctx, bodyLenth));
            } else {
                ToastUtils.getInstance().showToastShort("下载出小差了，麻烦您重新点击下载！！");
            }
            mSubscriberOnNextListener.sendEmptyMessage(103);
        }
    }

    /**
     * 根据路劲获取某个目录的可用空间
     *
     * @param path 文件的路径
     * @return result 返回该目录的可用空间大小
     */
    private long getAvailSpace(String path) {
        APPLog.e("getAvailSpace-path", path);
        StatFs statFs = new StatFs(path);

        long cont = statFs.getBlockCount();// 获取分区的个数
        long size = statFs.getBlockSize();// 获取分区的大小
        long blocks = statFs.getAvailableBlocks();// 获取可用分区的个数
        long result = blocks * size;
        return result;
    }
    public void onCanceldownLoad() {
        if (!this.isUnsubscribed()) {
            this.unsubscribe();
        }
        this.cancel = true;
    }

    private void copyFileUsingFileChannels(File source, File dest) throws IOException {
        FileChannel inputChannel = null;
        FileChannel outputChannel = null;
        try {
            inputChannel = new FileInputStream(source).getChannel();
            outputChannel = new FileOutputStream(dest).getChannel();
            outputChannel.transferFrom(inputChannel, 0, inputChannel.size());
        } finally {
            inputChannel.close();
            outputChannel.close();
        }
    }

}
