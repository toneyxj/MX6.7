package com.moxi.wechatshare;

import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.moxi.wechatshare.callback.QrcodeCallBack;
import com.mx.mxbase.base.MyApplication;
import com.mx.mxbase.utils.ToastUtils;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by xj on 2017/12/20.
 */

public class ShareControl implements QrcodeCallBack {
    private View rootview;
    private View.OnClickListener listener;

    public ShareControl( View rootview,
                        String appSession, String textContent, ArrayList<String> files,
                        View.OnClickListener listener) {
        this.listener = listener;
        this.rootview=rootview;
        this.appSession=appSession;
        this.textContent=textContent;
        this.files=files;

        initView();
    }
    private ImageButton quit;
    private ProgressBar progressBar;
    private TextView click_reset;
    private ImageView zxing;
    private String appSession;
    private String textContent;
    private ArrayList<String> files;
    private boolean isfinish=false;

    private  WechatShareTool wechatShareTool;

    private void initView(){
        quit = (ImageButton)rootview. findViewById(R.id.quit);
        click_reset = (TextView) rootview.findViewById(R.id.click_reset);
        progressBar = (ProgressBar) rootview.findViewById(R.id.progressBar);
        zxing = (ImageView) rootview.findViewById(R.id.zxing);

        wechatShareTool= new WechatShareTool(this);
        wechatShareTool.setQrcodeWith(400);//设置二维码宽度
        wechatShareTool.setQrcodeHeight(400);//设置二维码高度

        quit.setOnClickListener(listener);
        click_reset.setOnClickListener(listener);

        int whith=( MyApplication.ScreenWidth>MyApplication.ScreenHeight?MyApplication.ScreenHeight:MyApplication.ScreenWidth)/2;
        zxing.getLayoutParams().width=whith;
        zxing.getLayoutParams().height=whith;

        addImage();
    }
    public void addImage(){
        progressBar.setVisibility(View.VISIBLE);
        click_reset.setVisibility(View.INVISIBLE);
        wechatShareTool.start(appSession, textContent, getFiles());
    }

    public File[] getFiles(){
        if (null==files||files.size()==0)return null;
        File[] fs=new File[files.size()];
        for (int i = 0; i < files.size(); i++) {
            fs[i]=new File(files.get(i));
        }
        return fs;
    }

    @Override
    public void callBack(Bitmap bitmap) {
        if (isfinish)return;
        progressBar.setVisibility(View.INVISIBLE);
        zxing.setImageBitmap(bitmap);
    }

    @Override
    public void backFail(String msg) {
        if (isfinish)return;
        progressBar.setVisibility(View.INVISIBLE);
        click_reset.setVisibility(View.VISIBLE);
        ToastUtils.getInstance().showToastShort(msg);
    }

    public void ondestory(){
        this.isfinish=true;
    }
}
