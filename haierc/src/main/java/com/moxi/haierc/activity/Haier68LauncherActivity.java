package com.moxi.haierc.activity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.PersistableBundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.moxi.haierc.R;
import com.moxi.haierc.adapter.RecentReadingAdapter;
import com.moxi.haierc.application.HaiercApplication;
import com.moxi.haierc.callback.UpdateCallBack;
import com.moxi.haierc.model.BookStoreFile;
import com.moxi.haierc.model.StopOrPowerPhoto;
import com.moxi.haierc.ports.FinishCallBack;
import com.moxi.haierc.service.StartOrOffService;
import com.moxi.haierc.util.CheckVersionCode;
import com.moxi.haierc.util.RootCmd;
import com.moxi.haierc.util.Utils;
import com.moxi.updateapp.UpdateUtil;
import com.mx.mxbase.base.BaseActivity;
import com.mx.mxbase.constant.APPLog;
import com.mx.mxbase.constant.Constant;
import com.mx.mxbase.constant.LocationBookReadProgressUtils;
import com.mx.mxbase.interfaces.LocationInfoListener;
import com.mx.mxbase.model.LocationBookInfo;
import com.mx.mxbase.utils.FileUtils;
import com.mx.mxbase.utils.Log;
import com.mx.mxbase.utils.SharePreferceUtil;
import com.mx.mxbase.utils.StringUtils;
import com.mx.mxbase.utils.ToastUtils;
import com.mx.mxbase.utils.Toastor;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import okhttp3.Call;

public class Haier68LauncherActivity extends BaseActivity implements View.OnClickListener, UpdateCallBack {
    private static final String bookName = "快速使用指南.pdf";

    @Override
    protected int getMainContentViewId() {
        return R.layout.activity_haier68_launcher;
    }

    @Bind(R.id.tv_new_main_enter_library)
    TextView tv_new_main_enter_library;
    @Bind(R.id.grid_view_new_main_recent_reading)
    GridView gridViewRecentReading;
    @Bind(R.id.tv_re_load_recent_book)
    TextView tvReLoad;

    @Bind(R.id.main_daohan_group)
    LinearLayout main_daohan_group;
    @Bind(R.id.test_item)
    LinearLayout test_item;
    @Bind(R.id.bottom_1)
    LinearLayout bottom_1;
    @Bind(R.id.bottom_2)
    LinearLayout bottom_2;
    @Bind(R.id.bottom_3)
    LinearLayout bottom_3;
    @Bind(R.id.bottom_4)
    LinearLayout bottom_4;
    @Bind(R.id.bottom_5)
    LinearLayout bottom_5;
    @Bind(R.id.update_tv)
    TextView update_tv;

    private RecentReadingAdapter adapter;
    private List<BookStoreFile> listRecent = new ArrayList<>();
    private String updatePowerOrStopPhoto = null;
    private String bookPath = "";
    private int clickPosition = -1;

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

        tv_new_main_enter_library.setOnClickListener(this);
        tvReLoad.setOnClickListener(this);

        bottom_1.setOnClickListener(this);
        bottom_2.setOnClickListener(this);
        bottom_3.setOnClickListener(this);
        bottom_4.setOnClickListener(this);
        bottom_5.setOnClickListener(this);
        test_item.setOnClickListener(this);

        gridViewRecentReading.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (Utils.isFastClick()) {
                    String filePath = listRecent.get(position).getFilePath();
                    if (position == 0) {
                        File file = new File(filePath);
                        if (!file.exists() || !file.canRead() && file.length() <= 0) {
                            setBook();
                            ToastUtils.getInstance().showToastShort("文件准备中...");
                            return;
                        }
                    }
                    continueReader(filePath);
                    clickPosition = position;
                    if (position == 0) listRecent.clear();
                }
            }
        });

        LocationBookReadProgressUtils.getInstance(this).setListeners(infoListener);
    }

    private void clearDownlaod(){
        try {
            final long time=SharePreferceUtil.getInstance(this).getLong("clearDownlaod");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    if (Math.abs(time-System.currentTimeMillis())>24*60*60*1000){
                        String DOWN_SDK_DIR_NAME = "update";
                        String DOWN_SDK_DIR =
                                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                                        + File.separator + DOWN_SDK_DIR_NAME + File.separator;
                        StringUtils.deleteFile(DOWN_SDK_DIR);
                        APPLog.e("删除对应文件");
                        getHandler().post(new Runnable() {
                            @Override
                            public void run() {
                                APPLog.e("getHandler");
                                SharePreferceUtil.getInstance(Haier68LauncherActivity.this).setCache("clearDownlaod",System.currentTimeMillis());
                            }
                        });
                    }

                }
            }).start();
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    LocationInfoListener infoListener = new LocationInfoListener() {
        @Override
        public void onBackInfo(LocationBookInfo info) {
            if (adapter != null && gridViewRecentReading != null) {
                adapter.updateSelect(info, gridViewRecentReading);
            }
        }
    };
