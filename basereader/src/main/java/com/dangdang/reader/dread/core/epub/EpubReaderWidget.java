package com.dangdang.reader.dread.core.epub;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;

import com.dangdang.reader.R;
import com.dangdang.reader.dread.config.ReadConfig;
import com.dangdang.reader.dread.core.base.BaseEpubPageView;
import com.dangdang.reader.dread.core.base.BasePageView;
import com.dangdang.reader.dread.core.base.BaseReaderWidget;
import com.dangdang.reader.dread.core.base.IEpubPageView;
import com.dangdang.reader.dread.core.base.IEpubPageView.DrawingType;
import com.dangdang.reader.dread.core.base.IEpubReaderController;
import com.dangdang.reader.dread.core.base.IPageAdapter;
import com.dangdang.reader.dread.core.base.IReaderController;
import com.dangdang.reader.dread.core.base.IReaderController.DAnimType;
import com.dangdang.reader.dread.core.base.IReaderController.DDirection;
import com.dangdang.reader.dread.core.base.IReaderController.DPageIndex;
import com.dangdang.reader.dread.core.epub.GalleryView.OnGalleryPageChangeListener;
import com.dangdang.reader.dread.data.GallaryData;
import com.dangdang.reader.dread.holder.GalleryIndex;
import com.dangdang.reader.dread.view.Scroller;
import com.dangdang.zframework.utils.DRUiUtility;

import java.lang.ref.WeakReference;
import java.util.LinkedList;

