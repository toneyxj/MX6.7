package soft.com.update;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.TextView;
import android.widget.Toast;

import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONObject;

import java.util.List;

import okhttp3.Call;
import soft.com.update.util.FileUtil;
import soft.com.update.util.MXVersionManager;
import soft.com.update.util.PackageUtil;

import static com.mx.mxbase.constant.Constant.GETCODE;

public class AppManager extends Activity {

    private TextView install_tx;
    private String packageName = "";

    boolean isRun = true;
    int po = 0;

    private boolean appType = false;

    private PowerManager.WakeLock wakeLock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK
                | PowerManager.ON_AFTER_RELEASE, getClass()
                .getCanonicalName());
        if (null != wakeLock) {
            wakeLock.acquire();
        }

        int flag = getIntent().getFlags();
        if (flag == 0){
            String apkPath = getIntent().getStringExtra("install");
            final int acode = getIntent().getIntExtra("code",-1);
            appType = true;
            new Thread(new mThread()).start();
            if ("".equals(apkPath)){
                // TODO: 2016/10/17 安装文件路径为空
                Log.d("app","安装文件路径为空");
                finish();
                return;
            }
            setContentView(R.layout.activity_main);
            install_tx = (TextView)findViewById(R.id.install_tx);
            install_tx.setText("正在安装，请稍后");

            try{
                new PackageUtil().install(this,apkPath, new PackageUtil.InstallCall(){
                    @Override
                    public void installed(int code, final String packageName) {
                        // TODO: 2016/10/17 安装完成
                        if (code == 1){
                            setView("安装完成");
                            Log.d("app","安装完成 code：" + code + "，packageName：" + packageName);
                            if (acode == 1){
                                AppManager.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        runApp(packageName);
                                        AppManager.this.finish();
                                    }
                                });
                            }
                        }else{
                            // TODO: 2016/10/17 安装失败
                            AppManager.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    AppManager.this.finish();
                                }
                            });
                            Log.d("app","安装失败 code：" + code + "，packageName：" + packageName);
                        }
                    }
                });
            }catch (Exception e){
                // TODO: 2016/10/17 安装异常
                Log.d("app","安装异常 flag == 0:" + e.getMessage());
                setView("安装异常");
                this.finish();
            }

        }else if (flag == 1){
            //覆盖安装
            String apkPath = getIntent().getStringExtra("install");
            appType = true;
            new Thread(new mThread()).start();
            if ("".equals(apkPath)){
                // TODO: 2016/10/17 安装文件路径为空
                Log.d("app","安装文件路径为空");
                finish();
                return;
            }
            // TODO: 2016/10/17 验证apk文件是否存在

            setContentView(R.layout.activity_main);
            install_tx = (TextView)findViewById(R.id.install_tx);
            install_tx.setText("正在安装，请稍后");

            try{
                new PackageUtil().reInstall(this,apkPath, new PackageUtil.InstallCall(){
                    @Override
                    public void installed(int code, String packageName) {
                        // TODO: 2016/10/17 安装完成
                        if (code == 1){
                            setView("安装完成");
                            Log.d("app","安装完成 code：" + code + "，packageName：" + packageName);
                        }else{
                            // TODO: 2016/10/17 安装失败
                            setView("安装失败");
                            Log.d("app","安装失败 code：" + code + "，packageName：" + packageName);
                        }

                    }
                });
            }catch (Exception e){
                // TODO: 2016/10/17 安装异常
                Log.d("app","安装异常 flag == 1:" + e.getMessage());
                setView("安装异常");
                this.finish();
            }
        } else if (flag == 2){
            packageName = getIntent().getStringExtra("uninstall");
            appType = false;
            new Thread(new mThread()).start();
            if (packageName == null){
                // TODO: 2016/10/17 需要卸载的app包名为空
                Log.d("app","需要卸载的app包名为空1");
                finish();
            }
            if (packageName.equals("")){
                // TODO: 2016/10/17 需要卸载的app包名为空
                Log.d("app","需要卸载的app包名为空2");
                finish();
            }
            setContentView(R.layout.activity_main);
            install_tx = (TextView)findViewById(R.id.install_tx);
            install_tx.setText("正在卸载，请稍后");
            try{
                new PackageUtil().uninstall(this, packageName, new PackageUtil.UnInstallCall() {
                    @Override
                    public void unInstalled(int code, String packageName) {
                        // TODO: 2016/10/17 卸载完成
                        if (code == 1){
                            setView("卸载完成");
                            Log.d("app","卸载完成 code：" + code + "，packageName：" + packageName);
                        }else{
                            // TODO: 2016/10/17 卸载失败
                            setView("卸载失败");
                            Log.d("app","卸载失败 code：" + code + "，packageName：" + packageName);
                        }
                    }
                });
            }catch (Exception e){
                // TODO: 2016/10/17 卸载异常
                Log.d("app","卸载异常");
                setView("卸载异常");
                this.finish();
            }
        }else if (flag == 3){
            Log.d("update","flag == 3");
            setContentView(R.layout.activity_main);
            install_tx = (TextView)findViewById(R.id.install_tx);
            String unpackage = getIntent().getStringExtra("uninstall");
            final String verName = getIntent().getStringExtra("versionName");
            appType = true;
            new Thread(new mThread()).start();
            //判断是否含有强制更新的程序
            if (!"".equals(unpackage)){
                final String[] packages = unpackage.split(";");
                final int[] ss = {0};
                for (int i = 0;i<packages.length;i++){
                    String packageName = packages[i];
                    uninstall(packageName, new PackageUtil.UnInstallCall() {
                        @Override
                        public void unInstalled(int code, String packageName) {
                            if (code == 1){
                                //卸载成功
                                ss[0]++;
                                if (ss[0] == packages.length){
                                    /**
                                     * 所有需要卸载的程序卸载完成
                                     */
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            installAppList(0,verName);
                                        }
                                    }).start();
                                }
                            }else{
                                setView("卸载异常");
                                finish();
                            }
                        }
                    });
                }
            }else{
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        installAppList(0,verName);
                    }
                }).start();
            }
        }else if(flag == 4){
            Log.d("update","flag == 4");
            setContentView(R.layout.activity_main);
            install_tx = (TextView)findViewById(R.id.install_tx);
            String unpackage = getIntent().getStringExtra("uninstall");
            final String verName = getIntent().getStringExtra("versionName");
            appType = true;
            new Thread(new mThread()).start();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    installAppList(1,verName);
                }
            }).start();

        }

    }

    private void uninstall(String packageName,final PackageUtil.UnInstallCall uninstallcall){
        if (packageName == null){
            // TODO: 2016/10/17 需要卸载的app包名为空
            Log.d("app","需要卸载的app包名为空1");
            uninstallcall.unInstalled(-1,"需要卸载的app包名为空1");
            finish();
        }
        if (packageName.equals("")){
            // TODO: 2016/10/17 需要卸载的app包名为空
            Log.d("app","需要卸载的app包名为空2");
            uninstallcall.unInstalled(-1,"需要卸载的app包名为空2");
            finish();
        }
        setContentView(R.layout.activity_main);
        install_tx = (TextView)findViewById(R.id.install_tx);
        install_tx.setText("正在卸载，请稍后");
        try{
            new PackageUtil().uninstall(this, packageName, new PackageUtil.UnInstallCall() {
                @Override
                public void unInstalled(int code, String packageName) {
                    // TODO: 2016/10/17 卸载完成
                    uninstallcall.unInstalled(code,packageName);
                }
            });
        }catch (Exception e){
            // TODO: 2016/10/17 卸载异常
            Log.d("app","卸载异常");
            uninstallcall.unInstalled(-1,"卸载异常");
            this.finish();
        }
    }

    /**
     * 批量安装
     */
    private void installAppList(final int statu,final String verName){
        String paths = getIntent().getStringExtra("install");
        Log.d("app","paths===>" + paths);
        final String[] na = paths.split(";");
        final int naSize = na.length;
        final int[] po = {0};
        final int[] wrong = {0};
        Log.d("app","na.length===>" + na.length);


        for (int i = 0;i<na.length;i++){
            String apkPath = na[i];
            if ("".equals(apkPath)){
                // TODO: 2016/10/17 安装文件路径为空
                Log.d("app","flag = 3 安装文件路径为空");
                AppManager.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setView("安装异常，请重试");
                        finish();
                    }
                });
                return;
            }
            new PackageUtil().reInstall(apkPath,new PackageUtil.InstallCall(){
                @Override
                public void installed(int code, String packageName) {
                    // TODO: 2016/10/17 安装完成
                    if (code == 1){
                        po[0]++;
                        Log.d("app","安装完成 code：" + code + "，packageName：" + packageName);
                    }else{
                        wrong[0]++;
                        // TODO: 2016/10/17 安装失败
                        Log.d("app","安装失败 code：" + code + "，packageName：" + packageName);
                    }
                    if (wrong[0] + po[0] == naSize){
                        // TODO: 2016/10/26 安装完成
                        if (wrong[0] == 0){
                            // TODO: 2016/10/26 全部安装成功
                            AppManager.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    setView("更新成功");
                                    Toast.makeText(AppManager.this,"更新成功",Toast.LENGTH_LONG).show();
                                    Log.d("update","statu===>" + statu);
                                    if (statu == 0){
                                        getVersionCode(verName);
                                    }else{
                                        finish();
                                    }
//                                    finish();
                                }
                            });
                        }else{
                            // TODO: 2016/10/26 已经安装完成，但部分安装失败
                            AppManager.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(AppManager.this,"更新完成，但某些功能似乎有一些问题请重启重新升级",Toast.LENGTH_LONG).show();
                                    finish();
                                }
                            });
                        }
                    }
                }
            });
        }
    }

    private void getVersionCode(String verName){
        final OkHttpUtils okHttpUtils = OkHttpUtils.getInstance();
        Log.d("update","param verName===>" + verName);
//        okHttpUtils.post().url(GETCODE).addParams("update",verName)
        okHttpUtils.post().url(GETCODE).addParams("versionName",verName)
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                Log.d("update","message==>" + e.getMessage());
                finish();
            }

            @Override
            public void onResponse(String response, int id) {
                try{
                    Log.d("update","response===>" + response);
                    JSONObject jsonObject = new JSONObject(response);
                    int code = jsonObject.optInt("code",-1);
                    if (code == 0){
                        JSONObject result = jsonObject.getJSONObject("result");
                        String versionName = result.optString("versionName","");
                        if ("".equals(versionName)){
                            return;
                        }

//                        new FileUtil().saveVersionName(versionName);
                        Log.d("update",versionName);
                        MXVersionManager.insertReadFile(AppManager.this,"0",versionName,"com.mx.main");
                        Log.d("update","name===xxxxx==>" + MXVersionManager.queryReadFile(AppManager.this));
                        finish();

                    }
                }catch (Exception e){
                    Log.d("update","Exception e===>" + e.getMessage());
                    finish();
                }
            }
        });
    }

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int pp = msg.what;
            if (pp == 1){
                if (appType){
                    install_tx.setText("正在安装.");
                }else{
                    install_tx.setText("正在卸载.");
                }
            }else if (pp == 2){
                if (appType){
                    install_tx.setText("正在安装..");
                }else{
                    install_tx.setText("正在卸载..");
                }
            }else if (pp == 3){
                if (appType){
                    install_tx.setText("正在安装...");
                }else{
                    install_tx.setText("正在卸载...");
                }
            }else{
                if (appType){
                    install_tx.setText("正在安装....");
                }else{
                    install_tx.setText("正在卸载....");
                }
            }
        }
    };


    class mThread implements Runnable{

        @Override
        public void run() {

            try{
                while(isRun){
                    if (po == 4){
                        po = 0;
                    }
                    po ++;
                    handler.sendEmptyMessage(po);
                    Thread.sleep(2000);
                }
            }catch (Exception e){

            }
        }
    }

    private void startQuick(String packageName,String activityName){
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setComponent(new ComponentName(packageName,activityName));
        startActivity(intent);
        finish();
    }

    private void setView(final String text){
        AppManager.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                isRun = false;
                install_tx.setText(text);
            }
        });
    }

    private void runApp(String packageName) {
        PackageInfo pi;
        try {
            pi = getPackageManager().getPackageInfo(packageName, 0);
            Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
            resolveIntent.setPackage(pi.packageName);
            PackageManager pManager = getPackageManager();
            List apps = pManager.queryIntentActivities(
                    resolveIntent, 0);

            ResolveInfo ri = (ResolveInfo)apps.iterator().next();
            if (ri != null) {
                packageName = ri.activityInfo.packageName;
                String className = ri.activityInfo.name;
                Intent intent = new Intent(Intent.ACTION_MAIN);
                ComponentName cn = new ComponentName(packageName, className);
                intent.setComponent(cn);
                startActivity(intent);
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isRun = false;
    }
}
