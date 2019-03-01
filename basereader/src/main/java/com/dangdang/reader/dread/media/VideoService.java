package com.dangdang.reader.dread.media;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Rect;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.dangdang.reader.dread.config.ReadConfig;
import com.dangdang.reader.dread.holder.MediaHolder.MediaType;
import com.dangdang.reader.dread.media.FileEntry.FileType;
import com.dangdang.reader.dread.media.StreamOverHttp.PrepareListener;
import com.dangdang.reader.dread.view.VideoControlRelativeLayout;
import com.dangdang.reader.R;

public class VideoService extends BaseMediaService implements
		SurfaceHolder.Callback, OnPreparedListener, OnCompletionListener,
		OnErrorListener {

	protected static final int MSG_UPDATE_PROGRESS = 0;
	protected static final int MSG_HIDE_CONTROL = 1;
	private MediaPlayer mMediaPlayer;
	private SurfaceView mSurfaceView;
	private SurfaceHolder mSurfaceHolder;
	private ViewGroup mVideoView;
	private Rect mRect;
	private ProgressBar mProgressBar;
	private VideoControlRelativeLayout mControlRL;
	private Context mContext;
	private boolean mIsLoading;
	private boolean mIsPrepared;
	private int mLightInterval = 0;

	public VideoService() {
		super();
		handleProgress = new MyHandler(this);
	}

	public void prepare(String innerPath, String path, int bookType,
			PrepareListener l) throws IOException {
		mIsLoading = true;
		showProgressBar();
		openServer(innerPath, path, MediaType.Video, FileType.FileInner,
				bookType, l);
	}

	public boolean isPrepared() {
		return mIsPrepared;
	}

	public boolean isLoading() {
		return mIsLoading;
	}

	public View initView(Context context, Rect imgRect) {
		if (mVideoView == null) {
			this.mContext = context;
			this.mRect = imgRect;
			mVideoView = (ViewGroup) View.inflate(context,
					R.layout.reader_video_layout, null);
			mProgressBar = (ProgressBar) mVideoView
					.findViewById(R.id.progressBar);
			mProgressBar.setIndeterminate(true);
			mProgressBar.setIndeterminateDrawable(mContext.getResources()
					.getDrawable(R.drawable.progress_circle));
			RelativeLayout progressRL = (RelativeLayout) mVideoView
					.findViewById(R.id.progressRL);
			RelativeLayout.LayoutParams layoutParams = (android.widget.RelativeLayout.LayoutParams) progressRL
					.getLayoutParams();
			layoutParams.width = imgRect.width();
			layoutParams.height = imgRect.height();
			layoutParams.topMargin = imgRect.top;
			layoutParams.leftMargin = imgRect.left;
			progressRL.setLayoutParams(layoutParams);
			mControlRL = (VideoControlRelativeLayout) mVideoView
					.findViewById(R.id.video_control_vcrl);
			mControlRL.setOnClickListener(mOnClickListener);
			mControlRL.setOnSeekBarChangeListener(mSeekBarChangeListener);
		}
		mProgressBar.setVisibility(View.VISIBLE);
		return mVideoView;
	}

	public void initSurfaceView(Context context) {
		setLayoutParams(mRect);
		mSurfaceHolder.addCallback(this);
		mSurfaceView.setOnClickListener(mOnClickListener);
		mVideoView.setVisibility(View.VISIBLE);
	}

	public void showProgressBar() {
		mIsPrepared = false;
		if (mOnVideoListener != null) {
			mOnVideoListener.onPrepare();
		}
		mProgressBar.setVisibility(View.VISIBLE);
	}

	private void setLayoutParams(Rect rect) {
		FrameLayout mVideoPlayFL = (FrameLayout) mVideoView
				.findViewById(R.id.video_play_fl);
		RelativeLayout.LayoutParams layoutParams = (android.widget.RelativeLayout.LayoutParams) mVideoPlayFL
				.getLayoutParams();
		layoutParams.width = rect.width();
		layoutParams.height = rect.height();
		layoutParams.topMargin = rect.top;
		layoutParams.leftMargin = rect.left;
		mVideoPlayFL.setLayoutParams(layoutParams);
		mSurfaceView = (SurfaceView) mVideoView.findViewById(R.id.surfaceview);
		LayoutParams surfaceLayoutParams = mSurfaceView.getLayoutParams();
		surfaceLayoutParams.width = LayoutParams.MATCH_PARENT;
		surfaceLayoutParams.height = LayoutParams.MATCH_PARENT;
		mSurfaceView.setLayoutParams(surfaceLayoutParams);
		mSurfaceHolder = mSurfaceView.getHolder();
		mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		mVideoPlayFL.setOnClickListener(mOnClickListener);
		mSurfaceView.setOnClickListener(mOnClickListener);
	}

	protected void initMedia() {
		if (mMediaPlayer == null) {
			mMediaPlayer = new MediaPlayer();
		}
		mMediaPlayer.reset();
		mMediaPlayer.setVolume(50, 50);
		mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
		mMediaPlayer.setOnCompletionListener(this);
		mMediaPlayer.setDisplay(mSurfaceHolder);
		mMediaPlayer.setOnPreparedListener(this);
		mMediaPlayer.setOnErrorListener(this);
	}

	protected void loadMedia() {
		try {
			mMediaPlayer.setDataSource(getLocalServerPath());// "/sdcard/aVideo/李荣浩-模特(live).mp3"
			mMediaPlayer.prepareAsync();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}

	@Override
	public boolean onError(MediaPlayer mp, int what, int extra) {
		// Toast.makeText(mContext,
		// mContext.getString(R.string.fileexception_noread),
		// Toast.LENGTH_LONG).show();
		if (mOnVideoListener != null) {
			mOnVideoListener.reset();
		}
		return false;
	}

	@Override
	public void onPrepared(MediaPlayer mp) {
		mMediaPlayer.start();
		mIsPrepared = true;
		mProgressBar.setVisibility(View.GONE);
		setLightIntervalForever();
	}

	private void setLightIntervalForever() {
		mLightInterval = ReadConfig.getConfig().getLightInterval();
		ReadConfig.getConfig().setLightInterval(
				ReadConfig.READER_LIGHT_INTERVAL_FOREVER);
	}

	private void recoverLightIntervalForever() {
		if (mLightInterval != 0) {
			ReadConfig.getConfig().setLightInterval(mLightInterval);
		}
	}

	public void playAndPause() {
		if (mMediaPlayer != null) {
			if (mMediaPlayer.isPlaying()) {
				mMediaPlayer.pause();
			} else {
				mMediaPlayer.start();
			}
		} else {
			printLogE(" playAndPause Player is null ");
		}
	}

	public boolean isPlaying() {
		return mMediaPlayer != null && mIsPrepared && mMediaPlayer.isPlaying();
	}

	public boolean isShow() {
		return mIsLoading;
	}

	public boolean isVideoLandscape() {
		return mIsLandscape;
	}

	public void seekTo(int msec) {
		if (mMediaPlayer != null) {
			mMediaPlayer.seekTo(msec);
		}
	}

	public void stop() {
		if (mMediaPlayer != null) {
			if (mMediaPlayer.isPlaying()) {
				mMediaPlayer.stop();
			}
			mMediaPlayer.release();
			mMediaPlayer = null;
		}
	}

	public MediaPlayer getPlayer() {
		return mMediaPlayer;
	}

	public SurfaceView getSurfaceView() {
		return mSurfaceView;
	}

	public void destroy() {
		destroyWithOutOrientation();
		changeVideoOrientation();
	}

	public void destroyAndDeleteFile() {
		changeVideoOrientation();
		destroyWithOutOrientation();
		deleteFile();
	}

	public void destroyWithOutOrientation() {
		mIsLandscape = false;
		mIsLoading = false;
		mIsPrepared = false;
		mOnVideoListener = null;
		recoverLightIntervalForever();
		stop();
		cancelTimer();
		new Thread(new Runnable() {

			@Override
			public void run() {
				closeServer();
			}
		}).start();
	}

	private void deleteFile() {
		try {
			File file = new File(mPath);
			if (file.exists()) {
				file.delete();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void cancelTimer() {
		if (mTimer != null) {
			mTimer.cancel();
			mTimer = null;
		}
	}

	public void setOnCompletionListener(OnCompletionListener l) {
		if (mMediaPlayer != null) {
			mMediaPlayer.setOnCompletionListener(l);
		} else {
			printLogE(" setOnCompletionListener l is null ");
		}
	}

	public void setOnBufferingUpdateListener(OnBufferingUpdateListener l) {
		if (mMediaPlayer != null) {
			mMediaPlayer.setOnBufferingUpdateListener(l);
		} else {
			printLogE(" OnBufferingUpdateListener l is null ");
		}
	}

	public void setOnPreparedListener(OnPreparedListener l) {
		if (mMediaPlayer != null) {
			mMediaPlayer.setOnPreparedListener(l);
		} else {
			printLogE(" setOnPreparedListener l is null ");
		}
	}

	public void setOnErrorListener(OnErrorListener l) {
		if (mMediaPlayer != null) {
			mMediaPlayer.setOnErrorListener(l);
		} else {
			printLogE(" setOnErrorListener l is null ");
		}
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		initMedia();
		loadMedia();
		initProgressTimer();
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
	}

	private boolean mIsLandscape;
	OnClickListener mOnClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			int i = v.getId();
			if (i == R.id.video_play_pause_iv) {
				pausePlayMediaPlayer();

			} else if (i == R.id.video_orientation_iv) {
				if (!mIsLandscape) {
					setOrientationLandscape();
				} else {
					setOrientationPortrait();
				}
				onConfigurationChanged();

			} else if (i == R.id.video_play_fl) {
				setVideoControlVisiable();

			} else if (i == R.id.surfaceview) {
				setVideoControlVisiable();

			}
		}

	};

	private void setOrientationPortrait() {
		mIsLandscape = false;
		if (mContext != null) {
			((Activity) mContext)
					.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}
	}

	private void setOrientationLandscape() {
		mIsLandscape = true;
		if (mContext != null) {
			((Activity) mContext)
					.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

		}
	}

	public void onConfigurationChanged() {
		if (!mIsLandscape) {
			setLayoutParams(mRect);
		} else {
			setFillScreen();
		}

	}

	private void setFillScreen() {
		FrameLayout mVideoPlayFL = (FrameLayout) mVideoView
				.findViewById(R.id.video_play_fl);
		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		mVideoPlayFL.setLayoutParams(layoutParams);
		mSurfaceView = (SurfaceView) mVideoView.findViewById(R.id.surfaceview);
		FrameLayout.LayoutParams surfaceLayoutParams = (FrameLayout.LayoutParams) mSurfaceView
				.getLayoutParams();
		float minScale = getMinScale();
		surfaceLayoutParams.width = (int) (mRect.width() * minScale);
		surfaceLayoutParams.height = (int) (mRect.height() * minScale);
		surfaceLayoutParams.gravity = Gravity.CENTER;
		mSurfaceView.setLayoutParams(surfaceLayoutParams);
	}

	private float getMinScale() {
		DisplayMetrics dm = new DisplayMetrics();
		Display d = ((Activity) mContext).getWindowManager()
				.getDefaultDisplay();
		d.getMetrics(dm);
		int screenWith = dm.widthPixels;
		int screenHeight = dm.heightPixels;
		float wScale = ((float) screenWith) / mRect.width();
		float hScale = ((float) screenHeight) / mRect.height();
		return wScale > hScale ? hScale : wScale;
	}

	private Timer mTimer;

	private void pausePlayMediaPlayer() {
		if (mMediaPlayer != null) {
			mControlRL.setPausePlayIV(mMediaPlayer.isPlaying());
			if (mMediaPlayer.isPlaying()) {
				mMediaPlayer.pause();
			} else {
				mMediaPlayer.start();
			}
		}
	}

	private int getProgress() {
		return mMediaPlayer == null ? 0 : mMediaPlayer.getCurrentPosition();
	}

	private int getDuration() {
		return mMediaPlayer == null ? 0 : mMediaPlayer.getDuration();
	}

	private void setVideoControlVisiable() {
		if (mIsPrepared) {
			if (mControlRL.getVisibility() == View.VISIBLE) {
				mControlRLVisiable = false;
				mControlRL.setVisibility(View.GONE);
			} else {
				setProgress(true, getProgress(), getDuration());
				mControlRL.setVisibility(View.VISIBLE);
				mControlRLVisiable = true;
			}
		}
	}

	private int count;

	protected void initProgressTimer() {
		mTimer = new Timer();
		mTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				if (!mIsPrepared) {
					return;
				}
				if (mControlRLVisiable) {
					count += 1;
					if (count > 3) {
						mControlRLVisiable = false;
						count = 0;
						handleProgress.sendEmptyMessage(MSG_HIDE_CONTROL);
					}
				} else {
					count = 0;
				}
				handleProgress.sendEmptyMessage(MSG_UPDATE_PROGRESS);
			}
		}, 0, 1000);
	}

	private Handler handleProgress;
	
	private void dealMsg(Message msg){
		MediaPlayer player = getPlayer();
		if (player == null) {
			printLogE(" player == null ");
			return;
		}
		switch (msg.what) {
		case MSG_HIDE_CONTROL:
			setVideoControlGone();
			break;

		case MSG_UPDATE_PROGRESS:
			handleUpdateProgress(player);
			break;
		}
	}

	private static class MyHandler extends Handler {
		private final WeakReference<VideoService> mFragmentView;

		MyHandler(VideoService view) {
			this.mFragmentView = new WeakReference<VideoService>(view);
		}

		@Override
		public void handleMessage(Message msg) {
			VideoService service = mFragmentView.get();
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
	
	private void handleUpdateProgress(MediaPlayer player) {
		if (mIsPrepared && player.isPlaying() && !mControlRL.isSeekBarPressed()) {
			int duration = player.getDuration();
			int progress = player.getCurrentPosition();
			if (duration > 0) {
				if (progress >= duration) {
					// playerFinish();
				} else {
					setProgress(false, progress, duration);
				}
			}
		}
	}

	private void setVideoControlGone() {
		mControlRLVisiable = false;
		mControlRL.setVisibility(View.GONE);
	}

	private boolean mControlRLVisiable;

	private void setProgress(boolean isForce, int progress, int duration) {
		if (mControlRL != null && mMediaPlayer != null
				&& mMediaPlayer.isPlaying()) {
			if (isForce || mControlRL.getVisibility() == View.VISIBLE) {
				mControlRL.updatePregress(progress, duration);
			}
		}
	};

	final OnSeekBarChangeListener mSeekBarChangeListener = new OnSeekBarChangeListener() {

		@Override
		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) {
		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {

		}

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			int progress = seekBar.getProgress();
			int max = seekBar.getMax();
			if (progress == max) {
				playerFinish();

				if (!mMediaPlayer.isPlaying()) {
					progress = 0;
					return;
				}
			}
			mMediaPlayer.seekTo(progress);
		}

	};

	protected void playerFinish() {
		MediaPlayer player = getPlayer();
		if (player == null) {
			return;
		}
		try {
			int progress = 0;
			int duration = getDuration();
			setProgress(true, progress, duration);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setOnVideoListener(OnVideoListener onVideoListener) {
		this.mOnVideoListener = onVideoListener;
	}

	@Override
	public void onCompletion(MediaPlayer mp) {
		// if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
		// mMediaPlayer.stop();
		// }
		// closeServer();
		if (mIsLandscape && mContext != null) {
			mIsLandscape = false;
			((Activity) mContext)
					.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}
		if (mOnVideoListener != null) {
			mOnVideoListener.onCompletion();
		}
	}

	private OnVideoListener mOnVideoListener;

	public interface OnVideoListener {
		public void onPrepare();

		public void onCompletion();

		public void reset();
	}

	public boolean changeVideoOrientation() {
		boolean isChange = false;
		if (mIsLandscape) {
			isChange = true;
			setOrientationPortrait();
			onConfigurationChanged();
		}
		return isChange;
	}

}
