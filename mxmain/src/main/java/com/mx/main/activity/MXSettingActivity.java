package com.mx.main.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageInfo;
import android.mx.plugin.aidl.ILoadPlugin;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PersistableBundle;
import android.os.RemoteException;
import android.provider.Settings;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.moxi.updateapp.UpdateUtil;
import com.mx.main.R;
import com.mx.main.utils.CheckVersionCode;
import com.mx.mxbase.base.BaseActivity;
import com.mx.mxbase.constant.Constant;
import com.mx.mxbase.utils.AppUtil;
import com.mx.mxbase.utils.DeviceUtil;
import com.mx.mxbase.utils.Log;
import com.mx.mxbase.utils.MXVersionManager;
import com.mx.mxbase.utils.StorageUtil;
import com.mx.mxbase.utils.Toastor;
import com.mx.mxbase.view.AlertDialog;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.List;

import butterknife.Bind;
import okhttp3.Call;

/**
 * Created by Archer on 16/7/27.
 */
public class MXSettingActivity extends BaseActivity implements View.OnClickListener {

    private WifiManager mWifi;
    private TextView tvWifiName;
    @Bind(R.id.tv_setting_online_update)
    TextView tvUpdate;
    @Bind(R.id.tv_version_name)
    TextView tvVersionName;
    @Bind(R.id.tv_setting_no)
    TextView tvDeviceNo;
    @Bind(R.id.tv_cun_chu_info)
    TextView tvCunChuInfo;
    @Bind(R.id.img_setting_logo)
    ImageView imgLogo;
    @Bind(R.id.update_tv)
    TextView tvneed;

    @Override
    protected int getMainContentViewId() {
        return R.layout.mx_activity_setting;
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        init();
    }

    /**
     * 初始化视图
     */
    private void init() {
        //获取组件
        tvWifiName = (TextView) findViewById(R.id.tv_setting_wifi_name);

        //设置点击事件监听
        findViewById(R.id.ll_setting_wifi).setOnClickListener(this);
        findViewById(R.id.ll_setting_date).setOnClickListener(this);
        findViewById(R.id.ll_setting_feedback).setOnClickListener(this);
        findViewById(R.id.ll_setting_help).setOnClickListener(this);
        findViewById(R.id.ll_setting_input).setOnClickListener(this);
        findViewById(R.id.ll_setting_save).setOnClickListener(this);
        findViewById(R.id.ll_setting_sound).setOnClickListener(this);
        tvUpdate.setOnClickListener(this);
        imgLogo.setOnClickListener(this);

        //获取wifi信息
        mWifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        tvDeviceNo.setText("产品序列号:" + DeviceUtil.getDeviceSerial());

        //组件赋初值
        getHandler().postDelayed(new Runnable() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void run() {
                long total = 17179869184l;
                long avail = StorageUtil.getRomAvailableSize(MXSettingActivity.this);
                tvCunChuInfo.setText(StorageUtil.getPrintSize(total - avail) + "/" + "16G");
            }
        }, 5000);
    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {
        CheckVersionCode.getInstance(this).checkframework(tvneed);
        if (MXVersionManager.queryReadFile(this).equals("")) {
            tvVersionName.setText("版本号:" + AppUtil.getPackageInfo(this).versionName);
        } else {
            tvVersionName.setText("版本号:" + MXVersionManager.queryReadFile(this));
        }
        WifiInfo wifiInfo = mWifi.getConnectionInfo();
        wifiInfo.getSSID();
        if (mWifi.isWifiEnabled()) {
            if (wifiInfo.getSSID() == null) {
                tvWifiName.setText("未连接");
            } else {
                tvWifiName.setText(wifiInfo.getSSID());
            }
        } else {
            tvWifiName.setText("未打开");
        }
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

    }

