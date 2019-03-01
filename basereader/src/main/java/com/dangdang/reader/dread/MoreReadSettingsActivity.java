package com.dangdang.reader.dread;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import com.dangdang.reader.R;
import com.dangdang.reader.base.BaseStatisActivity;
import com.dangdang.reader.db.service.DDStatisticsService;
import com.dangdang.reader.dread.config.ReadConfig;
import com.dangdang.reader.dread.config.ReadConfig.PageTurnMode;
import com.dangdang.reader.dread.core.base.IReaderController.DAnimType;
import com.dangdang.reader.dread.core.epub.ReaderAppImpl;
import com.dangdang.reader.dread.font.FontListHandle;
import com.dangdang.reader.dread.function.FunctionCode;
import com.dangdang.reader.dread.jni.BaseJniWarp;
import com.dangdang.reader.utils.Constant;
import com.dangdang.reader.view.SlipPButton;
import com.dangdang.reader.view.SlipPButton.OnChangedListener;
import com.dangdang.zframework.log.LogM;
import com.dangdang.zframework.utils.DRUiUtility;
import com.dangdang.zframework.view.DDImageView;
import com.dangdang.zframework.view.DDTextView;

import java.lang.ref.WeakReference;
import java.util.Arrays;

/**
 * 更多设置
 */
public class MoreReadSettingsActivity extends BaseStatisActivity {

    public static final int REQ_UPDATE_SETTING = 5;
    public static final int RESULT_UPDATE_BG = 1;
    public static final int RESULT_CHINESE_CONVERT = 2;
    public static final String STRING_RESULT_UPDATE_BG = "result_update_bg";
    public static final String STRING_RESULT_CHINESE_CONVERT = "result_chinese_convert";
    private DDStatisticsService mDDService;
    private ReadConfig mConfig;

    private DDTextView mCurrentFont;
    private Button mSleepBtn1;
    private Button mSleepBtn2;
    private Button mSleepBtn3;
    private Button mSleepBtn4;
    private Button mFlipBtn1;
    private Button mFlipBtn2;
    private Button mFlipBtn3;
    private DDImageView mNightBtn1;
    private DDImageView mNightBtn2;
    private DDImageView mNightBtn3;
    private DDImageView mNightBtn4;
    private ViewGroup mColorLayout;

    private int mButtonTextNormalColor;
    private int mButtonTextSelectColor;

    private SlipPButton mBtnVolFlip;
    private boolean mIsFullScreen = true;

    private View mPageTurnDefault;
    private View mPageTurnSingle;

    private Handler mHandler;

    @Override
    protected void onCreateImpl(Bundle savedInstanceState) {
        try {
            mHandler = new MyHandler(this);
            setContentView(R.layout.read_more_settings);
            mIntent = new Intent();
            mConfig = ReadConfig.getConfig();
            mDDService = DDStatisticsService.getDDStatisticsService(this);
            mDDService.addData(DDStatisticsService.OTHER_SETTING_IN_READ,
                    DDStatisticsService.OPerateTime, System.currentTimeMillis()
                            + "");
            initUI();
            initFilpView();
            updateBgColor();
            initLightControl();
            initFullScreenStatus(mConfig.isFullScreen());

            // initFormatValue();
        } catch (Exception e) {
            e.printStackTrace();
            this.finish();
        }
    }

