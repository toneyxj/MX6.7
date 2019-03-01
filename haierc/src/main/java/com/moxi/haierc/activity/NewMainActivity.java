package com.moxi.haierc.activity;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
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

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.moxi.haierc.R;
import com.moxi.haierc.adapter.MainMenuAdapter;
import com.moxi.haierc.adapter.RecentReadingAdapter;
import com.moxi.haierc.callback.UpdateCallBack;
import com.moxi.haierc.model.BookStoreFile;
import com.moxi.haierc.model.UserModel;
import com.moxi.haierc.util.CheckVersionCode;
import com.moxi.haierc.util.Utils;
import com.moxi.updateapp.UpdateUtil;
import com.mx.mxbase.base.BaseActivity;
import com.mx.mxbase.constant.Constant;
import com.mx.mxbase.http.MXHttpHelper;
import com.mx.mxbase.utils.DeleteDDReaderBook;
import com.mx.mxbase.utils.MXUamManager;
import com.mx.mxbase.utils.Toastor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;

//import com.onyx.android.sdk.device.DeviceInfo;

/**
 * 新的海尔C主界面
 * Created by King on 2017/7/6.
 */

public class NewMainActivity extends BaseActivity implements View.OnClickListener, UpdateCallBack {

    @Bind(R.id.grid_view_new_main_recent_reading)
    GridView gridViewRecentReading;
    @Bind(R.id.new_main_menu)
    GridView gridViewMainMenu;
    @Bind(R.id.tv_new_main_enter_library)
    TextView tvEnter;
    @Bind(R.id.icon)
    ImageView imageViewIcon;
    @Bind(R.id.adversing)
    ImageView adversing;
    @Bind(R.id.rl_advertisement)
    RelativeLayout rlAdcertise;
    @Bind(R.id.tv_new_main_username)
    TextView tvUsername;
    @Bind(R.id.new_main_week)
    TextView tvWeek;
    @Bind(R.id.new_main_day)
    TextView tvDay;
    @Bind(R.id.tv_re_load_recent_book)
    TextView tvReLoad;

    private MXHttpHelper httpHelper;
    private boolean NEED_REGET = false;
    private String stuFlag = "";
    private ActivityManager mActivityManager = null;
    private List<BookStoreFile> listRecent = new ArrayList<>();
    private RecentReadingAdapter adapter;
    private int[] res = new int[]{
            R.mipmap.mx_img_new_avatar_0,
            R.mipmap.mx_img_new_avatar_1,
            R.mipmap.mx_img_new_avatar_2,
            R.mipmap.mx_img_new_avatar_3};

