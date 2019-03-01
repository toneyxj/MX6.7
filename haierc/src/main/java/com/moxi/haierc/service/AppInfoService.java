package com.moxi.haierc.service;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.text.TextUtils;


import com.moxi.haierc.model.AppModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by King on 2017/10/10.
 */

public class AppInfoService {
    private Context context;
    private PackageManager pm;

    public AppInfoService(Context context) {
        // TODO Auto-generated constructor stub
        this.context = context;
        pm = context.getPackageManager();
    }

    /**
     * 得到手机中所有的应用程序信息
     *
     * @return
     */
    public List<AppModel> getAppInfos() {
        //创建要返回的集合对象
        List<AppModel> appInfos = new ArrayList<>();
        //获取手机中所有安装的应用集合
        List<ApplicationInfo> applicationInfos = pm.getInstalledApplications(PackageManager.GET_UNINSTALLED_PACKAGES);
        //遍历所有的应用集合
        for (ApplicationInfo info : applicationInfos) {
            AppModel appInfo = new AppModel();
            //获取应用的名称
            String app_name = info.loadLabel(pm).toString();
            //获取应用的包名
            String packageName = info.packageName;
            try {
                String classname = getLauncherActivityNameByPackageName(context, packageName);
                if(TextUtils.isEmpty(classname))
                    continue;
                appInfo.setPosition(-1);
                appInfo.setAppName(app_name);
                appInfo.setAppPackageName(packageName);
                appInfo.setAppLauncherClass(classname);
                appInfo.setIsCanReplace(1);
                //判断应用程序是否是用户程序
                if (filterApp(info)) {
                    //过滤作业帮
                    if (!packageName.equals("com.zuoyebang.practice")) {
                        appInfos.add(appInfo);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }
        }
        return appInfos;
    }

    public String getLauncherActivityNameByPackageName(Context context, String packageName) {
        String className = null;
        Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);//android.intent.action.MAIN
        resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);//android.intent.category.LAUNCHER
        resolveIntent.setPackage(packageName);
        List<ResolveInfo> resolveinfoList = context.getPackageManager().queryIntentActivities(resolveIntent, 0);
        if (resolveinfoList != null && resolveinfoList.size() > 0) {
            ResolveInfo resolveinfo = resolveinfoList.iterator().next();
            if (resolveinfo != null) {
                className = resolveinfo.activityInfo.name;
            }
        }
        return className;
    }

    //判断应用程序是否是用户程序
    public boolean filterApp(ApplicationInfo info) {
        //原来是系统应用，用户手动升级
        if ((info.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0) {
            return false;
            //用户自己安装的应用程序
        } else if ((info.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
            return true;
        }
        return false;
    }
}
