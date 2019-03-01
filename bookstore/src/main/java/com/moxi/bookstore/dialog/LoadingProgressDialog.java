package com.moxi.bookstore.dialog;


import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import com.moxi.bookstore.R;

/**
 * Created by Administrator on 2016/10/20.
 */
public class LoadingProgressDialog extends Dialog {
    public LoadingProgressDialog(Context context) {
        super(context);
    }

    private  LoadingProgressDialog(Context context, int theme) {
        super(context, theme);
    }

    /**
     * 当窗口焦点改变时调用
     */
    public void onWindowFocusChanged(boolean hasFocus) {
        ImageView imageView = (ImageView) findViewById(R.id.loading_iv);
        // 获取ImageView上的动画背景
        AnimationDrawable animation = (AnimationDrawable) imageView.getBackground();
        // 开始动画
        animation.start();
    }

    /**
     * 给Dialog设置提示信息
     *
     * @param message
     */
    public void setMessage(CharSequence message) {
        if (message != null && message.length() > 0) {
            findViewById(R.id.progress_tv).setVisibility(View.VISIBLE);
            TextView txt = (TextView) findViewById(R.id.progress_tv);
            txt.setText(message);
            txt.invalidate();
        }
    }

    /**
     * 弹出自定义ProgressDialog
     *
     * @param context
     *            上下文
     * @param message
     *            提示
     * @param cancelable
     *            是否按返回键取消
     * @param cancelListener
     *            按下返回键监听
     * @return
     */
    static LoadingProgressDialog dialog;
    public  static LoadingProgressDialog create(Context context, String message, boolean cancelable, OnCancelListener cancelListener) {
        if (null==dialog){
                if (null == dialog) {
                    dialog = new LoadingProgressDialog(context, R.style.myProgressDialog);
                    dialog.setContentView(R.layout.loadingprogress);
                if (message == null || message.length() == 0) {
                    dialog.findViewById(R.id.progress_tv).setVisibility(View.GONE);
                } else {
                    TextView txt = (TextView) dialog.findViewById(R.id.progress_tv);
                    txt.setText(message);
                }
                // 按返回键是否取消
                dialog.setCancelable(cancelable);
                dialog.setCanceledOnTouchOutside(false);
                // 监听返回键处理
                dialog.setOnCancelListener(cancelListener);
                // 设置居中
                dialog.getWindow().getAttributes().gravity = Gravity.CENTER;
                WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
                // 设置背景层透明度
                //lp.dimAmount = 0.2f;
                dialog.getWindow().setAttributes(lp);
                // dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
                }

        }
        return dialog;
    }


}
