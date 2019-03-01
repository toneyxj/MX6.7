package com.moxi.wechatshare;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.mx.mxbase.base.MyApplication;

import java.util.ArrayList;

import static com.moxi.wechatshare.R.id.click_reset;


public class ShareActivity extends Activity implements View.OnClickListener {

    /**
     * 开启分享模式
     *
     * @param context     当前上下文
     * @param appSession  用户标识
     * @param textContent 发布的内容
     * @param files       发布的图片，可多张图片
     */
    public static void startShare(Context context, String appSession, String textContent, ArrayList<String> files) {
        Intent intent = new Intent(context, ShareActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("appSession", appSession);
        bundle.putString("textContent", textContent);
        bundle.putStringArrayList("files", files);
        intent.putExtras(bundle);
        context.startActivity(intent);
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

        //数据初始化
        Bundle bundle=savedInstanceState;
        if (bundle==null){
            bundle=getIntent().getExtras();
        }
        appSession=bundle.getString("appSession");
        textContent=bundle.getString("textContent");
        files=bundle.getStringArrayList("files");
        shareControl=new ShareControl((findViewById(R.id.share_layout)),appSession,textContent,files,this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("appSession", appSession);
        outState.putString("textContent", textContent);
        outState.putStringArrayList("files", files);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        shareControl.ondestory();
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.quit) {
            this.finish();
        } else if (i == click_reset) {
            shareControl.addImage();
        }
    }
}
