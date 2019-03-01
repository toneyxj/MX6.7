package com.dangdang.reader.dread.view.toolbar;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;

import com.dangdang.reader.R;
import com.dangdang.reader.dread.view.ProgressTrainingWheel;
import com.dangdang.reader.dread.view.ProgressWheel;
import com.dangdang.zframework.log.LogM;
import com.dangdang.zframework.view.DDImageView;
import com.dangdang.zframework.view.DDTextView;

public class BottomMorePopView extends RelativeLayout {

	private OnClickListener mReaderListener;

	private ProgressTrainingWheel mProgressBar;
	//private DDImageView mComments;
	DDTextView mTextView;

	public BottomMorePopView(Context context) {
		super(context);
	}

	public BottomMorePopView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public void setButtonClickListener(OnClickListener clickListener) {
		mReaderListener = clickListener;
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		mProgressBar = (ProgressTrainingWheel) findViewById(R.id.read_bottom_training_dayfinishrate);
		mProgressBar.setOnClickListener(mListener);
		mTextView = (DDTextView) findViewById(R.id.read_finishrate_wheel_text);
	}
	


	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		updateToolbarStatus();
	}
	

	private OnClickListener mListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			mReaderListener.onClick(v);
		}
	};

	private void updateToolbarStatus() {
		/*ReaderAppImpl readerApps = ReaderAppImpl.getApp();
		ReadInfo readInfo = (ReadInfo) readerApps.getReadInfo();
		if (readInfo.isDDBook()) {
			mComments.setVisibility(VISIBLE);
		} else {
			mComments.setVisibility(GONE);
		}*/
	}
	
	protected void pringLog(String log){
		LogM.i(getClass().getSimpleName(), log);
	}

	public void updateCurReadProgressRate(float finishRate) {
		int rate = (int)finishRate;
		if (finishRate - rate > 0.001)
			rate += 1;
		if (mProgressBar != null)
			mProgressBar.startProgress((int)rate);
		if (mTextView != null)
			mTextView.setText(String.format("%02d%%", rate));
	}
}