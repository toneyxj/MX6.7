package com.dangdang.reader.dread.view;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import com.dangdang.reader.dread.config.ReadConfig;
import com.dangdang.reader.dread.core.epub.DrawWrapper;
import com.dangdang.zframework.log.LogM;

public class ReaderRelativeLayout extends RelativeLayout {

	public ReaderRelativeLayout(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
	}

	public ReaderRelativeLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ReaderRelativeLayout(Context context) {
		super(context);
	}

	private DrawWrapper mDrawWrapper;
	private boolean mIsPdfAndNotReflow;

	@Override
	protected void dispatchDraw(Canvas canvas) {

		if (ReadConfig.getConfig().isImgBg() && !mIsPdfAndNotReflow) {
			if (mDrawWrapper == null) {
				mDrawWrapper = new DrawWrapper();
			}
			final ReadConfig readConfig = ReadConfig.getConfig();
			int width = readConfig.getReadWidth();
			int height = readConfig.getReadHeight();
			mDrawWrapper.drawBackground(canvas, readConfig, width, height);
		}
		super.dispatchDraw(canvas);
	}

	protected void printLog(String log) {
		LogM.i(getClass().getSimpleName(), log);
	}

	protected void printLogD(String log) {
		LogM.d(getClass().getSimpleName(), log);
	}

	protected void printLogE(String log) {
		LogM.e(getClass().getSimpleName(), log);
	}

	protected void printLogV(String log) {
		LogM.v(getClass().getSimpleName(), log);
	}

	protected void printLogW(String log) {
		LogM.w(getClass().getSimpleName(), log);
	}

	public void setIsPdfAndNotReflow(boolean isPdfAndNotReflow) {
		mIsPdfAndNotReflow = isPdfAndNotReflow;
	}
}
