package com.mx.main.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.mx.plugin.aidl.ILoadPlugin;
import android.os.IBinder;
import android.os.RemoteException;

import com.mx.main.view.MXUpdateDialog;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Created by Archer on 16/9/2.
 */
public class UnzipFromFile {

    public static UnzipFromFile getInstance() {
        return new UnzipFromFile();
    }

    private Context unZipcontext;

    /**
     * 解压文件到目标目录
     *
     * @param context
     * @param fileName
     * @param outputDirectory
     * @param isReWrite
     * @param dialog
     */
    public void unzip(Context context, String fileName, String outputDirectory
            , MXUpdateDialog dialog, boolean isReWrite) throws IOException {
        unZipcontext = context;
        // 创建解压目标目录
        File file = new File(outputDirectory);
        // 如果目标目录不存在，则创建
        if (!file.exists()) {
            file.mkdirs();
        }
        // 打开压缩文件
        InputStream inputStream = new FileInputStream(new File(fileName));
        ZipInputStream zipInputStream = new ZipInputStream(inputStream);
        // 读取一个进入点
        ZipEntry zipEntry = zipInputStream.getNextEntry();
        // 使用1Mbuffer
        byte[] buffer = new byte[1024 * 1024];
        // 解压时字节计数
        int count = 0;
        // 如果进入点为空说明已经遍历完所有压缩包中文件和目录
        while (zipEntry != null) {
            // 如果是一个目录
            if (zipEntry.isDirectory()) {
                file = new File(outputDirectory + File.separator + zipEntry.getName());
                // 文件需要覆盖或者是文件不存在
                if (isReWrite || !file.exists()) {
                    file.mkdir();
                }
            } else {
                // 如果是文件
                file = new File(outputDirectory + File.separator + zipEntry.getName());
                // 文件需要覆盖或者文件不存在，则解压文件
                if (isReWrite || !file.exists()) {
                    file.createNewFile();
                    FileOutputStream fileOutputStream = new FileOutputStream(file);
                    while ((count = zipInputStream.read(buffer)) > 0) {
                        fileOutputStream.write(buffer, 0, count);
                    }
                    fileOutputStream.close();
                }
            }
            // 定位到下一个文件入口
            zipEntry = zipInputStream.getNextEntry();
        }
        zipInputStream.close();
        dialog.setMsg("解压完成正在安装....");
        startCheckVersion();
    }

    private static final String ACTION_BIND_SERVICE = "android.mx.aidl.LOAD_PLUGIN";
    private ILoadPlugin iLoadPlugin;

    private ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            iLoadPlugin = ILoadPlugin.Stub.asInterface(iBinder);
            try {
                iLoadPlugin.selfRestaet();
                if (iLoadPlugin != null) {
                    unZipcontext.unbindService(mServiceConnection);
                    iLoadPlugin = null;
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            iLoadPlugin = null;
        }
    };

    private void startCheckVersion() {
        Intent intentService = new Intent(ACTION_BIND_SERVICE);
        intentService.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        unZipcontext.bindService(intentService, mServiceConnection, unZipcontext.BIND_AUTO_CREATE);
    }
}