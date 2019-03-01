package com.moxi.systemapp.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;

import com.moxi.systemapp.R;
import com.moxi.systemapp.adapter.AppsAdapter;
import com.moxi.systemapp.model.AppInfo;
import com.moxi.systemapp.utils.UninstallApp;
import com.mx.mxbase.base.BaseActivity;
import com.mx.mxbase.constant.APPLog;
import com.mx.mxbase.interfaces.InsureOrQuitListener;
import com.mx.mxbase.utils.StringUtils;
import com.mx.mxbase.utils.ToastUtils;
import com.mx.mxbase.view.SildeFrameLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.Bind;

/**
 * 应用列表
 */
public class AppsActivity extends BaseActivity implements View.OnClickListener, AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {
    private static List<String> showSystemAppS = new ArrayList<>();
    //教育版本与普通版本切换
    private  List<String> locationApp = new ArrayList<>();
    static {
        showSystemAppS.add("com.android.email");
        showSystemAppS.add("com.android.music");
        showSystemAppS.add("com.android.browser");
        showSystemAppS.add("com.netease.mobimail");
//        showSystemAppS.add("com.xrz.ebook.imagemanager");
//        showSystemAppS.add("com.android.settings");
    }
//{appLabel='作业帮一练', appIcon=android.graphics.drawable.BitmapDrawable@4217cfe8, intent=Intent { act=android.intent.action.MAIN cat=[android.intent.category.LAUNCHER]
// flg=0x10000000 pkg=com.zuoyebang.practice cmp=com.zuoyebang.practice/com.baidu.homework.activity.init.InitActivity },
    @Bind(R.id.silde_layout)
    SildeFrameLayout silde_layout;
    @Bind(R.id.back_item)
    GridView back_item;

    @Bind(R.id.last_page)
    ImageButton last_page;
    @Bind(R.id.show_index)
    TextView show_index;
    @Bind(R.id.next_page)
    ImageButton next_page;

    /**
     * 当前显示页数
     */
    private int pageIndex = 0;
    /**
     * 每页显示个数
     */
    private final int pageSize = 20;
    /**
     * 页面总页数
     */
    private int totalPage;
    private List<AppInfo> listData = new ArrayList<>();
    private List<AppInfo> middleModels = new ArrayList<>();
    private AppsAdapter adapter = null;

    @Override
    protected int getMainContentViewId() {
        return R.layout.activity_apps;
    }

