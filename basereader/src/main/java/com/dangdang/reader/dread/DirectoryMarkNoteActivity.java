package com.dangdang.reader.dread;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.dangdang.reader.R;
import com.dangdang.reader.dread.adapter.DmnFragmentPagerAdapter;
import com.dangdang.reader.dread.config.ReadConfig;
import com.dangdang.reader.dread.util.IntentK;
import com.dangdang.reader.dread.view.ReaderRelativeLayout;
import com.dangdang.reader.dread.view.ReaderScrollView.ScrollEvent;
import com.dangdang.reader.utils.Constant;
import com.dangdang.zframework.log.LogM;
import com.dangdang.zframework.view.DDImageView;
import com.dangdang.zframework.view.DDTextView;
import com.mx.mxbase.constant.APPLog;

import java.lang.ref.WeakReference;

/**
 * 目录显示
 */
public class DirectoryMarkNoteActivity extends BaseReadActivity {

    public final static int NULL = -1;
    public final static int DIR = 1;
    public final static int MARK = 3;
    public final static int NOTE = 5;
    public final static int DIC = 7;
    private DmnFragmentPagerAdapter mPagerAdapter;
    private int mCurrentModule;
    private DDTextView mDir;
    private DDTextView mMark;
    private DDTextView mNote;
    // private DDTextView mDic;
    private ViewGroup mTopLayout;
    private ViewPager mPager;
    private String mBookName;

    private boolean mClickEntryMark = false;

    private DmnBroadcastReceiver mDmnReceiver;
    private Handler handler;

    // private Class<? extends BaseReadActivity> mReadActivity =
    // ReadActivity.class;

    @Override
    protected void onCreateImpl(Bundle savedInstanceState) {
        setContentView(R.layout.read_dmn_layout);
        printLog(" onCreate() ");
        mCurrentModule = NULL;
        mBookName = getIntent().getStringExtra(IntentK.BookName);
        initUI();
        initReceiver();
        handler = new MyHandler(this);
    }

    @Override
    public boolean isTransparentSystemBar() {
        return false;
    }

