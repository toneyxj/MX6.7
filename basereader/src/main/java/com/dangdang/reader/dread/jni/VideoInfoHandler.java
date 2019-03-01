package com.dangdang.reader.dread.jni;

import android.graphics.Rect;

public class VideoInfoHandler {

	private int mVideoCount;
	private Rect mVideoRect;
	private boolean mIsAutoPlay;
	private boolean mIsControl;
	private boolean mIsLoop;
	private String mVideoFilePath;
	private String mPosterFilePath;

	public int getmVideoCount() {
		return mVideoCount;
	}

	public void setmVideoCount(int mVideoCount) {
		this.mVideoCount = mVideoCount;
	}

	public Rect getmVideoRect() {
		return mVideoRect;
	}

	public void setmVideoRect(Rect mVideoRect) {
		this.mVideoRect = mVideoRect;
	}

	public boolean ismIsAutoPlay() {
		return mIsAutoPlay;
	}

	public void setmIsAutoPlay(boolean mIsAutoPlay) {
		this.mIsAutoPlay = mIsAutoPlay;
	}

	public boolean ismIsControl() {
		return mIsControl;
	}

	public void setmIsControl(boolean mIsControl) {
		this.mIsControl = mIsControl;
	}

	public boolean ismIsLoop() {
		return mIsLoop;
	}

	public void setmIsLoop(boolean mIsLoop) {
		this.mIsLoop = mIsLoop;
	}

	public String getmVideoFilePath() {
		return mVideoFilePath;
	}

	public void setmVideoFilePath(String mVideoFilePath) {
		this.mVideoFilePath = mVideoFilePath;
	}

	public String getmPosterFilePath() {
		return mPosterFilePath;
	}

	public void setmPosterFilePath(String mPosterFilePath) {
		this.mPosterFilePath = mPosterFilePath;
	}

	public void setVideoCount(int nVideoCount) {
		mVideoCount = nVideoCount;
	}

	public void setVideoInfo(double dLeft, double dTop, double dRight,
			double dBottom, boolean bAutoPlay, boolean bControl, boolean bLoop,
			String strVideoFile, String strPosterFile) {
		mVideoRect = new Rect();
		mVideoRect.left = (int) dLeft;
		mVideoRect.top = (int) dTop;
		mVideoRect.right = (int) dRight;
		mVideoRect.bottom = (int) dBottom;
		mIsAutoPlay = bAutoPlay;
		mIsControl = bControl;
		mIsLoop = bLoop;
	}

}
