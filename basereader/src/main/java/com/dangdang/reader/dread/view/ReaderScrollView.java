package com.dangdang.reader.dread.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.Scroller;

import com.dangdang.reader.db.service.DDStatisticsService;
import com.dangdang.reader.dread.config.ReadConfig;
import com.dangdang.reader.dread.core.base.IReaderController;
import com.dangdang.reader.dread.core.base.IReaderController.ReadStatus;
import com.dangdang.reader.dread.core.epub.ReaderAppImpl;
import com.dangdang.reader.utils.Constant;
import com.dangdang.reader.utils.MathUtil;
import com.dangdang.zframework.log.LogM;

import java.util.LinkedList;

public class ReaderScrollView extends ViewGroup {

	private final static int TOUCH_STATE_REST = 0;
	private final static int TOUCH_STATE_SCROLLING = 1;
	private final static int SNAP_VELOCITY = 50;
	private final static int First_Screen = 0;
	private final static int Second_Screen = 1;
	private final static int Third_Screen = 2;
	private final static int NoIntercept_Screen = Second_Screen;// 阅读
	private final static int DURATION = 0;

	/**
	 * x大于 y 滑动距离 倍数 可以横向scroll
	 */
	private final static int HorizontalScrollAstrict = 3;
	private final float mReserveZone = 0.0f;// 阅读预留区
	private int mReserveWidth;

	private int mTouchState = TOUCH_STATE_REST;
	private int mTouchSlop;
	private float mLastMotionX;
	private int mMinSnapXDistance = 15;
	private int mMinMoveYDistance = 30;

	private final int mDefaultScreen = Second_Screen;
	private int mCurScreen;
	private boolean mScreenChanged;

	private ScrollEventAdapter mSEventAdapter;
	private Scroller mScroller;
	private VelocityTracker mVeyTracker;
	// private boolean mCanScroll = false;

	private Context mContext;
	private DDStatisticsService mDDService;