    private void initUI() {
        mReaderRelativeLayout = (ReaderRelativeLayout) findViewById(R.id.read_dmn_rootlayout);
        mReaderRelativeLayout.setIsPdfAndNotReflow(isPdfAndNotReflow());
        updateBackground();
        mTopLayout = (ViewGroup) findViewById(R.id.read_dmn_top_layout);
        mPager = (ViewPager) findViewById(R.id.read_dmn_container);
        mPagerAdapter = new DmnFragmentPagerAdapter(
                getSupportFragmentManager(), mBookName);
		if (isPdf() || isComics()||isPartComics()) {
			findViewById(R.id.read_dmn_note_layout).setVisibility(View.GONE);
            findViewById(R.id.read_dmn_note_line).setVisibility(View.GONE);
            mPagerAdapter.setCount(2);
        }
        mPager.setAdapter(mPagerAdapter);
        mPager.setOnPageChangeListener(mPageChangeListener);
        mDir = (DDTextView) findViewById(R.id.read_dmn_dir);
        mMark = (DDTextView) findViewById(R.id.read_dmn_mark);
        mNote = (DDTextView) findViewById(R.id.read_dmn_note);
        // mDic = (DDTextView) findViewById(R.id.read_dmn_dic);
        mMark.setOnClickListener(mClickListener);
        mNote.setOnClickListener(mClickListener);
        mDir.setOnClickListener(mClickListener);
        // mDic.setOnClickListener(mClickListener);
        setSelection();
//        mShareLayout.setOnClickListener(new OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                shareBook();
//            }
//        });
        DDImageView back = (DDImageView) findViewById(R.id.read_dmn_back);
        back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                snapToReadScreen();
            }
        });
    }

    private void initReceiver() {

        mDmnReceiver = new DmnBroadcastReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constant.ACTION_PARSER_FINISH);
        filter.addAction(Constant.ACTION_COMPOSING_FINISH);
        filter.addAction(Constant.ACTION_CHANGE_BACKGROUND);
        filter.addAction(Constant.ACTION_RESTORE_DIR);
        registerReceiver(mDmnReceiver, filter);
    }

    final OnClickListener mClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {

            int i = v.getId();
            if (i == R.id.read_dmn_dir) {
                mCurrentModule = DIR;
                mClickEntryMark = true;
                setSelection();
                loadChildModule();

            } else if (i == R.id.read_dmn_mark) {
                mCurrentModule = MARK;
                mClickEntryMark = true;
                setSelection();
                loadChildModule();

            } else if (i == R.id.read_dmn_note) {
                mCurrentModule = NOTE;
                setSelection();
                loadChildModule();

            /*
             * case R.id.read_dmn_dic: mCurrentModule = DIC; break;
			 */
            }
        }
    };

    private void setSelection() {
        final int currentIndex = mCurrentModule - 1;
        final int count = mTopLayout.getChildCount();

        for (int i = 0; i < count; i++) {
            View view = mTopLayout.getChildAt(i);
            view.setSelected(currentIndex == i);
        }

        if (mCurrentModule / 2 > 1)
            showBookNoteExportBtn();
        else
            showBookShareBtn();
    }

    @Override
    public void onScrollStart(ScrollEvent e) {
        mPagerAdapter.setAllNeedReload();
        int tmpModule = mCurrentModule == NULL ? DIR : mCurrentModule;
        if (e.params != null) {
            tmpModule = (Integer) e.params;
        }
        if (tmpModule != mCurrentModule) {
            mCurrentModule = tmpModule;
            loadChildModule();
        }
        setSelection();
        handler.sendEmptyMessageDelayed(MSG_RELOAD, 0);
        resetScrollCompleteRefresh();
        setShareLayout();
        APPLog.e("onScrollStart2",System.currentTimeMillis());
    }

    @Override
    public void onScrollComplete(ScrollEvent e) {
        printLog(" onScrollComplete " + e.curScreen + ", e.params = "
                + e.params);
        if (isNeedScrollCompleteRefresh()) {
            resetScrollCompleteRefresh();
            handler.sendEmptyMessage(MSG_RELOAD);
        }
        APPLog.e("onScrollComplete2",System.currentTimeMillis());
    }

    private boolean needScollEndRefresh = false;

    private void onBookParserFinish() {
        final boolean isCurrentRead = isCurrentRead();
        printLog(" onBookParserFinish() " + isCurrentRead);
        if (!isCurrentRead) {
            // loadChildModule(mCurrentModule);
            handler.sendEmptyMessage(MSG_RELOAD);
        } else {
            setScrollCompleteRefresh();
        }
    }

    private boolean isNeedScrollCompleteRefresh() {
        return needScollEndRefresh;
    }

    private void setScrollCompleteRefresh() {
        needScollEndRefresh = true;
    }

    private void resetScrollCompleteRefresh() {
        needScollEndRefresh = false;
    }

    private void loadChildModule() {
        mPager.setCurrentItem(mCurrentModule / 2, true);
    }

    private void setBackground(int color) {
        mReaderRelativeLayout.setBackgroundColor(color);
    }

    private boolean mKeyDown = false;

    public boolean onKeyDown(int keyCode, android.view.KeyEvent event) {
        printLog(" onKeyDown() " + keyCode);
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                mKeyDown = true;
                break;
        }
        return true;
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        printLog(" onKeyUp() " + keyCode);
        switch (keyCode) {
            case KeyEvent.KEYCODE_MENU:
                break;
            case KeyEvent.KEYCODE_BACK:
                if (mKeyDown) {
                    mKeyDown = false;
                    snapToReadScreen();
                }
                break;
            case  KeyEvent.KEYCODE_PAGE_UP:
            case KeyEvent.KEYCODE_PAGE_DOWN:
               int index= mPager.getCurrentItem();
                if (index==0){
                    mPagerAdapter.getDirFragment().moveDirectionDown(KeyEvent.KEYCODE_PAGE_DOWN==keyCode);
                }else if (index==1){
                    mPagerAdapter.getNoteFragment().moveDirectionDown(KeyEvent.KEYCODE_PAGE_DOWN==keyCode);
                }
                break;
        }

        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onDestroyImpl() {

        printLog(" onDestroy() ");

        if (mDmnReceiver != null) {
            unregisterReceiver(mDmnReceiver);
            mDmnReceiver = null;
        }

        handler.removeMessages(MSG_RELOAD);
        handler.removeMessages(MSG_RESET_TODIR);
    }

    public void resetToDir() {
        mCurrentModule = DIR;
        loadChildModule();
    }

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setShareLayout();
        if (mPagerAdapter.getDirFragment() != null) {
            mPagerAdapter.getDirFragment().onNewIntent(intent);
        }
    }

    private void setShareLayout() {

    }

    public class DmnBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (Constant.ACTION_PARSER_FINISH.equals(action)
                    || Constant.ACTION_COMPOSING_FINISH.equals(action)) {
                onBookParserFinish();
            } else if (Constant.ACTION_CHANGE_BACKGROUND.equals(action)) {
                updateBackground();
                mPagerAdapter.reloadAll();
            } else if (Constant.ACTION_RESTORE_DIR.equals(action)) {
                if (mCurrentModule != DIR) {
                    handler.sendEmptyMessageDelayed(MSG_RESET_TODIR, 800);
                }
            }
        }

    }

    private void updateBackground() {
        if (!isPdfAndNotReflow()) {
            setBackground(ReadConfig.getConfig().getReaderOtherBgColor());
        } else {
            setBackground(ReadConfig.OTHER_BG_COLOR_DAY[0]);
        }
    }

    public void showBookNoteExportBtn() {

    }

    public void hideBookNoteExportBtn() {

    }

    private void showBookShareBtn() {

    }

    private String getBookNoteExportContent() {
        return  mPagerAdapter.getBookNoteExportContent();
    }

    private int getBookNoteCount() {
        return mPagerAdapter.getBookNoteCount();
    }

    private final static int MSG_RELOAD = 1;
    private final static int MSG_RESET_TODIR = 2;

    private static class MyHandler extends Handler {
        private final WeakReference<DirectoryMarkNoteActivity> mActivity;

        MyHandler(DirectoryMarkNoteActivity view) {
            this.mActivity = new WeakReference<DirectoryMarkNoteActivity>(view);
        }

        @Override
        public void handleMessage(Message msg) {
            DirectoryMarkNoteActivity activity = mActivity.get();
            if (activity != null) {
                super.handleMessage(msg);
                try {
                    switch (msg.what) {
                        case MSG_RELOAD:
                            activity.reload();
                            break;
                        case MSG_RESET_TODIR:
                            activity.resetToDir();
                            break;
                    }
                } catch (Exception e) {
                }
            }
        }
    }

    public void reload() {
        mPagerAdapter.reLoad(mCurrentModule / 2);
    }

    public void printLog(String msg) {
        LogM.d(getClass().getSimpleName(), msg);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // TODO Auto-generated method stub
        //super.onSaveInstanceState(outState);
    }

    private OnPageChangeListener mPageChangeListener = new OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {
            mCurrentModule = position * 2 + 1;
            setSelection();
            mPagerAdapter.getItem(position).reloadIfNeed();
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }
    };
    private ReaderRelativeLayout mReaderRelativeLayout;

    @Override
    protected void onStatisticsResume() {
        // 添加友盟统计
//        UmengStatistics.onResume(this);
    }

    @Override
    protected void onStatisticsPause() {
        // 添加友盟统计
//        UmengStatistics.onPause(this);
    }

    @Override
    public boolean isSwipeBack() {
        return false;
    }
}
