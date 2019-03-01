package com.mx.main.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.mx.main.R;
import com.mx.main.view.DonutProgress;
import com.mx.mxbase.utils.StorageUtil;

/**
 * Created by Archer on 16/7/28.
 */
public class MXStorageSettingActivity extends Activity implements View.OnClickListener {

    private DonutProgress donutProgress;
    private TextView tvUse, tvEmpty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mx_activity_storage_setting);
        init();
    }

    /**
     * 初始化视图
     */
    private void init() {
        //初始化控件绑定
        donutProgress = (DonutProgress) findViewById(R.id.donut_progress_storage);
        tvUse = (TextView) findViewById(R.id.tv_storage_already_use);
        tvEmpty = (TextView) findViewById(R.id.tv_storage_already_empty);

        //绑定点击事件
        findViewById(R.id.img_storage_back).setOnClickListener(this);
        findViewById(R.id.tv_storage_back).setOnClickListener(this);

        //获取内部存储信息
        long total = 17179869184l;
        long avail = StorageUtil.getRomAvailableSize(this);
        String roAvail = StorageUtil.getPrintSize(avail);//StorageUtil.getRomAvailableSize(this)

        //控件赋初值
        donutProgress.setInnerBottomText(StorageUtil.getPrintSize(total - avail) + "B/16GB");
        donutProgress.setMax(total);
        donutProgress.setProgress(total - avail);
        tvEmpty.setText(roAvail + "B");
        tvUse.setText(StorageUtil.getPrintSize(total - avail) + "B");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_storage_back:
            case R.id.tv_storage_back:
                this.finish();
                break;
            default:
                break;
        }
    }
}
