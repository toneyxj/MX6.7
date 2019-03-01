package com.moxi.filemanager.utils;

import android.os.FileObserver;
import android.util.Log;

/**
 * Created by xj on 2017/9/13.
 */

public class SDCardListener  extends FileObserver {

    public SDCardListener(String path) {
              /*
               * 这种构造方法是默认监听所有事件的,如果使用 super(String,int)这种构造方法，
               * 则int参数是要监听的事件类型.
               */
        super(path);
    }

    @Override
    public void onEvent(int event, String path) {
        Log.e("all", "path:"+ path);
        Log.e("event", "path:"+ event);
        switch(event) {
            case FileObserver.ALL_EVENTS:
                Log.e("all", "path:"+ path);
                break;
            case FileObserver.CREATE:
                Log.e("Create", "path:"+ path);
                break;
        }
    }
}
