package com.mx.mxbase.utils;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mx.mxbase.constant.APPLog;
import com.mx.mxbase.constant.PhotoConfig;

import org.json.JSONObject;

import java.io.File;

/**
 * Created by xj on 2018/8/7.
 */

public class BookProgressUtils {

    /**
     * 设置阅读进度参数
     * @param totalPage
     * @param currentPage
     * @param read_progress
     */
    public static void setReadBookProgress(int totalPage, int currentPage, TextView read_progress){
        read_progress.setVisibility(View.VISIBLE);
        if (currentPage<=0){
            read_progress.setText("未读");
        }else if (totalPage<=0){
            read_progress.setText("未读");
        }else {
            float pro=(currentPage*100f)/totalPage;
            if (pro<=1){
                read_progress.setText("已读:1%");
            }else {
                read_progress.setText("已读:"+String.valueOf((int) pro)+"%");
            }
        }
    }
    /**
     * 设置0.00%类型书籍阅读数据阅读进度参数
     * @param read_progress
     */
    public static void setDDReadBookProgress(boolean isJson,String value, TextView read_progress){
        read_progress.setVisibility(View.VISIBLE);
        try {
            if (isJson){
                JSONObject object=new JSONObject(value);
                value=object.getString("readerProgress");
            }
            value= value.replace("%","");
            float pro=Float.parseFloat(value);

            if (pro<0){
                read_progress.setText("未知");
            }else {
                if (pro>0&&pro<1){
                    read_progress.setText("已读:1%");
                }else {
                    read_progress.setText("已读:"+(int)pro+"%");
                }
            }
        }catch (Exception e){
            read_progress.setText("未读");
        }
    }

    public static void setShowBookPic( ImageView view,String name){
       int index= name.lastIndexOf(".");
        String username=name.substring(0,index);
        String path="/mnt/sdcard/XRZBookCache/"+username.hashCode();
        File file=new File(path);
        if (file.exists()&&file.canRead()&&file.length()>0) {
            GlideUtils.getInstance().locatonPic(view.getContext(), view, path);
        }else {
            view.setImageResource(PhotoConfig.getSources(name));
        }
    }

    public static void sendPicGet(Context context,String path){
        Intent intent=new Intent("com.ebook.receiver");
        intent.putExtra("path",path);
        APPLog.e("com.ebook.receiver","发送广播 path="+path);
        context.sendBroadcast(intent);
    }

}
