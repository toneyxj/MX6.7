package com.moxi.biji;

import android.app.Application;

/**
 * Created by Administrator on 2019/3/1.
 */

public class MyApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        (new BijiUtils()).startBijiInit(this);
    }
}
