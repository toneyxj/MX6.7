package com.dangdang.reader.dread.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.dangdang.reader.R;
import com.dangdang.reader.utils.Utils;
import com.dangdang.zframework.view.DDTextView;

public class VideoControlRelativeLayout extends FrameLayout {
	private FrameLayout mVideoControlRL;
	private ImageView mPausePlayIV;
	private ImageView mOrientationIV;
	private OnClickListener mOnPausePlayListener;
	private OnSeekBarChangeListener mOnSeekBarChangeListener;

	public VideoControlRelativeLayout(Context context) {
		super(context);
		init(context);
	}

	public VideoControlRelativeLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	private void init(Context context) {
		mVideoControlRL = (FrameLayout) View.inflate(context,
				R.layout.video_control_layout, this);
		mPausePlayIV = (ImageView) mVideoControlRL
				.findViewById(R.id.video_play_pause_iv);
		mOrientationIV = (ImageView) mVideoControlRL
				.findViewById(R.id.video_orientation_iv);
		mSeekBar = (ReadSeekBar) mVideoControlRL
				.findViewById(R.id.video_seek_progress);
		mDurationTv = (DDTextView) mVideoControlRL
				.findViewById(R.id.video_duration);
		mProgressTv = (DDTextView) mVideoControlRL
				.findViewById(R.id.video_progress);

		mPausePlayIV.setOnClickListener(mOnClickListener);
		mOrientationIV.setOnClickListener(mOnClickListener);
		mSeekBar.setOnSeekBarChangeListener(mSeekLisenter);
	}

	public void setOnClickListener(OnClickListener clickListener) {
		mOnPausePlayListener = clickListener;
	}

	public void setOnSeekBarChangeListener(
			OnSeekBarChangeListener onSeekBarChangeListener) {
		this.mOnSeekBarChangeListener = onSeekBarChangeListener;
	}

	public void setPausePlayIV(boolean isPlay) {
		if (isPlay) {
			mPausePlayIV.setImageResource(R.drawable.read_video_play_bg);
		} else {
			mPausePlayIV.setImageResource(R.drawable.read_video_pause_bg);
		}
	}

	OnClickListener mOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			int i = v.getId();
			if (i == R.id.video_play_pause_iv) {
				if (mOnPausePlayListener != null) {
					mOnPausePlayListener.onClick(v);
				}

			} else if (i == R.id.video_orientation_iv) {
				if (mOnPausePlayListener != null) {
					mOnPausePlayListener.onClick(v);
				}

			}
		}
	};
	private ReadSeekBar mSeekBar;
	OnSeekBarChangeListener mSeekLisenter = new OnSeekBarChangeListener() {

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			if (mOnSeekBarChangeListener != null) {
				mOnSeekBarChangeListener.onStopTrackingTouch(seekBar);
			}
		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
			if (mOnSeekBarChangeListener != null) {
				mOnSeekBarChangeListener.onStartTrackingTouch(seekBar);
			}
		}

		@Override
		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) {
			if (mOnSeekBarChangeListener != null) {
				mOnSeekBarChangeListener.onProgressChanged(seekBar, progress,
						fromUser);
			}
		}
	};
	private DDTextView mDurationTv;
	private DDTextView mProgressTv;

	public boolean isSeekBarPressed() {
		return mSeekBar.isPressed();
	}

	public void updatePregress(int progress, int duration) {
		mSeekBar.setMax(duration);
		mSeekBar.setProgress(progress);
		setProgressTv(progress);
		setDurationTv(duration);
	}

	public void setProgressTv(long time) {
		String progress = Utils.dateFormatNoYear(time);
		mProgressTv.setText(progress);
	}

	public void setDurationTv(long time) {
		String duration = Utils.dateFormatNoYear(time);
		mDurationTv.setText("/" + duration);
	}
}
