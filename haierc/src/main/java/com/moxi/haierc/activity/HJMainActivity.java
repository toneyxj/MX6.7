package com.moxi.haierc.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Message;
import android.os.PersistableBundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.moxi.haierc.R;
import com.moxi.haierc.adapter.HJRecentReadingAdapter;
import com.moxi.haierc.hjbook.HJBookIndexActivity;
import com.moxi.haierc.hjbook.hjdata.HJBookData;
import com.moxi.haierc.hjbook.hjutils.ScanBookUtils;
import com.moxi.haierc.model.UserModel;
import com.moxi.haierc.util.HJCheckVersionCode;
import com.moxi.haierc.util.Utils;
import com.moxi.updateapp.UpdateUtil;
import com.mx.mxbase.base.BaseActivity;
import com.mx.mxbase.constant.Constant;
import com.mx.mxbase.utils.DeleteDDReaderBook;
import com.mx.mxbase.utils.FileUtils;
import com.mx.mxbase.utils.StringUtils;
import com.mx.mxbase.utils.Toastor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.Bind;

/**
 * Created by King on 2017/8/1.
 */

public class HJMainActivity extends BaseActivity implements View.OnClickListener {

    @Bind(R.id.rl_hj_main_recent_reading)
    RelativeLayout rlHjRecentReading;
    @Bind(R.id.grid_view_hj_main_recent_reading)
    GridView gridViewHjRecentReading;
    @Bind(R.id.hj_icon)
    ImageView imageViewIcon;
    @Bind(R.id.tv_hj_user_name)
    TextView tvUsername;
    @Bind(R.id.hj_main_day)
    TextView tvDay;
    @Bind(R.id.hj_main_week)
    TextView tvWeek;
    @Bind(R.id.update_tv)
    TextView tvneed;

    private int[] res = new int[]{
            R.mipmap.mx_img_new_avatar_0,
            R.mipmap.mx_img_new_avatar_1,
            R.mipmap.mx_img_new_avatar_2,
            R.mipmap.mx_img_new_avatar_3};

    private List<HJBookData> listRecent = new ArrayList<>();
    private HJRecentReadingAdapter adapter;
    private boolean NEED_REGET = false;
    private LocalBroadcastManager localBroadcastManager;

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        if (msg.arg1 == 2001) {
            if (msg.what == Integer.parseInt(Constant.SUCCESS)) {
                UserModel userModel = (UserModel) msg.obj;
                if (userModel != null) {
                    tvUsername.setText(userModel.getResult().getName());
                    if (userModel.getResult().getHeadPortrait() != -99) {
                        imageViewIcon.setImageResource(res[userModel.getResult().getHeadPortrait()]);
                    } else {
                        imageViewIcon.setImageResource(R.mipmap.img_mx_new_defate_icon);
                    }
                }
            }
        }
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        localBroadcastManager = LocalBroadcastManager.getInstance(this);

        findViewById(R.id.ll_hj_dict).setOnClickListener(this);
        findViewById(R.id.ll_hj_practice).setOnClickListener(this);
        findViewById(R.id.ll_hj_reading).setOnClickListener(this);
        findViewById(R.id.ll_hj_settings).setOnClickListener(this);
        findViewById(R.id.rl_hj_icon).setOnClickListener(this);

        listRecent = ScanBookUtils.getInstance(this).getBookData(2, "");
        adapter = new HJRecentReadingAdapter(this, gridViewHjRecentReading, listRecent);
        gridViewHjRecentReading.setAdapter(adapter);
        gridViewHjRecentReading.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (Utils.isFastClick()) {
                    HJBookData hjBookData = listRecent.get(position);
                    File curretnFile = new File(hjBookData.getFilePath());
                    if (curretnFile.exists()) {
                        FileUtils.getInstance().openFile(HJMainActivity.this, curretnFile);
                        ScanBookUtils.getInstance(HJMainActivity.this).updateBookReadTime(hjBookData.getId());
                    } else {
                        showToast("阅读文件已异常，请确认文件是否存在！！");
                    }
                }
            }
        });
        IntentFilter filter = new IntentFilter("com.moxi.broadcast.external.action");
        localBroadcastManager.registerReceiver(external, filter);
