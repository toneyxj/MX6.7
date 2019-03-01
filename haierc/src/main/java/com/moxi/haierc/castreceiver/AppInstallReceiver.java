package com.moxi.haierc.castreceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.support.v4.content.LocalBroadcastManager;

import com.moxi.haierc.model.AppModel;
import com.moxi.haierc.util.ACache;
import com.moxi.haierc.util.IndexApplicationUtils;
import com.mx.mxbase.utils.SharePreferceUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by King on 2017/10/12.
 */

public class AppInstallReceiver extends BroadcastReceiver {

    private ApplicationInfo applicationInfo;
    private LocalBroadcastManager localBroadcastManager;
    public SharePreferceUtil share;
    private String stuFlag;

    @Override
    public void onReceive(Context context, Intent intent) {
        share = SharePreferceUtil.getInstance(context);
        stuFlag = share.getString("flag_version_stu");
        if (stuFlag.equals("")) {
            stuFlag = "标准版";
        }
        localBroadcastManager = LocalBroadcastManager.getInstance(context);
        PackageManager manager = context.getPackageManager();
        //安装应用
        String packageName = intent.getData().getSchemeSpecificPart();
        if (packageName.startsWith("com.moxi") || packageName.equals("com.zuoyebang.practice")) {
            return;
        }
        if (intent.getAction().equals(Intent.ACTION_PACKAGE_ADDED)) {
            String tempEduPosition = ACache.get(context).getAsString("last_uninstall_edu_package_position");
            String tempBusPosition = ACache.get(context).getAsString("last_uninstall_bus_package_position");
            try {
                applicationInfo = manager.getApplicationInfo(packageName, PackageManager.GET_META_DATA);
                if ((tempEduPosition != null && !tempEduPosition.equals("-1")) || (tempBusPosition != null && !tempBusPosition.equals("-1"))) {
                    if ((tempEduPosition != null && !tempEduPosition.equals("-1"))) {
                        int cachePosition = Integer.parseInt(tempEduPosition);
                        ArrayList<AppModel> listEdu = IndexApplicationUtils.getInstance(context).selectAppByPosition("教育版", cachePosition);
                        if (listEdu != null && listEdu.size() > 0) {
                            AppModel temp = listEdu.get(0);
                            temp.setIsShow(0);
                            temp.setPosition(-1);
                            temp.setUpdateTime(System.currentTimeMillis());
                            IndexApplicationUtils.getInstance(context).updateAppInfo("教育版", temp);

                            AppModel installApp = new AppModel();
                            installApp.setIsShow(1);
                            installApp.setPosition(cachePosition);
                            installApp.setAppName(applicationInfo.loadLabel(manager).toString());
                            installApp.setAppPackageName(applicationInfo.packageName);
                            installApp.setIsCanReplace(1);
                            installApp.setAppLauncherClass(getLauncherActivityNameByPackageName(context, packageName));
                            installApp.setUpdateTime(System.currentTimeMillis());
                            IndexApplicationUtils.getInstance(context).insertAppLog("教育版", installApp);
                        } else {
                            AppModel newInstallApp = new AppModel();
                            newInstallApp.setIsShow(0);
                            newInstallApp.setPosition(-1);
                            newInstallApp.setAppName(applicationInfo.loadLabel(manager).toString());
                            newInstallApp.setAppPackageName(applicationInfo.packageName);
                            newInstallApp.setIsCanReplace(1);
                            newInstallApp.setAppLauncherClass(getLauncherActivityNameByPackageName(context, packageName));
                            newInstallApp.setUpdateTime(System.currentTimeMillis());
                            IndexApplicationUtils.getInstance(context).insertAppLog("教育版", newInstallApp);
                        }
                    } else {
                        AppModel newInstallApp = new AppModel();
                        newInstallApp.setIsShow(0);
                        newInstallApp.setPosition(-1);
                        newInstallApp.setAppName(applicationInfo.loadLabel(manager).toString());
                        newInstallApp.setAppPackageName(applicationInfo.packageName);
                        newInstallApp.setIsCanReplace(1);
                        newInstallApp.setAppLauncherClass(getLauncherActivityNameByPackageName(context, packageName));
                        newInstallApp.setUpdateTime(System.currentTimeMillis());
                        IndexApplicationUtils.getInstance(context).insertAppLog("教育版", newInstallApp);
                    }
                    if ((tempBusPosition != null && !tempBusPosition.equals("-1"))) {
                        int busPosition = Integer.parseInt(tempBusPosition);
                        ArrayList<AppModel> listBus = IndexApplicationUtils.getInstance(context).selectAppByPosition("商务版", busPosition);
                        if (listBus != null && listBus.size() > 0) {
                            AppModel temp = listBus.get(0);
                            temp.setIsShow(0);
                            temp.setPosition(-1);
                            temp.setUpdateTime(System.currentTimeMillis());
                            IndexApplicationUtils.getInstance(context).updateAppInfo("商务版", temp);

                            AppModel installApp = new AppModel();
                            installApp.setIsShow(1);
                            installApp.setPosition(busPosition);
                            installApp.setAppName(applicationInfo.loadLabel(manager).toString());
                            installApp.setAppPackageName(applicationInfo.packageName);
                            installApp.setIsCanReplace(1);
                            installApp.setAppLauncherClass(getLauncherActivityNameByPackageName(context, packageName));
                            installApp.setUpdateTime(System.currentTimeMillis());
                            IndexApplicationUtils.getInstance(context).insertAppLog("商务版", installApp);
                        } else {
                            AppModel newInstallApp = new AppModel();
                            newInstallApp.setIsShow(0);
                            newInstallApp.setPosition(-1);
                            newInstallApp.setAppName(applicationInfo.loadLabel(manager).toString());
                            newInstallApp.setAppPackageName(applicationInfo.packageName);
                            newInstallApp.setIsCanReplace(1);
                            newInstallApp.setAppLauncherClass(getLauncherActivityNameByPackageName(context, packageName));
                            newInstallApp.setUpdateTime(System.currentTimeMillis());
                            IndexApplicationUtils.getInstance(context).insertAppLog("商务版", newInstallApp);
                        }
                    } else {
                        AppModel newInstallApp = new AppModel();
                        newInstallApp.setIsShow(0);
                        newInstallApp.setPosition(-1);
                        newInstallApp.setAppName(applicationInfo.loadLabel(manager).toString());
                        newInstallApp.setAppPackageName(applicationInfo.packageName);
                        newInstallApp.setIsCanReplace(1);
                        newInstallApp.setAppLauncherClass(getLauncherActivityNameByPackageName(context, packageName));
                        newInstallApp.setUpdateTime(System.currentTimeMillis());
                        IndexApplicationUtils.getInstance(context).insertAppLog("商务版", newInstallApp);
                    }
                } else {
                    AppModel newInstallApp = new AppModel();
                    newInstallApp.setIsShow(0);
                    newInstallApp.setPosition(-1);
                    newInstallApp.setAppName(applicationInfo.loadLabel(manager).toString());
                    newInstallApp.setAppPackageName(applicationInfo.packageName);
                    newInstallApp.setIsCanReplace(1);
                    newInstallApp.setAppLauncherClass(getLauncherActivityNameByPackageName(context, packageName));
                    newInstallApp.setUpdateTime(System.currentTimeMillis());
                    IndexApplicationUtils.getInstance(context).insertAppLog("", newInstallApp);
                }
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
        //卸载应用
        if (intent.getAction().equals(Intent.ACTION_PACKAGE_REMOVED)) {
            int currentEduPosition = isCurrentShow("教育版", context, packageName);
            int currentBusPosition = isCurrentShow("商务版", context, packageName);
            if (currentEduPosition != -1 || currentBusPosition != -1) {//卸载的正显示在前台的应用
                if (currentEduPosition != -1) {
                    ACache.get(context).put("last_uninstall_edu_package_position", currentEduPosition + "", 5);
                    ArrayList<AppModel> listHide = IndexApplicationUtils.getInstance(context).getCurrentHideApps("教育版");
                    if (listHide != null && listHide.size() > 0) {
                        AppModel temp = listHide.get(0);
                        temp.setIsShow(1);
                        temp.setPosition(currentEduPosition);
                        temp.setUpdateTime(System.currentTimeMillis());
                        IndexApplicationUtils.getInstance(context).updateAppInfo("教育版", temp);
                    }
                } else {
                    ACache.get(context).put("last_uninstall_edu_package_position", "-1");
                }
                if (currentBusPosition != -1) {
                    ACache.get(context).put("last_uninstall_bus_package_position", currentBusPosition + "", 5);
                    ArrayList<AppModel> listHide = IndexApplicationUtils.getInstance(context).getCurrentHideApps("商务版");
                    if (listHide != null && listHide.size() > 0) {
                        AppModel temp = listHide.get(0);
                        temp.setIsShow(1);
                        temp.setPosition(currentBusPosition);
                        temp.setUpdateTime(System.currentTimeMillis());
                        IndexApplicationUtils.getInstance(context).updateAppInfo("商务版", temp);
                    }
                } else {
                    ACache.get(context).put("last_uninstall_bus_package_position", "-1");
                }
                IndexApplicationUtils.getInstance(context).deleteAppInfo(packageName);
            } else {//卸载的未显示在前台的应用
                ACache.get(context).put("last_uninstall_edu_package_position", "-1");
                ACache.get(context).put("last_uninstall_bus_package_position", "-1");
                IndexApplicationUtils.getInstance(context).deleteAppInfo(packageName);
            }
        }
        //替换应用
        if (intent.getAction().equals(Intent.ACTION_PACKAGE_REPLACED)) {

        }
        Intent intentCast = new Intent("com.moxi.haierc.UPDATE_APPLICATION");
        localBroadcastManager.sendBroadcast(intentCast);
    }

    /**
     * 判断此应用是否当前显示
     *
     * @param flag        哪一个版本也就是（商务版本or教育版本）
     * @param packageName
     * @return
     */

    public int isCurrentShow(String flag, Context context, String packageName) {
        List<AppModel> listEdu = IndexApplicationUtils.getInstance(context).getCurrentShowApps(flag);
        int result = -1;
        if (listEdu != null && listEdu.size() > 0) {
            for (AppModel app : listEdu) {
                if (app.getAppPackageName().equals(packageName)) {
                    result = app.getPosition();
                    break;
                }
            }
        }
        return result;
    }

    /**
     * 根据包名获取启动类
     *
     * @param context
     * @param packageName
     * @return
     */
    public String getLauncherActivityNameByPackageName(Context context, String packageName) {
        String className = null;
        Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);//android.intent.action.MAIN
        resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);//android.intent.category.LAUNCHER
        resolveIntent.setPackage(packageName);
        List<ResolveInfo> resolveinfoList = context.getPackageManager().queryIntentActivities(resolveIntent, 0);
        if (resolveinfoList==null||resolveinfoList.size()==0)return "";
        ResolveInfo resolveinfo = resolveinfoList.iterator().next();
        if (resolveinfo != null) {
            className = resolveinfo.activityInfo.name;
        }
        return className;
    }
}