    private MainMenuAdapter mainMenuAdapter;
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
        httpHelper = MXHttpHelper.getInstance(this);
        mActivityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        initView();
    }

    /**
     * 初始化view
     */
    private void initView() {
        stuFlag = share.getString("flag_version_stu");
        if (stuFlag.equals("")) {
            stuFlag = "标准版";
        }

        tvEnter.setOnClickListener(this);
        imageViewIcon.setOnClickListener(this);
        rlAdcertise.setOnClickListener(this);
        tvReLoad.setOnClickListener(this);

        mainMenuAdapter = new MainMenuAdapter(this, gridViewMainMenu, stuFlag);
        gridViewMainMenu.setAdapter(mainMenuAdapter);
        gridViewMainMenu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        if (stuFlag.equals("教育版")) {
                            if (Utils.isSDCardCanReadAndWrite()) {
                                try {
                                    Intent bookstore = new Intent();
                                    bookstore.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    ComponentName cnSound = new ComponentName("com.moxi.bookstore", "com.moxi.bookstore.activity.AllCatetoryActivity");
                                    bookstore.setComponent(cnSound);
                                    bookstore.putExtra("flag_version_stu", stuFlag);
                                    startActivity(bookstore);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    Toastor.showToast(NewMainActivity.this, "启动失败，请检测是否正常安装");
                                    new UpdateUtil(NewMainActivity.this, NewMainActivity.this).checkInstall("com.moxi.bookstore");
                                }
                            } else {
                                Toastor.showToast(NewMainActivity.this, "sd卡未准备好");
                            }
                        } else {
                            try {
                                Intent calendar = new Intent();
                                calendar.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                ComponentName cnSound = new ComponentName("com.moxi.calendar", "com.moxi.calendar.MainActivity");
                                calendar.setComponent(cnSound);
                                startActivity(calendar);
                            } catch (Exception e) {
                                e.printStackTrace();
                                Toastor.showToast(NewMainActivity.this, "启动失败，请检测是否正常安装");
                                new UpdateUtil(NewMainActivity.this, NewMainActivity.this).checkInstall("com.moxi.writeNote");
                            }
                        }
                        break;
                    case 1:
                        try {
                            Intent write = new Intent();
                            write.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            ComponentName cnSound = new ComponentName("com.moxi.writeNote", "com.moxi.writeNote.MainActivity");
                            write.setComponent(cnSound);
                            startActivity(write);
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toastor.showToast(NewMainActivity.this, "启动失败，请检测是否正常安装");
                            new UpdateUtil(NewMainActivity.this, NewMainActivity.this).checkInstall("com.moxi.writeNote");
                        }
                        break;
                    case 2:
                        if (stuFlag.equals("教育版")) {
                            try {
                                Intent exams = new Intent();
                                exams.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                ComponentName cnSound = new ComponentName("com.moxi.exams", "com.moxi.haierexams.activity.MXPracticeActivity");
                                exams.setComponent(cnSound);
                                startActivity(exams);
                            } catch (Exception e) {
                                e.printStackTrace();
                                Toastor.showToast(NewMainActivity.this, "启动失败，请检测是否正常安装");
                                new UpdateUtil(NewMainActivity.this, NewMainActivity.this).checkInstall("com.moxi.exams");
                            }
                        } else {
                            if (Utils.isSDCardCanReadAndWrite()) {
                                try {
                                    Intent bookstore = new Intent();
                                    bookstore.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    ComponentName cnSound = new ComponentName("com.moxi.bookstore", "com.moxi.bookstore.activity.AllCatetoryActivity");
                                    bookstore.setComponent(cnSound);
                                    bookstore.putExtra("flag_version_stu", stuFlag);
                                    startActivity(bookstore);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    Toastor.showToast(NewMainActivity.this, "启动失败，请检测是否正常安装");
                                    new UpdateUtil(NewMainActivity.this, NewMainActivity.this).checkInstall("com.moxi.bookstore");
                                }
                            } else {
                                Toastor.showToast(NewMainActivity.this, "sd卡未准备好");
                            }
                        }
                        break;
                    case 3:
                        if (stuFlag.equals("教育版")) {//字典
                            try {
                                Intent music = new Intent();
                                ComponentName musicOther = new ComponentName("com.onyx.dict", "com.onyx.dict.activity.DictMainActivity");
                                music.setComponent(musicOther);
                                startActivity(music);
                            } catch (Exception e) {
                                e.printStackTrace();
                                Toastor.showToast(NewMainActivity.this, "启动失败，请检测是否正常安装");
                            }
                        } else {//音乐
                            try {
                                Intent music = new Intent();
                                ComponentName musicOther = new ComponentName("com.android.music", "com.android.music.MusicBrowserActivity");
                                music.setComponent(musicOther);
                                startActivity(music);
                            } catch (Exception e) {
                                e.printStackTrace();
                                Toastor.showToast(NewMainActivity.this, "启动失败，请检测是否正常安装");
                            }
                        }
                        break;
                    case 4:
                        //文件管理
                        if (Utils.isSDCardCanReadAndWrite()) {
                            try {
                                Intent fileGm = new Intent();
                                fileGm.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                ComponentName cnSound = new ComponentName("com.moxi.filemanager", "com.moxi.filemanager.FileManagerActivity");
                                fileGm.setComponent(cnSound);
                                startActivity(fileGm);
                            } catch (Exception e) {
                                e.printStackTrace();
                                Toastor.showToast(NewMainActivity.this, "启动失败，请检测是否正常安装");
                                new UpdateUtil(NewMainActivity.this, NewMainActivity.this).checkInstall("com.moxi.filemanager");
                            }
                        } else {
                            Toastor.showToast(NewMainActivity.this, "sd卡未准备好");
                        }
                        break;
                    case 5:
                        //设置
                        startActivity(new Intent(NewMainActivity.this, MXNewSettingActivity.class));
                        break;
                    case 6:
                        if (stuFlag.equals("教育版")) {//课程表
                            try {
                                Intent table = new Intent();
                                table.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                ComponentName cnSound = new ComponentName("com.moxi.timetable", "com.mx.timetable.activity.MXTimeTablesActivity");
                                table.setComponent(cnSound);
                                startActivity(table);
                            } catch (Exception e) {
                                e.printStackTrace();
                                Toastor.showToast(NewMainActivity.this, "启动失败，请检测是否正常安装");
                                new UpdateUtil(NewMainActivity.this, NewMainActivity.this).checkInstall("com.moxi.timetable");
                            }
                        } else {//图库
                            try {
                                Intent gallery = new Intent();
                                ComponentName galleryOther = new ComponentName("com.android.gallery", "com.android.camera.GalleryPicker");
                                gallery.setComponent(galleryOther);
                                startActivity(gallery);
                            } catch (Exception e) {
                                e.printStackTrace();
                                Toastor.showToast(NewMainActivity.this, "启动失败，请检测是否正常安装");
                            }
                        }
                        break;
                    case 7:
                        //应用
                        Intent app = new Intent();
                        ComponentName cnApp = new ComponentName("com.onyx", "com.onyx.content.browser.activity.ApplicationsActivity");
                        app.setComponent(cnApp);
                        startActivity(app);
                        break;
                    default:
                        break;
                }
            }
        });

        listRecent = getrecentlyRead();
        adapter = new RecentReadingAdapter(this, gridViewRecentReading, listRecent);
        gridViewRecentReading.setAdapter(adapter);
        gridViewRecentReading.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (Utils.isFastClick()) {
                    String filePath = listRecent.get(position).getFilePath();
                    continueReader(filePath);
                }
            }
        });
        IntentFilter filter = new IntentFilter("com.moxi.broadcast.external.action");
        localBroadcastManager.registerReceiver(external, filter);
    }

    /**
     * 获得最近阅读书籍路径
     */
    public List<BookStoreFile> getrecentlyRead() {
        List<BookStoreFile> files = new ArrayList<>();
        ContentResolver contentResolver = getContentResolver();
        Uri selecturi = Uri.parse("content://com.moxi.bookstore.provider.RecentlyProvider/Recently");
        Cursor cursor = contentResolver.query(selecturi, null, null, null, null);
        if (cursor != null && cursor.getCount() > 0)
            while (cursor.moveToNext()) {
                dialogShowOrHide(false, "请稍后...");
                try {
                    gridViewRecentReading.setVisibility(View.VISIBLE);
                    tvReLoad.setVisibility(View.GONE);
                    BookStoreFile info = getModel(cursor);
                    files.add(info);
                } catch (Exception e) {
                    e.printStackTrace();
                    gridViewRecentReading.setVisibility(View.GONE);
                    tvReLoad.setVisibility(View.VISIBLE);
                }
            }
        if (cursor != null) cursor.close();
        return files;
    }

    private BookStoreFile getModel(Cursor cursor) {
        /** 索引id */
        long id = cursor.getLong(0);
        /** 文件保存路径 */
        String filePath = cursor.getString(1);
        /** 文件图片路径 */
        String photoPath = cursor.getString(2);
        /** 文件排序索引 */
        long _index = cursor.getInt(3);
        /** 文件全拼 */
        String fullPinyin = cursor.getString(4);
        /**是否为当当书记**/
        int isDdBook = cursor.getInt(6);
        /**书籍图片*/
        String bookImageUrl = cursor.getString(7);

        BookStoreFile model = new BookStoreFile();
        model.id = id;
        model.filePath = filePath;
        model.photoPath = photoPath;
        model._index = _index;
        model.fullPinyin = fullPinyin;
        model.isDdBook = isDdBook;
        model.bookImageUrl = bookImageUrl;
        return model;
    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    BroadcastReceiver external = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            listRecent = getrecentlyRead();
            adapter.dataChange(listRecent);
        }
    };

    @Override
    public void onActivityResumed(Activity activity) {

        stuFlag = share.getString("flag_version_stu");
        if (stuFlag.equals("")) {
            stuFlag = "标准版";
        }
        mainMenuAdapter.changeVersion(stuFlag);
        if (NEED_REGET) {
            listRecent = getrecentlyRead();
            adapter.dataChange(listRecent);
            NEED_REGET = false;
        }

        new DeleteDDReaderBook(NewMainActivity.this, false, null).execute();
//        getRunningAppProcessInfo();
        getUserInfo(MXUamManager.queryUser(this));
        CheckVersionCode.getInstance(this).checkframework(this);
//        DeviceInfo.currentDevice.showSystemStatusBar(this);
        setDate();
        getADData();
        getHandler().postDelayed(new Runnable() {
            @Override
            public void run() {
                openOrOff(true);
            }
        },1000);
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

    /**
     * 获取用户信息
     *
     * @param appSession 用户session
     */
    private void getUserInfo(String appSession) {
        HashMap<String, String> user = new HashMap<>();
        if ("".equals(appSession)) {
            imageViewIcon.setImageResource(R.mipmap.img_mx_new_defate_icon);
            tvUsername.setText("未登录");
        } else {
            user.put("appSession", appSession);
            httpHelper.postStringBack(2001, Constant.GET_USER_INFO, user, getHandler(), UserModel.class);
        }
    }

    private void getADData() {
        Glide.with(this)
                .load(Constant.GET_ADVSING2)
                .error(com.mx.mxbase.R.mipmap.error)
                .crossFade()
                .priority(Priority.NORMAL) //下载的优先级
                //all:缓存源资源和转换后的资源 none:不作任何磁盘缓存
                //source:缓存源资源   result：缓存转换后的资源
                .diskCacheStrategy(DiskCacheStrategy.ALL) //缓存策略
                .into(adversing);
    }

    private String[] pricessNames = new String[]{"com.neverland.oreader", "com.onyx.reader", "com.moxi.bookstore", "com.moxi.filemanager", "com.moxi.exams"};

    // 获得系统进程信息
    private void getRunningAppProcessInfo() {
        for (int i = 0; i < pricessNames.length; i++) {
            mActivityManager.killBackgroundProcesses(pricessNames[i]);
        }
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
        return R.layout.activity_new_main;
    }

    @Override
    public void onClick(View v) {
        if (!Utils.isFastClick()) {
            return;
        }
        switch (v.getId()) {
            case R.id.tv_new_main_enter_library:
                if (Utils.isSDCardCanReadAndWrite()) {
                    try {
                        Intent sound = new Intent();
                        sound.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        ComponentName cnSound = new ComponentName("com.moxi.bookstore", "com.moxi.bookstore.activity.bookManager.MXBookStoreActivity");
                        sound.setComponent(cnSound);
                        sound.putExtra("flag_version_stu", stuFlag);
                        startActivity(sound);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toastor.showToast(this, "启动失败，请检测是否正常安装");
                        new UpdateUtil(this, this).checkInstall("com.moxi.bookstore");
                    }
                } else {
                    Toastor.showToast(this, "sd卡未准备好");
                }
                break;
            case R.id.icon://用户登录
                try {
                    Intent sound = new Intent();
                    sound.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    ComponentName cnSound = new ComponentName("com.moxi.user", "com.mx.user.activity.MXLoginActivity");
                    sound.setComponent(cnSound);
                    sound.putExtra("flag_version_stu", stuFlag);
                    startActivity(sound);
                } catch (Exception e) {
                    e.printStackTrace();
                    Toastor.showToast(this, "启动失败，请检测是否正常安装");
                    new UpdateUtil(this, this).checkInstall("com.moxi.user");
                }
                break;
            case R.id.tv_re_load_recent_book:
                if (Utils.isSDCardCanReadAndWrite()) {
                    try {
                        Intent sound = new Intent();
                        sound.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        ComponentName cnSound = new ComponentName("com.moxi.bookstore", "com.moxi.bookstore.activity.bookManager.MXBookStoreActivity");
                        sound.setComponent(cnSound);
                        sound.putExtra("flag_version_stu", stuFlag);
                        startActivity(sound);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toastor.showToast(this, "启动失败，请检测是否正常安装");
                        new UpdateUtil(this, this).checkInstall("com.moxi.bookstore");
                    }
                } else {
                    Toastor.showToast(this, "sd卡未准备好");
                }
//                tvReLoad.setVisibility(View.GONE);
//                dialogShowOrHide(true, "请稍后...");
//                listRecent = getrecentlyRead();
//                adapter.dataChange(listRecent);
                break;
            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {
    }

    @Override
    public void needUpdate(boolean need) {
        if (isfinish)return;
        mainMenuAdapter.changeAdapter(need);
    }

}
