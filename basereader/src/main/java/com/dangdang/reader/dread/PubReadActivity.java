package com.dangdang.reader.dread;

import android.annotation.TargetApi;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.Window;
import android.view.WindowManager;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.dangdang.reader.db.service.DDStatisticsService;
import com.dangdang.reader.dread.config.ReadConfig;
import com.dangdang.reader.utils.MathUtil;
import com.dangdang.zframework.log.LogM;
import com.dangdang.zframework.utils.DRUiUtility;
import com.mx.mxbase.constant.APPLog;

import java.lang.ref.WeakReference;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @author luxu
 */
public abstract class PubReadActivity extends BaseReadActivity {
	
    public abstract void onReadCreateImpl(Bundle savedInstanceState);

    public abstract void onReadPauseImpl();

    public abstract void onReadResumeImpl();

    public abstract void onReadDestroyImpl();

    protected abstract boolean isMenuShow();

    protected abstract boolean needHideMenu();

    protected boolean isOperateMenuShow() {
        return false;
    }

    protected void hideOperateMenu() {

    }

    protected void removeLongClick() {
    }

    protected boolean processSelectedOption() {
        return true;
    }

    protected boolean supportDoubleAdjustLight() {
        return false;
    }

    protected void destoryData() {
        if (mLightTimer != null) {
            mLightTimer.cancel();
        }
    }


    private long mFirstInTime;
    private Timer mLightTimer;

    protected DDStatisticsService mDDService;

    @Override
    final protected void onCreateImpl(Bundle savedInstanceState) {
        mDDService = DDStatisticsService.getDDStatisticsService(this);

        mPubHandler = new MyHandler(this);
        onReadCreateImpl(savedInstanceState);

        mTouchSlop = ViewConfiguration.get(getBaseContext())
                .getScaledTouchSlop();

    }

    @Override
    final public void onPause() {
        super.onPause();

        onReadPauseImpl();
    }

    @Override
    final public void onResume() {
        super.onResume();

        onReadResumeImpl();
    }

    @Override
    final protected void onDestroyImpl() {
        try {
            onReadDestroyImpl();
        } catch (Exception e) {
            LogM.e(this.getClass().getName(), e.toString());
        }
    }

    protected void updateModeSetToolbarScreenLight() {
        final ReadConfig readConfig = ReadConfig.getConfig();
        float light = readConfig.getRealLight();
        updateScreenLight(light);
    }

    protected void updateScreenLight() {
        final ReadConfig readConfig = ReadConfig.getConfig();
        if (readConfig.isNightMode() && !readConfig.hasNightLight()) {
            updateScreenLight(readConfig.getNightLight());
            return;
        }

        if (readConfig.isSystemLight()) {
            updateScreenLight(-1);
        } else {
            updateModeSetToolbarScreenLight();
        }
    }

    protected void updateScreenLight(float light) {
        Window window = getWinD();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.screenBrightness = light;
        window.setAttributes(lp);
    }

    protected Window getWinD() {
        Window window = getWindow();
        if (getParent() != null) {
            window = getParent().getWindow();
        }
        return window;
    }

    final OnSeekBarChangeListener mSeekBarChangeLight = new OnSeekBarChangeListener() {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress,
                                      boolean fromUser) {
            if (fromUser) {
                float light = (1f * progress) / 100 + ReadConfig.MIN_LIGHT;
//				printLog(" onProgressChanged light = " + light);
                final ReadConfig readConfig = ReadConfig.getConfig();
                if (readConfig.isNightMode()) {
                    readConfig.saveNightLight(light);
                } else {
                    readConfig.saveLight(light);
                }

                updateModeSetToolbarScreenLight();
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            mDDService.addData(
                    DDStatisticsService.BRIGHTBUTTONADJUSTBRIGHTNESS,
                    DDStatisticsService.OPerateTime, System.currentTimeMillis()
                            + "", DDStatisticsService.BRIGHTVALUE,
                    (int) ReadConfig.getConfig().getRealLight() + "");
        }

    };

