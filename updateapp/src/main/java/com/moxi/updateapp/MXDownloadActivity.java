package com.moxi.updateapp;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.format.Formatter;
import android.util.Log;
import android.view.WindowManager;
import android.widget.TextView;

import com.moxi.updateapp.utils.DownloadTask;
import com.moxi.updateapp.view.ProgressView;
import com.mx.mxbase.constant.APPLog;
import com.mx.mxbase.constant.Constant;
import com.mx.mxbase.utils.StringUtils;
import com.mx.mxbase.utils.ToastUtils;
import com.mx.mxbase.utils.Toastor;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import soft.com.updateapp.R;

/**
 * Created by zhengdelong on 2016/10/18.
 */

public class MXDownloadActivity extends Activity {

    public static String HTTP_HOST = Constant.HTTP_HOST;

    private ProgressView progess;
    private TextView download_hitn;

    List<MXUpdateModel> mxUpdateModelList;

    boolean isRun = true;

    List<String> path = new ArrayList<>();

    private String uninstallPackageName = "";

    List<String> unpackage = new ArrayList<>();


    private Map<Long, Long> fileSizes = new HashMap<>();
    private int downloadIndex=0;
    private DownloadTask downloadTask;
    private Handler myhandler=new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_download);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        mxUpdateModelList = (List<MXUpdateModel>) getIntent().getSerializableExtra("down");
        download_hitn = (TextView) findViewById(R.id.download_hitn);
        progess = (ProgressView) findViewById(R.id.progess);
        progess.setMaxNumber(100);

        Log.d("app", "mxUpdateModelList size===>" + mxUpdateModelList.size());
        if (mxUpdateModelList != null) {
            // TODO: 2016/10/19 检查是否含有需要先卸载的app
            for (int j = 0; j < mxUpdateModelList.size(); j++) {
                MXUpdateModel mx = mxUpdateModelList.get(j);
                if (mx.getUpdateType() == 1) {
                    // TODO: 2016/10/19 必须卸载
                    unpackage.add(mx.getPackageName());
                }
            }
            StringBuffer sb = new StringBuffer();
            for (int k = 0; k < unpackage.size(); k++) {
                if (k == 0) {
                    sb.append(unpackage.get(k));
                } else {
                    sb.append(";" + unpackage.get(k));
                }
            }
            if (unpackage.size() > 0) {
                uninstallPackageName = sb.toString();
            } else {
                uninstallPackageName = "";
            }
            downlaodModel();
        } else {
            finish();
        }

    }
    private String totalSizeValue = null;
    /**
     * 下载更新阅读器
     */
    private void downlaodModel(){
        if (mxUpdateModelList.size()<=0){
            finish();
            return;
        }
        if (mxUpdateModelList.size()==downloadIndex){
            //下载全部完毕
            Log.i("cov", "onSuccess");
            StringBuffer sb = new StringBuffer();
            String luncherPath = "";
            for (int m = 0; m < mxUpdateModelList.size(); m++) {
                MXUpdateModel mxUpdateModel1 = mxUpdateModelList.get(m);
                String localPath = mxUpdateModel1.getApkLocalPath();
                int isLuncher = mxUpdateModel1.getIsLancher();

                if (m == 0) {
                    if (isLuncher == 1) {
                        Log.d("cov", "ap22==>" + 1);
                        luncherPath = localPath;
                    } else {
                        Log.d("cov", "ap22==>" + 2);
                        sb.append(localPath);
                    }

                } else {
                    if (isLuncher == 1) {
                        Log.d("cov", "ap22==>" + 3);
                        luncherPath = localPath;
                    } else {
                        Log.d("cov", "ap22==>" + 4);
                        if (!sb.toString().equals("")) {
                            sb.append(";" + localPath);
                        } else {
                            sb.append(localPath);
                        }

                    }

                }
            }

            String ap = sb.toString();
            Log.d("cov", "ap==>" + ap);
            Log.d("cov", "luncherPath==>" + luncherPath);
            reInstallApp(ap, luncherPath, MXDownloadActivity.this.getIntent().getIntExtra("install_flag", 3));
            return;
        }
        totalSizeValue=null;
        MXUpdateModel mxUpdateModel = mxUpdateModelList.get(downloadIndex);
        downloadIndex++;
        mxUpdateModel.setApkLocalPath(initApkPath(mxUpdateModel));
//        downLoadApk(mxUpdateModel);
        downloadTask =new DownloadTask(mxUpdateModel,downloadListener);
        downloadTask.run();


    }

    private String initApkPath(MXUpdateModel mxUpdateModel) {
        String DOWN_SDK_DIR_NAME = "update";
        String DOWN_SDK_DIR =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                        + File.separator + DOWN_SDK_DIR_NAME + File.separator;

//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-ddHHmmssSSS");
        String plugnName = StringUtils.stringToMD5(mxUpdateModel.getAppDesc()) + ".apk";

        File file = new File(DOWN_SDK_DIR);
        if (!file.isDirectory() && !file.exists()) {
            file.mkdirs();
//        long time = System.currentTimeMillis();
        }
        String apkPath = DOWN_SDK_DIR + plugnName;
        return apkPath;
    }
    DownloadTask.DownloadListener  downloadListener=new DownloadTask.DownloadListener() {
        @Override
        public void onDownloadFail(MXUpdateModel model, long totalSize) {
            if (MXDownloadActivity.this.isFinishing())return;

            ToastUtils.getInstance().showToastShort("文件下载异常，请稍后重试,错误码：-3");
            MXDownloadActivity.this.finish();
        }

        @Override
        public void onDownloadSucces(final MXUpdateModel model) {
            if (MXDownloadActivity.this.isFinishing())return;
            myhandler.post(new Runnable() {
                @Override
                public void run() {
                    String s =model.getApkLocalPath();
                    String md5 = "";
                    try {
                        md5 = getFileMD5(new File(s));
                    } catch (Exception e) {
                        Toastor.showLongToast(MXDownloadActivity.this, "文件下载异常，请稍后重试,错误码：6");
                    }

                    if (md5 == null) {
                        MXDownloadActivity.this.finish();
                        Toastor.showLongToast(MXDownloadActivity.this, "文件下载异常，请稍后重试,错误码：5");
                        return;
                    } else {
                        Log.i("cov", "local md5===>" + md5);
                    }

                    if ("".equals(md5)) {
                        MXDownloadActivity.this.finish();
                        Toastor.showLongToast(MXDownloadActivity.this, "文件下载异常，请稍后重试,错误码：9");
                        return;
                    }
                    downlaodModel();
                }
            });

        }

        @Override
        public void onDownloadStop(MXUpdateModel model) {
            if (MXDownloadActivity.this.isFinishing())return;
            myhandler.post(new Runnable() {
                @Override
                public void run() {

                }
            });
        }

        @Override
        public void onDownloadProgress(MXUpdateModel model, final long currentSize, final long totalSize) {
            if (MXDownloadActivity.this.isFinishing())return;
            myhandler.post(new Runnable() {
                @Override
                public void run() {
                    if (totalSizeValue == null) {
                        totalSizeValue = Formatter.formatFileSize(MXDownloadActivity.this, totalSize);
                    }
                    int progress = (int) ((currentSize * 100) / totalSize);
                    download_hitn.setText("下载/总数："+downloadIndex+"/"+mxUpdateModelList.size()+"\t文件大小：" + totalSizeValue + "\t进度：" + progress + "%");
                    progess.setCurNumber(progress);
                }
            });
        }
    };

    private long[] getCounts() {
        long[] sizes = new long[2];

        if (fileSizes.size() == mxUpdateModelList.size()) {
            long size0 = 0;
            long size1 = 0;
            Iterator<Map.Entry<Long, Long>> it = fileSizes.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<Long, Long> entry = it.next();
                size0 += entry.getKey();
                size1 += entry.getValue();
            }
            sizes[0]=size0;
            sizes[1]=size1;
        }
        return sizes;
    }

    private String getFileSize(long size) {
        return Formatter.formatShortFileSize(MXDownloadActivity.this, size);
    }

    private void reInstallApp(String apkPath, String luncherPath, int flag) {
        try {
            APPLog.e("app", "path==>" + apkPath);
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            ComponentName cn = new ComponentName("com.moxi.systemapp", "com.moxi.systemapp.activity.AppInstallOrUninstallActivity");
            intent.setComponent(cn);
            intent.setFlags(flag);
//        String tempVersion = FileUtil.getCode();
//        if (tempVersion.equals("")) {
//            tempVersion = AppUtil.getPackageInfo(this).versionName;
//        }
//        intent.putExtra("versionName", tempVersion);
//        Log.e("传递过去的版本号：", "flag = " + flag + "  versionName=" + tempVersion);
//        intent.putExtra("install", apkPath);
//        intent.putExtra("uninstall", uninstallPackageName);
//        intent.putExtra("luncher", luncherPath);//luncher 文件路径
            Bundle bundle = new Bundle();
            bundle.putString("filePath", apkPath);
            bundle.putString("luncherPath", luncherPath);
            bundle.putString("uninstall", uninstallPackageName);
            bundle.putInt("style", 1);
            intent.putExtras(bundle);
            startActivity(intent);
        } catch (Exception e) {

        } finally {
            this.finish();
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
    protected void onDestroy() {
        myhandler.removeCallbacksAndMessages(null);
        if (downloadTask != null && downloadTask.isAlive()) {
            downloadTask.setEndDownload(true);
        }
        super.onDestroy();
        isRun = false;
    }
}