public class EpubReaderWidget extends BaseReaderWidget implements Runnable,
        View.OnLongClickListener {

    private final static int Duration = 300;
    private final static int FastScrollTime = 400;
    private final static int FlipVelocity = 90;
    private final static int MinFlipTurn = 10;
    private final static int MinDragTurn = 80;

    private final static int CI_PREV = 2;
    private final static int CI_CURR = 1;
    private final static int CI_NEXT = 0;

    private IPageAdapter mAdapter;
    private IEpubReaderController mController;
    // private BaseAnimProvider mAnimProvider;

    private Scroller mScroller;

    private DAnimType mAnimType = DAnimType.Slide;
    private DAnimType mTmpAnimType = DAnimType.Shape;

    private int mScrollX;
    private int mScrollY;
    private int mScrollLastX;
    private int mScrollLastY;
    /**
     * 虚拟值
     */
    public int currentIndex = 1;
    /**
     * 轻击时翻页的最小距离
     */
    private int mFlipMinTurn = MinFlipTurn;
    /**
     * 拖行时翻页的最小距离
     */
    private int mDragMinTurn = MinDragTurn;

    private int mEmpValue = 1;

    private double mScreenSize;

    private boolean mControlAnimFinish = true;
    private boolean mUserInteracting = false;

    // ------ touch start -------
    // private int mMaximumVelocity;
    private VelocityTracker mVelocityTracker;
    private volatile LongClickRunnable mPendingLongClickRunnable;
    private volatile boolean mLongClickPerformed;
    private volatile boolean mPendingPress;
    // private volatile boolean mPendingDoubleTap;
    private int mPressedX;
    private int mPressedY;

    // ------ touch end -------

    private final SparseArray<View> mChildViews = new SparseArray<View>(3);
    private final LinkedList<View> mViewCache = new LinkedList<View>();
    private Thread mUiThread;

    public EpubReaderWidget(Context context) {
        super(context);
        cHandler = new MyHandler(this);
        mUiThread = Thread.currentThread();
        init(context);
    }

    private void init(Context context) {
        setBackgroundColor(ReadConfig.getConfig().getReaderBgColor());
        setOnLongClickListener(this);
        setDrawingCacheEnabled(false);
        setDrawingCacheBackgroundColor(Color.TRANSPARENT);

        Interpolator linearInter = new DangInterpolator();
        mScroller = new Scroller(context, linearInter);

        initScreenReleateParamsInner();
    }

    protected void initScreenReleateParamsInner() {
        mScreenSize = DRUiUtility.getScreenSize();
        float den = getDensity();
        if (den > 1) {
            mFlipMinTurn = (int) (MinFlipTurn * den);
            mDragMinTurn = getScreenWidth() / 3;
        }
        calcEmpValue();// (int) den*5;

        int stepTime = 40;
        if (mScreenSize > 4) {
            stepTime = 30;
        }
        mScroller.setStep(stepTime, 1f);
    }

    public static class DangInterpolator implements Interpolator {

        private final float mFactor;
        private final double mDoubleFactor;

        public DangInterpolator() {
            mFactor = 1.1f;
            mDoubleFactor = 2.0;
        }

        @Override
        public float getInterpolation(float input) {
            float ret = input;// (mFactor == 1.0f) ? input * input :
            // (float)Math.pow(input,
            // mDoubleFactor);//AccelerateInterpolator
            // float ret = (float)(Math.cos((input + 1) * Math.PI) / 2.0f) +
            // 0.5f;//AccelerateDecelerateInterpolator
            // LogM.i(getClass().getSimpleName(),
            // "[danim] getInterpolation input = " + input + ", ret = " + ret);
            return ret;
        }

    }

    private class LongClickRunnable implements Runnable {
        public void run() {
            try {
                if (performLongClick()) {
                    mLongClickPerformed = true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void postLongClickRunnable() {
        mLongClickPerformed = false;
        mPendingPress = false;
        if (mPendingLongClickRunnable == null) {
            mPendingLongClickRunnable = new LongClickRunnable();
        }
        postDelayed(mPendingLongClickRunnable,
                (long) (1.5 * ViewConfiguration.getLongPressTimeout()));
    }

    @Override
    public boolean onLongClick(View v) {
        ReadConfig.isFirstLongClick = true;
        final IReaderController controller = getController();
        if (controller == null) {
            return false;
        }
        mUserInteracting = false;
        setCanCross(controller);
        return controller.onFingerLongPress(mPressedX, mPressedY);
    }

    private void setCanCross(IReaderController controller) {
        BasePageView pageView = getCurrentView();
        if (pageView instanceof EpubPageView) {
            EpubPageView currentView = (EpubPageView) pageView;
            ((EpubReaderController) controller).setCanCross(!currentView
                    .isInGalleryRect(mPressedX, mPressedY));
        }
    }

    @Override
    public void run() {
        if (mScroller.computeScrollOffset()) {
            int x = mScroller.getCurrX();
            int y = mScroller.getCurrY();
            mScrollX += x - mScrollLastX;
            mScrollY += y - mScrollLastY;
            mScrollLastX = x;
            mScrollLastY = y;

            // printLog(" [danim] run x = " + x + ", y = " + y + ", XScroll = "
            // + mScrollX + ", YScroll = " + mScrollY + ", mScreenSize=" +
            // mScreenSize);
            requestLayout();
            post(this);
            // cHandler.postAtTime(this, SystemClock.uptimeMillis());
        } else {
            /*
             * DPageIndex pageIndex = DPageIndex.Current; if(isMinTurn()){
			 * pageIndex = getPageIndex(mEndX, mEndY);
			 * //onScrollEnd(pageIndex);s }
			 */
            printLog(" [danim] run Scroller.isFinished ");
            if (isControlAnimFinish()) {
                return;
            }
            cHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    printLog(" [danim] run Scroller.isFinished ControlAnimFinish=true");
                    setControlAnimFinish();
                }
            }, 150);
        }
    }

	/*
	 * protected BaseAnimProvider getAnimProvider(){ DAnimType animType =
	 * getAnimType(); if(animType == DAnimType.Shift){ mAnimProvider = new
	 * ShiftAnimProvider(); } else if(animType == DAnimType.Slide){
	 * mAnimProvider = new ShiftAnimProvider(); } else { mAnimProvider = new
	 * ShiftAnimProvider(); } return mAnimProvider; }
	 */

    private void onScrollEnd(DPageIndex pageIndex, int index) {
        IReaderController controller = getController();
        if (controller != null) {
            controller.onScrollingEnd(pageIndex);
        }
    }

    @Override
    public void onSizeChange() {
        initScreenReleateParams();
        initScreenReleateParamsInner();
        initChildPageViewSize();
        getController().onSizeChange();
    }

    private void initChildPageViewSize() {
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            if (child != null && child instanceof BasePageView) {
                ((BasePageView) child).initScreenReleateParams();
            }
        }
    }

    /**
     * 是否翻页最小距离
     *
     * @return
     */
	/*
	 * private boolean isMinTurn(){ int distance = mEndX - mStartX; return
	 * Math.abs(distance) > getWidth() / 2; }
	 */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int c = getChildCount();

        int measuredWidth = getScreenWidth();
        int measuredHeight = getScreenHeight();
        setMeasuredDimension(measuredWidth, measuredHeight);

        // printLog(" onMeasure " + c + ", " + measuredWidth + ", " +
        // measuredHeight);
        for (int i = 0; i < c; i++) {
            View v = getChildAt(i);
            if (v != null) {
                measureView(v);
            } else {
                printLogE(" onMeasure v == null, i = " + i + ", c = " + c);
            }
        }
    }

    private void measureView(View v) {
        v.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
		/*
		 * v.measure(View.MeasureSpec.EXACTLY | (int)(v.getMeasuredWidth()),
		 * View.MeasureSpec.EXACTLY | (int)(v.getMeasuredHeight()));
		 */
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right,
                            int bottom) {

        DPageIndex currIndex = getPageIndex(mEndX, mEndY);

        printLog(" onLayout in " + changed + ", " + left + ", " + right +
                ", current = " + currentIndex + ", userI = " + mUserInteracting +
                ", scrollX = " + mScrollX + ", scrollY = " + mScrollY + ", " +
                currIndex + ", " + isScrollFinish());


        View currentPageView = getCurrentView();//
        if (getAnimType() == DAnimType.Slide) {
            if (currIndex == DPageIndex.Previous) {
                currentPageView = getPrevView();
            }
            if (currentPageView != null) {
                scrollPrevOrNextBySlide(currentPageView, currIndex);
                removeOld();
            }
        } else {
            if (currentPageView != null) {
                scrollPrevOrNext(currentPageView, currIndex);
                removeOld();
            }
        }
        final boolean firstPage = isFirstPage();
        final boolean lastPage = isLastPage();

        currentPageView = getOrCreateChild(currentIndex, DPageIndex.Current);
        Rect cRect = layoutCurrent(currIndex, currentPageView, firstPage);
        currentPageView
                .layout(cRect.left, cRect.top, cRect.right, cRect.bottom);

        if (!firstPage) {
            View prevPageView = getOrCreateChild(currentIndex - 1,
                    DPageIndex.Previous);

            int pLeft = 0, pTop, pRight, pBottom;
            pLeft = getPrevLeft(cRect, prevPageView, currIndex);
            pTop = 0;
            pRight = pLeft + prevPageView.getMeasuredWidth();
            pBottom = pTop + prevPageView.getMeasuredHeight();

            prevPageView.layout(pLeft, pTop, pRight, pBottom);

            // printLog(" onLayout prev " + pLeft + ", " + pTop + ", " + pRight
            // + ", " + pBottom + ", " + prevPageView);
        }

        if (!lastPage) {
            View nextPageView = getOrCreateChild(currentIndex + 1,
                    DPageIndex.Next);

            int nLeft, nTop, nRight, nBottom;
            nLeft = getNextLeft(cRect);
            nTop = 0;
            nRight = nLeft + nextPageView.getMeasuredWidth();
            nBottom = nTop + nextPageView.getMeasuredHeight();
            nextPageView.layout(nLeft, nTop, nRight, nBottom);

            // printLog(" onLayout next " + nLeft + ", " + nTop + ", " + nRight
            // + ", " + nBottom + ", " + nextPageView);
        } else {
            printLog(" onLayout next last=true");
        }

        if (isFadeFinish()) {
            resetTmpAnimType();
        }

        final View nextView = getNextView();
        final View currentView = getCurrentView();
        final View prevView = getPrevView();

        final int nextCI = indexOfChild(nextView);
        final int currCI = indexOfChild(currentView);
        final int prevCI = indexOfChild(prevView);

        // printLog(" onLayout  childIndex start " + nextCI + ", " + currCI +
        // ", " + prevCI);
        if (nextCI != CI_NEXT || currCI != CI_CURR || prevCI != CI_PREV) {
            bringChildToFront(nextView);
            bringChildToFront(currentView);
            bringChildToFront(prevView);
            // printLog(" onLayout  childIndex end " + indexOfChild(nextView) +
            // ", " + indexOfChild(currentView) + ", " +
            // indexOfChild(prevView));
        }

        mScrollX = mScrollY = 0;
        printLog(" onLayout out currentIndex=" + currentIndex);
        // invalidate();
    }

    private int getPrevLeft(Rect cRect, View prevPageView, DPageIndex pageIndex) {
        if (pageIndex == DPageIndex.Previous && !isFadeFinish()) {
            return 0;
        }
        int pLeft = 0;
        if (getAnimType() == DAnimType.Shift) {
            pLeft = cRect.left - prevPageView.getMeasuredWidth();
        } else {
            int cLeft = prevPageView.getLeft() + mScrollX;
            pLeft = (pageIndex == DPageIndex.Previous) ? cLeft : -prevPageView
                    .getMeasuredWidth();
            if (isAnimFinish()) {
                pLeft = -prevPageView.getMeasuredWidth();
            }
            // printLogE("onLayout prev " + prevPageView.getLeft() + ", " +
            // mScrollX);
            pLeft = pLeft > 0 ? -prevPageView.getMeasuredWidth() : pLeft; // TODO
            // (待定)解决快速prev时翻过当前页问题?
        }

        return pLeft;
    }

    private int getNextLeft(Rect cRect) {
        if (!isFadeFinish()) {
            return 0;
        }
        int nLeft = cRect.right;
        if (getAnimType() == DAnimType.Shift) {
            nLeft = cRect.right;
        } else {
            nLeft = isAnimFinish() ? cRect.right : 0;
        }
        return nLeft;
    }

    public boolean isAnimFinish() {
        return !mUserInteracting && isScrollFinish();
    }

    protected boolean isScrollFinish() {
        return mScroller.isFinished();
    }

    private Rect layoutCurrent(DPageIndex currIndex, View currentPageView,
                               final boolean firstPage) {
        Rect cRect = new Rect();
        int cLeft, cTop, cRight, cBottom;

        cLeft = getCurrentLayoutLeft(currentPageView, currIndex);
        cTop = 0;// currentPageView.getTop() + mYScroll;
        cRight = cLeft + currentPageView.getMeasuredWidth();
        cBottom = cTop + currentPageView.getMeasuredHeight();
        // mScrollX = mScrollY = 0;

        if (isAnimFinish() || (firstPage && cLeft > 0)) {
            // Point corr = getCorrection(getScrollBounds(cLeft, cTop, cRight,
            // cBottom));
			/*
			 * cLeft = 0;//TODO ? //cLeft += corr.x; cTop += corr.y; cRight +=
			 * corr.x; cBottom += corr.y;
			 */
            cLeft = 0;
            cRight = cLeft + currentPageView.getMeasuredWidth();
            // printLog(" onLayout reset cLeft=0 " + (firstPage && cLeft > 0));
        }
        cRect.set(cLeft, cTop, cRight, cBottom);
        // currentPageView.store_ebook_pay_activity(cLeft, cTop, cRight, cBottom);
        // printLog(" onLayout current " + cLeft + ", " + cTop + ", " + cRight +
        // ", " + cBottom + ", " + isScrollFinish() + ", " + currentPageView);

        return cRect;
    }

    private int getCurrentLayoutLeft(View currentPageView, DPageIndex currIndex) {
        int cLeft = 0;
        if (getAnimType() == DAnimType.Shift) {
            cLeft = currentPageView.getLeft() + mScrollX;
        } else {
            cLeft = (currIndex == DPageIndex.Next) ? (currentPageView.getLeft() + mScrollX)
                    : 0;
        }
        return cLeft;
    }

    private void removeOld() {
        int numChildren = mChildViews.size();
        int childIndices[] = new int[numChildren];
        for (int i = 0; i < numChildren; i++) {
            childIndices[i] = mChildViews.keyAt(i);
        }
        for (int i = 0; i < numChildren; i++) {
            int ai = childIndices[i];
            if (ai < currentIndex - 1 || ai > currentIndex + 1) {
                View v = mChildViews.get(ai);
                mViewCache.add(v);
                removeViewInLayout(v);
                mChildViews.remove(ai);

                printLog(" onLayout remove = " + ai + ", current = " +
                        currentIndex + ", XScroll = " + mScrollX);

            }
        }
    }

    private void scrollPrevOrNext(View view, DPageIndex currIndex) {
        if (!isFadeFinish()) {
            printLogV("  scrollPrevOrNext isFadeFinish=false ");
            return;
        }

        int tmpHalf = view.getLeft() + view.getMeasuredWidth() + mScrollX;
		/*
		 * printLog(" tmpHalf = " + tmpHalf + ", " + currentPageView.getLeft() +
		 * ", " + currentPageView.getMeasuredWidth() + ", " + mScrollX);
		 */
        printLog(" onLayout scrollPrevOrNext currentIndex=" + currentIndex +
                ", tmpHalf=" + tmpHalf + ", " + getEmp());
        if (currIndex == DPageIndex.Next) {
            if (tmpHalf < getEmp()) {
                // post(this);//cHandler.postAtFrontOfQueue(this);
                currentIndex++;
                onScrollEnd(DPageIndex.Next, 0);
            }
        } else if (currIndex == DPageIndex.Previous) {
            int tmpHalf2 = view.getLeft() + mScrollX;
            if (tmpHalf2 > (getWidth() - getEmp())) {
                // post(this);
                currentIndex--;
                onScrollEnd(DPageIndex.Previous, 1);
            }
            printLog(" onLayout 2 currentIndex=" + currentIndex +
                    ", tmpHalf2=" + tmpHalf2);
        }
    }

    private void scrollPrevOrNextBySlide(View view, DPageIndex currIndex) {
        if (!isFadeFinish()) {
            printLogV("  scrollPrevOrNextBySlide isFadeFinish=false ");
            return;
        }
        if (currIndex == DPageIndex.Next) {
            int tmpHalf = view.getLeft() + view.getMeasuredWidth() + mScrollX;
			/*
			 * printLog(" tmpHalf = " + tmpHalf + ", " +
			 * currentPageView.getLeft() + ", " +
			 * currentPageView.getMeasuredWidth() + ", " + mScrollX);
			 */
            printLog(" onLayout Slide1 currentIndex=" + currentIndex
                    + ", tmpHalf=" + tmpHalf + ", " + getEmp());
            if (tmpHalf <= getEmp()) {
                // post(this);//cHandler.postAtFrontOfQueue(this);
                currentIndex++;
                onScrollEnd(DPageIndex.Next, 2);
            }
        } else if (currIndex == DPageIndex.Previous) {
            if (isPrevPageEndBySlide(view)) {
                // post(this);
                currentIndex--;
                onScrollEnd(DPageIndex.Previous, 3);
            }
            // printLog(" onLayout Slide2 currentIndex=" + currentIndex +
            // ", tmpHalf2=" + tmpHalf2);
        }
    }

    private boolean isPrevPageEndBySlide(View view) {
        boolean isEnd = false;
        if (mTmpAnimType == DAnimType.Shape) {
            isEnd = (view.getLeft() + mScrollX) > (getWidth() - getEmp());
        } else {
            int tmpHalf2 = view.getLeft() + mScrollX;
            isEnd = Math.abs(tmpHalf2) <= getEmp();
        }
        return isEnd;
    }

    private int getEmp() {
        return mEmpValue;
    }

    private Point getCorrection(Rect bounds) {
        int x = Math.min(Math.max(0, bounds.left), getNextLeft(bounds));
        int y = Math.min(Math.max(0, bounds.top), bounds.bottom);
        return new Point(x, y);
    }

	/*
	 * private Rect getScrollBounds(View v) { int left = v.getLeft() + mScrollX;
	 * int top = v.getTop() + mScrollY; int right = left + v.getMeasuredWidth();
	 * int bottom = top + v.getMeasuredHeight(); return getScrollBounds(left,
	 * top, right, bottom); }
	 */

    private Rect getScrollBounds(int left, int top, int right, int bottom) {
        // printLog(" getScrollBounds 1step = " + left + ", " + top + ", " +
        // right + ", " + bottom);
        int xmin = getWidth() - right;
        int xmax = -left;
        int ymin = getHeight() - bottom;
        int ymax = -top;
        // printLog(" getScrollBounds 2step = " + xmin + ", " + xmax + ", " +
        // ymin + ", " + ymax);
        if (xmin > xmax)
            xmin = xmax = (xmin + xmax) / 2;
        if (ymin > ymax)
            ymin = ymax = (ymin + ymax) / 2;

        return new Rect(xmin, ymin, xmax, ymax);
    }

    public View getOrCreateChild(int current, DPageIndex pageIndex) {

        View pageView = mChildViews.get(current);
        if (pageView == null) {
            View cacheView = getCached();
            pageView = mAdapter.getView(pageIndex, cacheView,
                    getPageContainer());
            addAndMeasureChild(pageView);
            mChildViews.append(current, pageView);
            printLog(" getOrCreateChild append = " + current + ", " +
                    pageView);
            pageView.setTag(R.drawable.icon, current);
        }

        return pageView;
    }

    private View getCached() {
        return mViewCache.size() == 0 ? null : mViewCache.removeFirst();
    }

    private void addAndMeasureChild(View v) {
        LayoutParams params = v.getLayoutParams();
        if (params == null) {
            params = new LayoutParams(LayoutParams.WRAP_CONTENT,
                    LayoutParams.WRAP_CONTENT);
        }
        addViewInLayout(v, 0, params, true);
        measureView(v);
    }

    private EpubReaderWidget getPageContainer() {
        return this;
    }

    public boolean isFirstPage() {
        return mController.isFirstPageInBook();
    }

    public boolean isLastPage() {
        return mController.isLastPageInBook();//
    }

	/*
	 * @Override public boolean dispatchTouchEvent(MotionEvent ev) {
	 * 
	 * printLog("  dispatchTouchEvent Action = " + ev.getAction() + "," +
	 * ev.getPointerCount());
	 * 
	 * return super.dispatchTouchEvent(ev); }
	 */

    private boolean hasMorePointer = false;

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        // printLog("  onTouchEvent Action = " + event.getAction() + "," +
        // event.getPointerCount() + ", " + hasMorePointer);

        final int action = event.getAction();
        final IReaderController controller = getController();
        if (controller == null) {
            return true;
        }
        int x = (int) event.getX();
        int y = (int) event.getY();
        obtainVelocityTracker(event);

        switch (action) {
            case MotionEvent.ACTION_UP:
                mVelocityTracker.computeCurrentVelocity(1000);
                boolean fast = isFlip(mVelocityTracker);

			/*
			 * if (mPendingDoubleTap) { controller.onFingerDoubleTap(x, y); }
			 */
                if (mLongClickPerformed) {
                    controller.onFingerReleaseAfterLongPress(x, y);
                } else {
                    if (mPendingLongClickRunnable != null) {
                        removeCallbacks(mPendingLongClickRunnable);
                        mPendingLongClickRunnable = null;
                    }
                    if (mPendingPress) {
                        if (!hasMorePointer) {
                            controller
                                    .onFingerSingleTap(x, y, event.getEventTime());
                        }
                    } else {
                        controller.onFingerRelease(x, y, fast);
                    }
                }
                releaseVelocityTracker();
                // mPendingDoubleTap = false;
                mPendingPress = false;
                mUserInteracting = false;
                hasMorePointer = false;
                break;
            case MotionEvent.ACTION_DOWN:
                postLongClickRunnable();
                mPendingPress = true;
                mUserInteracting = true;
                mPressedX = x;
                mPressedY = y;
                break;
            case MotionEvent.ACTION_MOVE:
                final int slop = ViewConfiguration.get(getContext())
                        .getScaledTouchSlop();
                final boolean isXMove = Math.abs(mPressedX - x) > slop;
                final boolean isYMove = Math.abs(mPressedY - y) > slop;
                // printLog(" onTouchEvent mPressedY = " + mPressedY + ", y = " +
                // y);
                final boolean isAMove = isXMove || isYMove;
                // if (mLongClickPerformed) {
                if (mLongClickPerformed && isAMove) {
                    controller.onFingerMoveAfterLongPress(x, y);
                } else {
                    if (mPendingPress) {
                        if (isAMove) {
                            if (mPendingLongClickRunnable != null) {
                                removeCallbacks(mPendingLongClickRunnable);
                            }
                            controller.onFingerPress(mPressedX, mPressedY);
                            mPendingPress = false;
                        }
                    }
                    if (!mPendingPress) {
                        mUserInteracting = controller.onFingerMove(x, y);
                    }
                }
                break;
            case MotionEvent.ACTION_POINTER_2_DOWN:
                setMorePointer();
                removeLongClick();
                mUserInteracting = false;
                break;
            case MotionEvent.ACTION_POINTER_2_UP:
            case MotionEvent.ACTION_CANCEL:
                resetMorePointer();
                mUserInteracting = false;
                break;
        }
        return true;
    }

    public void resetMorePointer() {
        hasMorePointer = false;
    }

    private void setMorePointer() {
        hasMorePointer = true;
    }

    private boolean isFlip(VelocityTracker vTracker) {
        float xVt = vTracker.getXVelocity();
        // float yVt = vTracker.getYVelocity();
        // printLog(" isFlip xVt=" + xVt + ", yVt=" + yVt);
        return Math.abs(xVt) > FlipVelocity;
    }

    public void removeLongClick() {
        if (mPendingLongClickRunnable != null) {
            removeCallbacks(mPendingLongClickRunnable);
            mPendingLongClickRunnable = null;
        }
    }

    private void obtainVelocityTracker(MotionEvent event) {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(event);
    }

    private void releaseVelocityTracker() {
        if (mVelocityTracker != null) {
            mVelocityTracker.recycle();
            mVelocityTracker = null;
        }
    }

    protected BasePageView getPrevView() {
        try {
            return (BasePageView) mChildViews.get(currentIndex - 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public BasePageView getCurrentView() {
        return (BasePageView) mChildViews.get(currentIndex);
    }

    protected BasePageView getNextView() {
        try {
            return (BasePageView) mChildViews.get(currentIndex + 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private int mStartX, mStartY;
    private int mEndX, mEndY;

    @Override
    public void startManualScrolling(int x, int y, DDirection direction) {

        mStartX = mEndX = x;
        mStartY = mEndY = y;
        // 动画开始位置？
        // getAnimProvider().startAnimatedScrolling(x, y);
    }

    @Override
    public boolean scrollManuallyTo(final int x, final int y) {
        printLog(" testanim scrollManuallyTo " + isScrollFinish() +
                ", currentIndex=" + currentIndex);
        boolean canScroll = getController().canScroll(getPageIndex(x, y));
        if (getAnimType() == DAnimType.None) {
            return true;
        }
        if (canScroll && isScrollFinish()) {

            int distanceX = mEndX - x;
            int distanceY = mEndY - y;
            mEndX = x;
            mEndY = y;
            mScrollX -= distanceX;
            mScrollY -= distanceY;
            requestLayout();
            printLog(" scrollManuallyTo " + distanceX + ", " + distanceY +
                    ", scrollX=" + mScrollX + ",scrollY=" + mScrollY);
            resetTmpAnimType();
        }
        checkAllVisiby();

        return true;
    }

    private DPageIndex getPageIndex(int x, int y) {

        DPageIndex pageIndex = DPageIndex.Current;
        // DAnimType animType = getAnimType();
        if (mStartX != x) {
            pageIndex = mStartX < x ? DPageIndex.Previous : DPageIndex.Next;
        }

        return pageIndex;
    }

    private DAnimType getAnimType() {
        mAnimType = ReadConfig.getConfig().getAnimationTypeNew();
        return mAnimType;
    }

    private void resetTmpAnimType() {
        mTmpAnimType = getAnimType();
    }

    @Override
    public void animChangeAfter() {
        calcEmpValue();
        for (int i = 0; i < getChildCount(); i++) {
            BasePageView pageView = (BasePageView) getChildAt(i);
            if (pageView != null) {
                pageView.animChangeAfter();
            }
        }
        clearViewCache();
        requestLayout();
    }

    private void calcEmpValue() {
        DAnimType animType = getAnimType();
        mEmpValue = (int) ((animType == DAnimType.Slide) ? 5 * DRUiUtility
                .getDensity() : 1);
    }

    private long mPrevScrollingTime = 0;

    @Override
    public boolean startAnimatedScrolling(int x, int y, int speed,
                                          boolean sourceAnim) {

        printLog(" startAnimatedScrolling " + x + "," + y);
        int width = getWidth();
        int dx = width;

        int dy = 0;
        if (x < width / 3) {// prev
            mStartX = 0; // 为了计算onScrollEnd的PageIndex
            mStartY = 0;
            mEndX = width;
            mEndY = 0;

        } else if (x > (width - width / 3)) {// next
            dx = -width;
            dy = 0;
            mStartX = width; // 为了计算onScrollEnd的PageIndex
            mStartY = 0;
            mEndX = 0;
            mEndY = 0;
        } else {
            return false;
        }
        final DPageIndex pageIndex = getPageIndex(x, y);
        final boolean canScroll = getController().canScroll(pageIndex);
        if (!canScroll) {
            printLog(" startAnimatedScrolling canScroll == false ");
            return false;
        }
        if (!isFadeFinish()) {
            printLog(" startAnimatedScrolling isFadeFinish == false ");
            if (isFastScroll()) {
                setFadeFinish();
            }
            return false;
        }
        if (!isScrollFinish() || !isControlAnimFinish()) {
            printLog(" startAnimatedScrolling not finish " + isScrollFinish()
                    + "|" + isControlAnimFinish());
            return false;
        }
        if (getAnimType() == DAnimType.None) {
            sourceAnim = false;
        }
        int dar = sourceAnim ? Duration : 1;
        startScroll(dx, dy, dar);
        mScrollLastX = mScrollLastY = 0;

        if (!sourceAnim && getAnimType() != DAnimType.None) {
            boolean isFast = isFastScroll();
            printLog(" startAnimatedScrolling " + isFast);
            if (!isFast) {
                startFadeAnim(pageIndex);
                return true;
            }
        }

        checkAllVisiby();
        return true;
    }

    private boolean isFastScroll() {
        long currentTime = System.currentTimeMillis();
        boolean isFast = (currentTime - mPrevScrollingTime) <= FastScrollTime;
        mPrevScrollingTime = currentTime;
        return isFast;
    }

    private void startFadeAnim(final DPageIndex pageIndex) {
        View outView = getCurrentView();
        View inView = pageIndex == DPageIndex.Previous ? getPrevView()
                : getNextView();
        if ((inView instanceof IEpubPageView)
                && (outView instanceof IEpubPageView)) {
            resetFadeFinish();
            mTmpAnimType = DAnimType.Shape;
            Animation mOutAnim = AnimationUtils.loadAnimation(getContext(),
                    R.anim.reader_fade_out);
            Animation mInAnim = AnimationUtils.loadAnimation(getContext(),
                    R.anim.reader_fade_in);
            outView.startAnimation(mOutAnim);
            inView.startAnimation(mInAnim);

            mInAnim.setAnimationListener(mAnimListener);
            checkVisiby(outView);
            checkVisiby(inView);

            printLog(" startFadeAnim " + pageIndex + "],[ " + outView + ", "
                    + inView);
            if (pageIndex == DPageIndex.Previous) {
                View next = getNextView();
                if (next != null) {
                    next.setVisibility(View.INVISIBLE);// 解决向上翻页渐变效果不对问题
                }
            }
        } else {
            checkVisiby(outView);
            checkVisiby(inView);
        }
    }

    private void checkAllVisiby() {
        checkVisiby(getCurrentView());
        checkVisiby(getPrevView());
        checkVisiby(getNextView());
    }

    private void checkVisiby(View view) {
        if (view != null && view.getVisibility() != VISIBLE) {
            view.setVisibility(VISIBLE);
        }
    }

    private boolean isFadeFinish = true;
    final AnimationListener mAnimListener = new AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {
            resetFadeFinish();
            printLog(" onAnimationStart ");
        }

        @Override
        public void onAnimationEnd(Animation animation) {
            setFadeFinish();
            mScrollX = mScroller.getFinalX();
            requestLayout();
            printLog(" onAnimationEnd ");
        }

        @Override
        public void onAnimationRepeat(Animation animation) {
        }
    };

    public boolean scrollRelease(int x, int y, boolean fast) {
        boolean rScroll = false;

        mScrollLastX = mScrollLastY = 0;
        final DPageIndex pageIndex = getPageIndex(x, y);
        final boolean canScroll = getController().canScroll(pageIndex);
        if (!canScroll && !isCurrNeedScroll()) {
            return rScroll;
        } else {
            printLog(" scrollRelease NeedScroll ");
        }
        if (!isScrollFinish()) {
            printLog(" scrollRelease not finish ");
            return rScroll;
        }
        setFadeFinish();

        View view = getCurrentView();//
        if (view == null) {
            printLogE(" scrollRelease view == null");
            return rScroll;
        }

        if (getAnimType() == DAnimType.None) {
            if (Math.abs(x - mStartX) > mFlipMinTurn) {
                int width = getWidth();
                int dx = 0;
                if (pageIndex == DPageIndex.Previous) {
                    mStartX = 0;
                    mEndX = width;
                    mEndY = 0;
                    dx = width;
                } else {
                    mStartX = width;
                    mEndX = 0;
                    mEndY = 0;
                    dx = -width;
                }
                int dy = 0;
                int dar = 1;
                startScroll(dx, dy, dar);
            }
        } else {
            int width = getWidth();
            int minTurn = fast ? mFlipMinTurn : mDragMinTurn;
            int dx = calcDx(x, y, width, minTurn);
            int dy = 0;
            int dar = calcDuration(dx);
            printLog(" 4 testanim  slideViewOntoScreen dx = " + dx + ", " +
                    dar + ", " + mScroller.getCurrX());//负向左，正向右
            startScroll(dx, dy, dar);
            resetTmpAnimType();
            rScroll = Math.abs(dx) > minTurn;
        }
        checkAllVisiby();
        resetControlAnimFinish();
        return rScroll;
    }

    protected void startScroll(int dx, int dy, int dar) {
        mScroller.startScroll(0, 0, dx, dy, dar);
        post(this);
    }

    private boolean isCurrNeedScroll() {
        View view = getCurrentView();//
        return view != null && view.getLeft() != 0;
    }

    private void setFadeFinish() {
        isFadeFinish = true;
    }

    private void resetFadeFinish() {
        isFadeFinish = false;
    }

    private boolean isFadeFinish() {
        return isFadeFinish;
    }

    private void setControlAnimFinish() {
        mControlAnimFinish = true;
    }

    private void resetControlAnimFinish() {
        mControlAnimFinish = false;
    }

    private boolean isControlAnimFinish() {
        return mControlAnimFinish;
    }

    private int calcDx(int x, int y, int width, int minTurn) {
        int moveXDistance = 0;
        View view = getCurrentView();
        try {
            if (getAnimType() == DAnimType.Slide) {
                if (getPageIndex(x, y) == DPageIndex.Previous) {
                    moveXDistance = getPrevView().getRight();
                } else {
                    moveXDistance = view.getLeft();
                }
            } else {
                moveXDistance = view.getLeft();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        int left = moveXDistance + mScrollX;
        int dx = left;
//		 printLog(" 3  slideViewOntoScreen left = " + left + ", minTurn = " +
//		 mMinTurn + ", scrollX = " + mScrollX + ", width = " + width);
        if (Math.abs(left) > minTurn) {
            dx = left > 0 ? -(left - width) : -(left + width);
        } else {
            dx = -left;
        }
        return dx;
    }

    private int calcDuration(int dx) {
        int dur = (int) (Math.abs(dx) * 1f / getScreenWidth() * Duration);
        int minDur = Duration / 2;
        return dur < minDur ? minDur : dur;
    }

    @Override
    public int doDrawing(final DPageIndex pageIndex, final DrawingType type,
                         final DrawPoint start, final DrawPoint end,
                         final DrawPoint current, final Rect[] rects, final int drawLineColor) {

        View view = getCurrentView();
        if (view == null || !(view instanceof BaseEpubPageView)) {
            return 0;
        }
        if (isScrollFinish()) {
            BaseEpubPageView pageView = (BaseEpubPageView) view;
            if (pageIndex == DPageIndex.Current) {
                pageView.doDrawing(type, start, end, current, rects, drawLineColor);
            } else {
                if (pageIndex == DPageIndex.Previous) {
                    pageView = (BaseEpubPageView) getPrevView();
                } else if (pageIndex == DPageIndex.Next) {
                    pageView = (BaseEpubPageView) getNextView();
                }
                if (pageView == null) {
                    return 0;
                }
                final BaseEpubPageView tPageView = pageView;
                cHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // printLog(" testcrosspage anim true " + tPageView);
                        tPageView.doDrawing(type, start, end, current, rects, drawLineColor);
                    }
                }, 250);
            }

        } else {
            printLog(" testcrosspage anim false");
        }

        return 0;
    }

    @Override
    public int drawFinish(final DrawingType type, final DrawPoint current,
                          boolean isPrev, boolean isNext) {
        if (!(getCurrentView() instanceof BaseEpubPageView)) {
            return 0;
        }

        if (isPrev || isNext) {
            cHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    EpubPageView pageView = (EpubPageView) getCurrentView();
                    pageView.drawFinish(type, current);
                }
            }, 250);
        } else {
            EpubPageView pageView = (EpubPageView) getCurrentView();
            pageView.drawFinish(type, current);
        }
        return 0;
    }

    @Override
    public void repaint() {
        printLogV(" repaint() ");
        post(new Runnable() {
            @Override
            public void run() {
                requestLayout();
                invalidate();
            }
        });
    }

    @Override
    public void repaintSync(boolean prevPaint, boolean nextPaint) {
        printLogV(" repaintSync() ");

        repaintCurrent();
        if (prevPaint) {
            repaintPrev();
        }
        if (nextPaint) {
            repaintNext();
        }
    }

    protected void repaintNext() {
        BasePageView next = getNextView();
        if (next != null) {
            getAdapter().refreshView(DPageIndex.Next, next, this);
        } else {
            printLogE(" repaintNext() next==null ");
        }
    }

    protected void repaintPrev() {
        BasePageView prev = getPrevView();
        if (prev != null) {
            getAdapter().refreshView(DPageIndex.Previous, prev, this);
        } else {
            printLogE(" repaintPrev() prev==");
        }
    }

    protected void repaintCurrent() {
        BasePageView current = getCurrentView();
        if (current != null) {
            getAdapter().refreshView(DPageIndex.Current, current, this);
        } else {
            printLogE(" repaintCurrent() current==null ");
        }
    }

    @Override
    public void repaintFooter() {

        BasePageView current = getCurrentView();
        if (current != null) {
            current.repaintFooter();
        }
        BasePageView prev = getPrevView();
        if (prev != null) {
            prev.repaintFooter();
        }
        BasePageView next = getNextView();
        if (next != null) {
            next.repaintFooter();
        }
    }

    @Override
    public void reset() {
        printLogV(" reset() ");
        if (isUiThread()) {
            clearInner();
        } else {
            post(new Runnable() {
                @Override
                public void run() {
                    clearInner();
                }
            });
        }
    }

    protected void clearInner() {
        clearChildViews();
        clearViewCache();
        removeAllViewsInLayout();
    }

    protected boolean isUiThread() {
        return mUiThread == Thread.currentThread();
    }

    @Override
    public void clear() {
        clearChildViews();
        clearViewCache();
    }

    protected void clearViewCache() {
        for (int i = 0, len = mViewCache.size(); i < len; i++) {
            clearOneView(i, mViewCache.get(i));
        }
        mViewCache.clear();
    }

    protected void clearChildViews() {
        for (int i = 0, len = mChildViews.size(); i < len; i++) {
            clearOneView(i, mChildViews.valueAt(i));
        }
        mChildViews.clear();
    }

    protected void clearOneView(int i, View v) {
        if (v instanceof BasePageView) {
            BasePageView epubV = (BasePageView) v;
            epubV.clear();
        } else {
            printLogE(" clear i=" + i + ", v = " + v);
        }
    }

    @Override
    public OnGalleryPageChangeListener getGalleryPageListener() {
        return mGalleryPageListener;
    }

    public void setController(IReaderController controller) {
        mController = (IEpubReaderController) controller;
    }

    protected IReaderController getController() {
        return mController;
    }

    @Override
    public void setAdapter(IPageAdapter adapter) {
        mAdapter = adapter;
    }

    public IPageAdapter getAdapter() {
        return mAdapter;
    }

    private Handler cHandler;

    final OnGalleryPageChangeListener mGalleryPageListener = new OnGalleryPageChangeListener() {
        @Override
        public void onPageChange(GallaryData gallaryData,
                                 GalleryIndex galleryIndex) {

            printLog(" onPageChange " + galleryIndex + "," + gallaryData);
            if (gallaryData == null) {
                return;
            }
            if (gallaryData.isHasImgDesc()) {
                BasePageView current = getCurrentView();
                if (current != null) {
                    getAdapter().refreshView(DPageIndex.Current, galleryIndex,
                            current, getPageContainer());
                } else {
                    printLogE(" repaintCurrent() current==null ");
                }
            } else {
                printLog(" onPageChange no gallery desc ");
            }
        }
    };

    private static class MyHandler extends Handler {
        private final WeakReference<EpubReaderWidget> mFragmentView;

        MyHandler(EpubReaderWidget view) {
            this.mFragmentView = new WeakReference<EpubReaderWidget>(view);
        }

        @Override
        public void handleMessage(Message msg) {
            EpubReaderWidget service = mFragmentView.get();
            if (service != null) {
                super.handleMessage(msg);
                try {
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void showInteractiveBlockIconView() {
        EpubPageView view = (EpubPageView) getCurrentView();
        if (view != null) {
            view.showInteractiveBlockIconView();
        }
    }

}
