package com.moxi.filemanager.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.os.IBinder;

import java.io.File;

//import com.onyx.android.sdk.data.util.FileUtil;
//import com.onyx.android.sdk.reader.ISetDocumentTitleService;
//import com.onyx.android.sdk.utils.StringUtils;

/**
 * 文件复制处理后对pdf文件的处理
 * Created by xj on 2017/10/18.
 */
public class FileCopyDispose {

    public static void toCkeckFileIsPDF(Context context, File target) {
        if (context==null)return;
        if (target==null)return;
        if (target.getPath().toLowerCase().endsWith(".pdf")) {
//            String titleToSet = FileUtil.getFileNameByPath(target.getPath());
//            if (StringUtils.isNotBlank(titleToSet)) {
//                titleToSet = FileUtil.getFileNameWithoutExtension(titleToSet);
//            }
//            setDocumentTitle(context, target, titleToSet);
        }
    }


    private static boolean setDocumentTitle(final Context context, final File file,
                                           final String titleToSet) {
//        final ContentBrowserServiceConnection connection = new ContentBrowserServiceConnection();
        boolean ret = false;
//        try {
//            final Intent serviceIntent = new Intent();
//            serviceIntent.setComponent(new ComponentName("com.onyx.kreader", "com.onyx.kreader.ui.ReaderSetDocumentTitleService"));
//            context.bindService(serviceIntent, connection, Context.BIND_AUTO_CREATE | Context.BIND_NOT_FOREGROUND);
//            connection.waitUntilConnected();
//            ISetDocumentTitleService setDocumentTitleService = ISetDocumentTitleService.Stub.asInterface(connection.getRemoteService());
//            ret = setDocumentTitleService.setTitle(file.getAbsolutePath(), titleToSet);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        return ret;
    }


    private static class ContentBrowserServiceConnection implements ServiceConnection {
        private volatile boolean connected = false;
        private volatile IBinder remoteService;
        public ContentBrowserServiceConnection() {
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

}
