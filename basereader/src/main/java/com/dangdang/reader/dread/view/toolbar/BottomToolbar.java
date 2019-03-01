package com.dangdang.reader.dread.view.toolbar;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.dangdang.reader.R;
import com.dangdang.reader.dread.config.ReadConfig;
import com.dangdang.reader.dread.core.epub.ReaderAppImpl;
import com.dangdang.reader.dread.data.ReadInfo;
import com.dangdang.reader.dread.view.ReadProgressSeekBar;
import com.dangdang.reader.dread.view.toolbar.ReaderToolbar.ToolbarListener;
import com.dangdang.reader.utils.Utils;
import com.dangdang.zframework.log.LogM;
import com.dangdang.zframework.view.DDImageView;

public class BottomToolbar extends RelativeLayout {

	private OnClickListener mReaderListener;
	private ReaderToolbar.onProgressBarChangeListener mProgressListener;
	private ToolbarListener mToolbarListener;
	private TextView mDmn;
	private TextView mSettings;
	private TextView mNight;
	private TextView mDay;

	private View mLoadingView;
	private ReadProgressSeekBar mSeekProgressBar;
	private ProgressBar mProgressBar;
	private int mProgressBarTop;
	private int mProgressBarLeft;
	private DDImageView mProgressPrevView;
	private int mProgressPrev;

	private boolean mDetectThumbPos;

	public BottomToolbar(Context context) {
		super(context);
	}

	public BottomToolbar(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		mDmn = (TextView) findViewById(R.id.read_bottom_dmn);
//		mFootprints = (TextView) findViewById(R.id.read_bottom_footprints);
		mSettings = (TextView) findViewById(R.id.read_bottom_settings);
		mNight = (TextView) findViewById(R.id.read_bottom_night);
		mDay = (TextView) findViewById(R.id.read_bottom_day);
		mSeekProgressBar = (ReadProgressSeekBar) findViewById(R.id.read_progress);
		mProgressBar = (ProgressBar)findViewById(R.id.read_compprogress);
		mLoadingView = findViewById(R.id.read_loading);
		mProgressPrevView = (DDImageView) findViewById(R.id.read_last_pos);

		mDmn.setOnClickListener(mListener);
//		mFootprints.setOnClickListener(mListener);
		mSettings.setOnClickListener(mListener);
		mNight.setOnClickListener(mListener);
		mDay.setOnClickListener(mListener);

		
		RelativeLayout.LayoutParams params = ((RelativeLayout.LayoutParams) mSeekProgressBar.getLayoutParams());
		mProgressBarLeft = params.leftMargin;
		mProgressBarTop = params.topMargin;
		mSeekProgressBar.setOnSeekBarChangeListener(mSeekLisenter);
		mSeekProgressBar.setVisibility(INVISIBLE);
		mProgressPrevView.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				mSeekProgressBar.setProgress(mProgressPrev);
				v.setVisibility(GONE);
				mProgressListener.onProgressBarChangeEnd(mProgressPrev);
				mDetectThumbPos = true;
				mReaderListener.onClick(v);
				return true;
			}
		});
	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		mDetectThumbPos = true;
		mProgressPrevView.setVisibility(GONE);
		updateNightMode();
	}

	public void setButtonClickListener(OnClickListener clickListener) {
		mReaderListener = clickListener;
	}

	public void setOnProgressBarChangeListener(ReaderToolbar.onProgressBarChangeListener progressListener) {
		mProgressListener = progressListener;
	}

	public void updateProgress(int pageIndexInBook, int pageSize) {
		mSeekProgressBar.setMax(pageSize);
		mSeekProgressBar.setProgress(pageIndexInBook);
	}
	
	public void updateRecompProgress(int curr, int total){
		//mSeekProgressBar.setMax(total);
		//mSeekProgressBar.setProgress(curr);
		mProgressBar.setMax(total);
		mProgressBar.setProgress(curr);
	}

	private OnClickListener mListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			int i = v.getId();
			if (i == R.id.read_bottom_settings) {
				mToolbarListener.showDetailSettingBar();

			} else if (i == R.id.read_bottom_night) {
				mDay.setVisibility(VISIBLE);
				mNight.setVisibility(GONE);
				ReadConfig.getConfig().setNightMode(true);

			} else if (i == R.id.read_bottom_day) {
				mNight.setVisibility(VISIBLE);
				mDay.setVisibility(GONE);
				ReadConfig.getConfig().setNightMode(false);

			} else {
			}
			mReaderListener.onClick(v);
		}
	};

	public void setToolbarListener(ToolbarListener toolbarListener) {
		mToolbarListener = toolbarListener;
	}

	private OnSeekBarChangeListener mSeekLisenter = new OnSeekBarChangeListener() {

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			mProgressListener.onProgressBarChangeEnd(seekBar.getProgress());
			mToolbarListener.hideDirPreviewBar();
		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
			if (mDetectThumbPos) {
				mProgressPrev = seekBar.getProgress();
				Rect thumbRect = null;
				try {
					thumbRect = mSeekProgressBar.getSeekBarThumb().getBounds();
				} catch (Exception e) {
					e.printStackTrace();
				}
				if(thumbRect == null){
					return;
				}
				RelativeLayout.LayoutParams params = ((RelativeLayout.LayoutParams) mProgressPrevView.getLayoutParams());
				params.topMargin = thumbRect.top + mProgressBarTop + getDiffValue(); 
				params.leftMargin = thumbRect.left + mProgressBarLeft - getDiffValue();
				mDetectThumbPos = false;
				mProgressPrevView.setVisibility(VISIBLE);
				requestLayout();
			}
			mToolbarListener.showDirPreviewBar(seekBar.getProgress());
		}

		@Override
		public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
			if (fromUser) {
				if (progress == 0)
					progress = 1;
				mToolbarListener.scrollDirPreviewBar(progress);
			}
		}
	};

	public void setBookLoadingFinish() {
		if(mLoadingView != null){
			mLoadingView.setVisibility(View.INVISIBLE);
		}
		mProgressBar.setVisibility(View.GONE);
		mSeekProgressBar.setClick();
		mSeekProgressBar.setVisibility(VISIBLE);
		mSeekProgressBar.requestLayout();
		mSeekProgressBar.invalidate();
	}
	
	protected int getDiffValue() {
		int diff = 0;
		try {
			diff = Utils.px2dip(getContext(), 2);
		} catch (Exception e) {
		}
		return diff;
	}

	private void updateNightMode() {
		if (ReadConfig.getConfig().isNightMode()) {
			mDay.setVisibility(VISIBLE);
			mNight.setVisibility(GONE);
		} else {
			mDay.setVisibility(GONE);
			mNight.setVisibility(VISIBLE);
		}
	}


	public void setBookLoadingStart() {
    	if(mLoadingView != null){
    		mLoadingView.setVisibility(View.VISIBLE);
    	}
        mProgressBar.setVisibility(View.VISIBLE);
    	mSeekProgressBar.setNoClick();
        mSeekProgressBar.setVisibility(View.GONE);
    }

    protected void printLog(String log){
    	LogM.i(getClass().getSimpleName(), log);
    }

}
