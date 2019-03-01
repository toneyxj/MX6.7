package com.moxi.haierc.util;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.moxi.haierc.R;
import com.moxi.haierc.view.ProgressView;
import com.mx.mxbase.utils.ToastUtils;

/**
 * Created by Administrator on 2016/10/19.
 */
public class ToastVolumeUtils {
    // 初始化类实列
    private static ToastVolumeUtils instatnce = null;
    private Toast toast;
    private View view;

    /**
     * 获得软键盘弹出类实列
     *
     * @return 返回初始化实列
     */
    public static ToastVolumeUtils getInstance(Context context) {
        if (instatnce == null) {
            synchronized (ToastUtils.class) {
                if (instatnce == null) {
                    instatnce = new ToastVolumeUtils(context.getApplicationContext());
                }
            }
        }
        return instatnce;
    }

    public  ToastVolumeUtils(Context context){
        toast=Toast.makeText(context,"",Toast.LENGTH_SHORT);
        initToastView(context);

    }

    /**
     * 长时间显示
     */
    public void showToast(int progress) {
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        setToastValue(progress);
        toast.setView(view);
        toast.show();
    }
    public void hide(){
        if (toast!=null){
            toast.cancel();
        }
    }
    private void initToastView(Context context){
        LayoutInflater inflater = LayoutInflater.from(context);
         view = inflater.inflate(R.layout.toast_voleume_layout, null);
        ProgressView current_progress = (ProgressView) view.findViewById(R.id.current_progress);
        current_progress.setMaxNumber(16);
    }
    private void setToastValue(int value){
        TextView toast_name = (TextView) view.findViewById(R.id.voleume);
        ProgressView current_progress = (ProgressView) view.findViewById(R.id.current_progress);
        toast_name.setText("音量:"+String.valueOf((int)(value*100)/15)+"%");
        int size=0;
        if (value > 0) {
            size=value;
        }
        current_progress.setCurNumber(size);

    }

}
