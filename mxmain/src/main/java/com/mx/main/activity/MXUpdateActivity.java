package com.mx.main.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.os.PersistableBundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mx.main.R;
import com.mx.main.model.MXUpdateMsg;
import com.mx.main.utils.UnzipFromFile;
import com.mx.main.view.MXUpdateDialog;
import com.mx.mxbase.base.BaseActivity;
import com.mx.mxbase.constant.Constant;
import com.mx.mxbase.http.LoadAsyncTask;
import com.mx.mxbase.utils.GsonTools;
import com.mx.mxbase.view.AlertDialog;

import java.io.IOException;

import butterknife.Bind;

/**
 * Created by Archer on 16/9/12.
 */
public class MXUpdateActivity extends BaseActivity implements View.OnClickListener {

    @Bind(R.id.ll_base_back)
    LinearLayout llBack;
    @Bind(R.id.tv_base_back)
    TextView tvBack;
    @Bind(R.id.tv_base_mid_title)
    TextView tvMidTitle;
    @Bind(R.id.img_update_from_net)
    ImageView imgUpdate;

    private MXUpdateDialog updateDialog;
    private MXUpdateMsg updateMsg;

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        switch (msg.what) {
            case 201:
                updateDialog.setMsg("正在下载更新包....").show();
                break;
            case 200:
                updateDialog.setMsg("下载完成正在解压....");
                try {
                    UnzipFromFile.getInstance().unzip(this, Environment.getExternalStorageDirectory() +
                            Constant.UPDATE_PATH + "/update.zip", Environment.getExternalStorageDirectory() +
                            Constant.UPDATE_ZIP_PATH, updateDialog, true);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    @Override
    protected int getMainContentViewId() {
        return R.layout.mx_activity_update;
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        init();
    }

    /**
     * 初始化视图
     */
    private void init() {
        updateDialog = new MXUpdateDialog(this).builder().setCancelable(false).setTitle("更新升级中");
        llBack.setVisibility(View.VISIBLE);
        tvBack.setText("设置");
        tvMidTitle.setText("在线升级");

        llBack.setOnClickListener(this);
        imgUpdate.setOnClickListener(this);

        updateMsg = GsonTools.getPerson(this.getIntent().getStringExtra("update_info"), MXUpdateMsg.class);
    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {

    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {

    }

    @Override
    public void onActivityRestoreInstanceState(Bundle savedInstanceState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_base_back:
                this.finish();
                break;
            case R.id.img_update_from_net:
                new AlertDialog(MXUpdateActivity.this).builder().
                        setTitle("更新提示").setMsg(updateMsg.getResult().getDesc()).setCancelable(false).setNegativeButton("确定", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // TODO: 16/9/2
                        updateDialog.setContent(updateMsg.getResult().getDesc());
                        new LoadAsyncTask(Environment.getExternalStorageDirectory() + Constant.UPDATE_PATH,
                                "update.zip", getHandler()).execute(Constant.DOWNLOAD_UPDATE_ZIP + updateMsg.getResult().getId());
                    }
                }).setPositiveButton("取消", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                }).show();
                break;
        }
    }
}