package com.dangdang.reader.dread.view.toolbar;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.dangdang.reader.R;
import com.dangdang.reader.dread.MoreReadSettingsActivity;
import com.dangdang.reader.dread.config.ReadConfig;
import com.dangdang.zframework.BaseActivity;
import com.dangdang.zframework.log.LogM;
import com.dangdang.zframework.view.DDImageView;

import java.util.Arrays;

public class DetailSettingToolbar extends RelativeLayout {

    private OnClickListener mFontListener;
    private OnClickListener mBgListener;
    DDImageView[] mColorView = new DDImageView[8];
    private DDImageView mColor1;
    private DDImageView mColor2;
    private DDImageView mColor3;
    private DDImageView mColor4;
    private DDImageView mColor5;
    private DDImageView mColor6;
    private DDImageView mColor7;
    private DDImageView mColor8;
    private TextView mMoreSettings;
    private DDImageView mFontZoomIn;
    private DDImageView mFontZoomOut;
    private DDImageView mLineSpacingS;
    private DDImageView mLineSpacingM;
    private DDImageView mLineSpacingL;
    private DDImageView mLineSpacingX;
    private DDImageView mLightSystem;
    private SeekBar mLightSeekBar;
    private ViewGroup mColorLayout;
    private OnSeekBarChangeListener mReaderLightSeekListener;
    private ReaderToolbar.ToolbarListener mToolbarListener;

    public DetailSettingToolbar(Context context) {
        super(context);
    }

