package com.moxi.haierc.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.moxi.haierc.R;
import com.moxi.haierc.adapter.DynamicMainMenuAdapter;
import com.moxi.haierc.adapter.RecentReadingAdapter;
import com.moxi.haierc.callback.UpdateCallBack;
import com.moxi.haierc.model.Advertisement;
import com.moxi.haierc.model.AppModel;
import com.moxi.haierc.model.BookStoreFile;
import com.moxi.haierc.model.UserInfoModel;
import com.moxi.haierc.ports.OnClickCallBack;
import com.moxi.haierc.util.CheckVersionCode;
import com.moxi.haierc.util.IndexApplicationUtils;
import com.moxi.haierc.util.Utils;
import com.moxi.haierc.view.ApplicationSelectView;
import com.moxi.updateapp.DownLoadSystemActivity;
import com.moxi.updateapp.UpdateUtil;
import com.moxi.updateapp.model.SystemOtaModel;
import com.mx.mxbase.base.BaseActivity;
import com.mx.mxbase.constant.APPLog;
import com.mx.mxbase.constant.Constant;
import com.mx.mxbase.utils.DeleteDDReaderBook;
import com.mx.mxbase.utils.GlideUtils;
import com.mx.mxbase.utils.GsonTools;
import com.mx.mxbase.utils.Log;
import com.mx.mxbase.utils.MXUamManager;
import com.mx.mxbase.utils.Toastor;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import okhttp3.Call;

/**
 * 新的海尔C主界面
 *
 * @modify 主界面功能模块动态改变
 * @modify_date 2017/10/16/13点54分
 * Created by King on 2017/7/6.
 */

public class DynamicMainActivity extends BaseActivity implements View.OnClickListener, UpdateCallBack {

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
    @Bind(R.id.ll_prent)
    LinearLayout llPrent;

    private boolean NEED_REGET = false;
    private List<BookStoreFile> listRecent = new ArrayList<>();
    private RecentReadingAdapter adapter;
    private int[] res = new int[]{
            R.mipmap.mx_img_new_avatar_0,
            R.mipmap.mx_img_new_avatar_1,
            R.mipmap.mx_img_new_avatar_2,
            R.mipmap.mx_img_new_avatar_3};

    private DynamicMainMenuAdapter mainMenuAdapter;
    private ApplicationSelectView popSelectView;
    private int oldApp = -1;
    private IntentFilter intentFilter;
    private LocalReceiver localReceiver;
    private LocalBroadcastManager localBroadcastManager;
    private String stuFlag = "";

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        initView();
        intentFilter = new IntentFilter();
        intentFilter.addAction("com.moxi.haierc.UPDATE_APPLICATION");
        localReceiver = new LocalReceiver();
        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        localBroadcastManager.registerReceiver(localReceiver, intentFilter);