    public static final int MIN_CLICK_DELAY_TIME = 1000;
    private long lastClickTime = 0;
    int times = 0;

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            //wifi设置
            case R.id.ll_setting_wifi:
                Intent wifi = new Intent(Settings.ACTION_WIFI_SETTINGS);
                startActivity(wifi);
                break;
            //声音设置
            case R.id.ll_setting_sound:
                Intent sound = new Intent();
                ComponentName cnSound = new ComponentName("com.onyx", "com.onyx.ebag.DeviceSoundSettingActivity");
                sound.setComponent(cnSound);
                startActivity(sound);
                break;
            //存储
            case R.id.ll_setting_save:
                Intent memory = new Intent(this, MXStorageSettingActivity.class);
                startActivity(memory);
                break;
            //输入法设置
            case R.id.ll_setting_input:
                Intent input = new Intent();
                ComponentName cnInput = new ComponentName("com.onyx", "com.onyx.ebag.InputMethodAndLanguageSettings");
                input.setComponent(cnInput);
                startActivity(input);
                break;
            //时间日期设置
            case R.id.ll_setting_date:
                Intent date = new Intent(Settings.ACTION_DATE_SETTINGS);
                startActivity(date);
                break;
            //其它
            case R.id.ll_setting_feedback:
                Intent other = new Intent();
                ComponentName cnOther = new ComponentName("com.onyx", "com.onyx.content.browser.activity.SettingsActivity");
                other.setComponent(cnOther);
                startActivity(other);
                break;
            //应用
            case R.id.ll_setting_help:
//                startActivity(new Intent(this, MXApplicationActivity.class));
                Intent app = new Intent();
                ComponentName cnApp = new ComponentName("com.onyx", "com.onyx.content.browser.activity.ApplicationsActivity");
                app.setComponent(cnApp);
                startActivity(app);
                break;
            case R.id.tv_setting_online_update:
                // TODO: 16/9/1
                // TODO: 16/9/1
                OkHttpUtils.post().url(Constant.CHECK_LOWER_UPDATE).addParams("versionName", AppUtil.getPackageInfo(this).versionName).build().execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Toastor.showToast(MXSettingActivity.this, "网络请求失败，请检查网络");
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONObject aa = new JSONObject(jsonObject.getString("result"));
                            int lowerVersion = aa.getInt("baseCode");
                            if (lowerVersion > Constant.LOCAL_VIR_VERSION) {
                                Intent sound = new Intent();//com.onyx.content.browser.activity.OnyxOTAActivity
                                ComponentName cnSound = new ComponentName("com.onyx", "com.onyx.content.browser.activity.SimpleOTAActivity");
                                sound.setComponent(cnSound);
                                MXSettingActivity.this.startActivity(sound);
                            } else {
                                new UpdateUtil(MXSettingActivity.this, MXSettingActivity.this).startUpdate();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
                break;
            case R.id.img_setting_logo:
                long currentTime = Calendar.getInstance().getTimeInMillis();
                if (currentTime - lastClickTime < MIN_CLICK_DELAY_TIME || lastClickTime == 0) {
                    times++;
                    Log.e("点击了", "aaaaa" + times);
                    if (times == 5) {
                        queryListModelVersion();
                    }
                } else {
                    times = 0;
                }
                lastClickTime = currentTime;
                break;
            default:
                break;
        }
    }

    private static final String ACTION_BIND_SERVICE = "android.mx.aidl.LOAD_PLUGIN";
    private ILoadPlugin iLoadPlugin;

    private ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            iLoadPlugin = ILoadPlugin.Stub.asInterface(iBinder);
            try {
                if (iLoadPlugin.checkVersion(0)) {

                }
                if (iLoadPlugin != null) {
                    unbindService(mServiceConnection);
                    iLoadPlugin = null;
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            iLoadPlugin = null;
        }
    };

    private void startCheckVersion() {
        Intent intentService = new Intent(ACTION_BIND_SERVICE);
        intentService.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        MXSettingActivity.this.bindService(intentService, mServiceConnection, BIND_AUTO_CREATE);
    }

    /**
     * 隐藏功能查看各个子模块版本号
     */
    private void queryListModelVersion() {
        List<PackageInfo> packages = this.getPackageManager().getInstalledPackages(0);
        String tempVersion = "";
        for (int i = 0; i < packages.size(); i++) {
            PackageInfo packageInfo = packages.get(i);
            String packageName = packageInfo.packageName;
            if (packageName.startsWith("com.moxi")) {
                String temp = packageInfo.applicationInfo.loadLabel(getPackageManager()).toString() + "\t" + packageInfo.versionName;
                tempVersion += temp + "\n";
            }
        }
        new AlertDialog(this).builder().setCancelable(false).setTitle("当前子模块版本号").setMsg(tempVersion).
                setPositiveButton("确定", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                }).show();
    }
}