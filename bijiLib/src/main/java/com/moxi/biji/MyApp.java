package com.moxi.biji;

import com.mx.mxbase.base.MyApplication;

/**
 * Created by Administrator on 2019/3/1.
 */

public class MyApp extends MyApplication {
    @Override
    public void onCreate() {
        super.onCreate();
        (new BijiUtils()).startBijiInit(this);
    }
}