        stuFlag = share.getString("flag_version_stu");
        if (stuFlag.equals("")) {
            stuFlag = "标准版";
        }
    }

    private ArrayList<AppModel> listTemp;

    private void getAppModels(String version) {
        listTemp = IndexApplicationUtils.getInstance(this).getCurrentShowApps(version);
        mainMenuAdapter = new DynamicMainMenuAdapter(this, gridViewMainMenu, listTemp);
        gridViewMainMenu.setAdapter(mainMenuAdapter);
        gridViewMainMenu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String packgeStr=listTemp.get(position).getAppPackageName().trim();
                try {
                    if (packgeStr.startsWith("com.moxi.")) {
                        Intent calendar = new Intent();
                        calendar.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        APPLog.e("启动",listTemp.get(position).getAppLauncherClass().trim());
                        ComponentName cnSound = new ComponentName(packgeStr, listTemp.get(position).getAppLauncherClass().trim().replace("..","."));
                        calendar.setComponent(cnSound);
                        startActivity(calendar);
                    }else {
                        PackageManager packageManager = getPackageManager();
                        Intent intent = new Intent();
                        intent = packageManager.getLaunchIntentForPackage(packgeStr);
                        startActivity(intent);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(DynamicMainActivity.this, "启动失败，请检测是否正常安装", Toast.LENGTH_SHORT).show();
                }
            }
        });
        gridViewMainMenu.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if (listTemp.get(position).getIsCanReplace() == 1) {
                    oldApp = position;
                    popSelectView = new ApplicationSelectView(DynamicMainActivity.this, new PopClickCallBack());
                    popSelectView.showAtLocation(llPrent, Gravity.CENTER, 0, 0);
                } else {
                    Toast.makeText(DynamicMainActivity.this, "此处无法替换", Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        });
        CheckVersionCode.getInstance(this).checkframework(this);
    }

    /**
     * 弹出窗口选中回调
     */
    class PopClickCallBack implements OnClickCallBack {

        @Override
        public void onClickCallBack(AppModel newAppModel) {
            AppModel oldAppModel = listTemp.get(oldApp);
            oldAppModel.setIsShow(0);
            oldAppModel.setPosition(-1);
            oldAppModel.setUpdateTime(System.currentTimeMillis());
            newAppModel.setPosition(oldApp);
            newAppModel.setIsShow(1);
            newAppModel.setUpdateTime(System.currentTimeMillis());

            IndexApplicationUtils.getInstance(DynamicMainActivity.this).updateAppInfo(stuFlag, oldAppModel);
            IndexApplicationUtils.getInstance(DynamicMainActivity.this).updateAppInfo(stuFlag, newAppModel);
            getAppModels(stuFlag);
        }
    }

    /**
     * 初始化view
     */
    private void initView() {
        tvEnter.setOnClickListener(this);
        imageViewIcon.setOnClickListener(this);
        rlAdcertise.setOnClickListener(this);
        tvReLoad.setOnClickListener(this);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        localBroadcastManager.unregisterReceiver(localReceiver);
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
        if (NEED_REGET) {
//            ReadManagerPicUtils.getInstance().clearMetdata();
            listRecent = getrecentlyRead();
            adapter.dataChange(listRecent);
            NEED_REGET = false;
            stuFlag = share.getString("flag_version_stu");
            if (stuFlag.equals("")) {
                stuFlag = "标准版";
            }
        }

        new DeleteDDReaderBook(DynamicMainActivity.this, false, null).execute();
        getUserInfo();
        setDate();
        getADData();
        getAppModels(stuFlag);
//        getHandler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                //TODO 开启电磁屏
////                openOrOff(true);
//            }
//        }, 1000);
//        new AsyncInitApplications(this).execute(new FinishCallBack() {
//            @Override
//            public void onFinish(Boolean finished) {
////                showToast("更新应用成功");
//            }
//        });
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

    private void getUserInfo() {
        String temp = MXUamManager.querUserBId(this);
        UserInfoModel userInfoModel = GsonTools.getPerson(temp, UserInfoModel.class);
        if (userInfoModel != null) {
            tvUsername.setText(userInfoModel.getResult().getMember().getName());
            int aaa = userInfoModel.getResult().getMember().getHeadPortrait();
            if (aaa != -99) {
                imageViewIcon.setImageResource(res[aaa]);
            } else {
                imageViewIcon.setImageResource(res[0]);
            }
        }
    }

    private void getADData() {
//        DisplayImageOptions options = new DisplayImageOptions.Builder()
//                .showImageOnLoading(R.mipmap.img_new_main_ad)
//                .showImageForEmptyUri(R.mipmap.img_new_main_ad)
//                .showImageOnFail(R.mipmap.img_new_main_ad)
////                .cacheInMemory(true)//设置下载的图片是否缓存在内存中
////                .cacheOnDisc(true)//设置下载的图片是否缓存在SD卡中
//                .imageScaleType(ImageScaleType.EXACTLY)
//                .bitmapConfig(Bitmap.Config.RGB_565)
//                .displayer(new FadeInBitmapDisplayer(300)).build();

        OkHttpUtils.post().url(Constant.GET_ADVSING2_JSON).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {

            }

            @Override
            public void onResponse(String response, int id) {
                if (isfinish||DynamicMainActivity.this.isFinishing())
                    return;
                try {
                    final Advertisement advertisement = GsonTools.getPerson(response, Advertisement.class);
                    if (advertisement != null) {
                        GlideUtils.getInstance().loadGreyImage(DynamicMainActivity.this, adversing, Constant.HTTP_HOST + advertisement.getResult().getImageUrl());
                        adversing.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                try {
                                    if (advertisement.getResult().getExtLink() != null) {
                                        Uri uri = Uri.parse(advertisement.getResult().getExtLink());
                                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                        startActivity(intent);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
//                ImageLoader.getInstance().displayImage(Constant.GET_ADVSING2, adversing, options);
                    }else{
                        Log.i("advertisment is null");
                    }
                }catch (Exception e){}

            }
        });
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
        return R.layout.activity_dynamic_main;
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
                        ComponentName cnSound = new ComponentName("com.moxi.bookstore", "com.moxi.bookstore.activity.bookManager.MXStacksActivity");
                        sound.setComponent(cnSound);
                        sound.putExtra("flag_version_stu", "");
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
//                try {
//                    Intent sound = new Intent();
//                    sound.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                    ComponentName cnSound = new ComponentName("com.moxi.user", "com.mx.user.activity.MXLoginActivity");
//                    sound.setComponent(cnSound);
//                    sound.putExtra("flag_version_stu", "");
//                    startActivity(sound);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    Toastor.showToast(this, "启动失败，请检测是否正常安装");
//                    new UpdateUtil(this, this).checkInstall("com.moxi.user");
//                }
                SystemOtaModel model=new SystemOtaModel();
                model.describe="更新描述信息";
                model.MD5="5af2a0049f545407fc485f32";
                model.url="http://moxiota.oss-cn-shenzhen.aliyuncs.com/Haier.H9.2018-05-08.zip";
                DownLoadSystemActivity.startDownLoadSystem(this,model);
                break;
            case R.id.tv_re_load_recent_book:
                if (Utils.isSDCardCanReadAndWrite()) {
                    try {
                        Intent sound = new Intent();
                        sound.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        ComponentName cnSound = new ComponentName("com.moxi.bookstore", "com.moxi.bookstore.activity.bookManager.MXBookStoreActivity");
                        sound.setComponent(cnSound);
                        sound.putExtra("flag_version_stu", "");
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
        if (mainMenuAdapter != null) {
            mainMenuAdapter.changeAdapter(need);
        }
    }

    class LocalReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            getAppModels(stuFlag);
        }
    }
}
