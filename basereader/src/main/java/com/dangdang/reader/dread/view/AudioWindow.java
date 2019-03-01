package com.dangdang.reader.dread.view;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.dangdang.reader.R;
import com.dangdang.reader.dread.config.ReadConfig;
import com.dangdang.reader.dread.jni.BaseJniWarp;
import com.dangdang.reader.dread.media.AudioService;
import com.dangdang.reader.dread.media.BaseMediaService.MediaListener;
import com.dangdang.reader.dread.media.StreamOverHttp.PrepareListener;
import com.dangdang.reader.utils.Utils;
import com.dangdang.reader.view.MyPopupWindow;
import com.dangdang.zframework.utils.DRUiUtility;
import com.dangdang.zframework.utils.UiUtil;

/**
 * @author luxu
 */
public class AudioWindow extends BaseWindow implements MediaListener, OnBufferingUpdateListener, OnCompletionListener, OnErrorListener, OnPreparedListener {
	
	private static final float ARROW_RES_WIDTH = 39;
	
	private Context mContext;
	private PopupWindow mWindow;
	private View mParent;
	private View mContentView;
	private int mCurrentX;
	private int mCurrentY;
	
	private ImageView mArrowView;
	private View mBackground;
	private RelativeLayout mRootView;
	
	private ImageView mPlayStatus;
	private TextView mPlayStatusText;
	private SeekBar mSeekBar;
	private TextView mPlayPrgs;
	
	private int mWindowHeight;
	private int mScrollViewWidth = 0;
	private int mScrollViewHeight = 0;
	private int mPopupWindowHeight = 0;
	private float mDensity = 1f;
	private int mScreenWidth = 0;
	private int left = 20;
	private int windowLeft;
	private int arrowLeft;
	
	private Point mFloatingPoint;
	
	private String mInnerPath;
	private String mPath;
	private int mBookType = BaseJniWarp.BOOKTYPE_DD_DRM_EPUB;
	private AudioService mAudioService;
	private Timer mTimer;
	private Handler mHandler;
	
	public AudioWindow(Context context, View parent){
		mContext = context;
		mParent = parent;
		
		mDensity = DRUiUtility.getDensity();
		mHandler = new MyHandler(this);
		
		final ReadConfig readConfig = ReadConfig.getConfig();
		mScreenWidth = readConfig.getReadWidth();
		//mScreenHeight = readConfig.getReadHeight();
		left = (int) (20 * mDensity);
		mWindowHeight = (int) (150 * mDensity);
		mFloatingPoint = new Point(0, 0);
		
		mScrollViewWidth = (int) (mScreenWidth);
		mScrollViewHeight = (int) (125 * mDensity);
		mPopupWindowHeight = mScrollViewHeight;
	}
	
	public void show(int x, int y, Rect imgRect, String innerPath, String localPath, int bookType){
		
		mInnerPath = innerPath;
		mPath = localPath;
		mBookType = bookType;
		try {
			initService();
		} catch (IOException e) {
			e.printStackTrace();
			showToast(R.string.fileexception_noread);
			return;
		}
		
		//setFloatingPoint(x, y);
		setFloatingPoint(getCenterPoint(x, y, imgRect));
		initView();
		int bottom = (int) (16 * mDensity);
		int readHeight = ReadConfig.getConfig().getReadHeight();
		if (mFloatingPoint.y + mWindowHeight > (readHeight - bottom)) { // 太靠下了
			showPopupWindowTop();
		} else {
			showPopupWindowBottom();
		}
	}
	
	private Point getCenterPoint(int x, int y, Rect imgRect){
		Point p = new Point(x, y);
		if(imgRect != null && imgRect.contains(x, y)){
			p.x = imgRect.centerX();
			p.y = imgRect.centerY();
		}
		return p;
	}
	
	public boolean isShowing() {
		if (mWindow != null) {
			return mWindow.isShowing();
		}
		return false;
	}

	public void hide() {
		if (mWindow != null) {
			mWindow.dismiss();
		}
		//stop();
	}
	
	public void destroy(){
		if(mTimer != null){
			mTimer.cancel();
		}
		mHandler.removeMessages(0);
		mHandler.removeMessages(MSG_AUDIO_INIT);
		if(mAudioService != null){
			mAudioService.destroy();
		}
		mContentView = null;
	}
	
	public void stop(){
		mHandler.removeMessages(0);
		mHandler.removeMessages(MSG_AUDIO_INIT);
		if(mAudioService != null){
			mAudioService.stop();
		}
	}
	
	private void initView() {
		if(mContentView == null){
			mContentView = LayoutInflater.from(mContext).inflate(R.layout.reader_audio_tip, null);
			mRootView = (RelativeLayout) mContentView.findViewById(R.id.reader_note_root_view);
			mArrowView = (ImageView) mContentView.findViewById(R.id.reader_note_arrow);
			mBackground = mContentView.findViewById(R.id.reader_note_bg);
			
			mPlayStatus = (ImageView) mContentView.findViewById(R.id.reader_playstatus);
			mPlayStatusText = (TextView) mContentView.findViewById(R.id.reader_playstatus_text);
			mSeekBar = (SeekBar) mContentView.findViewById(R.id.reader_playprogress);
			mPlayPrgs = (TextView) mContentView.findViewById(R.id.reader_playprgstext);
		}
		mPlayStatus.setOnClickListener(mClickListener);
		mSeekBar.setOnSeekBarChangeListener(mSeekBarChangeListener);
	}
	
