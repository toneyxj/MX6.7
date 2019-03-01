package com.mx.mxbase.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

/**
 * 开启新的activity跳转
 * Created by xj on 2018/1/10.
 */

public class StartActivityUtils {
    public static final String UserPakege="com.moxi.user";//"com.moxi.mxuser"
    public static final String DDReader="com.moxi.bookstore";//"com.moxi.mxuser"
    public static final  String brodcastLight="com.statusbar.broadcast";
    public static final  String screenShot="com.statusbar.screenShot";
    public static final  String plus="com.voleume.plus";
    public static final  String minus="com.voleume.minus";
    public static final  String screenShot_BookSend="com.booksend.statusbar.screenShot";
    /**
     * 启动图片背景手绘
     *
     * @param context     上下文
     * @param backImgPath 背景图保存路径
     * @param title       本次手绘的显示标题
     */
    public static void startPicPostil(Context context, String backImgPath, String title ) {
        try {
            Intent input = new Intent();
            ComponentName cnInput = new ComponentName("com.moxi.writeNote", "com.moxi.writeNote.Activity.PicPostilActivity");
            input.setComponent(cnInput);

            Bundle bundle = new Bundle();
            bundle.putString("backImgPath", backImgPath);
            bundle.putString("title", title);
            input.putExtras(bundle);

            context.startActivity(input);
        } catch (Exception e) {
            ToastUtils.getInstance().showToastShort("没有安装此模块");
        }
    }

    /**
     * 开启新的手写页
     * @param context 当前上下文
     * @param name 指定手写保存文件的名称
     * @param path 文件保存路径，用来唯一标识文件
     */
    public static void StartExternalActivity(Context context,String name,String path){
        Intent intent=new Intent();
        intent.setClassName("com.moxi.writeNote","com.moxi.writeNote.Activity.ExternalActivity");
        intent.putExtra("name",name);
        intent.putExtra("path",path);
        context.startActivity(intent);
    }
    /**
     * 启动个人中心登录绑定三方账号界面
     *
     * @param context     上下文
     */
    public static void startDDUerBind(Context context) {
        try {
            Intent input = new Intent();
            ComponentName cnInput = new ComponentName(StartActivityUtils.UserPakege, "com.mx.user.activity.DDUserBindActivity");
            input.setComponent(cnInput);
            input.putExtra("is_back",true);
            input.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(input);
        } catch (Exception e) {
            ToastUtils.getInstance().showToastShort("没有安装此模块");
        }
    }

    /**
     * 跳转到金山词霸翻译的界面
     */
    public static void StartCiBa(Context context,String word){
        try {
            Intent input = new Intent();
            ComponentName cnInput = new ComponentName("com.moxi.bookstore", "com.moxi.bookstore.activity.JinShanCiBaActivity");
            input.setComponent(cnInput);
            Bundle bundle=new Bundle();
            bundle.putString("word",word);
            input.putExtras(bundle);
            context.startActivity(input);
        } catch (Exception e) {
            ToastUtils.getInstance().showToastShort("没有安装此模块");
        }
    }
    public static void sendOpenLight(Context context){
        Intent intent=new Intent(brodcastLight);
        intent.putExtra("name","backlight");
        context.sendBroadcast(intent);
    }

    /**
     * 模拟点击按钮
     * @param context 上下文
     * @param type 文件
     */
    public static void sendSimulatorClick(Context context,int type){
        Intent intent=new Intent("com.moxi.systemapp.brodcast.SimulatorClick");
        intent.putExtra("type",type);
        context.sendBroadcast(intent);
    }


}