    private boolean mPointer2Down = false;
    private boolean mUpdateLight = false;
    // private boolean mPointerSingleDown = false;
    private float mDownX = 0;
    private float mDownY = 0;
    private float mPreMoveY = 0;
    private int mTouchSlop = 10;
    // private int mSinglePointerTime = 0;
    private int mTwoPtrDistanceDown = 0;

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        try {
            createWakeLock();
        } catch (Throwable e1) {
            e1.printStackTrace();
        }
        if (!supportDoubleAdjustLight() || isOperateBookMark()) {
            return super.dispatchTouchEvent(ev);
        }
        try {
            if (isMenuShow()) {
                // if(!readerLayout.isShow()){
                if (ev.getAction() == MotionEvent.ACTION_UP
                        || ev.getAction() == MotionEvent.ACTION_POINTER_2_DOWN) {
                    needHideMenu();
                }
                return true;
                // }
            }
            if (processOperateMenu(ev)) {
                return true;
            }

            final int pointerCount = ev.getPointerCount();
            if (pointerCount == 1) {
                if (mUpdateLight) {
                    mUpdateLight = false;
                    return true;
                }
                return super.dispatchTouchEvent(ev);
            }
            if (!processSelectedOption()) {
                return super.dispatchTouchEvent(ev);
            }

            boolean returnSuper = false;
            final float x = ev.getX();
            final float y = ev.getY();
            final boolean isCursorShow = false;// readerApps.getController().isCursorShow();
            switch (ev.getAction()) {
                case MotionEvent.ACTION_POINTER_2_DOWN:
                    returnSuper = performActionPointer2Down(ev, pointerCount, x, y);
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (!isOperateBookMark() && !mPointer2Down && pointerCount == 2) {
                        returnSuper = performActionPointer2Down(ev, pointerCount,
                                x, y);
                        return true;
                    }
                    if (!isDownDistance()) {
                        returnSuper = true;
                        break;
                    }
                    boolean twoPtrHasUpdate = false;
                    if (pointerCount == 2) {
                        removeLongClick();

                        int twoPtrDistance = getTwoPtrDistance(ev);
                        int maxChangeDistance = ReadConfig.getConfig()
                                .getTwoPtrLightDistance();
                        if (twoPtrDistance - mTwoPtrDistanceDown <= maxChangeDistance) {
                            twoPtrHasUpdate = true;
                        }
                    }

                    if (mPointer2Down && twoPtrHasUpdate && !isCursorShow) {// &&
                        // pointerCount
                        // == 2
                        if (calcLightValueAndUpdate(x, y)) {
                            mUpdateLight = true;
                        }
                        returnSuper = true;
                    }
                    break;
                case MotionEvent.ACTION_POINTER_2_UP:
                case MotionEvent.ACTION_POINTER_UP:
                    // case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    boolean ret = false;
                    if (mPointer2Down) {
                        try {
//						printLog(" mUpdateLight = " + mUpdateLight);
                            if (mUpdateLight) {
                                mDDService
                                        .addData(
                                                DDStatisticsService.GESTUREADJUSTBRIGHTNESS,
                                                DDStatisticsService.OPerateTime,
                                                System.currentTimeMillis() + "");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (!isCursorShow) {
                            ret = true;
                        }
                    }
                    // mPointerSingleDown = false;
                    // mSinglePointerTime = 0;
                    resetTouch();
                    if (ret) {
                        // readerWidget.setTwoPtrProcess(true);
                        returnSuper = true;
                    }
                    break;
            }

			/*
             * printLog(" dispatchTouchEvent getAction = " + ev.getAction() +
			 * ", PointerCount = " + ev.getPointerCount() + ", returnSuper = " +
			 * returnSuper);
			 */

            if (returnSuper) {
                removeLongClick();
                return true;
            }
            return super.dispatchTouchEvent(ev);
        } catch (Throwable e) {
            APPLog.e(" dispatchTouchEvent " + e.toString());
            e.printStackTrace();
            return super.dispatchTouchEvent(ev);
        }
    }

    protected boolean processOperateMenu(MotionEvent ev) {
        if (isOperateMenuShow()) {
            if (ev.getAction() == MotionEvent.ACTION_UP
                    || ev.getAction() == MotionEvent.ACTION_POINTER_2_DOWN) {
                hideOperateMenu();
            }
            return true;
        }
        return false;
    }

    private boolean performActionPointer2Down(MotionEvent ev,
                                              final int pointerCount, final float x, final float y) {
        mPointer2Down = true;
        mDownX = x;
        mDownY = y;
        mPreMoveY = y;
        mUpdateLight = false;
        if (pointerCount == 2) {
            mTwoPtrDistanceDown = getTwoPtrDistance(ev);
        }
        if (!isDownDistance()) {
            return true;
        }
        return false;
    }

    protected boolean isOperateBookMark() {
        return false;
    }

    /**
     * 双指按下的距离是否符合要求
     *
     * @return
     */
    protected boolean isDownDistance() {
        return mTwoPtrDistanceDown < ReadConfig.getConfig().getTwoPtrDistance();
    }

    private int getTwoPtrDistance(MotionEvent ev) {// twoPtrDistance
        float x0 = ev.getX(0);
        float y0 = ev.getY(0);
        float x1 = ev.getX(1);
        float y1 = ev.getY(1);
        return MathUtil.getDistance(new Point((int) x0, (int) y0), new Point(
                (int) x1, (int) y1));
    }

    private boolean calcLightValueAndUpdate(float x, float y) {

        boolean isUpdate = false;

        final float realYValue = mPreMoveY - y;
        final float dy = Math.abs(realYValue);
        final float seX = Math.abs(x - mDownX);
        final float seY = Math.abs(y - mDownY);
		/*
		 * printLog(" dispatchTouchEvent move seX = " + seX + ", seY = " + seY +
		 * ", mPreMoveY = " + mPreMoveY + ", y = " + y + ", isDy = " + (dy >
		 * mTouchSlop));
		 */
        if (dy > mTouchSlop && seY >= 2 * seX) {

            ReadConfig readConfig = ReadConfig.getConfig();
            final float light = readConfig.getRealLight();
            int screenHeight = DRUiUtility.getScreenHeight();
            float ratio = dy / screenHeight;

            float tmpLight = light;
            if (realYValue > 0) {
                tmpLight = ratio + light;
                if (tmpLight > ReadConfig.MAX_LIGHT) {
                    tmpLight = ReadConfig.MAX_LIGHT;
//					showToast(R.string.max_light);
                }
                // tmpLight = tmpLight > ReadConfig.MAX_LIGHT ?
                // ReadConfig.MAX_LIGHT : tmpLight;
            } else {
                tmpLight = light - ratio;
                if (tmpLight < ReadConfig.MIN_LIGHT) {
                    tmpLight = ReadConfig.MIN_LIGHT;
//					showToast(R.string.min_light);
                }
                // tmpLight = tmpLight < ReadConfig.MIN_LIGHT ?
                // ReadConfig.MIN_LIGHT : tmpLight;
            }
            // printLog(" dispatchTouchEvent  ratio = " + ratio + ", light = " +
            // light + ", tmpLight = " + tmpLight);
            if (readConfig.isNightMode()) {
                readConfig.saveNightLight(tmpLight);
            } else {
                readConfig.saveLight(tmpLight);
            }
            readConfig.setSystemLight(false);
            updateScreenLight(tmpLight);
            mPreMoveY = y;
            isUpdate = true;
        }
        return isUpdate;
    }

    private void resetTouch() {
        mPointer2Down = false;
        mDownX = 0;
        mDownY = 0;
        mPreMoveY = 0;
        mTwoPtrDistanceDown = 0;
        // mUpdateLight = false;
    }

    private PowerManager.WakeLock mWakeLock;
    private boolean mWakeLockToCreate;

    public final void createWakeLock() {
        initFirstInTime();
        if (mWakeLockToCreate) {
            synchronized (this) {
                if (mWakeLockToCreate) {
                    mWakeLockToCreate = false;
                    try {
                        mWakeLock = ((PowerManager) getSystemService(POWER_SERVICE))
                                .newWakeLock(
                                        PowerManager.SCREEN_BRIGHT_WAKE_LOCK,
                                        "DDReader");
                        mWakeLock.acquire();
                    } catch (Exception e) {
                        LogM.e(e.toString());
                    }
                    initLightTimer();
                }
            }
        }
    }

    protected final void switchWakeLock(boolean on) {
        if (on) {
            if (mWakeLock == null) {
                mWakeLockToCreate = true;
            }
        } else {
            if (mWakeLock != null) {
                synchronized (this) {
                    try {
                        if (mWakeLock != null) {
                            mWakeLock.release();
                            mWakeLock = null;
                        }
                    } catch (Exception e) {
                        LogM.e(e.toString());
                    }
                }
            }
        }
    }

    public void initFirstInTime() {
        mFirstInTime = new Date().getTime();
    }

    public synchronized void initLightTimer() {
        if (mLightTimer != null)
            return;
        mLightTimer = new Timer();
        mLightTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                int interval = ReadConfig.getConfig().getLightInterval();
                if (interval == ReadConfig.READER_LIGHT_INTERVAL_FOREVER)
                    return;
                else if (new Date().getTime() - mFirstInTime >= interval) {
                    Message msg = mPubHandler.obtainMessage();
                    msg.what = LIGHT_CHANGE;
                    mPubHandler.sendMessage(msg);
                }
            }
        }, 0, 10000);
    }

    public void processReflow() {
        getReadMain().processReflow();
    }

    public static final int LIGHT_CHANGE = 0x03;

    private Handler mPubHandler;

    private static class MyHandler extends Handler {
		private final WeakReference<PubReadActivity> mFragmentView;

		MyHandler(PubReadActivity view) {
			this.mFragmentView = new WeakReference<PubReadActivity>(view);
		}

		@Override
		public void handleMessage(Message msg) {
			PubReadActivity service = mFragmentView.get();
			if (service != null) {
				super.handleMessage(msg);
				try {
					switch (msg.what) {
					case LIGHT_CHANGE:
	                    service.switchWakeLock(false);
	                    break;
					}
				} catch (Exception e) {
					LogM.e(service.TAG, e.toString());
				}
			}
		}
	}
    
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    protected void setLightsOutMode(boolean isLightsOut) {
        isLightsOut=false;
        if (android.os.Build.VERSION.SDK_INT >= 14) {
            if (isLightsOut) {
                mRootView
                        .setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
            } else {
                mRootView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
            }
        }
    }

    public interface OnBookMarkListener {

        void addMark();

        void removeMark();

    }

    public interface OnBookFollowListener {

        void follow();

        void unFlollow();

    }

    @Override
    public boolean isSwipeBack() {
        return false;
    }

    /**
     * 设置阅读正常退出
     */
    protected void exitNormal(){
        ReadConfig.getConfig().setIsReadNormalExit(true);
    }

}
