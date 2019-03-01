package com.mx.main.activity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.mx.plugin.aidl.ILoadPlugin;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.PersistableBundle;
import android.os.RemoteException;
import android.view.KeyEvent;
import android.view.View;

import com.moxi.updateapp.UpdateUtil;
import com.mx.main.R;
import com.mx.main.model.UserModel;
import com.mx.main.utils.CheckVersionCode;
import com.mx.main.view.RoundView;
import com.mx.mxbase.base.BaseActivity;
import com.mx.mxbase.constant.Constant;
import com.mx.mxbase.http.MXHttpHelper;
import com.mx.mxbase.utils.DateUtil;
import com.mx.mxbase.utils.FileUtils;
import com.mx.mxbase.utils.MXReaderManager;
import com.mx.mxbase.utils.MXUamManager;
import com.mx.mxbase.utils.Toastor;
import com.onyx.android.sdk.device.EpdController;

import java.io.File;
import java.util.Date;
import java.util.HashMap;

import butterknife.Bind;

public class MXMainActivity extends BaseActivity implements View.OnClickListener, RoundView.OnRoundMenuViewListener {

    @Bind(R.id.round_view_menu)
    RoundView roundView;

    private int[] imgs = new int[]{R.mipmap.menu4, R.mipmap.menu6, R.mipmap.menu3, R.mipmap.menu1,
            R.mipmap.menu2, R.mipmap.menu5};//R.mipmap.menu_kouyu,
    private String loadPugin = "";
    private long lastClickTime = 0;
    private boolean NEED_REFEESH = false;
    private MXHttpHelper httpHelper;

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        if (msg.arg1 == 1001) {
            if (msg.what == Integer.parseInt(Constant.SUCCESS)) {
                UserModel um = (UserModel) msg.obj;
                if (um != null) {
                    roundView.setUserName(um.getResult().getName());
                }
            } else {
                Toastor.showToast(this, "");
            }
        }
    }

    @Override
    protected int getMainContentViewId() {
        return R.layout.mx_activity_main;
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        init();
    }

    /**
     * 初始化视图
     */
    private void init() {

        httpHelper = MXHttpHelper.getInstance(this);

        roundView.setDate(DateUtil.getCurDateStr("yyyy年MM月dd日"));
        roundView.setWeek(DateUtil.getWeekOfDate(new Date()));
        roundView.setMenuImg(imgs);
        roundView.setOnRoundMenuViewListener(this);
    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {
        lastClickTime = System.currentTimeMillis();
        CheckVersionCode.getInstance(this).checkframework(roundView);
        String appSession = MXUamManager.queryUser(this);
        HashMap<String, String> user = new HashMap<>();
        if ("".equals(appSession)) {
            roundView.setUserName("未登录");
        } else {
            user.put("appSession", appSession);
            httpHelper.postStringBack(1001, Constant.GET_USER_INFO, user, getHandler(), UserModel.class);
        }
        if (NEED_REFEESH) {
            triggerFullRefresh(this, 1000);
            NEED_REFEESH = false;
        }
    }

    public void triggerFullRefresh(final Activity activity, int delay) {
        getHandler().postDelayed(new Runnable() {
            @Override
            public void run() {
                EpdController.invalidate(activity.getWindow().getDecorView(), EpdController.UpdateMode.GC);
            }
        }, delay);
    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {
        try {
            dialogShowOrHide(false, "请稍后...");
        } catch (Exception e) {
            e.printStackTrace();
        }
        NEED_REFEESH = true;
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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            return true;
        } else
            return super.onKeyDown(keyCode, event);
    }

    private static final String ACTION_BIND_SERVICE = "android.mx.aidl.LOAD_PLUGIN";
    private ILoadPlugin iLoadPlugin;

    private ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            iLoadPlugin = ILoadPlugin.Stub.asInterface(iBinder);
            try {
                if (loadPugin.equals("-1")) {
                    iLoadPlugin.selfRestaet();
                } else {
                    iLoadPlugin.loadPluginFile(loadPugin);
                }
                if (iLoadPlugin != null) {
                    unbindService(mServiceConnection);
                    iLoadPlugin = null;
                }
            } catch (RemoteException e) {
                e.printStackTrace();
                Toastor.showToast(MXMainActivity.this, "未安装此应用");
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            iLoadPlugin = null;
        }
    };

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
        }
    }

    @Override
    public void onSingleTapUp(int position) {
        if (System.currentTimeMillis() - lastClickTime < 1000) {
            return;
        }
        lastClickTime = System.currentTimeMillis();
        switch (position) {
//            case 0:
                //com.moxi.teacher
//                startAppByPackage("com.kk.yingyu100k");
//                loadPugin = "exams.apk";
//                startPlugin();
//                break;
            case 0:
                startAppByPackage("com.moxi.filemanager");
//                loadPugin = "copybook.apk";
//                startPlugin();
                break;
            case 2:
                startAppByPackage("com.moxi.reader");
//                loadPugin = "reader.apk";
//                startPlugin();
                break;
            case 1:
                //com.onyx.android.note   com.moxi.handwritten
                startAppByPackage("com.moxi.mxwriter");
//                loadPugin = "handwritten.apk";
//                startPlugin();
                break;
            case 3:
                startAppByPackage("com.moxi.exams");
//                loadPugin = "timetable.apk";
//                startPlugin();
                break;
            case 4:
                startAppByPackage("com.moxi.timetable");
                break;
            case 5:
                startActivity(new Intent(this, MXSettingActivity.class));
//                loadPugin = "mxuser.apk";
//                startPlugin();
                break;
            case 6:
                startAppByPackage("com.moxi.mxuser");
                break;
            case 99:
                String lastRead = MXReaderManager.queryReadFile(this);
                if (!lastRead.equals("")) {
                    FileUtils.getInstance().openFile(this, new File(lastRead));
                } else {
                    Toastor.showToast(this, "未找到最近阅读书籍，赶快去阅读吧");
                }
                break;
            default:
                break;
        }
    }

    /**
     * 根据包名启动已安装程序
     *
     * @param packagename
     */
    private void startAppByPackage(String packagename) {
        try {
            Intent intent = this.getPackageManager().getLaunchIntentForPackage(packagename);
            startActivity(intent);
        } catch (Exception e) {
            Toastor.showToast(this, "没有安装此模块");
            new UpdateUtil(this, this).checkInstall(packagename);
        }
    }

    /**
     * 启动插件
     */
    private void startPlugin() {
        dialogShowOrHide(true, "请稍后...");
        Intent intentService = new Intent(ACTION_BIND_SERVICE);
        intentService.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        MXMainActivity.this.bindService(intentService, mServiceConnection, BIND_AUTO_CREATE);
    }
}