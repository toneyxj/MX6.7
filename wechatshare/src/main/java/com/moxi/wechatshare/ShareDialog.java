package com.moxi.wechatshare;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;

import com.mx.mxbase.base.MyApplication;

import java.util.ArrayList;

/**
 * Created by xj on 2017/12/20.
 */

public class ShareDialog extends Dialog implements View.OnClickListener{
    public static void startShare(Context context,String appSession, String textContent, ArrayList<String> files) {
        ShareDialog dialog=  new ShareDialog(context, com.mx.mxbase.R.style.AlertDialogStyle,appSession,textContent, files);
        dialog.setCanceledOnTouchOutside(true);
        Window window = dialog.getWindow();
        window.setBackgroundDrawableResource(R.color.colorWihte);
        window.setGravity(Gravity.CENTER);
        dialog.show();
    }

    public ShareDialog(Context context, int themeResId, String appSession, String textContent, ArrayList<String> files) {
        super(context, themeResId);

        this.appSession=appSession;
        this.textContent=textContent;
        this.files=files;
    }

    private String appSession;
    private String textContent;
    private ArrayList<String> files;

    private ShareControl shareControl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);
        //设置dialog宽度
        int whith= MyApplication.ScreenWidth>MyApplication.ScreenHeight?MyApplication.ScreenHeight:MyApplication.ScreenWidth;
        getWindow().getAttributes().width = whith/2;
        getWindow().getAttributes().height = whith/2+50;
        shareControl=new ShareControl((findViewById(R.id.share_layout)),appSession,textContent,files,this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        shareControl.ondestory();
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.quit) {
            this.dismiss();
        } else if (i == R.id.click_reset) {
            shareControl.addImage();
        }
    }
}
