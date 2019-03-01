package com.moxi.systemapp.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.RecoverySystem;
import android.text.format.Formatter;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloader;
import com.moxi.systemapp.R;
import com.moxi.systemapp.ok.DownloadListener;
import com.moxi.systemapp.ok.OkDownlaodUtils;
import com.moxi.systemapp.utils.DownloadUtil;
import com.moxi.systemapp.views.ProgressView;
import com.mx.mxbase.constant.APPLog;
import com.mx.mxbase.constant.Constant;
import com.mx.mxbase.netstate.NetWorkUtil;
import com.mx.mxbase.utils.FileUtils;
import com.mx.mxbase.utils.StringUtils;
import com.mx.mxbase.utils.ToastUtils;
import com.mx.mxbase.utils.Toastor;
import com.mx.mxbase.view.AlertDialog;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class DownLoadSystemActivity extends Activity {

    public static void startDownLoadSystem(final Context context, String url, String MD5) {
        Intent intent = new Intent(context, DownLoadSystemActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("url", url);
        bundle.putString("MD5", MD5);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    /**
     * 重sd卡里面复制zip去系统升级
     *
     * @param context
     */
    public static void startDownLoadSystem(final Context context, String path) {
        Intent intent = new Intent(context, DownLoadSystemActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("url", path);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    private String TAG = "DownLoadSystemActivity";

    public static String HTTP_HOST = Constant.HTTP_HOST;

    private ProgressView progess;
    private TextView download_hitn;
//    private TextView download_size;

    private String MD5;
    private String url;

    boolean isfinish = false;
    private DownloadUtil downloadUtil;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("test", true);
    }
    private String systemPath="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_down_load_system);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        if (savedInstanceState != null) finish();
        url = getIntent().getExtras().getString("url", "");
        MD5 = getIntent().getExtras().getString("MD5", "");

        downloadUtil = new DownloadUtil();
        acquireWakeLock();

        progess = (ProgressView) findViewById(R.id.progess);
        download_hitn = (TextView) findViewById(R.id.download_hitn);
//        download_size = (TextView) findViewById(download_size);

        progess.setMaxNumber(100);
         systemPath = FileUtils.getInstance().getDownloadSystemPath();
        if (StringUtils.isNull(url)) url = StringUtils.getSDCardPath() + "/update.zip";
        if (StringUtils.isNull(url) || StringUtils.isNull(systemPath)) {
            ToastUtils.getInstance().showToastShort("文件下载异常，请稍后重试,错误码：7");
            finish();
        } else {
            //下载
            if (url.startsWith("http")) {
//                downloadFile(url, systemPath);
//                startDownload();
//                updateFile();
                DownloadOK();


            } else {
                File file = new File(url);
                if (file.exists()) {
                    startCopy();
                } else {
                    ToastUtils.getInstance().showToastShort("文件不存在");
                    finish();
                }
            }
        }
    }
    private OkDownlaodUtils okDownlaodUtils;
    private void DownloadOK(){
        if (!NetWorkUtil.isNetworkConnected(DownLoadSystemActivity.this)){
            ToastUtils.getInstance().showToastShort("请检查网络连接！");
            finish();
            return;
        }
        okDownlaodUtils=new OkDownlaodUtils(this);
        okDownlaodUtils.setDownlaod(url, systemPath, new DownloadListener() {
            @Override
            public void onProgress(int progress, long total, long hasDownlaod) {
                if (isFinishing())return;
                progess.setCurNumber(progress);
                setGitn(total,hasDownlaod);
            }

            @Override
            public void onSuccess() {
                if (isFinishing())return;
                //下载完成
                String md5 = "";
                try {
                    md5 = getFileMD5(new File(systemPath));
                } catch (Exception e) {
                    ToastUtils.getInstance().showToastShort("文件下载异常，请稍后重试,错误码：6");

                    DownLoadSystemActivity.this.finish();
                }

                if (md5 == null) {
                    ToastUtils.getInstance().showToastShort("文件下载异常，请稍后重试,错误码：5");
                    DownLoadSystemActivity.this.finish();
                    return;
                } else {
                    APPLog.d("cov", "local md5===>" + md5);
                }

                if ("".equals(md5)) {
                    ToastUtils.getInstance().showToastShort("文件下载异常，请稍后重试,错误码：9");
                    DownLoadSystemActivity.this.finish();
                    return;
                }
                initSystem();
            }

            @Override
            public void onFailed(Exception e) {
                if (isFinishing())return;
                if (NetWorkUtil.isNetworkConnected(DownLoadSystemActivity.this)){
                    ToastUtils.getInstance().showToastShort("下载失败"+e.getMessage());
                }else {
                    ToastUtils.getInstance().showToastShort("请检查网络连接！");
                }
                finish();
            }

            @Override
            public void onPaused() {

            }

            @Override
            public void onCanceled() {

            }
        });
        okDownlaodUtils.startDownlaod();
    }

    private int connectionindex=0;

    private void  updateFile(){
        FileDownloader.getImpl().create(url)
                .setPath(systemPath)
                .setListener(new FileDownloadListener() {
                    @Override
                    protected void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                        APPLog.d("pending");
                    }

                    @Override
                    protected void connected(BaseDownloadTask task, String etag, boolean isContinue, int soFarBytes, int totalBytes) {
                        APPLog.d("connected");
                    }

                    @Override
                    protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                        APPLog.d("update download pregress",getFileSize(soFarBytes));
                        connectionindex=0;
                        int progress = (int) (soFarBytes * 1.0f / totalBytes * 100); // 下载中
                        progess.setCurNumber(progress);
                        setGitn(totalBytes,soFarBytes);
                        handler.removeMessages(100);
                        handler.sendEmptyMessageDelayed(100,15000);//30秒没有反应就返回
                    }

                    @Override
                    protected void blockComplete(BaseDownloadTask task) {
                        APPLog.d("blockComplete");
                    }

                    @Override
                    protected void retry(final BaseDownloadTask task, final Throwable ex, final int retryingTimes, final int soFarBytes) {
                        APPLog.d("retry");
                    }

                    @Override
                    protected void completed(BaseDownloadTask task) {
                        APPLog.d("completed");
                        //下载完成
                        String md5 = "";
                        try {
                            md5 = getFileMD5(new File(systemPath));
                        } catch (Exception e) {
                            ToastUtils.getInstance().showToastShort("文件下载异常，请稍后重试,错误码：6");
                            DownLoadSystemActivity.this.finish();
                        }

                        if (md5 == null) {
                            ToastUtils.getInstance().showToastShort("文件下载异常，请稍后重试,错误码：5");
                            DownLoadSystemActivity.this.finish();
                            return;
                        } else {
                            APPLog.d("cov", "local md5===>" + md5);
                        }

                        if ("".equals(md5)) {
                            ToastUtils.getInstance().showToastShort("文件下载异常，请稍后重试,错误码：9");
                            DownLoadSystemActivity.this.finish();
                            return;
                        }
                        initSystem();
                    }

                    @Override
                    protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                        APPLog.d("paused");
                    }

                    @Override
                    protected void error(BaseDownloadTask task, Throwable e) {
                        APPLog.e("error",e.getMessage());
                        if (NetWorkUtil.isNetworkConnected(DownLoadSystemActivity.this)){
                            if(connectionindex>=5) {
                                File file=new File(systemPath);
                                file.delete();
                                ToastUtils.getInstance().showToastShort("文件下载异常，请稍后重试,错误码：-3");
                                finish();
                                return;
                            }
                            connectionindex++;
                            updateFile();
                        }else {
                            ToastUtils.getInstance().showToastShort("请检查网络连接！");
                            finish();
                        }
                    }

                    @Override
                    protected void warn(BaseDownloadTask task) {
                        APPLog.d("warn");
                        ToastUtils.getInstance().showToastShort("文件下载异常，请稍后重试,错误码：-4");
                        DownLoadSystemActivity.this.finish();
                    }
                }).start();

    }

    private void downloadFile(String url, String tagetPath) {
        APPLog.e("url",url);
        APPLog.e("tagetPath",tagetPath);
        downloadUtil.download(url, tagetPath, new DownloadUtil.OnDownloadListener() {
            @Override
            public void onDownloadSuccess(final String path) {
                if (isfinish) return;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String md5 = "";
                        try {
                            md5 = getFileMD5(new File(path));
                        } catch (Exception e) {
                            ToastUtils.getInstance().showToastShort("文件下载异常，请稍后重试,错误码：6");
                            DownLoadSystemActivity.this.finish();
                        }

                        if (md5 == null) {
                            ToastUtils.getInstance().showToastShort("文件下载异常，请稍后重试,错误码：5");
                            DownLoadSystemActivity.this.finish();
                            return;
                        } else {
                            APPLog.d("cov", "local md5===>" + md5);
                        }

                        if ("".equals(md5)) {
                            ToastUtils.getInstance().showToastShort("文件下载异常，请稍后重试,错误码：9");
                            DownLoadSystemActivity.this.finish();
                            return;
                        }
//                        if (md5.equals(MD5)) {
                        //MD5验证成功
                        initSystem();
//                        } else {
//                            ToastUtils.getInstance().showToastShort(  "文件下载异常，请稍后重试,错误码：-2");
//                            DownLoadSystemActivity.this.finish();
//                        }
                    }
                });

            }

            @Override
            public void onDownloading(final int progress, final long count, final long currentsize) {
                if (isfinish) return;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progess.setCurNumber(progress);
                        setGitn(count,currentsize);
                    }
                });

            }

            @Override
            public void onDownloadFailed(String path) {
                if (isfinish) return;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(StringUtils.isStorageLow10M()){
                            ToastUtils.getInstance().showToastShort("内存不足！！");
                        }else {
                            Toastor.showLongToast(DownLoadSystemActivity.this, "文件下载异常，请稍后重试,错误码：-3");
                            DownLoadSystemActivity.this.finish();
                        }
                    }
                });

            }
        });

    }

    private String getFileSize(long size) {
        return Formatter.formatShortFileSize(DownLoadSystemActivity.this, size);
    }

    private void initSystem() {
        File file = new File(FileUtils.getInstance().getDownloadSystemPath());
        try {
            RecoverySystem.verifyPackage(file, new RecoverySystem.ProgressListener() {
                @Override
                public void onProgress(int progress) {
                    setGitn(0,0);
                }
            }, null);
            RecoverySystem.installPackage(getApplicationContext(), file);
        } catch (Exception e) {
            StringUtils.deleteFile(systemPath);
            ToastUtils.getInstance().showToastShort("文件验证失败！");
            finish();
            return;
        }
    }

    /**
     * get file md5
     *
     * @param file
     * @return
     * @throws NoSuchAlgorithmException
     * @throws IOException
     */
    private static String getFileMD5(File file) throws NoSuchAlgorithmException, IOException {
        if (!file.isFile()) {
            return null;
        }
        MessageDigest digest;
        FileInputStream in;
        byte buffer[] = new byte[1024];
        int len;
        digest = MessageDigest.getInstance("MD5");
        in = new FileInputStream(file);
        while ((len = in.read(buffer, 0, 1024)) != -1) {
            digest.update(buffer, 0, len);
        }
        in.close();
        return bytesToHexString(digest.digest());
    }

    public static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }


    @Override
    public void onBackPressed() {
        new AlertDialog(this).builder().
                setTitle("返回提示").setMsg("请确认退出系统更新").setCancelable(false).setNegativeButton("确定", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        }).setPositiveButton("取消", new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        }).show();
    }

    private void startCopy() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Long[] sizes = new Long[2];
                    long bytesum = 0;
                    int byteread = 0;
                    InputStream inStream = new FileInputStream(url); //读入原文件
                    FileOutputStream fs = new FileOutputStream(FileUtils.getInstance().getDownloadSystemPath());
                    byte[] buffer = new byte[2048];
                    sizes[0] = (new File(url)).length();
                    long timec = 0;
                    while ((byteread = inStream.read(buffer)) != -1) {
                        if (isfinish) return;
                        bytesum += byteread; //字节数 文件大小
                        fs.write(buffer, 0, byteread);
                        if (System.currentTimeMillis() - timec > 1000) {
                            timec = System.currentTimeMillis();
                            sizes[1] = bytesum;
                            Message message = new Message();
                            message.what = 0;
                            message.obj = sizes;
                            handler.sendMessage(message);
                        }
                    }
                    inStream.close();
                    handler.sendEmptyMessage(1);
                } catch (Exception e) {
                    handler.sendEmptyMessage(2);
                }
            }
        }).start();
    }


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (isfinish) return;
            switch (msg.what) {
                case 0://
                    Long[] sizes = (Long[]) msg.obj;
                    long current = sizes[1];
                    long count = sizes[0];
                    int pro = (int) ((current * 100 / count));
                    progess.setCurNumber(pro);
                    setGitn(count,current);
                    break;
                case 1://复制成功
                    initSystem();
                    break;
                case 2://失败
                    ToastUtils.getInstance().showToastShort("文件准备失败！");
                    finish();
                    break;
                case 100://下载长时间不反应
                    FileDownloader.getImpl().pauseAll();
//                    updateFile();
//                    ToastUtils.getInstance().showToastShort("网络连接不稳定，请稍后重试！");
//                    finish();
                default:
                    break;
            }
        }
    };

    private void setGitn(long count, long currentsize) {
        if (currentsize == 0&&count==0) {
//            download_hitn.setGravity(Gravity.CENTER_HORIZONTAL);
//            download_size.setVisibility(View.GONE);
            download_hitn.setText("文件验证中...");
            return;
        } else {
//            download_hitn.setGravity(Gravity.RIGHT);
//            download_size.setVisibility(View.VISIBLE);
        }
        if (url.startsWith("http")) {
            download_hitn.setText("文件大小：" + getFileSize(count) + " 下载进度：" + getFileSize(currentsize));
        }else {
            download_hitn.setText("文件大小：" + getFileSize(count) + " 拷贝进度：" + getFileSize(currentsize));
        }
//        if (currentsize != 0)
//            download_size.setText(getFileSize(currentsize));
    }

    @Override
    protected void onDestroy() {
        handler.removeCallbacksAndMessages(null);
        if (okDownlaodUtils!=null)okDownlaodUtils.onDestory();
        if (downloadUtil!=null)downloadUtil.isfinish = true;
        FileDownloader.getImpl().pauseAll();
        isfinish = true;
        releaseWakeLock();
        super.onDestroy();
    }

    PowerManager.WakeLock wakeLock = null;

    //获取电源锁，保持该服务在屏幕熄灭时仍然获取CPU时，保持运行
    private void acquireWakeLock() {
//        if (null == wakeLock || !wakeLock.isHeld()) {
//            APPLog.e("开启WakeLock");
//            PowerManager pm = (PowerManager) this.getSystemService(Context.POWER_SERVICE);
//            wakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ON_AFTER_RELEASE,  getClass()
//                    .getCanonicalName());
//            if (null != wakeLock) {
//                wakeLock.acquire();
//            }
//        }
    }

    //释放设备电源锁
    private void releaseWakeLock() {
//        if (null != wakeLock && wakeLock.isHeld()) {
//            APPLog.e("释放WakeLock");
//            wakeLock.release();
//            wakeLock = null;
//        }
    }
}