	private void showPopupWindowTop() {
		RelativeLayout.LayoutParams lp1 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		lp1.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		lp1.bottomMargin = (int) (9 * mDensity);
		mRootView.updateViewLayout(mBackground, lp1);

		mArrowView.setImageResource(R.drawable.reader_note_arrow_down);
		mBackground.setBackgroundResource(R.drawable.reader_note_tip);
		
		mBackground.measure(lp1.width, lp1.height);
		/*mScrollViewWidth = mBackground.getMeasuredWidth();
		mScrollViewHeight = mBackground.getMeasuredHeight();*/

		calArrowLeft();
		RelativeLayout.LayoutParams lp2 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		lp2.leftMargin = arrowLeft;
		lp2.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		mRootView.updateViewLayout(mArrowView, lp2);
		initPopupWindow(mScrollViewWidth, mScrollViewHeight);
		mWindow.showAtLocation(mParent, Gravity.NO_GRAVITY, windowLeft, mFloatingPoint.y - mPopupWindowHeight - getFontSize());
	}
	
	public void setFloatingPoint(int x, int y) {
		this.mFloatingPoint.x = x;
		this.mFloatingPoint.y = y;
	}
	
	public void setFloatingPoint(Point point) {
		this.mFloatingPoint = point;
	}
	
	private int getFontSize() {
		return (int) ReadConfig.getConfig().getFontSize();
	}
	
	private void initPopupWindow(int width, int height) {
		/*if (width > (mScreenWidth - 2 * left)) {
			width = (mScreenWidth - 2 * left);
			int num = mScrollViewWidth / width + 1;
			height = mScrollViewHeight * num;
		}
		height += (int) (10 * mDensity);
		if (height > mWindowHeight) {
			height = mWindowHeight;
		}
		mPopupWindowHeight = height;*/
		mWindow = new MyPopupWindow(mContentView, 
				ViewGroup.LayoutParams.MATCH_PARENT, mPopupWindowHeight);
	}

	private void showPopupWindowBottom() {
		/*RelativeLayout.LayoutParams lp1 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		lp1.topMargin = (int) (9 * mDensity);
		lp1.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		mRootView.updateViewLayout(mBackground, lp1);*/

		mArrowView.setImageResource(R.drawable.reader_note_arrow_up);
		mBackground.setBackgroundResource(R.drawable.reader_note_tip);
		//mBackground.measure(lp1.width, lp1.height);
		
		//mScrollViewWidth = (int) (mScreenWidth);//mBackground.getMeasuredWidth();
		//mScrollViewHeight = (int) ((46 + 17 + 17 + 9)*mDensity);//mBackground.getMeasuredHeight();

		calArrowLeft();
		RelativeLayout.LayoutParams lp2 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		lp2.leftMargin = arrowLeft;
		lp2.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		mRootView.updateViewLayout(mArrowView, lp2);

		initPopupWindow(mScrollViewWidth, mScrollViewHeight);
		mWindow.showAtLocation(mParent, Gravity.NO_GRAVITY, windowLeft, mFloatingPoint.y);
	}
	
	private void calArrowLeft() {
		int maxWidth = mScreenWidth - 2 * left;
		int arrowWidth = (int) (mDensity * ARROW_RES_WIDTH);

		arrowLeft = left;
		if (mScrollViewWidth > maxWidth) { // full width
			windowLeft = left;
			arrowLeft = mFloatingPoint.x - left - arrowWidth / 3;
			arrowLeft = Math.min(arrowLeft, mScreenWidth - windowLeft * 2 - arrowWidth);
		} else if ((mFloatingPoint.x + mScrollViewWidth) > mScreenWidth) { // align right
			windowLeft = mScreenWidth - mScrollViewWidth;// - left  
			arrowLeft = mFloatingPoint.x - arrowWidth / 2 - windowLeft;
		} else if ((mFloatingPoint.x - left) < mScrollViewWidth / 2) { // align left
			windowLeft = left;
			arrowLeft = mFloatingPoint.x - left - arrowWidth / 2;
		} else if ((mFloatingPoint.x - left) >= mScrollViewWidth / 2) {// floating
			windowLeft = mFloatingPoint.x - mScrollViewWidth / 2;
			arrowLeft = mScrollViewWidth / 2 - arrowWidth / 2;
		}
		arrowLeft = Math.max(0, arrowLeft);
	}
	
