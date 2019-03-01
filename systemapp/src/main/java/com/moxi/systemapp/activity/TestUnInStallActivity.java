package com.moxi.systemapp.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.text.format.Formatter;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloader;
import com.moxi.systemapp.R;
import com.mx.mxbase.base.BaseActivity;
import com.mx.mxbase.constant.APPLog;
import com.mx.mxbase.netstate.NetWorkUtil;
import com.mx.mxbase.utils.StringUtils;
import com.mx.mxbase.utils.ToastUtils;

import java.io.File;

import butterknife.Bind;

public class TestUnInStallActivity extends BaseActivity {
    @Bind(R.id.install_edite)
    EditText install_edite;
    @Bind(R.id.install)
    Button install;
    @Bind(R.id.uninstall_edite)
    EditText uninstall_edite;
    @Bind(R.id.uninstall)
    Button uninstall;

    @Override
    protected int getMainContentViewId() {
        return R.layout.activity_test_un_in_stall;
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        install.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                dialogShowOrHide(true,"应用安装中...");
//                new InstallApp(TestUnInStallActivity.this, install_edite.getText().toString().trim(), new InstallApp.InstallListener() {
//                    @Override
//                    public void onInstall(int is) {
//                        ToastUtils.getInstance().showToastShort("安装" + (is == 1 ? "成功" : "失败"));
//                        dialogShowOrHide(false,"应用安装中...");
//                    }
//                }).execute("");
            }
        });
        uninstall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                dialogShowOrHide(true,"应用卸载中...");
//                new UninstallApp(TestUnInStallActivity.this, uninstall_edite.getText().toString().trim(), new UninstallApp.UnInstallListener() {
//                    @Override
//                    public void onUnInstall(int is) {
//                        ToastUtils.getInstance().showToastShort("卸载" + (is == 1 ? "成功" : "失败"));
//                        dialogShowOrHide(false,"应用安装中...");
//                    }
//                }).execute("");

//                updateFile("", StringUtils.getSDPath()+"updatetest.zip");
                updateFile("http://moxiota.oss-cn-shenzhen.aliyuncs.com/Haier.H9.2018-05-08.zip", StringUtils.getSDPath()+"updatetest.zip");


            }
        });

    }
    private int connectionindex=0;

    private void  updateFile(final String url, final String path){
        FileDownloader.getImpl().create(url)
                .setPath(path)
                .setListener(new FileDownloadListener() {
                    @Override
                    protected void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                        String value="文件大小：" + getFileSize(totalBytes) + " 下载进度："+getFileSize(soFarBytes);
                        APPLog.e("pending",value);
                    }

                    @Override
                    protected void connected(BaseDownloadTask task, String etag, boolean isContinue, int soFarBytes, int totalBytes) {
                        String value="文件大小：" + getFileSize(totalBytes) + " 下载进度："+getFileSize(soFarBytes);
                        APPLog.e("connected",value);
                    }

                    @Override
                    protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                        connectionindex=0;
                        String value="文件大小：" + getFileSize(totalBytes) + " 下载进度："+getFileSize(soFarBytes);
                        APPLog.e("progress",value);
                    }

                    @Override
                    protected void blockComplete(BaseDownloadTask task) {
                        APPLog.e("blockComplete");
                    }

                    @Override
                    protected void retry(final BaseDownloadTask task, final Throwable ex, final int retryingTimes, final int soFarBytes) {
                        String value="文件大小：" + getFileSize(0) + " 下载进度："+getFileSize(soFarBytes);
                        APPLog.e("retry",value);
                    }

                    @Override
                    protected void completed(BaseDownloadTask task) {
                        //下载完成
                        APPLog.e("completed");
                    }

                    @Override
                    protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                        String value="文件大小：" + getFileSize(0) + " 下载进度："+getFileSize(soFarBytes);
                        APPLog.e("paused",value);
                    }

                    @Override
                    protected void error(BaseDownloadTask task, Throwable e) {
                        APPLog.e("error",e.getMessage());
                        if (NetWorkUtil.isNetworkConnected(TestUnInStallActivity.this)){
                            if(connectionindex>=5) {
                                File file=new File(path);
                                file.delete();
                                ToastUtils.getInstance().showToastShort("下载失败！！");
                                finish();
                                return;
                            }
                            connectionindex++;
                                updateFile(url, path);
                        }else {
                            ToastUtils.getInstance().showToastShort("请检查网络连接！");
                            finish();
                        }
                    }

                    @Override
                    protected void warn(BaseDownloadTask task) {
                        APPLog.e("warn");
                        ToastUtils.getInstance().showToastShort("下载文件出问题！");
                        finish();
                    }
                }).start();

    }
    private String getFileSize(long size) {
        return Formatter.formatShortFileSize(this, size);
    }
    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {

    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {

    }

    @Override
    public void onActivityRestoreInstanceState(Bundle savedInstanceState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        FileDownloader.getImpl().pauseAll();
    }
}