    private void initUI() {
        mCurrentFont = (DDTextView) findViewById(R.id.read_more_settings_text);
        initFontListHandle();
        mSleepBtn1 = (Button) findViewById(R.id.read_more_settings_sleep_btn1);
        mSleepBtn1.setOnClickListener(mSleepClickListener);
        mSleepBtn2 = (Button) findViewById(R.id.read_more_settings_sleep_btn2);
        mSleepBtn2.setOnClickListener(mSleepClickListener);
        mSleepBtn3 = (Button) findViewById(R.id.read_more_settings_sleep_btn3);
        mSleepBtn3.setOnClickListener(mSleepClickListener);
        mSleepBtn4 = (Button) findViewById(R.id.read_more_settings_sleep_btn4);
        mSleepBtn4.setOnClickListener(mSleepClickListener);
        findViewById(R.id.read_more_settings_text_layout).setOnClickListener(
                new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MoreReadSettingsActivity.this, FontsActivity.class);
                        startActivityForResult(intent, 1);
                    }
                });

        mFlipBtn1 = (Button) findViewById(R.id.read_more_settings_flip_btn1);
        mFlipBtn1.setOnClickListener(mFlipClickListener);
        mFlipBtn2 = (Button) findViewById(R.id.read_more_settings_flip_btn2);
        mFlipBtn2.setOnClickListener(mFlipClickListener);
        mFlipBtn3 = (Button) findViewById(R.id.read_more_settings_flip_btn3);
        mFlipBtn3.setOnClickListener(mFlipClickListener);

        mColorLayout = (ViewGroup) findViewById(R.id.deep_night_mode_layout);
        mNightBtn1 = (DDImageView) findViewById(R.id.deep_night_mode_1);
        mNightBtn1.setOnClickListener(mNightClickListener);
        mNightBtn2 = (DDImageView) findViewById(R.id.deep_night_mode_2);
        mNightBtn2.setOnClickListener(mNightClickListener);
        mNightBtn3 = (DDImageView) findViewById(R.id.deep_night_mode_3);
        mNightBtn3.setOnClickListener(mNightClickListener);
        mNightBtn4 = (DDImageView) findViewById(R.id.deep_night_mode_4);
        mNightBtn4.setOnClickListener(mNightClickListener);

        mButtonTextNormalColor = getResources().getColor(
                R.color.read_dir_text_default_color);
        mButtonTextSelectColor = getResources().getColor(R.color.white);
        ViewGroup backLayout = (ViewGroup) findViewById(R.id.activity_add_roster_title_ll);
        backLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        findViewById(R.id.common_back).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        ((TextView) findViewById(R.id.common_title)).setText("更多设置");
        final ReadConfig readConfig = ReadConfig.getConfig();

        mBtnVolFlip = (SlipPButton) findViewById(R.id.SlipButton_volumn);
        mBtnVolFlip.SetOnChangedListener(new SlipPButton.OnChangedListener() {
            public void OnChanged(SlipPButton sb, boolean checkState) {
                readConfig.setVolKeyFlip(checkState);
            }
        });
        mBtnVolFlip.setChecked(readConfig.isVolKeyFlip());

        SlipPButton fullSlipButton = (SlipPButton) findViewById(R.id.SlipButton_isFull);
        fullSlipButton.setChecked(readConfig.isFullScreen());
        fullSlipButton.SetOnChangedListener(new OnChangedListener() {
            @Override
            public void OnChanged(SlipPButton sb, boolean CheckState) {
                mDDService.addData(DDStatisticsService.FULL_SCREEN,
                        DDStatisticsService.OPerateTime,
                        System.currentTimeMillis() + "",
                        DDStatisticsService.SURRENT_VALUE,
                        CheckState ? DDStatisticsService.ON
                                : DDStatisticsService.OFF);
                readConfig.setFullScreen(CheckState);

                initFullScreenStatus(CheckState);
            }

        });
        mIsFullScreen = readConfig.isFullScreen();

        SlipPButton convertSlipPButton = (SlipPButton) findViewById(R.id.SlipButton_is_convert);
        final ReaderAppImpl readerApp = ReaderAppImpl.getApp();
        final boolean isUnSupport = !readerApp.getReadInfo().isSupportConvert();

        if (isUnSupport) {
            convertSlipPButton.setChecked(false);
            convertSlipPButton.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    readerApp.doFunction(FunctionCode.FCODE_SHOWTOAST, R.string.tips_unsupport_convert);
                    return true;
                }
            });
        } else {
            convertSlipPButton.setChecked(readConfig.getChineseConvert());
        }
        convertSlipPButton.SetOnChangedListener(new OnChangedListener() {

            @Override
            public void OnChanged(SlipPButton sb, boolean CheckState) {
                readConfig.setChineseConvert(MoreReadSettingsActivity.this,
                        CheckState);
                BaseJniWarp.setBig5Encoding(CheckState);
                setResult(REQ_UPDATE_SETTING, mIntent.putExtra(
                        STRING_RESULT_CHINESE_CONVERT, RESULT_CHINESE_CONVERT));
            }
        });
        mPageTurnDefault = findViewById(R.id.read_more_settings_pageturn_default);
        mPageTurnSingle = findViewById(R.id.read_more_settings_pageturn_single);

        setPageTurnMode(readConfig.getPageTurnMode());
        mPageTurnDefault.setOnClickListener(mPageTurnModeClickListener);
        mPageTurnSingle.setOnClickListener(mPageTurnModeClickListener);

        View spacingSetView = findViewById(R.id.read_more_settings_spacing);
        spacingSetView.setOnClickListener(mClickListener);

    }

    private void setPageTurnMode(int mode) {
        boolean isSingleHanded = PageTurnMode.isSingleHanded(mode);
        mPageTurnDefault.setSelected(!isSingleHanded);
        mPageTurnSingle.setSelected(isSingleHanded);
    }

    private void initFullScreenStatus(boolean isFullScreen) {
        DRUiUtility.setActivityFullScreenStatus(this, isFullScreen);
    }

    @Override
    public boolean isTransparentSystemBar() {
        return false;
    }

    final OnClickListener mClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            int i = v.getId();
            if (i == R.id.read_more_settings_spacing) {
                mDDService.addData(DDStatisticsService.INDIVIDUAL_DICTIONARY,
                        DDStatisticsService.OPerateTime,
                        System.currentTimeMillis() + "");
                Intent intent = new Intent(getApplicationContext(),
                        ReadSpacingActivity.class);
                startActivityForResult(intent, 2);

            }
        }
    };

    final OnClickListener mPageTurnModeClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int mode = PageTurnMode.MODE_DEFAULT;
            int i = v.getId();
            if (i == R.id.read_more_settings_pageturn_default) {
                mDDService.addData(DDStatisticsService.FLIP_PAGE_IN_READER,
                        DDStatisticsService.OPerateTime,
                        System.currentTimeMillis() + "",
                        DDStatisticsService.SURRENT_VALUE,
                        DDStatisticsService.CLASSICAL);
                mode = PageTurnMode.MODE_DEFAULT;

            } else if (i == R.id.read_more_settings_pageturn_single) {
                mDDService.addData(DDStatisticsService.FLIP_PAGE_IN_READER,
                        DDStatisticsService.OPerateTime,
                        System.currentTimeMillis() + "",
                        DDStatisticsService.SURRENT_VALUE,
                        DDStatisticsService.SINGLE);
                mode = PageTurnMode.MODE_SINGLEHAND;

            }
            mConfig.setPageTurnMode(mode);
            setPageTurnMode(mode);

            View ptTip = findViewById(R.id.read_more_settings_pageturn_tip);
            ptTip.setVisibility(View.VISIBLE);
            if (PageTurnMode.isSingleHanded(mode)) {
                findViewById(R.id.read_more_settings_pageturn_tip_bottom).setVisibility(View.VISIBLE);
            } else {
                findViewById(R.id.read_more_settings_pageturn_tip_bottom).setVisibility(View.GONE);
            }
            ptTip.startAnimation(AnimationUtils.loadAnimation(getApplication(),
                    R.anim.popwindow_fade_animation_start));
            mHandler.removeMessages(MSG_HIDE_PAGETURNMODE);
            mHandler.sendEmptyMessageDelayed(MSG_HIDE_PAGETURNMODE, 3 * 1000);
        }
    };

    final OnClickListener mSleepClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            clearSleepSelected();
            v.setSelected(true);
            ((Button) v).setTextColor(mButtonTextSelectColor);
            int interval = ReadConfig.READER_LIGHT_INTERVAL2;
            int i = v.getId();
            if (i == R.id.read_more_settings_sleep_btn1) {
                interval = ReadConfig.READER_LIGHT_INTERVAL2;

            } else if (i == R.id.read_more_settings_sleep_btn2) {
                interval = ReadConfig.READER_LIGHT_INTERVAL5;

            } else if (i == R.id.read_more_settings_sleep_btn3) {
                interval = ReadConfig.READER_LIGHT_INTERVAL10;

            } else if (i == R.id.read_more_settings_sleep_btn4) {
                interval = ReadConfig.READER_LIGHT_INTERVAL_FOREVER;

            } else {
            }
            ReadConfig.getConfig().setLightInterval(interval);
        }

        private void clearSleepSelected() {
            mSleepBtn1.setSelected(false);
            mSleepBtn2.setSelected(false);
            mSleepBtn3.setSelected(false);
            mSleepBtn4.setSelected(false);
            mSleepBtn1.setTextColor(mButtonTextNormalColor);
            mSleepBtn2.setTextColor(mButtonTextNormalColor);
            mSleepBtn3.setTextColor(mButtonTextNormalColor);
            mSleepBtn4.setTextColor(mButtonTextNormalColor);
        }
    };

    final OnClickListener mFlipClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            clearFlipSelected();
            v.setSelected(true);
            ((Button) v).setTextColor(mButtonTextSelectColor);
            int position = 0;
            int i = v.getId();
            if (i == R.id.read_more_settings_flip_btn1) {
                mDDService.addData(DDStatisticsService.LEFTTORIGHTFLIPPAGE,
                        DDStatisticsService.OPerateTime,
                        System.currentTimeMillis() + "");
                position = 0;

            } else if (i == R.id.read_more_settings_flip_btn2) {
                mDDService.addData(DDStatisticsService.UPTODOWNFLIPPAGE,
                        DDStatisticsService.OPerateTime,
                        System.currentTimeMillis() + "");
                position = 1;

            } else if (i == R.id.read_more_settings_flip_btn3) {
                mDDService.addData(DDStatisticsService.NOFLIPPAGE,
                        DDStatisticsService.OPerateTime,
                        System.currentTimeMillis() + "");
                position = 2;

            } else {
            }
            setAnimationType(position);
        }
    };

    private void clearFlipSelected() {
        mFlipBtn1.setSelected(false);
        mFlipBtn2.setSelected(false);
        mFlipBtn3.setSelected(false);
        mFlipBtn1.setTextColor(mButtonTextNormalColor);
        mFlipBtn2.setTextColor(mButtonTextNormalColor);
        mFlipBtn3.setTextColor(mButtonTextNormalColor);
    }

    private void initFilpView() {
        DAnimType type = mConfig.getAnimationTypeNew();
        switch (type) {
            case Shift:
                mFlipBtn1.setSelected(true);
                mFlipBtn1.setTextColor(mButtonTextSelectColor);
                break;
            case Slide:
                mFlipBtn2.setSelected(true);
                mFlipBtn2.setTextColor(mButtonTextSelectColor);
                break;
            case None:
                mFlipBtn3.setSelected(true);
                mFlipBtn3.setTextColor(mButtonTextSelectColor);
                break;
            default:
                break;
        }
    }

    public void setAnimationType(int position) {
        mConfig.setAnimationType(ReadConfig.ANIMATION_TYPE[position]);
    }

    final OnClickListener mNightClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            if (v.isSelected()) {
                printLog(" same bg ");
                return;
            }
            clearNightSelected();
            v.setSelected(true);
            int index = 0;
            int i = v.getId();
            if (i == R.id.deep_night_mode_1) {
                index = 0;

            } else if (i == R.id.deep_night_mode_2) {
                index = 1;

            } else if (i == R.id.deep_night_mode_3) {
                index = 2;

            } else if (i == R.id.deep_night_mode_4) {
                index = 3;

            } else {
            }
            int color = ReadConfig.READER_BG_COLOR_NIGHT[index];
            ReadConfig config = ReadConfig.getConfig();
            config.setNightMode(true);
            config.setReaderBgColorNight(color);
            // setResult(RESULT_UPDATE_BG);
            setResult(REQ_UPDATE_SETTING,
                    mIntent.putExtra(STRING_RESULT_UPDATE_BG, RESULT_UPDATE_BG));
        }

        private void clearNightSelected() {
            mNightBtn1.setSelected(false);
            mNightBtn2.setSelected(false);
            mNightBtn3.setSelected(false);
            mNightBtn4.setSelected(false);
        }
    };

    public void initFontListHandle() {
        FontListHandle handle = FontListHandle.getHandle(getApplicationContext());
        mCurrentFont.setText(getText(R.string.read_settings_font_name) + handle.getDefaultFontName());
    }

    @Override
    protected void onDestroyImpl() {
        try {
            if (mIsFullScreen != ReadConfig.getConfig().isFullScreen()) {
                Intent intent = new Intent(Constant.ACTION_READAREA_CHANGED);
                sendBroadcast(intent);
            }
            mHandler.removeMessages(MSG_HIDE_PAGETURNMODE);
        } catch (Exception e) {
            LogM.e(TAG, e.toString());
        }
    }

    private void updateBgColor() {
        if (!ReadConfig.getConfig().isNightMode()) {
            return;
        }
        int index = Arrays.asList(ReadConfig.READER_BG_COLOR_NIGHT).indexOf(
                ReadConfig.getConfig().getReaderBgColorNight());
        if (index < 0)
            return;
        View color = mColorLayout.getChildAt(index);
        color.setSelected(true);
    }

    private void initLightControl() {
        int inteval = ReadConfig.getConfig().getLightInterval();
        View v = mNightBtn1;
        switch (inteval) {
            case ReadConfig.READER_LIGHT_INTERVAL2:
                v = mSleepBtn1;
                break;
            case ReadConfig.READER_LIGHT_INTERVAL5:
                v = mSleepBtn2;
                break;
            case ReadConfig.READER_LIGHT_INTERVAL10:
                v = mSleepBtn3;
                break;
            case ReadConfig.READER_LIGHT_INTERVAL_FOREVER:
                v = mSleepBtn4;
                break;
            default:
                break;
        }
        v.setSelected(true);
        ((Button) v).setTextColor(mButtonTextSelectColor);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        initFontListHandle();
        // initFormatValue();
    }

	/*
     * private void initFormatValue(){ TextView spacingTipView = (TextView)
	 * findViewById(R.id.read_more_settings_spacingtip); ReadConfig readCofig =
	 * ReadConfig.getConfig(); int lineSpacingToPx =
	 * ReadConfig.getConfig().getLineSpacingToPx(readCofig.getLineSpacing());
	 * String spacingNumTip = getString(R.string.read_spacing_num_tip,
	 * (int)readCofig.getPaddingLeft(), lineSpacingToPx);
	 * spacingTipView.setText(spacingNumTip); }
	 */

    protected void onResume() {
        // 调用顺序
        super.onResume();
        // 友盟统计
//        UmengStatistics.onPageStart(getClass().getSimpleName());
    }

    protected void onPause() {
        // 调用顺序
//        UmengStatistics.onPageEnd(getClass().getSimpleName()); // 友盟统计 保证
        // onPageEnd
        // 在onPause
        // 之前调用,因为
        // onPause
        // 中会保存信息
        super.onPause();
    }

    private final static int MSG_HIDE_PAGETURNMODE = 1;

    private void dealMsg(Message msg) {
        switch (msg.what) {
            case MSG_HIDE_PAGETURNMODE:
                View ptTip = findViewById(R.id.read_more_settings_pageturn_tip);
                ptTip.startAnimation(AnimationUtils.loadAnimation(
                        getApplication(), R.anim.popwindow_fade_animation_end));
                ptTip.setVisibility(View.GONE);
                break;
        }
    }

    private static class MyHandler extends Handler {
        private final WeakReference<MoreReadSettingsActivity> mFragmentView;

        MyHandler(MoreReadSettingsActivity view) {
            this.mFragmentView = new WeakReference<MoreReadSettingsActivity>(view);
        }

        @Override
        public void handleMessage(Message msg) {
            MoreReadSettingsActivity service = mFragmentView.get();
            if (service != null) {
                super.handleMessage(msg);
                try {
                    service.dealMsg(msg);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private Intent mIntent;
}