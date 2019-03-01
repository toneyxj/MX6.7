package com.moxi.haierc.activity;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.os.PersistableBundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.moxi.haierc.R;
import com.moxi.haierc.model.UserModel;
import com.moxi.haierc.util.CheckVersionCode;
import com.moxi.haierc.util.Utils;
import com.moxi.updateapp.UpdateUtil;
import com.mx.mxbase.base.BaseActivity;
import com.mx.mxbase.constant.Constant;
import com.mx.mxbase.http.MXHttpHelper;
import com.mx.mxbase.interfaces.YesOrNo;
import com.mx.mxbase.utils.DeleteDDReaderBook;
import com.mx.mxbase.utils.Log;
import com.mx.mxbase.utils.MXUamManager;
import com.mx.mxbase.utils.Toastor;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import net.tsz.afinal.FinalHttp;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import butterknife.Bind;

//import com.onyx.android.sdk.device.DeviceInfo;
//import com.onyx.android.sdk.device.EpdController;

/**
 * 海尔c端主界面
 */
public class MainActivity extends BaseActivity implements View.OnClickListener {

    private String adFilePath = "";
    private String fileName = "ad.jpg";
    Target target;
    Picasso picasso;
    FinalHttp fh = new FinalHttp();

    private TextView date_tx;
    private TextView week;
    private TextView day;
    private TextView read;
    private MXHttpHelper httpHelper;
    private int[] res = new int[]{
            R.mipmap.mx_img_avatar_0,
            R.mipmap.mx_img_avatar_1,
            R.mipmap.mx_img_avatar_2,
            R.mipmap.mx_img_avatar_3};
    @Bind(R.id.rl_day_day_practice)
    RelativeLayout rlDayPractice;
    @Bind(R.id.rl_file_manager)
    RelativeLayout rlFileManager;
    @Bind(R.id.rl_hand_write)
    RelativeLayout rlHandWrite;
    @Bind(R.id.rl_settings)
    RelativeLayout rlSettings;
    @Bind(R.id.rl_time_table)
    RelativeLayout rlTimeTable;
    @Bind(R.id.rl_user_logo)
    RelativeLayout rlUserLogo;
    @Bind(R.id.rl_reader)
    ImageView rlReader;
    @Bind(R.id.tit_tx)
    TextView tvReaderTitle;
    @Bind(R.id.tv_user_name)
    TextView tvUserName;
    @Bind(R.id.update_tv)
    TextView tvneed;
    @Bind(R.id.icon)
    ImageView imgAvatar;
    @Bind(R.id.adversing)
    ImageView adversing;
    @Bind(R.id.tiantianlian)
    ImageView imgDayDay;
    @Bind(R.id.tv_day_day_practice)
    TextView tvDayDay;
    @Bind(R.id.kechengbiao)
    ImageView imgKeCheng;
    @Bind(R.id.tv_ke_cheng_biao)
    TextView tvKeCheng;
//    @Bind(R.id.view_null)
//    View nullView;

