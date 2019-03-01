package com.moxi.systemapp.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.RecoverySystem;
import android.widget.TextView;

import com.moxi.systemapp.R;
import com.mx.mxbase.utils.FileUtils;
import com.mx.mxbase.utils.StringUtils;
import com.mx.mxbase.utils.ToastUtils;

import java.io.File;
import java.io.FileInputStream;
import java.math.BigInteger;
import java.security.MessageDigest;

/**
 * 系统升级
 */
public class InstailSystemActivity extends Activity {
    public static void startInstalSystem(Context context, String MD5) {
        Intent intent = new Intent(context, InstailSystemActivity.class);
//        Bundle bundle = new Bundle();
//        bundle.putString("MD5", MD5);
//        intent.putExtras(bundle);
        intent.putExtra("MD5",MD5);
        context.startActivity(intent);
    }

    private String MD5="";
    private TextView content;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("MD5", MD5);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instail_system);

        content=(TextView)findViewById(R.id.content);

        if (savedInstanceState != null) {
            finish();
        }
//        MD5 = getIntent().getStringExtra("MD5");
        String path = FileUtils.getInstance().getDownloadSystemPath();
        if (StringUtils.isNull(path)) {
            ToastUtils.getInstance().showToastShort("读取文件失败！--错误码01");
            finish();
            return;
        }
        File file = new File(path);
        if (!file.exists()) {
            ToastUtils.getInstance().showToastShort("读取文件失败！-错误码02");
            finish();
            return;
        }

        PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "systemUpdate");
        try {
            wl.acquire();
            RecoverySystem.verifyPackage(file, new RecoverySystem.ProgressListener() {
                @Override
                public void onProgress(int progress) {
                    content.setText("文件验证中："+progress+"%");
                }
            }, null);
            RecoverySystem.installPackage(getApplicationContext(),file);
        } catch (Exception e) {
            ToastUtils.getInstance().showToastShort("文件验证失败！");
            finish();
            return;
        }

    }

    /**
     * 获取单个文件的MD5值！
     *
     * @param file
     * @return
     */

    public static String getFileMD5(File file) {
        if (!file.isFile()) {
            return null;
        }
        MessageDigest digest = null;
        FileInputStream in = null;
        byte buffer[] = new byte[1024];
        int len;
        try {
            digest = MessageDigest.getInstance("MD5");
            in = new FileInputStream(file);
            while ((len = in.read(buffer, 0, 1024)) != -1) {
                digest.update(buffer, 0, len);
            }
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        BigInteger bigInt = new BigInteger(1, digest.digest());
        return bigInt.toString(16);
    }

}
