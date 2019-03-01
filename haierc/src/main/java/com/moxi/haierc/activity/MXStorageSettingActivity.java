package com.moxi.haierc.activity;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.os.Bundle;
import android.os.StatFs;
import android.os.storage.StorageManager;
import android.view.View;
import android.widget.TextView;

import com.moxi.haierc.R;
import com.moxi.haierc.view.DonutProgress;
import com.mx.mxbase.utils.Log;
import com.mx.mxbase.utils.StorageUtil;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Method;

/**
 * Created by Archer on 16/7/28.
 */
public class MXStorageSettingActivity extends Activity implements View.OnClickListener {

    private DonutProgress donutProgress, donutExt;
    private TextView tvUse, tvEmpty, tvTotal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mx_activity_storage_setting);
        init();
        readSDCard();
    }

    private void readSDCard() {
        String[] paths = getSdPath();
        if (paths != null && paths.length > 0) {
            long total = 17179869184l;
            long avail = StorageUtil.getRomAvailableSize(this);

            StatFs sf = new StatFs(paths[1]);
            long blockSize = sf.getBlockSize();
            long blockCount = sf.getBlockCount();
            long availCount = sf.getAvailableBlocks();

            long extStorage = blockSize * blockCount;
            long extTotalTemp = (long) getInitGB(extStorage);
            String initGb = StorageUtil.getPrintSize((long) getInitGB(extStorage));
            if (extStorage > 0) {
                donutExt.setMax(extTotalTemp);
                donutExt.setProgress(extTotalTemp - availCount * blockSize);
                donutExt.setInnerBottomText(StorageUtil.getPrintSize(extTotalTemp - availCount * blockSize) + "B/" + initGb);
            } else {
                donutExt.setMax(0);
                donutExt.setProgress(0);
                donutExt.setText("没有外部存储卡");
                donutExt.setInnerBottomText("");
            }

            donutProgress.setInnerBottomText(StorageUtil.getPrintSize(total - avail) + "B/" + StorageUtil.getPrintSize((long) getInitGB(total)));
            donutProgress.setMax(total);
            donutProgress.setProgress(total - avail);

            String roAvail = StorageUtil.getPrintSize(avail + availCount * blockSize);
            tvEmpty.setText(roAvail + "B");
            tvUse.setText(StorageUtil.getPrintSize(total + extTotalTemp - avail - availCount * blockSize) + "B");
            tvTotal.setText(StorageUtil.getPrintSize(total + extTotalTemp) + "B");
        }
    }

    /**
     * 获取2的n次方GB数
     * @param size
     * @return
     */
    private double getInitGB(long size){
        double temp;
        if (size < 1024){
            return size;
        } else {
            size = size / 1024;
        }

        if (size < 1024) {
            return size * Math.pow(1024, 1);
        } else {
            size = size / 1024;
        }

        if (size < 1024) {
            return size * Math.pow(1024, 2);
        } else {
            size = size / 1024;
        }
        for (int i = 0; i < 10; i++) {
            temp = Math.pow(2, i);
            if (temp >= size) {
                size = (long) temp;
                break;
            }
        }
        return size * Math.pow(1024, 3);
    }

    /**
     * 获取所有外置存储器的目录
     * @return
     */
    private String[] getSdPath(){
        String[] paths;
        StorageManager manager = (StorageManager) this.getSystemService(STORAGE_SERVICE);
        try {
            Method methodGetPaths = manager.getClass().getMethod("getVolumePaths");
            paths = (String[]) methodGetPaths.invoke(manager);
            return paths;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 初始化视图
     */
    private void init() {
        //初始化控件绑定
        donutProgress = (DonutProgress) findViewById(R.id.donut_progress_storage);
        donutExt = (DonutProgress) findViewById(R.id.donut_ext_storage);
        tvUse = (TextView) findViewById(R.id.tv_storage_already_use);
        tvEmpty = (TextView) findViewById(R.id.tv_storage_already_empty);
        tvTotal = (TextView) findViewById(R.id.tv_total_storage);

        //绑定点击事件
        findViewById(R.id.img_storage_back).setOnClickListener(this);
        findViewById(R.id.tv_storage_back).setOnClickListener(this);
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

    // 获得可用的内存
    public long getmem_UNUSED(Context mContext) {
        long MEM_UNUSED;
        ActivityManager am = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(mi);
        MEM_UNUSED = mi.availMem / 1024;
        return MEM_UNUSED;
    }

    // 获得总内存
    public long getmem_TOLAL() {
        long mTotal;
        String path = "/proc/meminfo";
        String content = null;
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(path), 8);
            String line;
            if ((line = br.readLine()) != null) {
                content = line;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        int begin = content.indexOf(':');
        int end = content.indexOf('k');
        content = content.substring(begin + 1, end).trim();
        mTotal = Integer.parseInt(content);
        return mTotal;
    }
}