package com.moxi.systemapp.dialog;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.moxi.systemapp.R;
import com.moxi.systemapp.model.HwtModel;
import com.moxi.systemapp.ok.DownloadListener;
import com.moxi.systemapp.ok.DownloadTask;
import com.moxi.systemapp.utils.RootCmd;
import com.mx.mxbase.base.MyApplication;
import com.mx.mxbase.utils.ToastUtils;

public class InsureActivity extends Activity implements View.OnClickListener{

    public static void startUpFile (Context context , HwtModel model){
        Bundle bundle=new Bundle();
        bundle.putSerializable("model",model);
        Intent intent=new Intent(context,InsureActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    private TextView describe;
    private TextView hitn;
    private  HwtModel model;

    private Button quit;
    private Button insure;
    private boolean isopen=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insure);
        getWindow().getAttributes().width = (int) (MyApplication.ScreenWidth * 0.6);
        Bundle bundle=savedInstanceState;
        if (bundle==null){
            bundle=getIntent().getExtras();
        }
        if (bundle==null)this.finish();
        model= (HwtModel) bundle.getSerializable("model");

        hitn = (TextView) findViewById(R.id.hitn);
        describe = (TextView) findViewById(R.id.describe);
        quit = (Button) findViewById(R.id.quit);
        insure = (Button) findViewById(R.id.insure);


        describe.setText(model.descibe);

        quit.setOnClickListener(this);
        insure.setOnClickListener(this);

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("model",model);
    }


    @Override
    public void onClick(View v) {
        if (v==insure){
            if (isopen)return;
            //确认升级
            //开始下载文件
            RootCmd.deleteFile();
            setHitn("文件下载中");
            isopen=true;

        }else {
            onBackPressed();
        }
    }
    private DownloadTask loadTask;
    private void downloadFile(String url, final String path){
        loadTask= new DownloadTask(url, path, new DownloadListener() {
           @Override
           public void onProgress(int progress, long total, long hasDownlaod) {

           }

           @Override
           public void onSuccess() {
               if(isFinishing())return;
               if (path.equals(model.downloadHwt)){
                   //下载另一个文件
                   downloadFile(model.downloadFir,RootCmd.firmware_name);
               }else {
                   setHitn("升级中");
                   //下载完成

               }
           }

           @Override
           public void onFailed(Exception e) {
               if(isFinishing())return;
               isopen=false;
               ToastUtils.getInstance().showToastShort("更新失败");
           }

           @Override
           public void onPaused() {

           }

           @Override
           public void onCanceled() {

           }
       });
    }

    @Override
    public void onBackPressed() {
        if (loadTask!=null){
            loadTask.cancelDownload();

        }
            super.onBackPressed();

    }
    public void setHitn(String value){
        if (isFinishing())return;
        hitn.setText(value);
    }
}
