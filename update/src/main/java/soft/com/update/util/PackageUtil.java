package soft.com.update.util;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageDeleteObserver;
import android.content.pm.IPackageInstallObserver;
import android.content.pm.IPackageManager;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.IBinder;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhengdelong on 2016/10/17.
 */

public class PackageUtil {

    private static final int SUCCESS_CODE = 1;

    public void install(final Context context, final String apkPath, final InstallCall installCall) throws Exception{

        installApp(apkPath,installCall);

//        String packageName = getPackageName(context,apkPath);
//        Log.d("app","packageName:" + packageName);
//        if (isAppInstalled(context,packageName)){
//            //已经安装，先卸载，再安装
//            Log.d("app","已经安装，先卸载，再安装");
//            uninstall(context, packageName, new UnInstallCall() {
//                @Override
//                public void unInstalled(int code, String packageName) {
//                    try{
//                        Log.d("app","再安装");
//                        installApp(apkPath,installCall);
//                    }catch (Exception e){
//                        Log.d("app","再安装:" + e.getMessage());
//                        installCall.installed(-1,"安装失败-1");
//                    }
//                }
//            });
//        }else{
//            /**
//             * 未安装直接安装
//             */
//            Log.d("app","未安装直接安装");
//            installApp(apkPath,installCall);
//        }
    }

    /**
     * 普通安装
     * @param apkPath 文件路径
     * @param installCall 回调
     * @throws Exception
     */
    public void installApp(String apkPath,final InstallCall installCall) throws Exception{
        Uri packageUri = Uri.parse(apkPath);
        Class<?> clazz = Class.forName("android.os.ServiceManager");
        Method method = clazz.getMethod("getService", String.class);
        IBinder iBinder = (IBinder) method.invoke(null, "package");
        IPackageManager ipm = IPackageManager.Stub.asInterface(iBinder);
        ipm.installPackage(packageUri,new PackageInstallObserver(installCall),1,"");
    }

    public void reInstall(Context context,String apkPath,final InstallCall installCall) throws Exception{
        String[] args = { "pm", "install", "-r", apkPath };
        String result = "";
        ProcessBuilder processBuilder = new ProcessBuilder(args);
        Process process = null;
        InputStream errIs = null;
        InputStream inIs = null;

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int read = -1;
        process = processBuilder.start();
        errIs = process.getErrorStream();
        while ((read = errIs.read()) != -1) {
            baos.write(read);
        }
        baos.write('\n');
        inIs = process.getInputStream();
        while ((read = inIs.read()) != -1) {
            baos.write(read);
        }
        byte[] data = baos.toByteArray();
        result = new String(data);
        if ("".equals(result)){
            installCall.installed(-1,"覆盖安装失败result 为空");
        }else{
            if (result.contains("Success")){
                Log.d("app","update 覆盖安装成功：" + result);
                // TODO: 2016/10/18 获取apk包名
//                String packageName = getPackageName(context,apkPath);
//                installCall.installed(SUCCESS_CODE,packageName);
//                Log.d("app","packageName:" + packageName);
                installCall.installed(SUCCESS_CODE,apkPath);
            }else{
                installCall.installed(-1,"覆盖安装失败result not endwith success" + result);
            }
        }
        if (errIs != null) {
            errIs.close();
        }
        if (inIs != null) {
            inIs.close();
        }

        try {
            if (process != null) {
                // use exitValue() to determine if process is still running.
                process.exitValue();
            }
        } catch (IllegalThreadStateException e) {
            // process is still running, kill it.
            process.destroy();
        }


//        try {
//            ByteArrayOutputStream baos = new ByteArrayOutputStream();
//            int read = -1;
//            process = processBuilder.start();
//            errIs = process.getErrorStream();
//            while ((read = errIs.read()) != -1) {
//                baos.write(read);
//            }
//            baos.write('\n');
//            inIs = process.getInputStream();
//            while ((read = inIs.read()) != -1) {
//                baos.write(read);
//            }
//            byte[] data = baos.toByteArray();
//            result = new String(data);
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            try {
//                if (errIs != null) {
//                    errIs.close();
//                }
//                if (inIs != null) {
//                    inIs.close();
//                }
//            } catch (IOException e) {
//
//            }
//            if (process != null) {
//                process.destroy();
//            }
//        }
    }

    public void reInstall(String apkPath,final InstallCall installCall){
        String[] args = { "pm", "install", "-r", apkPath };
        String result = "";
        ProcessBuilder processBuilder = new ProcessBuilder(args);
        Process process = null;
        InputStream errIs = null;
        InputStream inIs = null;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            int read = -1;
            process = processBuilder.start();
            errIs = process.getErrorStream();
            while ((read = errIs.read()) != -1) {
                baos.write(read);
            }
            baos.write('\n');
            inIs = process.getInputStream();
            while ((read = inIs.read()) != -1) {
                baos.write(read);
            }
            byte[] data = baos.toByteArray();
            result = new String(data);

            if ("".equals(result)){
                installCall.installed(-1,"覆盖安装失败result 为空");
            }else{
                if (result.contains("Success")){
                    Log.d("app","update 覆盖安装成功：" + result);
                    // TODO: 2016/10/18 获取apk包名
                    installCall.installed(SUCCESS_CODE,apkPath);
                }else{
                    installCall.installed(-1,"覆盖安装失败result not endwith success" + result);
                }
            }


        } catch (IOException e) {
            Log.d("update","IOExceptio:" + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            Log.d("update","Exception:" + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (errIs != null) {
                    errIs.close();
                }
                if (inIs != null) {
                    inIs.close();
                }
            } catch (IOException e) {
                Log.d("update","" + "IOException:" + e.getMessage());
                e.printStackTrace();
            }
//            if (process != null) {
//                process.destroy();
//            }
            try {
                if (process != null) {
                    // use exitValue() to determine if process is still running.
                    process.exitValue();
                }
            } catch (IllegalThreadStateException e) {
                // process is still running, kill it.
                process.destroy();
            }
        }
    }

    /**
     * 卸载
     * @param context
     * @param packageName 包名
     * @param unInstallCall 回调
     * @throws Exception
     */
    public void uninstall(Context context, String packageName, final UnInstallCall unInstallCall) throws Exception{
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);

        if (!isAppInstalled(context,packageName)){
            Log.d("app","未安装：" + packageName);
            unInstallCall.unInstalled(-1,"未安装该应用");
            return;
        }

        Class<?> clazz = Class.forName("android.os.ServiceManager");
        Method method = clazz.getMethod("getService", String.class);
        IBinder iBinder = (IBinder) method.invoke(null, "package");
        IPackageManager ipm = IPackageManager.Stub.asInterface(iBinder);
        ipm.deletePackage(packageName,new PackageUninstallObserver(unInstallCall),0);
    }