private boolean isFirst=true;

    private void initRecentlyRead() {
        if (StringUtils.isNull(bookPath)) {
            if (!StringUtils.getSDPath("Books").isEmpty()) {
                bookPath = StringUtils.getSDPath("Books") + bookName;
            }
        }
        List<BookStoreFile> list = getrecentlyRead();
        int deleteindex = -1;
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).filePath.equals(bookPath)) {
                //如果有不一样的就直接返回
                deleteindex = i;
                break;
            }
        }
        if (deleteindex >= 0) {
            list.remove(deleteindex);
        }
        if (!StringUtils.isNull(bookPath)) {
            BookStoreFile ff = new BookStoreFile();
            ff.filePath = bookPath;
            ff.isDdBook = 0;
            list.add(0, ff);
        }
        if (list.size()>8)list.remove(8);
        if (list.size() != listRecent.size()) {
//            listRecent.addAll(list);
        } else {
            boolean isreturn = true;
            for (int i = 0; i < list.size(); i++) {
                if (!listRecent.get(i).filePath.equals(list.get(i).filePath)) {
                    //如果有不一样的就直接返回

                    isreturn = false;
                    break;
                }
            }
            if (isreturn && clickPosition == -1) return;
        }
        LocationBookReadProgressUtils.getInstance(this).ClearData();
        listRecent.clear();
        listRecent.addAll(list);
        if (listRecent.size() == 0) {
            gridViewRecentReading.setVisibility(View.INVISIBLE);
            tvReLoad.setVisibility(View.VISIBLE);
        } else {
            tvReLoad.setVisibility(View.INVISIBLE);
        }
        if (adapter == null) {
            adapter = new RecentReadingAdapter(this, gridViewRecentReading, listRecent);
            gridViewRecentReading.setAdapter(adapter);
        } else {
            adapter.dataChange(listRecent);
        }
        clickPosition = -1;

        if (isFirst){
            isFirst=false;
            if (listRecent.size()<=1){
                getHandler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        initRecentlyRead();

                    }
                },2000);
            }
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
                showToast("阅读文件异常，请确认文件是否存在！！");
            }
        } else {
            showToast("还没有阅读记录哟！");
        }
    }

    @Override
    public void onClick(View v) {
        //点击过于频繁限制
        if (!Utils.isFastClick()) {
            return;
        }
        switch (v.getId()) {
            case R.id.tv_new_main_enter_library://本地书库
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
            case R.id.tv_re_load_recent_book://没有获取到数据
                initRecentlyRead();
                break;
            case R.id.bottom_1://书城
                startActivity("com.moxi.bookstore", "com.moxi.bookstore.activity.OnlieBookCityActivity");
//               APPLog.e(" RootCmd.gethwtV()", RootCmd.gethwtV());
                break;
            case R.id.test_item:
                dialogShowOrHide(true,"更新屏幕固件");
                String path = StringUtils.getSaveSystemCorrelationPhotoFloder()+"firmware_name.bin";
                String tagetPath=StringUtils.getSDPath()+"firmware_name.bin";
                copyFile(tagetPath, path);
                //移动完成
                RootCmd.updateFirmware(path, new FinishCallBack() {
                    @Override
                    public void onFinish(final Boolean finished) {
                        //切换到主线程
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                dialogShowOrHide(false,"更新屏幕固件");
                                ToastUtils.getInstance().showToastShort("升级"+(finished?"成功":"失败"));
                            }
                        });
                    }
                });
                break;
            case R.id.bottom_2://文件
                startActivity("com.moxi.filemanager", "com.moxi.filemanager.FileManagerActivity");
                break;
            case R.id.bottom_3://手写
                if (!startActivity("com.moxi.writeNote", "com.moxi.writeNote.MainActivity"))
                    startActivity("com.example.handwrite", "com.example.handwrite.MainActivity");