//        try {
//            UnzipFromAssets.unZip(this, "hj_dir_imgs.zip", FileUtils.getInstance().getDataFilePath(), true);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    //当外部存储器准备好的时候调用
    BroadcastReceiver external = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            listRecent = ScanBookUtils.getInstance(HJMainActivity.this).getBookData(2, "");
            adapter.dataChange(listRecent);
        }
    };

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {
        if (NEED_REGET) {
            listRecent = ScanBookUtils.getInstance(HJMainActivity.this).getBookData(2, "");
            adapter.dataChange(listRecent);
            NEED_REGET = false;
        }
        new DeleteDDReaderBook(HJMainActivity.this, false, null).execute();
//        getRunningAppProcessInfo();
        HJCheckVersionCode.getInstance(this).checkframework(tvneed);
//        DeviceInfo.currentDevice.showSystemStatusBar(this);
//        openOrOff(true);
        setDate();
    }

    private void setDate() {
        final String dayNames[] = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
        Date date = new Date();
        SimpleDateFormat sim = new SimpleDateFormat("yyyy年MM月dd日");
        tvDay.setText(sim.format(date));

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (dayOfWeek < 0) dayOfWeek = 0;
        tvWeek.setText(dayNames[dayOfWeek]);
    }

    private void openOrOff(boolean open) {
        if (open) {
            writeStringValueToFile("/sys/devices/platform/onyx_misc.0/tp_disable", "0");
        } else {
            writeStringValueToFile("/sys/devices/platform/onyx_misc.0/tp_disable", "1");
        }
    }

    private boolean writeStringValueToFile(final String path, String value) {
        FileOutputStream fout = null;
        try {
            File file = new File(path);
            if (!file.exists()) {
                String command = "touch " + path;
                do_exec(command);
            }
            fout = new FileOutputStream(file);
            byte[] bytes = value.getBytes();
            fout.write(bytes);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            if (fout != null) {
                try {
                    fout.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return true;
    }

    private void do_exec(String command) {
        try {
            Process p = Runtime.getRuntime().exec(command);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityPaused(Activity activity) {
        NEED_REGET = true;
        dialogShowOrHide(false, "");
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
        localBroadcastManager.unregisterReceiver(external);
    }

    @Override
    protected int getMainContentViewId() {
        return R.layout.mx_activity_hj_main;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_hj_dict:
                try {
                    Intent music = new Intent();
                    ComponentName musicOther = new ComponentName("com.onyx.dict", "com.onyx.dict.activity.DictMainActivity");
                    music.setComponent(musicOther);
                    startActivity(music);
                } catch (Exception e) {
                    e.printStackTrace();
                    Toastor.showToast(HJMainActivity.this, "启动失败，请检测是否正常安装");
                }
                break;
            case R.id.ll_hj_practice://操作手册
                if (StringUtils.getSDPath("hjschool").equals(""))return;

                String instructionName = "mx_instruction_20170925.pdf";
                String instructionPath = StringUtils.getSDPath("hjschool") + instructionName;
                if (!new File(instructionPath).exists()) {
                    FileUtils.copyAssets(HJMainActivity.this, instructionName, StringUtils.getSDPath("hjschool"));
                }
                FileUtils.getInstance().openFile(HJMainActivity.this, new File(instructionPath));
                break;
            case R.id.ll_hj_reading://天天阅读
                if (Utils.isSDCardCanReadAndWrite()) {
                    startActivity(new Intent(this, HJBookIndexActivity.class));
                } else {
                    Toastor.showToast(HJMainActivity.this, "sd卡未准备好");
                }
                break;
            case R.id.ll_hj_settings:
                //设置
                startActivity(new Intent(HJMainActivity.this, HJSettingActivity.class));
                break;
            case R.id.rl_hj_icon:
                try {
                    Intent sound = new Intent();
                    sound.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    ComponentName cnSound = new ComponentName("com.moxi.user", "com.mx.user.activity.MXLoginActivity");
                    sound.setComponent(cnSound);
                    sound.putExtra("flag_version_stu", "标准版");
                    startActivity(sound);
                } catch (Exception e) {
                    e.printStackTrace();
                    Toastor.showToast(this, "启动失败，请检测是否正常安装");
                    new UpdateUtil(this, this).checkInstall("com.moxi.user");
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {
    }
}
