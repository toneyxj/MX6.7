package com.moxi.bookstore.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.os.IBinder;

import java.io.File;

//import com.onyx.android.sdk.reader.IMetadataService;

/**
 * Created by xj on 2017/12/26.
 */

public class OnlyXMetadataManager {
    private final String SERVICE_PKG_NAME = "com.onyx.kreader";
    private final String SERVICE_CLASS_NAME = "com.onyx.kreader.ui.ReaderMetadataService";
//    private IMetadataService extractService;

    public boolean initOnlyXMetadataManager(Context context,String path) {
        final MetadataServiceConnection connection = new MetadataServiceConnection();
        boolean ret=false;
//        try {
//            final Intent intent = new Intent();
//            intent.setComponent(new ComponentName(SERVICE_PKG_NAME, SERVICE_CLASS_NAME));
//            context.bindService(intent, connection, Context.BIND_AUTO_CREATE | Context.BIND_NOT_FOREGROUND);
//            connection.waitUntilConnected();
//
//             extractService = IMetadataService.Stub.asInterface(connection.getRemoteService());
//            ret = extractService.extractMetadataAndThumbnail(path, -1);
//            context.unbindService(connection);
//            APPLog.e("ret="+ret,"启动service="+path);
//        } catch (Exception e) {
//            APPLog.e("失败");
//            e.printStackTrace();
//        }
        return ret;
    }

    private class MetadataServiceConnection implements ServiceConnection {
        private volatile boolean connected = false;
        private volatile IBinder remoteService;

        public MetadataServiceConnection() {
            super();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            connected = true;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            connected = true;
            remoteService = service;
        }

        public IBinder getRemoteService() {
            return remoteService;
        }

        public void waitUntilConnected() throws InterruptedException {
            while (!connected) {
                Thread.sleep(100);
            }
        }

    }
    public boolean addpic(String path){
        boolean ret=false;
//        try {
//            if (extractService!=null)
//                ret = extractService.extractMetadataAndThumbnail(path, -1);
//        } catch (RemoteException e) {
//            e.printStackTrace();
//        }
        return ret;
    }
    public boolean addPic(File file){
        return addpic(file.getAbsolutePath());
    }
}
