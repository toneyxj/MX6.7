package com.moxi.bookstore.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;

import com.moxi.bookstore.R;
import com.moxi.bookstore.db.EbookDB;
import com.moxi.bookstore.db.SacnReadFileUtils;
import com.moxi.bookstore.db.TableConfig;
import com.moxi.bookstore.db.TableOperate;
import com.moxi.bookstore.utils.StartUtils;
import com.mx.mxbase.base.BaseApplication;
import com.mx.mxbase.constant.APPLog;
import com.mx.mxbase.utils.FileUtils;
import com.mx.mxbase.utils.SharePreferceUtil;
import com.mx.mxbase.utils.ToastUtils;

import java.io.File;

/**
 * Created by Administrator on 2016/10/20.
 */
public class RecentlyActivity extends Activity {
    public static void StartRecentActivity(Context context,String file){
        Intent intent=new Intent(context,RecentlyActivity.class);
        intent.putExtra("file",file);
        context.startActivity(intent);
    }
    boolean isfirst=true;
    boolean isDD=false;
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("isfirst",isfirst);
        outState.putBoolean("isDD",isDD);
    }
    Handler handler=new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recently);
        if (savedInstanceState!=null||!isfirst){
            isfirst=false;
//            isfirst=savedInstanceState.getBoolean("isfirst");
        }else {
            String path = getIntent().getStringExtra("file");
            if (path.contains("'") && path.contains("\"")) {
                ToastUtils.getInstance().showToastShort("文件路径命名有误");
            } else {
                if (path == null) {
                    path = SharePreferceUtil.getInstance(this).getString("Recently");
                }
                if (!path.equals("")) {
                    File file = new File(path);
                    if (file.exists()) {
                        EbookDB ebookDB = TableOperate.getInstance().queryByPath(TableConfig.TABLE_NAME, path);
                        if (ebookDB == null) {
                            SacnReadFileUtils.getInstance(this).updateIndex(path);
                            SharePreferceUtil.getInstance(this).setCache("Recently", path);
//                    new PrepareCMS(RecentlyActivity.this).insertCMS(path);
                            FileUtils.getInstance().openFile(RecentlyActivity.this, file);
//                    new StartFile(this, file.getAbsolutePath());
                        } else {
                            isDD=true;
                            StartUtils.OpenDDRead(RecentlyActivity.this, ebookDB);
                        }
                    } else {
                        BaseApplication.Toast("请检查，打开文件不存在！");
                    }
                } else {
                    BaseApplication.Toast("还没有阅读记录哟！");
                }
            }
        }
        //退出
        onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();

       if (isfirst){
           isfirst=false;
       }else{
           if (isDD)this.finish();
           APPLog.e("onResume-","进入销毁程序");
           setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//            DeviceInfo.currentDevice.showSystemStatusBar(RecentlyActivity.this);
           handler.postDelayed(new Runnable() {
               @Override
               public void run() {
                   APPLog.e("onResume-finish","进入销毁程序");
                   RecentlyActivity.this.finish();
               }
           },500);

        }
    }

    @Override
    public void onBackPressed() {

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return true;
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }
}
