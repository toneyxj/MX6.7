package com.dangdang.reader.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Scroller;

import com.dangdang.zframework.utils.DeviceUtil;

public class TabScrollView extends View {

	private int mCurrentXOffset = 0;
	private int mCurrentRow = 0;				// 从0开始
	private int mRowCount = 3;
	private int mRowWidth = 0;

	private Drawable mThumbDrawable;
	private AnimationRunnable mCurrentAnimationRunnable;
	private int mGap;
	private int width;
	public TabScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public void setScrollDrawable(int resId) {
		mThumbDrawable = getContext().getResources().getDrawable(resId);
	}

	public void setRowParam(int rowCount, int rowWidth) {
		this.mRowCount = rowCount;
		this.mRowWidth = rowWidth;
	}
	public void setWidthCustom(int width){
		this.width=width;
	}
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (mThumbDrawable != null) {
			if (mRowWidth == 0) {
				mRowWidth = getDisplayWidth() / mRowCount;
			}
			if (mGap == 0) {
				mGap = (getDisplayWidth() - mRowWidth * mRowCount) / (mRowCount - 1);
				mThumbDrawable
						.setBounds(0, 0, mRowWidth, getBottom() - getTop());
			}
			if (mCurrentXOffset == -1) {
				mCurrentXOffset = mRowWidth * mCurrentRow + mGap * mCurrentRow;
			}
			
			if(mCurrentXOffset == 0){
				mCurrentXOffset = ((getDisplayWidth()/mRowCount) - mRowWidth) /2;
			}

			canvas.translate(mCurrentXOffset, 0);
			mThumbDrawable.draw(canvas);
		}
	}

	/**
	 * 设置要滑动到的列
	 */
	public void setTargetRow(int row) {
		if (mCurrentRow == row)
			return;
		mCurrentRow = row;
		int diffDistance = 0;
		diffDistance = getDisplayWidth() * mCurrentRow/mRowCount + ((getDisplayWidth()/mRowCount) - mRowWidth) /2 - mCurrentXOffset;

		// 开始动画
		if (mCurrentAnimationRunnable != null) {
			mCurrentAnimationRunnable.end();
		} else {
			mCurrentAnimationRunnable = new AnimationRunnable();
		}
		mCurrentAnimationRunnable.start(diffDistance);
	}

	public int getDisplayWidth(){
		if (width!=0)
			return width;
		return DeviceUtil.getInstance(getContext()).getDisplayWidth();
	}

	private class AnimationRunnable implements Runnable {
		private Scroller scroller = new Scroller(getContext(),
				new AccelerateDecelerateInterpolator());

		public void start(int diffDistance) {
			scroller.startScroll(mCurrentXOffset, 0, diffDistance, 0, 300);
			post(this);
		}

		@Override
		public void run() {
			if (scroller.computeScrollOffset()) {
				mCurrentXOffset = scroller.getCurrX();
				postInvalidate();
				post(this);
			} else {
				end();
			}
		}

		public void end() {
			scroller.abortAnimation();
		}
	}

}
