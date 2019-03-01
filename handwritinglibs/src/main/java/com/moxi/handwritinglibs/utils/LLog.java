package com.moxi.handwritinglibs.utils;

import android.util.Log;

/**
 * Created by xj on 2017/11/7.
 */

public class LLog {
    private static final boolean is=false;
    public static void e(Object title, Object msg){
        if (is)
        Log.e(title.toString(),msg.toString());
    }
    public static void e( Object msg){
        e("手写默认输出",msg);
    }
}