    private ActivityManager mActivityManager = null;
    private String stuFlag = "";

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        if (msg.arg1 == 1001) {
            if (msg.what == Integer.parseInt(Constant.SUCCESS)) {
                UserModel userModel = (UserModel) msg.obj;
                if (userModel != null) {
                    tvUserName.setText(userModel.getResult().getName());
                    if (userModel.getResult().getHeadPortrait() != -99) {
                        imgAvatar.setImageResource(res[userModel.getResult().getHeadPortrait()]);
                    } else {
                        imgAvatar.setImageResource(res[0]);
                    }
                }
            }
        }
    }

    @Override
    protected int getMainContentViewId() {
        return R.layout.activity_main;
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        mActivityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        httpHelper = MXHttpHelper.getInstance(this);
        adFilePath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "ad.jpg";
        picasso = Picasso.with(this);
        stuFlag = share.getString("flag_version_stu");
        if (stuFlag.equals("")) {
            stuFlag = "标准版";
        }
        initView();
        setDate();
    }

    private void initView() {
        read = (TextView) findViewById(R.id.read);
        date_tx = (TextView) findViewById(R.id.date_tx);
        week = (TextView) findViewById(R.id.week);
        day = (TextView) findViewById(R.id.day);
        //设置点击监听
        read.setOnClickListener(this);
        rlDayPractice.setOnClickListener(this);
        rlFileManager.setOnClickListener(this);
        rlHandWrite.setOnClickListener(this);
        rlSettings.setOnClickListener(this);
        rlTimeTable.setOnClickListener(this);
        rlUserLogo.setOnClickListener(this);
        rlReader.setOnClickListener(this);
    }

    private void setDate() {
        final String dayNames[] = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
        Date date = new Date();
        SimpleDateFormat sim = new SimpleDateFormat("yyyy年MM月");
        SimpleDateFormat simday = new SimpleDateFormat("dd");
        day.setText(sim.format(date));
        date_tx.setText(simday.format(date));

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (dayOfWeek < 0) dayOfWeek = 0;
        week.setText(dayNames[dayOfWeek]);
        CheckVersionCode.getInstance(this).checkframework(new YesOrNo() {
            @Override
            public void onYesOrNo(boolean is) {
                if (isfinish)return;
                tvneed.setVisibility(is?View.VISIBLE:View.GONE);
            }
        });
    }

    /**
     * 获取用户信息
     *
     * @param appSession 用户session
     */
    private void getUserInfo(String appSession) {
        HashMap<String, String> user = new HashMap<>();
        if ("".equals(appSession)) {
            tvUserName.setText("未登录");
        } else {
            user.put("appSession", appSession);
            httpHelper.postStringBack(1001, Constant.GET_USER_INFO, user, getHandler(), UserModel.class);
        }
    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    private void setAdversing() {

        File file = new File(adFilePath);
        if (!file.exists()) {
            Log.d("class", "setAdversing is not exists...");
            adversing.setImageResource(R.mipmap.adversing);
        } else {
            Log.d("class", "setAdversing is exists...");
            adversing.setImageBitmap(BitmapFactory.decodeFile(adFilePath));
        }
    }

    private void getADData() {
        Log.d("class", "response===>displayImage");
        Log.d("clss", "adFilePath==>" + adFilePath);
        File file = new File(adFilePath);
        if (file != null) {
            file.delete();
            Log.d("class", "cache img delete ...");
        }
        final String path = Environment.getExternalStorageDirectory().getAbsolutePath();
        target = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                FileOutputStream ostream = null;
                try {
                    ostream = new FileOutputStream(new File(adFilePath));
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, ostream);
                    ostream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Log.d("class", "DownLoad Success!");
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
            }
        };

        //Picasso下载
        picasso.load(Constant.GET_ADVSING).memoryPolicy(MemoryPolicy.NO_CACHE)
                .networkPolicy(NetworkPolicy.NO_CACHE)
                .into(target);
    }

    @Override
    public void onActivityResumed(Activity activity) {
        stuFlag = share.getString("flag_version_stu");
        if (stuFlag.equals("")) {
            stuFlag = "标准版";
        }
        if (stuFlag.equals("教育版")) {
            imgDayDay.setImageResource(R.mipmap.tiantianlian);
            imgKeCheng.setImageResource(R.mipmap.kecheng);
            tvDayDay.setText("好题天天练");
            tvKeCheng.setText("课程表");
        } else {
            imgDayDay.setImageResource(R.mipmap.img_index_cal);
            imgKeCheng.setImageResource(R.mipmap.img_index_email);
            tvDayDay.setText("日历");
            tvKeCheng.setText("邮箱");
        }
        setDate();
        String title = getrecentlyRead();
        tvReaderTitle.setText(title.substring(title.lastIndexOf("/") + 1));
        getUserInfo(MXUamManager.queryUser(this));
        triggerFullRefresh(this, 1000);
        new DeleteDDReaderBook(MainActivity.this, false, null).execute();
        getRunningAppProcessInfo();
        CheckVersionCode.getInstance(this).checkframework(new YesOrNo() {
            @Override
            public void onYesOrNo(boolean is) {
                if (isfinish)return;
                tvneed.setVisibility(is?View.VISIBLE:View.GONE);
            }
        });

        setAdversing();
//        DeviceInfo.currentDevice.showSystemStatusBar(this);
        openOrOff(true);
    }

    private String[] pricessNames = new String[]{"com.neverland.oreader", "com.onyx.reader", "com.moxi.bookstore", "com.moxi.filemanager", "com.moxi.exams"};

    // 获得系统进程信息
    private void getRunningAppProcessInfo() {
        for (int i = 0; i < pricessNames.length; i++) {
            mActivityManager.killBackgroundProcesses(pricessNames[i]);
        }
    }

    /**
     * GC当前屏幕
     *
     * @param mainActivity 当前activity
     * @param i            时间间隔
     */
    private void triggerFullRefresh(final MainActivity mainActivity, int i) {
        getHandler().postDelayed(new Runnable() {
            @Override
            public void run() {
//                EpdController.invalidate(mainActivity.getWindow().getDecorView(), EpdController.UpdateMode.GC);
            }
        }, i);
    }

    @Override
    public void onActivityPaused(Activity activity) {
        getADData();
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

    @Override
    public void onBackPressed() {
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.read:
                //TODO 继续阅读
                continueReader(getrecentlyRead());
                break;
            case R.id.rl_reader:
                if (Utils.isSDCardCanReadAndWrite()) {
                    startAppByPackage("com.moxi.bookstore", "com.moxi.bookstore.activity.bookManager.MXBookStoreActivity", stuFlag);
                } else {
                    Toastor.showToast(this, "sd卡未准备好");
                }
                break;
            case R.id.rl_settings:
                //设置
                startActivity(new Intent(this, MXSettingActivity.class));
                break;
            case R.id.rl_day_day_practice:
                //好题天天练
                if (stuFlag.equals("教育版")) {
                    startAppByPackage("com.moxi.exams", "com.moxi.haierexams.activity.MXPracticeActivity");
                } else {
                    startAppByPackage("com.moxi.calendar", "com.moxi.calendar.MainActivity");
                }
                break;
            case R.id.rl_file_manager:
                //文件管理
                if (Utils.isSDCardCanReadAndWrite()) {
                    startAppByPackage("com.moxi.filemanager", "com.moxi.filemanager.FileManagerActivity");
                } else {
                    Toastor.showToast(this, "sd卡未准备好");
                }
                break;
            case R.id.rl_time_table:
                //课程表
                if (stuFlag.equals("教育版")) {
                    startAppByPackage("com.moxi.timetable", "com.mx.timetable.activity.MXTimeTablesActivity");
                } else {
                    startAppByPackage("com.android.email", "com.android.email.activity.Welcome");
                }
                break;
            case R.id.rl_hand_write:
                //手写
                startAppByPackage("com.moxi.writeNote", "com.moxi.writeNote.MainActivity");
                break;
            case R.id.rl_user_logo:
                //用户登录
                startAppByPackage("com.moxi.user", "com.mx.user.activity.MXLoginActivity", stuFlag);
                break;
            default:
                break;
        }
    }

    /**
     * 通过包名 类名启动app
     *
     * @param packName  包名 com.moxi.xxxx
     * @param className 类名
     */
    private void startAppByPackage(String packName, String className) {
        try {
            Intent sound = new Intent();
            sound.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            ComponentName cnSound = new ComponentName(packName, className);
            sound.setComponent(cnSound);
            startActivity(sound);
        } catch (Exception e) {
            e.printStackTrace();
            Toastor.showToast(this, "启动失败，请检测是否正常安装");
            new UpdateUtil(this, this).checkInstall(packName);
        }
    }

    /**
     * 通过包名 类名启动app
     *
     * @param packName  包名 com.moxi.xxxx
     * @param className 类名
     */
    private void startAppByPackage(String packName, String className, String stuFlag) {
        try {
            Intent sound = new Intent();
            sound.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            ComponentName cnSound = new ComponentName(packName, className);
            sound.setComponent(cnSound);
            sound.putExtra("flag_version_stu", stuFlag);
            startActivity(sound);
        } catch (Exception e) {
            e.printStackTrace();
            Toastor.showToast(this, "启动失败，请检测是否正常安装");
            new UpdateUtil(this, this).checkInstall(packName);
        }
    }

    /**
     * 继续阅读
     *
     * @param path 最近阅读书籍文件路径
     */
    private void continueReader(String path) {
        if (!path.equals("")) {
            File file = new File(path);
            if (file.exists()) {
                Intent input = new Intent();
                input.putExtra("file", path);
                ComponentName cnInput = new ComponentName("com.moxi.bookstore", "com.moxi.bookstore.activity.RecentlyActivity");
                input.setComponent(cnInput);
                startActivity(input);
            } else {
                showToast("阅读文件已异常，请确认文件是否存在！！");
            }
        } else {
            showToast("还没有阅读记录哟！");
        }
    }

    /**
     * 获得最近阅读书籍路径
     */
    public String getrecentlyRead() {
        ContentResolver contentResolver = getContentResolver();
        Uri selecturi = Uri.parse("content://com.moxi.bookstore.provider.RecentlyProvider/Recently");
        Cursor cursor = contentResolver.query(selecturi, null, null, null, null);
        if (cursor != null)
            if (cursor.moveToNext()) {
                String recently = cursor.getString(0);
                return recently;
            }
        if (cursor != null) cursor.close();
        return "";
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
}