    //安装回调
    class PackageInstallObserver extends IPackageInstallObserver.Stub {

        InstallCall installCall;

        public PackageInstallObserver(InstallCall installCall){
            this.installCall = installCall;
        }

        @Override
        public void packageInstalled(String packageName, int returnCode) {
            if (returnCode == SUCCESS_CODE){
                /**
                 * 安装成功
                 */
                Log.d("app","returnCode" + returnCode);
                Log.d("app","packageName" + packageName);
            }
            installCall.installed(returnCode,packageName);
        }
    };

    /**
     * 卸载回调
     */
    class PackageUninstallObserver extends IPackageDeleteObserver.Stub {

        UnInstallCall unInstallCall;

        public PackageUninstallObserver(UnInstallCall unInstallCall){
            this.unInstallCall = unInstallCall;
        }

        @Override
        public void packageDeleted(String packageName, int returnCode)  {
            if (returnCode == SUCCESS_CODE){
                /**
                 * 卸载成功
                 */
                Log.d("app","卸载成功");
                Log.d("app","returnCode：" + returnCode);
                Log.d("app","packageName：" + packageName);
            }else{
                Log.d("app","卸载失败");
                Log.d("app","returnCode：" + returnCode);
                Log.d("app","packageName：" + packageName);
            }
            unInstallCall.unInstalled(returnCode,packageName);
        }
    }

    public boolean isAppInstalled(Context context, String packageName) {
        final PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);
        List<String> pName = new ArrayList<String>();
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName;
                pName.add(pn);
            }
        }
        return pName.contains(packageName);
    }

    public String getPackageName(Context context,String apkPath){
        PackageManager pm = context.getPackageManager();
        if (pm == null){
            Log.d("app","pm is null");
        }
        Log.d("app", "getPackageName apkPath==>"+ apkPath);
        PackageInfo info = pm.getPackageArchiveInfo(apkPath, PackageManager.GET_ACTIVITIES);
        if (info == null){
            Log.d("info","info is null");
        }
        ApplicationInfo appInfo = info.applicationInfo;
        if (appInfo == null){
            Log.d("appInfo","appInfo is null");
            return appInfo.packageName;
        }else{
            return apkPath;
        }
//        return  appInfo.packageName;  //得到安装包名称
    }

    public interface InstallCall{
        public void installed(int code, String packageName);
    }

    public interface UnInstallCall{
        public void unInstalled(int code, String packageName);
    }

}