	private void initService() throws IOException{
		if(mAudioService == null){
			mAudioService = new AudioService();
		}
		mAudioService.setMediaListener(this);
		mAudioService.prepare(mInnerPath, mPath, mBookType, new PrepareListener() {
			@Override
			public void prepareFinish(boolean status) {
				printLog(" prepare status=" + status);
				if(status){
					mHandler.sendEmptyMessageAtTime(MSG_AUDIO_INIT, 200);
				}
			}
		});
	}
	
	protected void init() {
		mAudioService.init(mInnerPath, mPath);
		mAudioService.setOnBufferingUpdateListener(this);
		mAudioService.setOnCompletionListener(this);
		mAudioService.setOnErrorListener(this);
		mAudioService.setOnPreparedListener(this);
		
		resetProgress();
		
		//initProgressTimer();
	}

	protected void initProgressTimer() {
		mTimer = new Timer();
		mTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				MediaPlayer player = getPlayer();
				if (player == null){
					return;
				}
				if (player.isPlaying() && !mSeekBar.isPressed()) {
					mHandler.sendEmptyMessage(0);
				}
			}
		}, 0, 1000);
	}

	protected void resetProgress() {
		mSeekBar.setProgress(0);
		mSeekBar.setMax(100);
		mPlayPrgs.setText("");
		mPlayPrgs.invalidate();
	}

	public void showToast(int msgId) {
        UiUtil.showToast(mContext, msgId);
	}
	
	@Override
	public void onPrepared(MediaPlayer mp) {
		printLog(" onPrepared ");
		
		mAudioService.playAndPause();
		updatePlayStatus();
		
		int progress = mp.getCurrentPosition();
		int duration = mp.getDuration();
		setProgress(progress, duration);
		initProgressTimer();
	}

	@Override
	public boolean onError(MediaPlayer mp, int what, int extra) {
		printLog(" onFail " + what + "," + extra);
		return false;
	}

	private boolean isFinishStatus = false;
	@Override
	public void onCompletion(MediaPlayer mp) {
		printLog(" onCompletion " + mp.isPlaying());
		
		if(!isFinishStatus){
			isFinishStatus = true;
			playerFinish();
		}
		
	}

	@Override
	public void onBufferingUpdate(MediaPlayer mp, int percent) {
		//printLog(" onBufferingUpdate " + percent);
	}

	@Override
	public void onDuration(int duration) {
		printLog(" onDuration " + duration);
		
		//setProgress(0, duration);
	}

	protected void setProgress(int progress, int duration) {
		mSeekBar.setProgress(progress);
		mSeekBar.setMax(duration);
		mPlayPrgs.setText(Utils.dateFormatNoYear(progress) + " / " + Utils.dateFormatNoYear(duration));
		mPlayPrgs.invalidate();
	}

	protected void updatePlayStatus() {
		if(mAudioService.isPlaying()){
			mPlayStatus.setImageResource(R.drawable.read_media_playpause);
			mPlayStatusText.setText(R.string.player_ing);
		} else {
			mPlayStatus.setImageResource(R.drawable.read_media_playstart);
			mPlayStatusText.setText(R.string.player_pouse);
		}
	}
	
	protected void playerFinish(){
		MediaPlayer player = getPlayer();
		if(player == null){
			return;
		}
		try {
			mPlayStatus.setImageResource(R.drawable.read_media_playstart);
			mPlayStatusText.setText(R.string.player_prepare);
			int progress = 0;//player.getDuration();
			int duration = player.getDuration();
			setProgress(progress, duration);
			
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}

	protected MediaPlayer getPlayer() {
		return mAudioService.getPlayer();
	}

	final OnClickListener mClickListener = new OnClickListener(){
		@Override
		public void onClick(View v) {
			int i = v.getId();
			if (i == R.id.reader_playstatus) {
				if (!mAudioService.isPlaying()) {
					isFinishStatus = false;
				}
				mAudioService.playAndPause();
				updatePlayStatus();

			}
		}
	};
	
	
	final OnSeekBarChangeListener mSeekBarChangeListener = new OnSeekBarChangeListener(){

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
			if(progress == max){
				playerFinish();
				
				if(!mAudioService.isPlaying()){
					progress = 0;
					return;
				}
			}
			mAudioService.seekTo(progress);
		}
		
	};
	
	private final static int MSG_AUDIO_INIT = 1;
	
	private void dealMsg(Message msg){
		switch(msg.what){
		case 0:{
			MediaPlayer player = getPlayer();
			if(player == null){
				printLogE(" player == null ");
				return;
			}
			int progress = player.getCurrentPosition();
			int duration = player.getDuration();
			if (duration > 0) {
				if(progress >= duration){
					//playerFinish();
				} else {
					setProgress(progress, duration);
				}
			}
		}
			break;
		case MSG_AUDIO_INIT:
			init();
			break;
		}
	}
	
	private static class MyHandler extends Handler {
		private final WeakReference<AudioWindow> mFragmentView;

		MyHandler(AudioWindow view) {
			this.mFragmentView = new WeakReference<AudioWindow>(view);
		}

		@Override
		public void handleMessage(Message msg) {
			AudioWindow service = mFragmentView.get();
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
}
