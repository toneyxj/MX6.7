package com.moxi.systemapp.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.moxi.systemapp.R;
import com.moxi.systemapp.utils.InstallApp;
import com.moxi.systemapp.utils.UninstallApp;
import com.mx.mxbase.constant.APPLog;
import com.mx.mxbase.utils.StringUtils;
import com.mx.mxbase.utils.ToastUtils;

public class AppInstallOrUninstallActivity extends Activity {
    /**
     * 应用安装
     *
     * @param context  上下文
     * @param filePath 下载文件路径
     */
    public static void StartAppInstallActivity(Context context, String filePath, String luncherPath, String uninstall) {
        Intent intent = new Intent(context, AppInstallOrUninstallActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("filePath", filePath);
        bundle.putString("luncherPath", luncherPath);
        bundle.putString("uninstall", uninstall);
        bundle.putInt("style", 1);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    /**
     * 应用卸载
     *
     * @param context      上下文
     * @param packageValue 包名
     */
    public static void StartAppUninstallActivity(Context context, String packageValue) {
        Intent intent = new Intent(context, AppInstallOrUninstallActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("packageValue", packageValue);
        bundle.putInt("style", 2);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    private String filePath = "", packageValue = "", luncherPath = "", uninstall = "";
    private String[] pakeges = null;
    private String[] uninstalls = null;
    private int style;
    private TextView content;
    private boolean isFinish = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_install_or_uninstall);

        Bundle bundle = savedInstanceState;
        if (bundle == null) {
            bundle = getIntent().getExtras();
        }
        if (bundle == null) this.finish();

        style = bundle.getInt("style", 0);
        filePath = bundle.getString("filePath", "");
        packageValue = bundle.getString("packageValue", "");
        luncherPath = bundle.getString("luncherPath", "");
        uninstall = bundle.getString("uninstall", "");
        APPLog.e("uninstall", uninstall);
        APPLog.e("luncherPath", luncherPath);
        APPLog.e("packageValue", packageValue);
        APPLog.e("filePath", filePath);
        APPLog.e("style", style);

        pakeges = filePath.split(";");
        uninstalls = uninstall.split(";");
        if (pakeges.length == 0) finish();

        content = (TextView) findViewById(R.id.content);

        content.setText(getText());
        initApp();
    }

    private String getText() {
        String value = "";
        switch (style) {
            case 1:
                value = "安装中...";

                break;
            case 2:
                value = "卸载中...";
                break;
            default:
                value = "未知状态...";
                break;
        }
        return value;
    }

    private int i = 0;

    private void initApp() {
        switch (style) {
            case 1:
                for (i = 0; i < uninstalls.length; i++) {
                    String path = uninstalls[i].trim();
                    if (StringUtils.isNull(path)) continue;
                    new UninstallApp(this, path, new UninstallApp.UnInstallListener() {
                        @Override
                        public void onUnInstall(int is) {
                            if (isFinish) return;
                            ToastUtils.getInstance().showToastShort("卸载" + (is == 1 ? "成功" : "失败"));
                            if (pakeges.length == 0 && i >= uninstalls.length - 1)
                                AppInstallOrUninstallActivity.this.finish();
                        }
                    }).execute("");
                }

                if (!(pakeges.length == 1 && StringUtils.isNull(pakeges[0]))) {
                    for (i = 0; i < pakeges.length; i++) {
                        String path = pakeges[i].trim();
                        if (StringUtils.isNull(path)) continue;
                        new InstallApp(this, path, new InstallApp.InstallListener() {
                            @Override
                            public void onInstall(int is) {
                                if (isFinish) return;
                                ToastUtils.getInstance().showToastShort("安装" + (is == 1 ? "成功" : "失败"));
                                if (i >= pakeges.length - 1)
                                    AppInstallOrUninstallActivity.this.finish();
                            }
                        }).execute("");
                    }
                }
                break;
            case 2:
                new UninstallApp(this, packageValue, new UninstallApp.UnInstallListener() {
                    @Override
                    public void onUnInstall(int is) {
                        if (isFinish) return;
                        ToastUtils.getInstance().showToastShort("卸载" + (is == 1 ? "成功" : "失败"));
                        AppInstallOrUninstallActivity.this.finish();
                    }
                }).execute("");
                break;
            default:
                onBackPressed();
                break;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        onBackPressed();
//        bundle.putString("packageValue",packageValue);
//        bundle.putString("filePath",filePath);
//        bundle.putInt("style",1);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isFinish = true;
    }
}