	public ReaderScrollView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
		// mContext = context;
	}

	public ReaderScrollView(Context context, AttributeSet attrs, int defStyle) {
		// super(context, attrs, defStyle);
		super(context, attrs);
		mContext = context;
		mDDService = DDStatisticsService.getDDStatisticsService(mContext);

		mSEventAdapter = new ScrollEventAdapter();
		mScroller = new Scroller(context);

		mCurScreen = mDefaultScreen;
		mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();

		DisplayMetrics dm = new DisplayMetrics();
		Display d = ((Activity) getContext()).getWindowManager()
				.getDefaultDisplay();
		d.getMetrics(dm);
		mReserveWidth = (int) (mReserveZone * d.getWidth());
		printLog(" mReserveWidth = " + mReserveWidth + ", getWidth() = "
				+ d.getWidth());

		mMinSnapXDistance = (int) (mMinSnapXDistance * dm.density);
		mMinMoveYDistance = (int) (mMinMoveYDistance * dm.density);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		// LogM.e(getClass().getSimpleName(), " onLayout ");
		// if (changed) {
		try{
			int childLeft = 0;
			final int childCount = getChildCount();
			for (int i = 0; i < childCount; i++) {

				final View childView = getChildAt(i);
				if (childView.getVisibility() != View.GONE) {
					final int childWidth = childView.getMeasuredWidth();
					final int childTop = 0;
					final int childRight = childLeft + childWidth;
					final int childBottom = childView.getMeasuredHeight();
					// LogM.e(getClass().getSimpleName(), " onLayout child "+ i
					// +" [ cL = " + childLeft
					// + ", cR = " + childRight + ", cB = " + childBottom + " ]");
					childView.layout(childLeft, childTop, childRight, childBottom);
					childLeft += childWidth;
				}
			}
		}catch(Throwable e){
			e.printStackTrace();
		}		
		// }
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		final int width = MeasureSpec.getSize(widthMeasureSpec);
		final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		if (widthMode != MeasureSpec.EXACTLY) {
			throw new IllegalStateException(
					"ScrollLayout only canmCurScreen run at EXACTLY mode!");
		}

		final int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		if (heightMode != MeasureSpec.EXACTLY) {
			throw new IllegalStateException(
					"ScrollLayout only can run at EXACTLY mode!");
		}

		// The children are given the same width and height as the scrollLayout
		final int count = getChildCount();
		for (int i = 0; i < count; i++) {

			getChildAt(i).measure(widthMeasureSpec, heightMeasureSpec);
		}
		int scrollToX = mCurScreen * width;
		if (isFirstScreen(mCurScreen)) {
			scrollToX = mReserveWidth;// (int) (mReserveZone * width);
		} else if (isLastScreen(mCurScreen)) {
			scrollToX = scrollToX - mReserveWidth;
		}
		// LogM.e(getClass().getSimpleName(), " onMeasure scrollToX = " +
		// scrollToX);
		scrollTo(scrollToX, 0);
	}

	public void snapToDestination() {

		final int screenWidth = getWidth();
		final int destScreen = (getScrollX() + screenWidth / 2) / screenWidth;
		final int scrollDistance = Math.abs(mCurScreen * getWidth()
				- getScrollX());
		printLog(" snapToDestination() destScreen = " + destScreen
				+ ", scrollX = " + getScrollX() + " , scrollDistance = "
				+ scrollDistance);
		snapToScreen(destScreen);
	}

	public void snapToScreen(int whichScreen) {
		snapToScreen(whichScreen, true);
	}

	public void snapToScreen(int whichScreen, boolean notifyStartEvent) {
		whichScreen = Math.max(0, Math.min(whichScreen, getChildCount() - 1));
		if (getScrollX() != (whichScreen * getWidth())) {
			if (notifyStartEvent) {
				ScrollEvent event = new ScrollEvent(getCurScreen());
				mSEventAdapter.notifyStartEvent(event);
			}
			int delta = getScrollDestX(whichScreen);
			mScroller.startScroll(getScrollX(), 0, delta, 0, DURATION);
			printLog(" snapToScreen getScrollX() = " + getScrollX()
					+ ", delta = " + delta);
			if (mCurScreen != whichScreen) {
				mCurScreen = whichScreen;
				mScreenChanged = true;
			}
			invalidate(); // Redraw the store_ebook_pay_activity
		}
	}

	private int getScrollDestX(int whichScreen) {
		int delta = whichScreen * getWidth() - getScrollX();
		if (isFirstScreen(whichScreen)) {
			delta = delta + mReserveWidth;// (int) (delta + getWidth() *
											// mReserveZone);
		} else if (isLastScreen(whichScreen)) {
			delta = delta - mReserveWidth;
		}
		return delta;
	}

	private boolean isFirstScreen(int whichScreen) {
		return whichScreen == First_Screen;
	}

	private boolean isLastScreen(int whichScreen) {
		return whichScreen == Third_Screen;
	}

	public void setToScreen(int whichScreen) {
		whichScreen = Math.max(0, Math.min(whichScreen, getChildCount() - 1));
		mCurScreen = whichScreen;

		scrollTo(whichScreen * getWidth(), 0);
		ScrollEvent e = new ScrollEvent(whichScreen);
		mSEventAdapter.notifyCompleteEvent(e);
		invalidate();

	}

	public int getCurScreen() {
		return mCurScreen;
	}

	@Override
	public void computeScroll() {
		if (mScroller.computeScrollOffset()) {
			scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
			postInvalidate();
		} else {
			if (mScreenChanged) {
				ScrollEvent e = new ScrollEvent(mCurScreen);
				mSEventAdapter.notifyCompleteEvent(e);
				mScreenChanged = false;
			}
		}
	}

	private int mDownTouchX = 0;
	private int mDownTouchY = 0;

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		try {
			printLog(" onTouchEvent action = " + event.getAction());
			if (noInterceptTouch()) {
				if (!mPointer2Move) {
					return super.onTouchEvent(event);
				}
			}

			final int action = event.getAction();
			final int x = (int) event.getX();
			final int y = (int) event.getY();
			/*
			 * if(isReserveZone(x, getCurScreen())){ if(action ==
			 * MotionEvent.ACTION_DOWN){ setDownTouchX(x, y); mCanScroll = true;
			 * } else if(action == MotionEvent.ACTION_UP){
			 * snapToScreen(Second_Screen); }
			 * printLog(" onTouchEvent reserveZone action = " + action); return
			 * true; }
			 */

			if (mVeyTracker == null) {
				mVeyTracker = VelocityTracker.obtain();
			}
			mVeyTracker.addMovement(event);

			// boolean isReserveZone = isReserveZone(x, getCurScreen());
			boolean ret = true;

			switch (action) {
			case MotionEvent.ACTION_DOWN:
				if (!mScroller.isFinished()) {
					mScroller.abortAnimation();
				}
				/*
				 * if(isReserveZone) { mCanScroll = true; }
				 */
				mLastMotionX = x;
				setDownTouchX(x, y);
				printLog(" <--- onTouchEvent down---> mDownTouchX = "
						+ mDownTouchX);
				break;
			case MotionEvent.ACTION_MOVE:
				/*
				 * if(!mCanScroll){ break; }
				 */
				int deltaX = (int) (mLastMotionX - x);
				mLastMotionX = x;
				final int screenWidth = getWidth();
				printLog(" <--- onTouchEvent move---> mDownTouchX = "
						+ mDownTouchX + " , x = " + x + " , deltaX = " + deltaX
						+ ", mCurScreen = " + mCurScreen + ", screenWidth = "
						+ screenWidth);
				int isXMove = (int) (mDownTouchX - x);
				final int scrollX = getScrollX();
				int nextScollX = scrollX + deltaX;
				if (isLastScreen(mCurScreen)) {// TODO add ?
					// deltaX = nextScollX > screenWidth ? (deltaX - nextScollX)
					// : deltaX;
					// if(isXMove > 0 || scrollX > screenWidth){
					if (isXMove > 0) {
						break;
					}
				} else if (isFirstScreen(mCurScreen)) {
					deltaX = nextScollX < 0 ? (deltaX - nextScollX) : deltaX;
					if (isXMove < 0 || scrollX < 0) {
						break;
					}
				} else if (mCurScreen == Second_Screen) {
					/*
					 * LogM.v(getClass().getSimpleName(),
					 * " onTouchEvent move deltaX = " + deltaX +
					 * ", getScrollX() = " + scrollX + ", mCurScreen = " +
					 * mCurScreen + ", break = " + isXMove + ", nextScollX = " +
					 * nextScollX);
					 */
					if (isXMove > 0) {
						if (scrollX >= screenWidth) {
							break;
						} else if (nextScollX > screenWidth) {
							deltaX = screenWidth - scrollX;
						}
					}
				}
				// LogM.e(getClass().getSimpleName(),
				// " onTouchEvent move isXMove = " + isXMove + ", deltaX = " +
				// deltaX + ", scrollX = " + getScrollX());
				scrollBy(deltaX, 0);
				break;
			case MotionEvent.ACTION_UP:
			case MotionEvent.ACTION_CANCEL:// TODO add time 2013-12-16
				// if(isReserveZone){
				// snapToScreen(Second_Screen);
				// } else if(mCanScroll){
				// if (mTouchState == TOUCH_STATE_SCROLLING) {
				final VelocityTracker velocityTracker = mVeyTracker;
				velocityTracker.computeCurrentVelocity(1000);
				final int velocityX = (int) velocityTracker.getXVelocity();
				// final int scrollXDistance = Math.abs(mCurScreen*getWidth() -
				// getScrollX());
				final int scrollXDistance = mDownTouchX - x;
				final int moveYDistance = Math.abs(scrollXDistance);
				printLog(" onTouchEvent ACTION_UP velocityX = " + velocityX
						+ ", mDownTouchX = " + mDownTouchX + " , x = " + x
						+ ", XDistance = " + scrollXDistance);

				if (canPrevScreen(velocityX, scrollXDistance, moveYDistance)) {
					// Fling enough to move left
					printLog(" <--- onTouchEvent up--1->  ");
					if (mCurScreen == 1) {
						mDDService.addData(
								DDStatisticsService.GESTUREGETDIRECTORY,
								DDStatisticsService.OPerateTime,
								System.currentTimeMillis() + "");
					}
					snapToScreen(mCurScreen - 1, false);
				} else if (canNextScreen(scrollXDistance, moveYDistance)) {
					// Fling enough to move right
					printLog(" <--- onTouchEvent up--2->  moveYDistance = "
							+ moveYDistance + ", minYD = " + mMinMoveYDistance);
					// if(moveYDistance < mMinMoveYDistance || getScrollX() !=
					// 0){
					if (getScrollX() != 0) {
						snapToScreen(mCurScreen + 1);
					} else {
						ret = false;
					}
				} else {
					printLog(" <--- onTouchEvent up--3->  ");
					snapToDestination();
				}
				// }
				reSet(true);

				if (mVeyTracker != null) {
					mVeyTracker.recycle();
					mVeyTracker = null;
				}
				// }
				mTouchState = TOUCH_STATE_REST;
				break;
			/*
			 * case MotionEvent.ACTION_CANCEL: mTouchState = TOUCH_STATE_REST;
			 * break;
			 */
			}
			if (ret) {
				return true;
			} else {
				return super.onTouchEvent(event);
			}
		} catch (Exception e) {
			LogM.e(e.toString());
			return true;
		}
	}

	private boolean canPrevScreen(final int velocityX,
			final int scrollXDistance, final int moveYDistance) {

		return (velocityX > SNAP_VELOCITY || scrollXDistance < mMinSnapXDistance)
				&& moveYDistance > mMinSnapXDistance
				&& mCurScreen > First_Screen;
	}

	private boolean canNextScreen(final int scrollXDistance,
			final int moveYDistance) {

		return scrollXDistance > mMinSnapXDistance
				&& moveYDistance > mMinSnapXDistance
				&& mCurScreen < Second_Screen;// && mCurScreen < Third_Screen;

	}

	private void setDownTouchX(final float x, final float y) {
		mDownTouchX = (int) x;
		mDownTouchY = (int) y;
		mLastMotionX = mDownTouchX;
	}

	private int mPressInterceptX = 0;
	private int mPressInterceptY = 0;
	private boolean mPointer2Move = false;
	// private boolean mSinglePointMove = false;
	private boolean mPointer2Press = false;
	private int mTwoPtrDistanceDown = 0;

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {

		final int x = (int) ev.getX();
		final int y = (int) ev.getY();
		printLog(" onInterceptTouchEvent 1.0 action = " + ev.getAction()
				+ ", x = " + x + ", scrollX = " + getScrollX());
		if (noInterceptTouch()) {
			boolean isSuper = true;// 是否交给阅读模块处理touch事件

			switch (ev.getAction()) {
			case MotionEvent.ACTION_POINTER_2_DOWN:
				mPointer2Press = true;
				mPressInterceptX = x;
				mPressInterceptY = y;
				mTwoPtrDistanceDown = getDistance(ev);
				printLog(" onInterceptTouchEvent 1.1 down mPressInterceptX = "
						+ mPressInterceptX + ", twoPtrDistance="
						+ mTwoPtrDistanceDown);
				break;
			case MotionEvent.ACTION_MOVE:
				if (!isDownDistance()) {
					break;
				}

				int distance = Math.abs(mPressInterceptX - x);
				final boolean reader2Other = canOtherModule();
				printLog(" onInterceptTouchEvent 1.19 move mPointer2Press = "
						+ mPointer2Press + ", mPointer2Move = " + mPointer2Move
						+ ", reader2Other = " + reader2Other
						+ ", mPressInterceptX = " + mPressInterceptX + ", x = "
						+ x + ", distance = " + distance + ", mTouchSlop = "
						+ mTouchSlop);

				if (mPointer2Press && !mPointer2Move && reader2Other) {// TODO ?
					if (mPressInterceptX < x) {
						if (distance > mTouchSlop) {
							isSuper = false;
							mPointer2Move = true;
							// mCanScroll = true;
							setDownTouchX(mPressInterceptX, mPressInterceptY);
							removeLongClick();
							ScrollEvent event = new ScrollEvent(getCurScreen());
							mSEventAdapter.notifyStartEvent(event);
							printLog(" onInterceptTouchEvent 1.2 move setDownTouchX = "
									+ mPressInterceptX);
						}
					}
				}
				break;
			case MotionEvent.ACTION_POINTER_2_UP:
			case MotionEvent.ACTION_UP:
			case MotionEvent.ACTION_CANCEL:
				printLog(" onInterceptTouchEvent 1.3 up setDownTouchX = "
						+ mPressInterceptX);
				reSet(false);
				break;
			}
			printLog(" onInterceptTouchEvent 1.4 action = " + ev.getAction()
					+ ", isSuper = " + isSuper);
			if (isSuper) {
				return super.onInterceptTouchEvent(ev);
			}
		}

		/*
		 * if(isReserveZone(x, getCurScreen())){ return true; }
		 */

		final int action = ev.getAction();
		if ((action == MotionEvent.ACTION_MOVE)
				&& (mTouchState != TOUCH_STATE_REST)) {
			printLog(" onInterceptTouchEvent 2.0 action = " + ev.getAction());
			return true;
		}

		switch (action) {
		case MotionEvent.ACTION_MOVE:
			final int xDiff = (int) Math.abs(mLastMotionX - x);
			final int yDiff = Math.abs(y - mDownTouchY);
			if (xDiff >= (HorizontalScrollAstrict * yDiff)
					&& xDiff > mTouchSlop) {
				mTouchState = TOUCH_STATE_SCROLLING;
			}
			printLog(" onInterceptTouchEvent 3.1 xDiff = " + xDiff
					+ ", yDiff = " + yDiff);
			break;
		case MotionEvent.ACTION_DOWN:
			postInvalidate();
			// mLastMotionX = x;//TODO ?
			// mDownTouchY = (int) y; //TODO ?
			setDownTouchX(x, y); // TODO ?
			// mTouchState = mScroller.isFinished() ? TOUCH_STATE_REST :
			// TOUCH_STATE_SCROLLING;
			mTouchState = TOUCH_STATE_REST;
			printLog(" onInterceptTouchEvent 3.2 " + mTouchState);
			break;
		case MotionEvent.ACTION_CANCEL:
		case MotionEvent.ACTION_UP:
			this.postInvalidate();
			mTouchState = TOUCH_STATE_REST;
			printLog(" onInterceptTouchEvent 3.3 ");
			break;
		}
		printLog(" onInterceptTouchEvent 4.0 mTouchState = " + mTouchState
				+ " return = " + (mTouchState != TOUCH_STATE_REST));

		if (mSEventAdapter.isSelfProcessTouch()) {
			printLog(" isSelfProcessTouch == true curScreen = " + mCurScreen);
		}

		return mTouchState != TOUCH_STATE_REST
				&& !mSEventAdapter.isSelfProcessTouch();
	}

	protected boolean canOtherModule() {
		boolean has = true;
		try {
			IReaderController controller = ReaderAppImpl.getApp().getReaderController();
			if(controller != null){
				has = controller.getReadStatus() != ReadStatus.TTS && !controller.isSelectedStatus();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return has;
	}

	/**
	 * 双指按下的距离是否符合要求
	 * 
	 * @return
	 */
	protected boolean isDownDistance() {
		return mTwoPtrDistanceDown < ReadConfig.getConfig().getTwoPtrDistance();
	}

	private int getDistance(MotionEvent ev) {// twoPtrDistance
		float x0 = ev.getX(0);
		float y0 = ev.getY(0);
		float x1 = ev.getX(1);
		float y1 = ev.getY(1);
		return MathUtil.getDistance(new Point((int) x0, (int) y0), new Point(
				(int) x1, (int) y1));
	}

	private void removeLongClick() {
		Intent intent = new Intent();
		intent.setAction(Constant.ACTION_REMOVE_LONGCLICK);
		sendBroadcast(intent);
	}

	private void reSet(boolean isOnTouch) {
		LogM.d(getClass().getSimpleName(), " reSet() ");

		// mCanScroll = false;
		mPointer2Press = false;
		mPointer2Move = false;
		mPressInterceptX = 0;
		mPressInterceptY = 0;
		mDownTouchX = 0;
		if (isOnTouch) {
			mDownTouchY = 0;
		}
	}

	private boolean noInterceptTouch() {
		return getCurScreen() == NoIntercept_Screen
				|| getCurScreen() == First_Screen;
	}

	private boolean isReserveZone(int y, int currentScreen) {

		boolean isReserve = false;
		if (isFirstScreen(currentScreen)) {
			final int reserveStartY = getWidth() - mReserveWidth;
			if (y > reserveStartY) {
				isReserve = true;
			}
		} else if (isLastScreen(currentScreen)) {
			final int reserveStartY = mReserveWidth;
			if (y < reserveStartY) {
				isReserve = true;
			}
		}

		return isReserve;
	}

	public void setOnScrollCompleteLinstenner(OnScrollStatusListener listener) {
		mSEventAdapter.addListener(listener);
	}

	public void printLog(String msg) {
		 LogM.i(getClass().getSimpleName(), msg);
	}

	public static class ScrollEvent {

		public int curScreen;
		public Object params;

		public ScrollEvent(int curScreen) {
			this.curScreen = curScreen;
		}

		@Override
		public boolean equals(Object o) {

			ScrollEvent other = (ScrollEvent) o;

			return other.curScreen == this.curScreen;
		}
	}

	public static interface OnScrollStatusListener {

		public boolean isSelfProcessTouch();

		public void onScrollStart(ScrollEvent e);

		public void onScrollComplete(ScrollEvent e);

	}

	public static class ScrollEventAdapter {

		private LinkedList<OnScrollStatusListener> mListeners;

		public ScrollEventAdapter() {
			mListeners = new LinkedList<OnScrollStatusListener>();
		}

		public void notifyStartEvent(ScrollEvent e) {
			for (OnScrollStatusListener l : mListeners) {
				l.onScrollStart(e);
			}
		}

		public void notifyCompleteEvent(ScrollEvent e) {
			for (OnScrollStatusListener l : mListeners) {
				l.onScrollComplete(e);
			}
		}

		public void addListener(OnScrollStatusListener l) {
			mListeners.add(l);
		}

		public boolean isSelfProcessTouch() {
			if (mListeners != null && mListeners.size() > 0) {
				return mListeners.get(0).isSelfProcessTouch();
			}
			return false;
		}
	}

	private void sendBroadcast(Intent intent){
		if(intent == null)
			return;
		intent.setPackage(getContext().getPackageName());
		getContext().sendBroadcast(intent);
	}
}
