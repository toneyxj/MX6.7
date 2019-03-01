package com.mx.mxbase.constant;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.widget.TextView;

import com.mx.mxbase.interfaces.LocationInfoListener;
import com.mx.mxbase.model.LocationBookInfo;
import com.mx.mxbase.utils.BookProgressUtils;
import com.mx.mxbase.utils.StringUtils;

import java.util.HashMap;
import java.util.Map;

import javax.crypto.spec.OAEPParameterSpec;

/**
 * Created by xj on 2018/8/7.
 */

public class LocationBookReadProgressUtils {
    // 初始化类实列
    private static LocationBookReadProgressUtils instatnce = null;

    /**
     * 获得软键盘弹出类实列
     *
     * @return 返回初始化实列
     */
    public static LocationBookReadProgressUtils getInstance(Context context) {
        if (instatnce == null) {
            synchronized (LocationBookReadProgressUtils.class) {
                if (instatnce == null) {
                    instatnce = new LocationBookReadProgressUtils(context);
                }
            }
        }
        return instatnce;
    }
    private Context context;
    private Map<String,LocationBookInfo> maps=new HashMap<>();
    private LocationInfoListener listeners=null;
    private boolean isOnPasue=false;

    public void onReadPasue() {
        isOnPasue = true;
    }
    public void onReadResume() {
        isOnPasue = false;
    }

    public void setListeners(LocationInfoListener listeners) {
        this.listeners = listeners;
    }

    public  LocationBookReadProgressUtils(Context context) {
        this.context = context;
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.ebook.send");
        context.registerReceiver(receiver, filter);
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String path = intent.getStringExtra("path");
            String currentpage = intent.getStringExtra("currentpage");
            String totalpage = intent.getStringExtra("totalpage");
            APPLog.e("com.ebook.receiver-path",path);
            APPLog.e("com.ebook.receiver-currentpage",currentpage);
            APPLog.e("com.ebook.receiver-totalpage",totalpage);
            if (isOnPasue&&maps.get(path)!=null)return;
            LocationBookInfo locationBookInfo=new LocationBookInfo(path,currentpage,totalpage);

            maps.put(path,locationBookInfo);
            if (listeners!=null){
                listeners.onBackInfo(locationBookInfo);
            }

        }
    };

    public boolean addProgress(String filePath, TextView view){
        if (StringUtils.isNull(filePath))return true;
        if (maps!=null&&view!=null){
            LocationBookInfo info= maps.get(filePath);
            if (info!=null){
                BookProgressUtils.setReadBookProgress(info.getTotalpage(), info.getCurrentpage(), view);
                return false;
            }
        }
        BookProgressUtils.sendPicGet(context,filePath);
        return true;
    }
    public void ClearData(){
        maps.clear();
        maps=null;
        maps=new HashMap<>();
    }
    public void removePath(String path){
        if (maps!=null)maps.remove(path);
    }


    public void onDestory() {
        context.unregisterReceiver(receiver);
        listeners=null;
        maps.clear();
        maps=null;
        maps=new HashMap<>();
        instatnce=null;
    }


}