//                ToastUtils.getInstance().showToastShort("敬请期待！！");
                break;
            case R.id.bottom_4://应用
                startActivity("com.moxi.systemapp", "com.moxi.systemapp.activity.AppsActivity");
                break;
            case R.id.bottom_5://设置
                startActivity("com.moxi.haierc", "com.moxi.haierc.activity.MXNewSettingActivity");
                break;
            default:
                break;

        }
    }

    private boolean startActivity(String packgeStr, String launcherClass) {
        try {
            Intent calendar = new Intent();
            calendar.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            ComponentName cnSound = new ComponentName(packgeStr, launcherClass.trim());
            calendar.setComponent(cnSound);
            String stuFlag = share.getString("flag_version_stu");
            if (stuFlag.equals("")) {
                stuFlag = "标准版";
            }
            //添加标记是标准版还是学生版本
            calendar.putExtra("flag_version_stu", stuFlag);
            //可以用contentprovider 或者Messager
            startActivity(calendar);
            return true;
        } catch (Exception e) {
            ToastUtils.getInstance().showToastShort("应用无法打开！！");
            return false;
        }

    }


    @Override
    public void needUpdate(boolean need) {
        if (isfinish) return;
        if (need) update_tv.setVisibility(View.VISIBLE);
        else update_tv.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {
        Intent intent = new Intent(this, StartOrOffService.class);
        startService(intent);

        LocationBookReadProgressUtils.getInstance(this).onReadResume();
        //检查是否有更新
        CheckVersionCode.getInstance(this).checkframework(this);
        //删除没用用的当当书籍
//        new DeleteDDReaderBook(Haier68LauncherActivity.this, false, null).execute();
        //更新显示的书籍
        initRecentlyRead();
        //刷新底部控件
        getHandler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isFinishing())return;
                main_daohan_group.invalidate();
//                getWindow().getDecorView().invalidate();
            }
        }, 500);
        bookRead(false);
        if (Constant.CLENT_APP.equals("haier")) {
            getstopOrStartimg();
        }
        clearDownlaod();
    }

    private void bookRead(final boolean open) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                setBook();
            }
        }).start();
    }

    private void setBook() {
        if (StringUtils.getSDPath("Books").equals("")) return;
        String instructionPath = StringUtils.getSDPath("Books") + bookName;
        if (!new File(instructionPath).exists()) {
            FileUtils.copyAssets(HaiercApplication.applicationContext, bookName, StringUtils.getSDPath("Books"));
        }
    }

    @Override
    public void onActivityPaused(Activity activity) {
        LocationBookReadProgressUtils.getInstance(this).onReadPasue();
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
//        unregisterReceiver(receiver);
        LocationBookReadProgressUtils.getInstance(this).onDestory();
    }

    /**
     * 获得最近阅读书籍路径
     */
    public List<BookStoreFile> getrecentlyRead() {
        List<BookStoreFile> files = new ArrayList<>();
        try {
            ContentResolver contentResolver = getContentResolver();
            Uri selecturi = Uri.parse("content://com.moxi.bookstore.provider.RecentlyProvider/Recently");
            Cursor cursor = contentResolver.query(selecturi, null, null, null, null);
            if (cursor != null && cursor.getCount() > 0)
                while (cursor.moveToNext()) {
//                dialogShowOrHide(false, "请稍后...");
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
            tvReLoad.setOnClickListener(null);
        } catch (Exception e) {
            tvReLoad.setOnClickListener(this);
        }
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
        String progress = "";
        try {
            progress = cursor.getString(8);
        } catch (Exception e) {

        }


        BookStoreFile model = new BookStoreFile();
        model.id = id;
        model.filePath = filePath;
        model.photoPath = photoPath;
        model._index = _index;
        model.fullPinyin = fullPinyin;
        model.isDdBook = isDdBook;
        model.bookImageUrl = bookImageUrl;
        model.progress = progress;

        return model;
    }

    private void getstopOrStartimg() {
        if (htwtim==0) {
            htwtim = SharePreferceUtil.getInstance(this).getLong("htwtime");
        }
        long cu=System.currentTimeMillis();
        //一个小时查看一次看是否有需要更新的屏幕数据
        if (Math.abs(cu-htwtim)>7200000) {
            SharePreferceUtil.getInstance(this).setCache("htwtime", System.currentTimeMillis());
            if (StringUtils.isNull(StringUtils.getSDCardPath())) return;
            //判断是可以读取的
            getHtwString();

            OkHttpUtils.post().url(Constant.GET_POWER_AND_STOP_PHOTO).build().execute(new StringCallback() {
                @Override
                public void onError(Call call, Exception e, int id) {
                }

                @Override
                public void onResponse(String response, int id) {
                    if (isfinish || Haier68LauncherActivity.this.isFinishing())
                        return;
                    htwtim=System.currentTimeMillis();
                    APPLog.e("response", response);
                    try {
                        updatePowerOrStopPhoto = response;
                        StopOrPowerPhoto powerPhoto = JSON.parseObject(response, StopOrPowerPhoto.class);
                        StopOrPowerPhoto.ResultPhoto resultPhoto = powerPhoto.getUpdateImg(Haier68LauncherActivity.this);
                        if (resultPhoto == null) return;
                        if (!StringUtils.isNull(resultPhoto.getPowerImage())) {//关机图片
                            String pathstop = Constant.HTTP_HOST + resultPhoto.getPowerImage();
                            downloadFile(pathstop, StringUtils.getSDPath() + StringUtils.getSaveSystemCorrelationPhotoName(0));
                        }
                        if (!StringUtils.isNull(resultPhoto.getWaitImage())) {//待机图片
                            String pathpower = Constant.HTTP_HOST + resultPhoto.getWaitImage();
                            downloadFile(pathpower, StringUtils.getSDPath() + StringUtils.getSaveSystemCorrelationPhotoName(2));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }
    private long htwtim=0;

//    @Override
//    public boolean dispatchKeyEvent(KeyEvent event) {
//        return true;
//    }

    private void getHtwString() {

//        OkHttpUtils.post().url(Constant.GET_POWER_AND_STOP_PHOTO).build().execute(new StringCallback() {
//            @Override
//            public void onError(Call call, Exception e, int id) {
//            }
//
//            @Override
//            public void onResponse(String response, int id) {
//                if (isfinish || Haier68LauncherActivity.this.isFinishing())
//                    return;
//                try {
//
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        });
    }

    @Override
    public void onBackPressed() {
    }

    private void updateChomd(String tagPath) {
        try {
            String command = "chmod 777 " + tagPath;
            Runtime runtime = Runtime.getRuntime();
            Process proc = runtime.exec(command);
        } catch (IOException e) {
            APPLog.e("updateChomd", e.getMessage());
        }
    }

    private void downloadFile(String url, String tagetPath) {
        FinalHttp finalHttp = new FinalHttp();
        //中文转utf-8
        finalHttp.download(url, tagetPath, new AjaxCallBack<File>() {
            @Override
            public void onLoading(long count, long current) {
            }


            @Override
            public void onSuccess(File file) {
                super.onSuccess(file);
                if (isfinish || Haier68LauncherActivity.this.isfinish) {
                    StringUtils.deleteFile(file);
                    return;
                } else {
                    //移动图片
                    String path = StringUtils.getSaveSystemCorrelationPhotoFloder() + file.getName();
                    APPLog.e("move-to-path", path);
                    copyFile(file.getAbsolutePath(), path);
                    StopOrPowerPhoto.setPhotoString(Haier68LauncherActivity.this, updatePowerOrStopPhoto);
                }
            }

            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                APPLog.e(errorNo, strMsg);
                StringUtils.deleteFile(StringUtils.getSDPath() + StringUtils.getSaveSystemCorrelationPhotoName(0));
                StringUtils.deleteFile(StringUtils.getSDPath() + StringUtils.getSaveSystemCorrelationPhotoName(2));
            }
        });
    }
    private void downloadHWTFile(String url) {
        String tagetPath=StringUtils.getSDPath()+"firmware_name.bin";
        StringUtils.deleteFile(tagetPath);
        FinalHttp finalHttp = new FinalHttp();
        //中文转utf-8
        finalHttp.download(url, tagetPath, new AjaxCallBack<File>() {
            @Override
            public void onLoading(long count, long current) {

            }
            @Override
            public void onSuccess(File file) {
                super.onSuccess(file);
                if (isfinish || Haier68LauncherActivity.this.isfinish) {
                    StringUtils.deleteFile(file);
                    return;
                } else {
                    //移动图片
                    String path = StringUtils.getSaveSystemCorrelationPhotoFloder()+"firmware_name.bin";
                    APPLog.e("downloadHWTFilemove-to-path", path);
                    copyFile(file.getAbsolutePath(), path);
                    //移动完成
                    RootCmd.updateFirmware(path, new FinishCallBack() {
                        @Override
                        public void onFinish(final Boolean finished) {
                            //切换到主线程
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ToastUtils.getInstance().showToastShort("升级"+(finished?"成功":"失败"));
                                }
                            });
                        }
                    });
                }
            }

            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                Log.i(errorNo, strMsg);
                ToastUtils.getInstance().showToastShort("下载失败");
            }
        });
    }

    /**
     * 复制单个文件
     *
     * @param oldPath String 原文件路径 如：c:/fqf.txt
     * @param newPath String 复制后路径 如：f:/fqf.txt
     * @return boolean
     */
    private void copyFile(final String oldPath, final String newPath) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    int byteread = 0;
                    File oldfile = new File(oldPath);
                    if (oldfile.exists()) { //文件存在时
                        InputStream inStream = new FileInputStream(oldPath); //读入原文件
                        FileOutputStream fs = new FileOutputStream(newPath);
                        byte[] buffer = new byte[2048];
                        while ((byteread = inStream.read(buffer)) != -1) {
                            fs.write(buffer, 0, byteread);
                        }
                        fs.close();
                        inStream.close();
                        updateChomd(newPath);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    StringUtils.deleteFile(newPath);
                } finally {
                    StringUtils.deleteFile(oldPath);
                }
            }
        }).start();


    }

}
