package com.moxi.bookstore.request;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by Administrator on 2016/2/17.
 */
public class NetUtil {
    /**
     * @param context 上下文
     * @return 有网络连接返回true，没有返回false
     */
    public static boolean checkNetworkInfo(Context context) {

        ConnectivityManager conMan = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        // mobile 3G Data Network
        NetworkInfo mobileInfo = conMan
                .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        NetworkInfo wifiInfo = conMan
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        if (mobileInfo == null) {
            NetworkInfo.State wifiState = wifiInfo.getState();
            if (wifiState == NetworkInfo.State.CONNECTED || wifiState == NetworkInfo.State.CONNECTING) {
                return true;
            } else {
                return false;
            }
        }

        NetworkInfo.State mobile = conMan.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
                .getState();
        // wifi
        NetworkInfo.State wifi = conMan.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
                .getState();
        // 判断网络状态
        if ((mobile == NetworkInfo.State.CONNECTED || mobile == NetworkInfo.State.CONNECTING
                || wifi == NetworkInfo.State.CONNECTED || wifi == NetworkInfo.State.CONNECTING)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 获得进度条提示框
     * @param context 上下文
     * @param clickProgress 点击进度条点击按钮回调
     * @param title 提示标题
     * @param hint 提示内容
     * @param buttonTxt 按钮显示文字
     * @return
     */
    public static ProgressDialog getDialog(Context context,final ClickProgressButton clickProgress, String title, String hint, String buttonTxt) {
        ProgressDialog dialog = new ProgressDialog(context);
        // 设置进度条风格，风格为圆形，旋转的
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        // 设置ProgressDialog 标题
        dialog.setTitle(title);
        // 设置ProgressDialog 提示信息
        dialog.setMessage(hint);
        // 设置ProgressDialog 标题图标
        dialog.setIcon(android.R.drawable.ic_dialog_alert);
        // 设置ProgressDialog的最大进度
        dialog.setMax(100);
        //  设置ProgressDialog 的一个Button
        if (buttonTxt != null && !"".equals(buttonTxt)) {
            dialog.setButton( buttonTxt, new ProgressDialog.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (clickProgress!=null)
                    clickProgress.onClickProgress(dialog, which);
                }
            });
        }
        // 设置ProgressDialog 是否可以按退回按键取消
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(false);
        // 显示
        dialog.show();

        // 设置ProgressDialog的当前进度
        dialog.setProgress(0);
        return dialog;
    }

    /**
     * 点击进度条按钮回掉
     */
    public interface ClickProgressButton{
        public void onClickProgress(DialogInterface dialog, int which);
    }
}