    public DetailSettingToolbar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        updateLightProgress();
        updateClickSystemLight(ReadConfig.getConfig().isSystemLight());
        updateBgColor();
        initLineSpacingImg();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mColorLayout = (ViewGroup) findViewById(R.id.read_detail_color);
        mColorView[0] = (DDImageView) findViewById(R.id.toolbar_font_color_1);
        mColorView[1] = (DDImageView) findViewById(R.id.toolbar_font_color_2);
        mColorView[2] = (DDImageView) findViewById(R.id.toolbar_font_color_3);
        mColorView[3] = (DDImageView) findViewById(R.id.toolbar_font_color_4);
        mColorView[4] = (DDImageView) findViewById(R.id.toolbar_font_color_5);
        mColorView[5] = (DDImageView) findViewById(R.id.toolbar_font_color_6);
        mColorView[6] = (DDImageView) findViewById(R.id.toolbar_font_color_7);
        mColorView[7] = (DDImageView) findViewById(R.id.toolbar_font_color_8);
        mMoreSettings = (TextView) findViewById(R.id.read_more_settings);
        mFontZoomIn = (DDImageView) findViewById(R.id.read_font_zoom_in_layout);
        mFontZoomOut = (DDImageView) findViewById(R.id.read_font_zoom_out_layout);
        mLineSpacingS = (DDImageView) findViewById(R.id.read_line_spacing_s);
        mLineSpacingM = (DDImageView) findViewById(R.id.read_line_spacing_m);
        mLineSpacingL = (DDImageView) findViewById(R.id.read_line_spacing_l);
        mLineSpacingX = (DDImageView) findViewById(R.id.read_line_spacing_x);
        mLightSystem = (DDImageView) findViewById(R.id.read_detail_light_sys);
        mLightSeekBar = (SeekBar) findViewById(R.id.read_detail_light_progress);
        mColorView[0].setOnClickListener(mListener);
        mColorView[1].setOnClickListener(mListener);
        mColorView[2].setOnClickListener(mListener);
        mColorView[3].setOnClickListener(mListener);
        mColorView[4].setOnClickListener(mListener);
        mColorView[5].setOnClickListener(mListener);
        mColorView[6].setOnClickListener(mListener);
        mColorView[7].setOnClickListener(mListener);
        mMoreSettings.setOnClickListener(mListener);
        mFontZoomIn.setOnClickListener(mListener);
        mFontZoomOut.setOnClickListener(mListener);
        mLineSpacingS.setOnClickListener(mListener);
        mLineSpacingM.setOnClickListener(mListener);
        mLineSpacingL.setOnClickListener(mListener);
        mLineSpacingX.setOnClickListener(mListener);
        mLightSystem.setOnClickListener(mListener);
        mLightSeekBar.setOnSeekBarChangeListener(mLightSeekListener);
    }

    /**
     * 初始化行间距按钮
     *
     * @param
     */
    private void initLineSpacingImg() {
        final ReadConfig readConfig = ReadConfig.getConfig();
        float lineSpacing = readConfig.getLineSpacing();
        mLineSpacingS.setEnabled(lineSpacing != ReadConfig.LINESPACING_DEFAULT_S);
        mLineSpacingM.setEnabled(lineSpacing != ReadConfig.LINESPACING_DEFAULT_M);
        mLineSpacingL.setEnabled(lineSpacing != ReadConfig.LINESPACING_DEFAULT_L);
        mLineSpacingX.setEnabled(lineSpacing != ReadConfig.LINESPACING_DEFAULT_X);
    }

    /**
     * 更新行间距按钮状态
     *
     * @param id
     */
    private void changeLineSpacingImg(int id) {
        mLineSpacingS.setEnabled(id != R.id.read_line_spacing_s);
        mLineSpacingM.setEnabled(id != R.id.read_line_spacing_m);
        mLineSpacingL.setEnabled(id != R.id.read_line_spacing_l);
        mLineSpacingX.setEnabled(id != R.id.read_line_spacing_x);
    }

    public void setLightSeekListener(OnSeekBarChangeListener l) {
        mReaderLightSeekListener = l;
    }

    public void setFontClickListener(OnClickListener fontListener) {
        mFontListener = fontListener;
    }

    public void setBgClickListener(OnClickListener bgListener) {
        mBgListener = bgListener;
    }

    private OnSeekBarChangeListener mLightSeekListener = new OnSeekBarChangeListener() {

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            mReaderLightSeekListener.onStopTrackingTouch(seekBar);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            mReaderLightSeekListener.onStartTrackingTouch(seekBar);
            updateClickSystemLight(false);
            ReadConfig.getConfig().setSystemLight(false);
        }

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress,
                                      boolean fromUser) {
            mReaderLightSeekListener.onProgressChanged(seekBar, progress,
                    fromUser);
        }
    };

    private OnClickListener mListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            final ReadConfig readConfig = ReadConfig.getConfig();
            int lineWordNum = readConfig.getLineWordNum();
            int i = v.getId();
            if (i == R.id.read_more_settings) {
                BaseActivity a = ((BaseActivity) getContext());
                Intent intent = new Intent(a, MoreReadSettingsActivity.class);
                a.startActivityForResult(intent, MoreReadSettingsActivity.REQ_UPDATE_SETTING);
                mToolbarListener.switchToolbarShowing();

            } else if (i == R.id.toolbar_font_color_1 || i == R.id.toolbar_font_color_2 || i == R.id.toolbar_font_color_3 || i == R.id.toolbar_font_color_4 || i == R.id.toolbar_font_color_5 || i == R.id.toolbar_font_color_6 || i == R.id.toolbar_font_color_7 || i == R.id.toolbar_font_color_8) {
                if (v.isSelected()) {
                    LogM.i(getClass().getSimpleName(), " same bg ");
                    return;
                }
                clearSelectedColor();
                v.setSelected(true);
                ReadConfig.getConfig().setNightMode(false);
                updateLightProgress();
                mBgListener.onClick(v);

            } else if (i == R.id.read_font_zoom_in_layout) {
                if (lineWordNum <= readConfig.getMinLineWord()) {
                    setViewEnable(v, false);
                }
                setViewEnable(mFontZoomOut, true);
                mFontListener.onClick(v);

            } else if (i == R.id.read_font_zoom_out_layout) {
                if (lineWordNum >= readConfig.getMaxLineWord()) {
                    setViewEnable(v, false);
                }
                setViewEnable(mFontZoomIn, true);
                mFontListener.onClick(v);

            } else if (i == R.id.read_line_spacing_s || i == R.id.read_line_spacing_m || i == R.id.read_line_spacing_l || i == R.id.read_line_spacing_x) {
                changeLineSpacingImg(v.getId());
                mFontListener.onClick(v);

            } else if (i == R.id.read_detail_light_sys) {
                updateClickSystemLight(!v.isSelected());
                ReadConfig.getConfig().setSystemLight(v.isSelected());
                mBgListener.onClick(v);

            } else {
            }
        }
    };

    private void clearSelectedColor() {
        mColorView[0].setSelected(false);
        mColorView[1].setSelected(false);
        mColorView[2].setSelected(false);
        mColorView[3].setSelected(false);
        mColorView[4].setSelected(false);
        mColorView[5].setSelected(false);
        mColorView[6].setSelected(false);
        mColorView[7].setSelected(false);
    }

    protected void updateClickSystemLight(boolean selected) {
        mLightSystem.setSelected(selected);
        setLightSeekBar(!selected);
    }

    private void setViewEnable(View v, boolean enable) {
        if (v instanceof DDImageView) {
            Drawable drawable = ((DDImageView) v).getDrawable();
            if (enable) {
                drawable.clearColorFilter();
            } else {
                drawable.setColorFilter(
                        getResources().getColor(
                                R.color.read_toolbar_disable_mask),
                        PorterDuff.Mode.MULTIPLY);
            }
            v.invalidate();
        }
    }

    private void updateLightProgress() {
        final ReadConfig readConfig = ReadConfig.getConfig();
        final float screenLight = readConfig.getRealLight();
        final int progress = (int) ((screenLight - ReadConfig.MIN_LIGHT) * 100);
        mLightSeekBar.setProgress(progress);
    }

    private void updateBgColor() {
        clearSelectedColor();
        if (ReadConfig.getConfig().isNightMode()) {
            return;
        }
        int index = Arrays.asList(ReadConfig.READER_BG_COLOR_DAY).indexOf(
                ReadConfig.getConfig().getReaderBgColorDay());
        if (index < 0)
            return;

        if (index < mColorView.length)
            mColorView[index].setSelected(true);
    }

    private void setLightSeekBar(boolean enabled) {
        if (enabled) {
            if (android.os.Build.VERSION.SDK_INT >= 11) {
                mLightSeekBar.setThumb(getResources().getDrawable(
                        R.drawable.reader_bottom_progress_thumb));
            }
            mLightSeekBar.setSecondaryProgress(0);
        } else {
            if (android.os.Build.VERSION.SDK_INT >= 11) {
                mLightSeekBar.setThumb(getResources().getDrawable(
                        R.drawable.reader_bottom_progress_thumb_disabled));
            }
            mLightSeekBar.setSecondaryProgress(mLightSeekBar.getProgress());
        }
    }

    public void setToolbarListener(
            ReaderToolbar.ToolbarListener mToolbarListener) {
        this.mToolbarListener = mToolbarListener;
    }
}
