package com.dangdang.reader.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.Scroller;
import android.widget.TextView;

import com.dangdang.reader.R;

public class ScrollLineView extends LinearLayout {

	private final static int minLineHeight = 2;
	private int mModuleWidth = 0;
	private int mLineHeight = minLineHeight;
	private int mDisplayWidth = 0;
	private int mModuleCount = 0;
	private int mCurrentIndex = 0;

	private LinearLayout mLinearLayout = null;
	private TextView mLineView = null;
	private Scroller mScroller;

	public ScrollLineView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public ScrollLineView(Context context) {
		super(context);
		init();
	}

	public void setLineColor(int color) {
		mLineView.setBackgroundColor(color);
	}

	private void init() {
		LayoutInflater flater = LayoutInflater.from(getContext());
		mLinearLayout = (LinearLayout) flater.inflate(
				R.layout.view_scroll_line, null);
		mLineView = (TextView) mLinearLayout.findViewById(R.id.tv);
		addView(mLinearLayout);
		mScroller = new Scroller(getContext());
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		final int measuredWidth = mDisplayWidth;
		final int measuredHeight = mLineHeight;
		setMeasuredDimension(measuredWidth, measuredHeight);
	}

	@Override
	public void computeScroll() {
		if (mScroller.computeScrollOffset()) {
			scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
			postInvalidate();
		}
	}

	public boolean isScrollerFinished() {
		return mScroller.isFinished();
	}

	public synchronized void selectModule(int index, int lineWidth) {
		if (index < 1 || index > mModuleCount) {
			throw new IllegalArgumentException(
					" index > 1 and index < moduleCount ");
		}

		if (!mScroller.isFinished()) {
			scrollTo(mScroller.getFinalX(), 0);
			mScroller.abortAnimation();
		}

		index = index - 1;
		if (mCurrentIndex != index) {
			mLineView.setWidth(lineWidth);

			final int sx = getScrollX();
			final int dx = (mCurrentIndex - index) * mModuleWidth;
			mScroller.startScroll(sx, 0, dx, 0, 300);
			postInvalidate();

			mCurrentIndex = index;
		}
	}

	public void setParamater(int moduleWidth, int lineWidth, int itemCount,
			int displayWidth, float density) {
		mModuleWidth = moduleWidth;
		mModuleCount = itemCount;
		mDisplayWidth = displayWidth;
		mLineHeight = (int) (minLineHeight * density);

		mLineView.setWidth(lineWidth);
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
				mModuleWidth, mLineHeight);
		mLinearLayout.setLayoutParams(lp);
	}
}