    private BroadcastReceiver installReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (TextUtils.equals(intent.getAction(), "installSucess")) {
                redureshApp();
            }
        }
    };
    private String stuFlag="";

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("stuFlag",stuFlag);
        //  /system/media/bootanimation.zip C:\Users\xj\Desktop
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

        if (savedInstanceState!=null){
            stuFlag=savedInstanceState.getString("stuFlag");
        }else {
            stuFlag=getIntent().getStringExtra("flag_version_stu");
        }
        stuFlag = StringUtils.isNull(stuFlag)? "标准版" : stuFlag;
        locationApp.clear();
        if (stuFlag.contains("教育")) {//教育版本应用显示
            locationApp.add("com.moxi.calendar");
            locationApp.add("com.moxi.timetable");
            locationApp.add("com.moxi.exams");
            locationApp.add("com.moxi.bookreader");
            locationApp.add("com.zuoyebang.practice");
            locationApp.add("com.netease.mobimail");
        }else {//标准版本应用显示
            locationApp.add("com.netease.mobimail");
            locationApp.add("com.moxi.calendar");
        }

        last_page.setOnClickListener(this);
        next_page.setOnClickListener(this);
        silde_layout.setListener(sildeEventListener);
        back_item.setOnItemClickListener(this);
        back_item.setOnItemLongClickListener(this);

        IntentFilter filter = new IntentFilter();
        filter.addAction("installSucess");
        registerReceiver(installReceiver, filter);

    }

    private void redureshApp() {
        if (isfinish) return;
        if (listData == null)listData=new ArrayList<>();
        listData.clear();
        listData.addAll(scanInstallApp(this));
        changePage();
    }
    /**
     * 修改页面
     */
    private void changePage() {
        int size = listData.size();
        totalPage = (size / pageSize) + ((size % pageSize == 0) ? 0 : 1);
        if (totalPage <= pageIndex) {
            pageIndex = totalPage - 1;
        }
        initAdapter();
    }

    /**
     * 初始化adapter
     */
    private void initAdapter() {
        //修改当前页面的索引值
        middleModels.clear();
        if (totalPage > 0 && pageIndex >= 0) {
            if ((totalPage - 1) > pageIndex) {
                middleModels.addAll(listData.subList(pageSize * pageIndex, pageSize * (pageIndex + 1)));
            } else {
                middleModels.addAll(listData.subList(pageSize * pageIndex, listData.size()));
            }
        }
        if (adapter == null) {
            adapter = new AppsAdapter(this, middleModels);
            back_item.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged();
        }
        show_index.setText(String.valueOf(pageIndex + 1) + "/" + String.valueOf(totalPage));
    }

    private void moveRight() {
        if (pageIndex >= totalPage - 1) {
            ToastUtils.getInstance().showToastShort("已经是最后一页");
            return;
        } else {
            pageIndex++;
            initAdapter();
        }
    }

    private void moveLeft() {
        if (pageIndex > 0 && (pageIndex <= totalPage - 1)) {
            pageIndex--;
            initAdapter();
        } else {
            ToastUtils.getInstance().showToastShort("已经是第一页");
        }
    }

    private SildeFrameLayout.SildeEventListener sildeEventListener = new SildeFrameLayout.SildeEventListener() {
        @Override
        public void onSildeEventLeft() {
            moveLeft();
        }

        @Override
        public void onSildeEventRight() {
            moveRight();
        }
    };

    /**
     * 获取已安装非系统应用
     *
     * @return
     */
    private List<AppInfo> scanInstallApp(Context mContext) {
        List<AppInfo> appInfos = new ArrayList<>();
        PackageManager pm = mContext.getPackageManager(); // 获得PackageManager对象
        List<ApplicationInfo> listAppcations = pm
                .getInstalledApplications(PackageManager.GET_UNINSTALLED_PACKAGES);
        Collections.sort(listAppcations,
                new ApplicationInfo.DisplayNameComparator(pm));// 字典排序

        for (ApplicationInfo app : listAppcations) {
            int flaog = 0;
            if ((app.flags & ApplicationInfo.FLAG_SYSTEM) <= 0) {//非系统程序
                flaog = 1;
            }//本来是系统程序，被用户手动更新后，该系统程序也成为第三方应用程序了
            else if ((app.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0) {
                flaog = 2;
            }
            if (StringUtils.isNull(app.packageName))continue;
            if (app.packageName.equals("com.xrz.ebook"))continue;
            if ((flaog==0&&showSystemAppS.contains(app.packageName)
                    ||locationApp.contains(app.packageName)
                    ||(flaog==1&&!app.packageName.startsWith("com.moxi.")&&!app.packageName.equals("com.zuoyebang.practice")))){
                AppInfo info = getAppInfo(app, pm, flaog);
                if (info.getIntent() == null) {
                    continue;
                }
                APPLog.e(info.toString());
                appInfos.add(info);
            }
        }
        return appInfos;
    }

    /**
     * 构造一个AppInfo对象 ，并赋值
     */
    private static AppInfo getAppInfo(ApplicationInfo app, PackageManager pm, int style) {
        AppInfo appInfo = new AppInfo();
        appInfo.setAppLabel(pm.getApplicationLabel(app).toString());//应用名称
        appInfo.setAppIcon(app.loadIcon(pm));//应用icon
        appInfo.setPkgName(app.packageName);//应用包名，用来卸载
        appInfo.setFlag(style);
        Intent intent = pm.getLaunchIntentForPackage(app.packageName);
        appInfo.setIntent(intent);
        return appInfo;
    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {
        redureshApp();
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
        unregisterReceiver(installReceiver);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.last_page:
//                DownLoadSystemActivity.startDownLoadSystem(this,"https://moxiota.oss-cn-shenzhen.aliyuncs.com/TopSirH68/evk_6sl_r6801-ota-20170210.zip","111");
                moveLeft();
                break;
            case R.id.next_page:
                moveRight();
                break;
            default:
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        try {
            startActivity(middleModels.get(position).getIntent());
        } catch (Exception e) {
            ToastUtils.getInstance().showToastShort("无法打开应用");
        }
    }


    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        if (position < 0) return true;
        final AppInfo info = middleModels.get(position);
        if ((info.getFlag() != 0&&!info.getPkgName().startsWith("com.moxi"))||!locationApp.contains(info.getPkgName())) {
            insureDialog("应用卸载", "请确认是否卸载应用"+info.getAppLabel(), "卸载", "取消", 1, new InsureOrQuitListener() {
                @Override
                public void isInsure(Object code, boolean is) {
                    if (is) {//卸载应用
//                        ToolUtils.uninstall(AppsActivity.this,info.getPkgName());
                        dialogShowOrHide(true, "卸载中...");
                        new UninstallApp(AppsActivity.this, info.getPkgName(), new UninstallApp.UnInstallListener() {
                            @Override
                            public void onUnInstall(int is) {
                                ToastUtils.getInstance().showToastShort("卸载" + (is == 1 ? "成功" : "失败"));
                                dialogShowOrHide(false, "卸载中...");
                                if (is == 1) {
                                    redureshApp();
                                }
                            }
                        }).execute("");
                    }
                }
            });
        } else {
            ToastUtils.getInstance().showToastShort("系统应用无法卸载！");
        }


        return true;
    }
}
