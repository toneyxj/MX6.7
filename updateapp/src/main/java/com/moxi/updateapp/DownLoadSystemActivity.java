package com.moxi.updateapp;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.moxi.updateapp.model.SystemOtaModel;
import com.moxi.updateapp.view.ProgressView;
import com.mx.mxbase.constant.APPLog;
import com.mx.mxbase.constant.Constant;
import com.mx.mxbase.utils.FileUtils;
import com.mx.mxbase.utils.StringUtils;
import com.mx.mxbase.utils.ToastUtils;
import com.mx.mxbase.utils.Toastor;
import com.mx.mxbase.view.AlertDialog;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import soft.com.updateapp.R;


public class DownLoadSystemActivity extends Activity {

    public static void startDownLoadSystem(final Context context , final SystemOtaModel data){
        APPLog.e("系统更新提示",data.toString());
        new AlertDialog(context).builder().
                setTitle("系统更新提示").setMsg(data.describe).setCancelable(true).setNegativeButton("确定", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();//com.onyx.content.browser.activity.OnyxOTAActivity
                ComponentName cnSound = new ComponentName("com.moxi.systemapp", "com.moxi.systemapp.activity.DownLoadSystemActivity");
                intent.setComponent(cnSound);
                Bundle bundle = new Bundle();
                APPLog.e("url", data.url);
                APPLog.e("MD5", data.MD5);
                bundle.putString("url", data.url);
                bundle.putString("MD5", data.MD5);
                intent.putExtras(bundle);
                context.startActivity(intent);
            }
        }).setPositiveButton("取消", new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        }).show();
    }

    FinalHttp fh = new FinalHttp();
    public static String HTTP_HOST = Constant.HTTP_HOST;

    private ProgressView progess;
    private TextView download_hitn;

    int downloadSize = 0;

    private SystemOtaModel data;

    boolean isRun = true;


//    private PowerManager.WakeLock wakeLock;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("test",true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);
        if (savedInstanceState!=null)finish();

        data= (SystemOtaModel) getIntent().getExtras().getSerializable("data");
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
//        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
//        wakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK
//                | PowerManager.ON_AFTER_RELEASE, getClass()
//                .getCanonicalName());
//        if (null != wakeLock) {
//            wakeLock.acquire();
//        }

        progess = (ProgressView) findViewById(R.id.progess);
        download_hitn = (TextView) findViewById(R.id.download_hitn);
        download_hitn.setVisibility(View.VISIBLE);
        progess.setMaxNumber(100);
        String systemPath= FileUtils.getInstance().getDownloadSystemPath();
        if (data==null||StringUtils.isNull(data.url)||StringUtils.isNull(systemPath)) {
            ToastUtils.getInstance().showToastShort("文件下载异常，请稍后重试,错误码：7");
            finish();
        } else {
            //下载
            downloadFile(data.url,systemPath);
        }

    }

    private void downloadFile(String url,String tagetPath) {
        FinalHttp finalHttp = new FinalHttp();
        //中文转utf-8
        finalHttp.download(url, tagetPath, new AjaxCallBack<File>() {
            @Override
            public void onLoading(long count, long current) {
                int pro=(int) ((current*100/count));
                progess.setCurNumber(pro);
                download_hitn.setText("文件大小："+getFileSize(count)+" 已下载："+ getFileSize(current));
            }


            @Override
            public void onSuccess(File file) {
                super.onSuccess(file);
                String s = file.getAbsoluteFile().toString();
                String md5 = "";
                try {
                    md5 = getFileMD5(new File(s));
                }catch (Exception e){
                    Toastor.showLongToast(DownLoadSystemActivity.this,"文件下载异常，请稍后重试,错误码：6");
                }

                if (md5 == null){
                    DownLoadSystemActivity.this.finish();
                    Toastor.showLongToast(DownLoadSystemActivity.this,"文件下载异常，请稍后重试,错误码：5");
                    return;
                }else{
                    Log.i("cov", "local md5===>" + md5);
                }

                if ("".equals(md5)){
                    DownLoadSystemActivity.this.finish();
                    Toastor.showLongToast(DownLoadSystemActivity.this,"文件下载异常，请稍后重试,错误码：9");
                    return;
                }
                if (md5.equals(data.MD5)){
                    //MD5验证成功
                    Intent sound = new Intent();//com.onyx.content.browser.activity.OnyxOTAActivity
                    ComponentName cnSound = new ComponentName("com.moxi.systemapp", "com.moxi.systemapp.activity.InstailSystemActivity");
                    sound.setComponent(cnSound);
                    DownLoadSystemActivity.this.startActivity(sound);
                    DownLoadSystemActivity.this.finish();
                }else{
                    DownLoadSystemActivity.this.finish();
                    Toastor.showLongToast(DownLoadSystemActivity.this,"文件下载异常，请稍后重试,错误码：-2");
                }
            }

            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                APPLog.e("DownLoadSystemActivity","下载失败-=="+strMsg);
                Toastor.showLongToast(DownLoadSystemActivity.this,"文件下载异常，请稍后重试,错误码：-3");
                DownLoadSystemActivity.this.finish();
            }
        });
    }
    private String getFileSize(long size){
        return Formatter.formatShortFileSize(DownLoadSystemActivity.this, size);
    }



    /**
     * get file md5
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
    }

    @Override
    protected void onDestroy() {
//        if (wakeLock != null){
//            wakeLock.release();
//        }
        super.onDestroy();
        isRun = false;
    }
}
