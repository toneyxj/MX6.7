package com.dangdang.reader.dread.view;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.Scroller;

import com.dangdang.reader.R;
import com.dangdang.reader.cloud.MarkNoteManager;
import com.dangdang.reader.db.service.DDStatisticsService;
import com.dangdang.reader.dread.core.base.BasePageView;
import com.dangdang.reader.dread.core.base.IEpubPageView;
import com.dangdang.reader.dread.core.base.IEpubReaderController;
import com.dangdang.reader.dread.core.epub.EpubReaderWidget;
import com.dangdang.reader.dread.core.epub.IGlobalWindow;
import com.dangdang.reader.dread.core.epub.ReaderAppImpl;
import com.dangdang.reader.dread.data.ReadInfo;
import com.dangdang.reader.dread.format.IndexRange;
import com.dangdang.zframework.log.LogM;
import com.dangdang.zframework.utils.DRUiUtility;

public class ReaderLayout extends ViewGroup implements
		IAddOrDeleteBookMarkCallBack {
	private static final int DURATION = 300;
	private static final int TO_NONE = -1;
	private static final int TO_EXIT = 0;
	private static final int TO_BOOK_REVIEW = 1;
	private static final int TO_SHARE = 2;
	private static final int TO_BOOK_DETAIL = 3;
	private static final int OPERATE_VELOCITY = 50;

	private int mTouchSlop;
	private int mScreenHeight = 0;
	private int mBelowHeight = 0;
	private int menuHeight = -1;

	private float mLastMotionY = -1;
	private float mDownTouchY;

	private int mPressedX = 0;
	private int mPressedY = 0;
	private int menuY = 0;
	private boolean mFingerY = false;
	private boolean mFingerX = false;
	private boolean mShowMenu = false;

	private Scroller mScroller;

	private EpubReaderWidget mReaderWidget;
	private boolean mDirectionUp;
	private OnSizeChangedListener mSizeChangeListener;
	private IMoveOperateBookMarkListener mBookMarkListener;
	private IMoveCompleteOperateCallBack mBookMarkOperateCallback;
	private boolean mAddedBookMark;
	private Rect mOutRect = new Rect();
	private boolean mPassDown;
	private int mDelayOperationType = -1;
	private boolean mIsSelected;
	private boolean mIsInvalidate;
	private boolean mCloseOperateBookMark = false;
	private boolean mCloseUpMenu = false;

	public ReaderLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public ReaderLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public ReaderLayout(Context context) {
		super(context);
		init();
	}

	private void init() {
		ViewConfiguration viewConfig = ViewConfiguration.get(getContext());
		mTouchSlop = viewConfig.getScaledTouchSlop();
		mScroller = new Scroller(getContext());
		mScreenHeight = DRUiUtility.getScreenHeight();
		mBelowHeight = mScreenHeight / 2;
	}

	public void closeOperateBookMark() {
		mCloseOperateBookMark = true;
	}

	public void openOperateBookMark() {
		mCloseOperateBookMark = false;
	}

	public void openUpMenu() {
		mCloseUpMenu = false;
	}

	public void closeUpMenu() {
		mCloseUpMenu = true;
	}

	private void initHeight() {
		if (menuHeight < 0) {
			View menuLayout = findViewById(R.id.read_bottommenu_layout);
			menuHeight = menuLayout.getHeight();
			menuY = mScreenHeight - menuHeight;
		}
	}

	private void reSet() {
		LogM.e(getClass().getSimpleName(), " <-- reSet() --> ");
		mFingerY = false;
		mFingerX = false;
		mIsSelected = false;
		mPressedY = 0;
		mPressedX = 0;
	}

	private void setDownTouchY(final float y) {
		mDownTouchY = (int) y;
		mLastMotionY = mDownTouchY;
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		final int action = ev.getAction();
		final int y = (int) ev.getY();
		final int x = (int) ev.getX();
		if (action == MotionEvent.ACTION_DOWN) {
			mPassDown = isTouchInReaderWidget(x, y);
		}
		if ((!mScroller.isFinished() || (!mIsMarkOperateViewScrollEnd))
				&& !mIsInvalidate) {
			this.mIsInvalidate = true;
			return true;
		}
		mIsSelected = mBookMarkOperateCallback.isSelected();
		if (mPassDown || mIsSelected) {
			return super.onInterceptTouchEvent(ev);
		}
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			mPressedX = x;
			mPressedY = y;
			mIsInvalidate = false;
			setDownTouchY(mPressedY);
			if (mShowMenu && !mFingerY) {
				mFingerY = true;
				mDirectionUp = true;
			}
			break;
		case MotionEvent.ACTION_MOVE:
			final int distanceY = mPressedY - y;
			final int distanceX = Math.abs(mPressedX - x);

			if (distanceX > mTouchSlop && !mFingerX) {
				if (!mFingerY) {// TODO 如果已经向上Finger,那么不能设置横向
					mFingerX = true;
				}
			}
			if (mCloseOperateBookMark && distanceY < 0) {
				return super.onInterceptTouchEvent(ev);
			}
			if (!mFingerX) {// TODO 如果已经横向Finger,那么不能向上
				if (!mFingerY) {
					if (Math.abs(distanceY) > mTouchSlop
							|| (mShowMenu && mPressedY < menuY)) {
						mFingerY = true;
						mDirectionUp = distanceY < 0 ? false : true;
						mAddedBookMark = isAddedBookMark();
						if (!mDirectionUp && mBookMarkListener != null) {
							mIsMarkOperateViewScrollEnd = false;
							mBookMarkListener.onPrepare(!mAddedBookMark, this);
						}
						mReaderWidget.removeLongClick();
					}
				}
			}
			break;
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_CANCEL:
			if (mShowMenu && !mFingerY) {
				hideMenu();
				return true;
			}
			reSet();
			break;
		}
		if (mFingerY) {
			return true;
		}
		return super.onInterceptTouchEvent(ev);
	}

	private boolean isTouchInReaderWidget(int x, int y) {
		if (this.getChildCount() > 0) {
			View child = this.getChildAt(0);
			child.getGlobalVisibleRect(mOutRect);
			return !mOutRect.contains(x, y);
		}
		return false;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (!mScroller.isFinished() || mIsInvalidate) {
			return true;
		}
		mIsSelected = mBookMarkOperateCallback.isSelected();
		if (mPassDown || mIsSelected) {
			return super.onTouchEvent(event);
		}
		try {
			IGlobalWindow igw = ReaderAppImpl.getApp().getReaderController().getWindow();
			if (igw.isShowingWindow()){
                igw.hideWindow(true);
                return super.onTouchEvent(event);
            }
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (!mShowMenu && !mFingerY) {
			return super.onTouchEvent(event);
		}
		initHeight();
		acquireVelocityTracker(event);
		final int action = event.getAction();
		final int y = (int) event.getY();
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			mLastMotionY = y;
			break;
		case MotionEvent.ACTION_MOVE:
			if (!mTouchDown) {
				mTouchDown = true;
				MotionEvent tempEv = MotionEvent.obtain(event);
				tempEv.setAction(MotionEvent.ACTION_DOWN);
				return onTouchEvent(tempEv);
			}
			int mDeltaY = mDirectionUp ? (int) (mLastMotionY - y) / 4
					: (int) (mLastMotionY - y) / 2;
			int mScrollY = getScrollY();
			mLastMotionY = y;
			int newScrollY = mDeltaY + mScrollY;
			if (mDirectionUp) {
				if (newScrollY <= 0) {
					mDeltaY = -mScrollY;
				}
				if (newScrollY > (mScreenHeight - mBelowHeight)) {
					mDeltaY = Math.max((mScreenHeight - mBelowHeight)
							- mScrollY, 0);
				}
			} else {
				if (mDeltaY != 0 && null != mBookMarkListener) {
					mBookMarkListener.onDragMove(mDeltaY, mDeltaY);
				}
			}
			if (mCloseUpMenu) {
				break;
			}
			if (mDeltaY != 0 && mDirectionUp) {
//				scrollBy(0, mDeltaY);
			}
			break;
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_CANCEL:
			final VelocityTracker velocityTracker = mVelocityTracker;
			velocityTracker.computeCurrentVelocity(1000);
			int yVelocity = (int) velocityTracker.getYVelocity();
			if (mFingerY && mDirectionUp && !mCloseUpMenu) {
				handleMenu((mPressedY - y) > mTouchSlop);
			}
			if (mFingerY && !mDirectionUp) {
				mBookMarkListener.onEnd(yVelocity > OPERATE_VELOCITY);
			}
			reSet();
			releaseVelocityTracker();
			break;
		}

		return true;
	}

	private void acquireVelocityTracker(final MotionEvent event) {
		if (null == mVelocityTracker) {
			mVelocityTracker = VelocityTracker.obtain();
		}
		mVelocityTracker.addMovement(event);
	}

	private void releaseVelocityTracker() {
		if (null != mVelocityTracker) {
			mVelocityTracker.clear();
			mVelocityTracker.recycle();
			mVelocityTracker = null;
		}
	}

	@Override
	public void computeScroll() {
		if (mScroller.computeScrollOffset()) {
			scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
			postInvalidate();
		}
	}

	public boolean isAddedBookMark() {
		boolean exist = false;
		ReaderAppImpl readerApps = ReaderAppImpl.getApp();
		ReadInfo readInfo = (ReadInfo) readerApps.getReadInfo();
		IEpubReaderController controller = (IEpubReaderController) readerApps
				.getReaderController();
		MarkNoteManager markNoteManager = readerApps.getMarkNoteManager();
		IndexRange range = controller.getCurrentPageRange();
		if (range != null) {
			exist = markNoteManager.checkMarkExist(readInfo.getDefaultPid(), readInfo.getEpubModVersion(),
					readInfo.isBoughtToInt(), readInfo.getChapterIndex(),
					range.getStartIndexToInt(), range.getEndIndexToInt());
		}
		return exist;
	}

	public boolean isTouchProcess() {
		return isShow() || mFingerY;
	}

	public boolean isShow() {
		return mShowMenu;
	}

	public void showMenu() {
		handleMenu(true);
	}

	public void hideMenu() {
		if (isShow()) {
			handleMenu(false);
		}
	}

	private void handleMenu(boolean showMenu) {
		final int startY = getScrollY();
		int dy = 0;
		if (showMenu) {
			dy = menuHeight - getScrollY();
		} else {
			dy = -startY;
		}
//		mScroller.startScroll(0, startY, 0, dy, DURATION);
		invalidate();
//		mShowMenu = showMenu;
	}

	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		super.onScrollChanged(l, t, oldl, oldt);
		if (t == 0 && oldt != 0 && mBookMarkOperateCallback != null
				&& mDelayOperationType != TO_NONE) {
			scrollEndOperate();
		}
	}

	private void scrollEndOperate() {
		switch (mDelayOperationType) {
		case TO_EXIT:
			mDelayOperationType = TO_NONE;
			mBookMarkOperateCallback.toExitRead();
			break;
		case TO_BOOK_REVIEW:
			mDelayOperationType = TO_NONE;
			mBookMarkOperateCallback.toBookReview();
			break;
		case TO_SHARE:
			mDelayOperationType = TO_NONE;
			mBookMarkOperateCallback.toShare();
			break;
		case TO_BOOK_DETAIL:
			mDelayOperationType = TO_NONE;
			mBookMarkOperateCallback.toBookDetail();
			break;
		}
	}

	private void setMarkViewVisiable(boolean isVisiable) {
		BasePageView v = mReaderWidget.getCurrentView();
		if (v instanceof IEpubPageView) {
			IEpubPageView ev = (IEpubPageView) v;
			ev.operationMarkView(isVisiable);
		}
	}

	public void setReaderWidget(EpubReaderWidget readerWidget) {
		mReaderWidget = readerWidget;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		// final int width = MeasureSpec.getSize(widthMeasureSpec);
		final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		if (widthMode != MeasureSpec.EXACTLY) {
			throw new IllegalStateException(
					"ScrollLayout only canmCurScreen run at EXACTLY mode!");
		}

		// final int height = MeasureSpec.getSize(heightMeasureSpec);
		final int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		if (heightMode != MeasureSpec.EXACTLY) {
			throw new IllegalStateException(
					"ScrollLayout only can run at EXACTLY mode!");
		}

		// The children are given the same width and height as the ReaderLayout
		final int count = getChildCount();
		for (int i = 0; i < count; i++) {
			getChildAt(i).measure(widthMeasureSpec, heightMeasureSpec);
		}

		// LogM.e(getClass().getSimpleName(), " onMeasure ");
		scrollTo(0, 0);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);

		if (w == 0 || h == 0 || oldw == 0 || oldh == 0) {
			return;
		}
		if ((w != oldw || h != oldh) && (oldw != h && oldh != w)) {
			if (mSizeChangeListener != null) {
				mSizeChangeListener.onSizeChanged(w, h, oldw, oldh);
			}
		}
	}

	public void setOnSizeChangeListener(OnSizeChangedListener l) {
		mSizeChangeListener = l;
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		int childTop = 0;
		final int childCount = getChildCount();
		for (int i = 0; i < childCount; i++) {
			final View childView = getChildAt(i);
			if (childView.getVisibility() != View.GONE) {

				final int childWidth = childView.getMeasuredWidth();
				final int childHeight = childView.getMeasuredHeight();
				final int childLeft = 0;
				final int childRight = childWidth;
				final int childBottom = childTop + childHeight;

				childView.layout(childLeft, childTop, childRight, childBottom);

				childTop += childHeight;
			}
		}
		if (mShowMenu) {
//			scrollTo(0, menuHeight);
		}
	}

	public void setOnBottomClickListener(boolean isDDBook) {
		findViewById(R.id.read_bottom_hide_exit).setOnClickListener(
				mClickListener);
		View reviewView = findViewById(R.id.read_bottom_hide_review);
		View shareView = findViewById(R.id.read_bottom_hide_share);
		View detailView = findViewById(R.id.read_bottom_hide_book_detail);
		if (isDDBook) {
			reviewView.setOnClickListener(mClickListener);
			shareView.setOnClickListener(mClickListener);
			detailView.setOnClickListener(mClickListener);
		} else {
			reviewView.setVisibility(View.GONE);
			shareView.setVisibility(View.GONE);
			detailView.setVisibility(View.GONE);
		}
	}

	public boolean isOperate() {
		return mFingerY;
	}

	public void setOnOperateBookMarkListener(
			IMoveOperateBookMarkListener bookMarkListener) {
		this.mBookMarkListener = bookMarkListener;
	}

	public void setOnMoveCompleteOperateCallBack(
			IMoveCompleteOperateCallBack callBack) {
		this.mBookMarkOperateCallback = callBack;
	}

	public interface IMoveOperateBookMarkListener {
		public void onPrepare(boolean isAdd,
				IAddOrDeleteBookMarkCallBack callBack);

		public void onDragMove(int deltaY, int scrollValue);

		public void onEnd(boolean isVelocityToOperate);

	}

	public interface IMoveCompleteOperateCallBack {
		public void toExitRead();

		public void toBookReview();

		public void toShare();

		public void toBookDetail();

		public void toAddBookMark(boolean isAdd);

		public boolean isSelected();
	}

	public static interface OnSizeChangedListener {

		public void onSizeChanged(int w, int h, int oldw, int oldh);

	}

	@Override
	public void addOrDelete() {
		if (mBookMarkOperateCallback != null) {
			if (!mAddedBookMark) {
				DDStatisticsService.getDDStatisticsService(getContext())
						.addData(DDStatisticsService.PULL_DOWN_MARK,
								DDStatisticsService.ACTION, "add",
								DDStatisticsService.OPerateTime,
								System.currentTimeMillis() + "");
			} else {
				DDStatisticsService.getDDStatisticsService(getContext())
						.addData(DDStatisticsService.PULL_DOWN_MARK,
								DDStatisticsService.ACTION, "cancel",
								DDStatisticsService.OPerateTime,
								System.currentTimeMillis() + "");
			}
			mBookMarkOperateCallback.toAddBookMark(!mAddedBookMark);
			setMarkViewVisiable(!mAddedBookMark);
		}
	}

	private OnClickListener mClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			int i = v.getId();
			if (i == R.id.read_bottom_hide_exit) {
				DDStatisticsService.getDDStatisticsService(getContext())
						.addData(DDStatisticsService.UPPER_MENU,
								DDStatisticsService.UPPER_MENU, "shelf",
								DDStatisticsService.OPerateTime,
								System.currentTimeMillis() + "");
				mDelayOperationType = TO_EXIT;

			} else if (i == R.id.read_bottom_hide_review) {
				DDStatisticsService.getDDStatisticsService(getContext())
						.addData(DDStatisticsService.UPPER_MENU,
								DDStatisticsService.UPPER_MENU, "comment",
								DDStatisticsService.OPerateTime,
								System.currentTimeMillis() + "");
				mDelayOperationType = TO_BOOK_REVIEW;

			} else if (i == R.id.read_bottom_hide_share) {
				DDStatisticsService.getDDStatisticsService(getContext())
						.addData(DDStatisticsService.UPPER_MENU,
								DDStatisticsService.UPPER_MENU, "shareBook",
								DDStatisticsService.OPerateTime,
								System.currentTimeMillis() + "");
				mDelayOperationType = TO_SHARE;

			} else if (i == R.id.read_bottom_hide_book_detail) {
				DDStatisticsService.getDDStatisticsService(getContext())
						.addData(DDStatisticsService.UPPER_MENU,
								DDStatisticsService.UPPER_MENU, "detail",
								DDStatisticsService.OPerateTime,
								System.currentTimeMillis() + "");
				mDelayOperationType = TO_BOOK_DETAIL;

			}
			hideMenu();
		}
	};
	private boolean mTouchDown;
	private VelocityTracker mVelocityTracker;
	private boolean mIsMarkOperateViewScrollEnd = true;

	@Override
	public void setMarkVisiable(boolean isVisiable) {
		setMarkViewVisiable(isVisiable);
	}

	@Override
	public void resetScrollState() {
		this.mIsMarkOperateViewScrollEnd = true;
	}

}
