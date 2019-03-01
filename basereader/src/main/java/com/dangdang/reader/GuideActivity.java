package com.dangdang.reader;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.dangdang.reader.base.BaseReaderActivity;
import com.dangdang.reader.dread.StartRead;
import com.dangdang.reader.dread.font.FontListHandle;
import com.dangdang.reader.utils.ConfigManager;
import com.dangdang.reader.utils.DangdangFileManager;
import com.dangdang.reader.utils.FirstGuideManager;
import com.dangdang.reader.utils.FirstGuideManager.FirstGuideTag;
import com.dangdang.reader.utils.PresetManager;
import com.dangdang.reader.utils.UpdateCss;
import com.dangdang.zframework.log.LogM;
import com.dangdang.zframework.utils.UiUtil;
import com.mx.mxbase.constant.APPLog;

import java.io.File;
import java.lang.ref.WeakReference;

public class GuideActivity extends BaseReaderActivity {
    private final static int MSG_GOTO_MAIN = 1;
    private Handler mHandler;
    private static final long MIN_DELAY_TIME = 2500;
    private long mStartTime;

    private static class MyHandler extends Handler {
        private final WeakReference<GuideActivity> mFragmentView;

        MyHandler(GuideActivity view) {
            this.mFragmentView = new WeakReference<GuideActivity>(view);
        }

        @SuppressWarnings("unchecked")
        @Override
        public void handleMessage(Message msg) {
            GuideActivity service = mFragmentView.get();
            if (service != null) {
                super.handleMessage(msg);
                try {
                    switch (msg.what) {
                        case MSG_GOTO_MAIN:
                            service.startOffPrintRead();
                            break;
                    }
                } catch (Exception e) {
                    LogM.e(service.TAG, e.toString());
                }
            }
        }
    }

    @Override
    protected void onCreateImpl(Bundle savedInstanceState) {
        mStartTime = System.currentTimeMillis();
        try {
            setContentView(R.layout.guide);
        } catch (OutOfMemoryError e) {
            UiUtil.showToast(mContext, "内存过低，请关闭不用的后台进程:");
            finish();
            return;
        }
        mHandler = new MyHandler(this);
        new Thread(mInitTask).start();
    }


    private Runnable mInitTask = new Runnable() {

        @Override
        public void run() {
            checkCss();
            checkPreReadFile();
            checkOffPrintFile();
            APPLog.e("进入执行这里的代码");
            DangdangFileManager.moveXdb_rules(getApplicationContext());

            PresetManager presetMgr = new PresetManager(getApplication());
            presetMgr.copyPreset();
            long hasDelayed = System.currentTimeMillis() - mStartTime;
            if (hasDelayed >= MIN_DELAY_TIME) {
                mHandler.sendEmptyMessageDelayed(MSG_GOTO_MAIN, 0);
            } else {
                mHandler.sendEmptyMessageDelayed(MSG_GOTO_MAIN, MIN_DELAY_TIME - hasDelayed);
            }
        }
    };


    protected void checkCss() {
        ConfigManager configManager = new ConfigManager(getApplication());
        UpdateCss updateCss = new UpdateCss(configManager);
        updateCss.execute(getApplication());
    }

    protected void checkPreReadFile() {
        if (DangdangFileManager.isReadExists()) {
            // 4.8.8以前的版本以，已经存在，先删除。
            if (FirstGuideManager.getInstance(this).isFirstGuide(
                    FirstGuideTag.IS_FIRST_READFILE)) {
                File file = new File(DangdangFileManager.getPreSetReadDir());
                DangdangFileManager.deleteDir(file);
            }
        }
        if (!DangdangFileManager.isReadExists()) {
            String path = DangdangFileManager.getPreSetTTF();
            boolean move = true;
            try {
                if (!DangdangFileManager.isReadExists()) {
                    move = DangdangFileManager
                            .moveReadFile(getApplicationContext());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (move) {
                ((DDApplication) (GuideActivity.this.getApplication()))
                        .setTTF();
                FontListHandle.getHandle(mContext).setFontVersion(FontListHandle.CURRENT_FONT_VERSION);
            }
        } else {
            ((DDApplication) (GuideActivity.this.getApplication())).setTTF();
        }
    }

    protected void checkOffPrintFile() {
        if (DangdangFileManager.isOffPrintExists()) {
            if (FirstGuideManager.getInstance(this).isFirstGuide(
                    FirstGuideTag.IS_FIRST_OFFPRINT_FILE)) {
                //如果是第一次的话删除之前的文件目录
                File file = new File(DangdangFileManager.getPreSetOffPrintDir());
                DangdangFileManager.deleteDir(file);
            }
        }
        APPLog.e("文件路径1111="+DangdangFileManager.getPreSetOffPrintDir());
        if (!DangdangFileManager.isOffPrintExists()) {
            boolean move = true;
            try {
                if (!DangdangFileManager.isOffPrintExists()) {
                    move = DangdangFileManager
                            .moveOffPrintFile(getApplicationContext());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void startOffPrintRead() {
        StartRead startRead = new StartRead();
        startRead.startOffPrintRead(this);
        finish();
    }


    @Override
    protected boolean isFitSystemWindow() {
        return false;
    }

    @Override
    protected int getSystemBarColor() {
        return R.color.transparent;
    }

    @Override
    public void onBackPressed() {

    }


//    @Override
//    public void finish() {
//        super.finish();
//        overridePendingTransition(R.anim.push_right_in, R.anim.push_left_out);
//    }

    @Override
    protected boolean isAnimation() {
//        return super.isAnimation();
        return false;
    }

    @Override
    protected void onDestroyImpl() {
        mHandler.removeMessages(MSG_GOTO_MAIN);
    }

    @Override
    public boolean isSwipeBack() {
        return false;
    }
}
