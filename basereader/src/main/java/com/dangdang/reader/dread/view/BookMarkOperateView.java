package com.dangdang.reader.dread.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.RelativeLayout;
import android.widget.Scroller;

import com.dangdang.reader.R;
import com.dangdang.reader.dread.view.ReaderLayout.IMoveOperateBookMarkListener;
import com.dangdang.zframework.view.DDImageView;

public class BookMarkOperateView extends RelativeLayout implements
		IMoveOperateBookMarkListener {
	private static final int DURATION = 300;
	private static final int STATE_ON_PREPARE = 0;
	private static final int STATE_ON_PREPARE_WITH_ANIMATION = 1;
	private static final int STATE_ON_RELEASE = 2;
	private DDImageView mMarkIv;
	private boolean mIsAdd;
	private Scroller mScroller;
	private int mCurrnetState = STATE_ON_PREPARE;
	private IAddOrDeleteBookMarkCallBack mIAddOrDeleteBookMarkCb;
	private RelativeLayout mRootView;
	private int mMarkHeight;
	private boolean mIsOnTouch;

	public BookMarkOperateView(Context context) {
		super(context);
		init(context);
	}

	public BookMarkOperateView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public BookMarkOperateView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	private void init(Context context) {
		mScroller = new Scroller(context);
		mRootView = (RelativeLayout) LayoutInflater.from(context).inflate(
				R.layout.book_mark_operate_layout, this);
		mMarkIv = (DDImageView) mRootView
				.findViewById(R.id.book_mark_operate_mark_iv);
		mRootView.getViewTreeObserver().addOnGlobalLayoutListener(
				new OnGlobalLayoutListener() {

					@Override
					public void onGlobalLayout() {
						mMarkHeight = mMarkIv.getHeight();
						mRootView.scrollTo(0, mMarkHeight);
						mRootView.getViewTreeObserver()
								.removeGlobalOnLayoutListener(this);
					}
				});
	}

	@Override
	public void computeScroll() {
		if (mScroller.computeScrollOffset()) {
			scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
			postInvalidate();
		}
	}

	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		// TODO Auto-generated method stub
		super.onScrollChanged(l, t, oldl, oldt);
		if (oldt != mMarkHeight && t == mMarkHeight && !mIsOnTouch
				&& mIAddOrDeleteBookMarkCb != null) {
			mIAddOrDeleteBookMarkCb.resetScrollState();
		}

	}

	@Override
	public void onPrepare(boolean isAdd, IAddOrDeleteBookMarkCallBack callBack) {
		this.mCurrnetState = STATE_ON_PREPARE;
		this.mIsOnTouch = true;
		this.mIAddOrDeleteBookMarkCb = callBack;
		this.mIsAdd = isAdd;
		reset();
	}

	@Override
	public void onDragMove(int deltaY, int scrollValue) {
		int scrollY = getScrollY();
		if (Math.abs(scrollValue) <= mMarkHeight) {
			int newScrollY = scrollY + deltaY;
			if (newScrollY <= 0) {
				newScrollY = 0;
			}
			if (newScrollY >= mMarkHeight) {
				newScrollY = mMarkHeight;
			}
			if (mCurrnetState == STATE_ON_RELEASE && newScrollY > 0
					&& newScrollY <= mMarkHeight) {
				setPrepareWithAnimation();
			} else if (mCurrnetState != STATE_ON_RELEASE && newScrollY == 0) {
				setToRelease();
			}
			scrollTo(0, newScrollY);
		} else {
			if (scrollY != 0) {
				setToRelease();
				scrollTo(0, 0);
			}
		}
	}

	private void reset() {
		mMarkIv.setSelected(true);
	}

	public void setPrepareWithAnimation() {
		mCurrnetState = STATE_ON_PREPARE_WITH_ANIMATION;
		reset();
	}

	public void setToRelease() {
		mCurrnetState = STATE_ON_RELEASE;
		mIAddOrDeleteBookMarkCb.setMarkVisiable(false);
		if (mIsAdd) {
			mMarkIv.setSelected(true);
		} else {
			mMarkIv.setSelected(false);
		}
	}

	@Override
	public void onEnd(boolean isVelocityToOperate) {
		mIsOnTouch = false;
		if (null != mIAddOrDeleteBookMarkCb) {
			if (getScrollY() == 0) {
				if (mIsAdd && mMarkIv.isSelected()) {
					mIAddOrDeleteBookMarkCb.addOrDelete();
				} else if (!mIsAdd && !mMarkIv.isSelected()) {
					mIAddOrDeleteBookMarkCb.addOrDelete();
				}
			} else if (isVelocityToOperate) {
				mIAddOrDeleteBookMarkCb.addOrDelete();
			} else {
				if (!mIsAdd) {
					mIAddOrDeleteBookMarkCb.setMarkVisiable(true);
				} else {
					mIAddOrDeleteBookMarkCb.setMarkVisiable(false);
				}
			}
		}
		if (getScrollY() != mMarkHeight) {
			mScroller.startScroll(0, getScrollY(), 0, mMarkHeight
					- getScrollY(), DURATION);
		} else {
			mIAddOrDeleteBookMarkCb.resetScrollState();
		}
		invalidate();
	}

}
