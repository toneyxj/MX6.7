package com.dangdang.reader.dread.core.epub;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Scroller;

import com.dangdang.reader.dread.GalleryViewActivity;
import com.dangdang.reader.dread.data.GallaryData;
import com.dangdang.reader.dread.holder.GalleryIndex;
import com.dangdang.reader.utils.DangdangConfig;
import com.dangdang.reader.view.FlowIndicator;

import java.util.LinkedList;

public class GalleryView extends ViewGroup {
    public static final String SCROLL_GALLERY_ACTION = DangdangConfig.ParamsType.mPagekageName+".scrollgallery";
    private int mGalleryId;
    private GalleryViewAdapter mAdapter;
    private Scroller mScroller;
    private GallaryData mGallaryData;
    private FlowIndicator mFlowIndicator;

    private int mCurrentPage = 0;
    private int mScrollX = 0;
    private float mLastMotionX;

    private int mTouchSlop = 0;
    private VelocityTracker mVelocityTracker;
    private static final int SNAP_VELOCITY = 1000;

    private final static int TOUCH_STATE_REST = 0;
    private final static int TOUCH_STATE_SCROLLING = 1;
    private int mTouchState = TOUCH_STATE_REST;

    private int mWidth;
    private int mHeight;
    private LinkedList<View> mCacheViews;
    private OnGalleryPageChangeListener mPageChangeListener;
    private OnClickListener mOnClickListener;
    private ScrollBroadcastReceiver mScrollBroadcastReceiver;
    private boolean mScrollEnd;
    private boolean mPageChange;
    private int mLastPage;
    private boolean mIsMove;

    public GalleryView(Context context) {
        super(context);
        init(context);
    }

    public GalleryView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        mScroller = new Scroller(context);
        mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
        mCacheViews = new LinkedList<View>();
        mScrollBroadcastReceiver = new ScrollBroadcastReceiver();
        IntentFilter filter = new IntentFilter(SCROLL_GALLERY_ACTION);
        context.registerReceiver(mScrollBroadcastReceiver, filter);
    }

    public void setGalleryData(GallaryData gallaryData, int index) {
        this.mGalleryId = index;
        this.mGallaryData = gallaryData;
        Rect imageRect = mGallaryData.getImageRect();
        setGallerySize(imageRect.right - imageRect.left, imageRect.bottom
                - imageRect.top);
        if (mFlowIndicator != null) {
            mFlowIndicator.setCount(mGallaryData.getCount());
        }
    }

    public void setGallerySize(int width, int height) {
        mWidth = width;
        mHeight = height;
    }

    public int getGalleryId() {
        return mGalleryId;
    }

    public GallaryData getGalleryData() {
        return this.mGallaryData;
    }

    public void setAdapter(GalleryViewAdapter adapter) {
        if (this.mAdapter != null) {
            this.mAdapter.clear();
        }
        this.mAdapter = adapter;
        requestLayout();
    }

    public GalleryViewAdapter getAdapter() {
        return mAdapter;
    }

    public void setFlowIndicator(FlowIndicator flowIndicator) {
        this.mFlowIndicator = flowIndicator;
    }

    public void setGalleryPageChangeListener(
            OnGalleryPageChangeListener pageChangeListener) {
        this.mPageChangeListener = pageChangeListener;
    }

    private View getCachedView() {
        if (mCacheViews == null || mCacheViews.size() == 0) {
            return null;
        }
        return mCacheViews.removeFirst();
    }

    public void setOnGalleryClickListener(OnClickListener clickListener) {
        this.mOnClickListener = clickListener;
        setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mScroller.isFinished()) {
                    mScrollX = getScrollX();
                    int page = Math.abs(mScrollX / mWidth);
                    if (page == mCurrentPage && mOnClickListener != null) {
                        cancelOption();
                        mOnClickListener.onClick(v);
                    }
                }
            }

        });
    }

    private void cancelOption() {
        ReaderAppImpl.getApp().getReaderController().cancelOption(true);
    }

    public int getCurrentPageIndex() {
        return mCurrentPage;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        final int action = ev.getAction();
        if ((action == MotionEvent.ACTION_MOVE)
                && (mTouchState != TOUCH_STATE_REST)) {
            return true;
        }
        final float x = ev.getX();

        switch (ev.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                mIsMove = false;
                requestLayout();
                mLastMotionX = x;
                mTouchState = TOUCH_STATE_REST;
                break;
            case MotionEvent.ACTION_MOVE:
                final int deltaX = (int) (mLastMotionX - x);
                boolean xMoved = Math.abs(deltaX) > mTouchSlop;
                if (xMoved) {
                    final int availableToScroll = (mAdapter.getCount() - 1)
                            * mWidth - mScrollX;
                    if ((deltaX < 0 && mScrollX <= 0)
                            || (deltaX > 0 && availableToScroll <= 0)) {
                        mTouchState = TOUCH_STATE_REST;
                        return true;
                    }
                    mTouchState = TOUCH_STATE_SCROLLING;
                    cancelOption();
                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                mTouchState = TOUCH_STATE_REST;
                break;
        }
        if (mTouchState == TOUCH_STATE_SCROLLING) {
            return true;
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(event);

        final int action = event.getAction();
        final float x = event.getX();

        switch (action & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                mIsMove = false;
                requestLayout();
                mLastMotionX = x;
                mTouchState = TOUCH_STATE_REST;
                break;
            case MotionEvent.ACTION_MOVE:
                final int deltaX = (int) (mLastMotionX - x);
                boolean isMoved = Math.abs(deltaX) > mTouchSlop;
                if (mTouchState == TOUCH_STATE_REST && isMoved) {
                    if (!mScroller.isFinished()) {
                        mScroller.abortAnimation();
                    }
                    final int availableToScroll = (mAdapter.getCount() - 1)
                            * mWidth - mScrollX;
                    if ((deltaX < 0 && mScrollX <= 0)
                            || (deltaX > 0 && availableToScroll <= 0)) {
                        mTouchState = TOUCH_STATE_REST;
                        mIsMove = true;
                        return true;
                    }
                    mTouchState = TOUCH_STATE_SCROLLING;
                    cancelOption();
                    mIsMove = true;
                }
                if (mTouchState == TOUCH_STATE_SCROLLING) {
                    mLastMotionX = x;
                    if (deltaX < 0) {
                        if (mScrollX > 0) {
                            scrollBy(Math.max(-mScrollX, deltaX), 0);
                        }
                    } else if (deltaX > 0) {
                        final int availableToScroll = (mAdapter.getCount() - 1)
                                * mWidth - mScrollX;
                        if (availableToScroll > 0) {
                            scrollBy(Math.min(availableToScroll, deltaX), 0);
                        }
                    }
                    mScrollX = this.getScrollX();
                    return true;
                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                if (mTouchState == TOUCH_STATE_SCROLLING) {
                    final VelocityTracker velocityTracker = mVelocityTracker;
                    velocityTracker.computeCurrentVelocity(1000);
                    int velocityX = (int) velocityTracker.getXVelocity();

                    if (velocityX > SNAP_VELOCITY && mCurrentPage - 1 >= 0) {
                        snapToPage(mCurrentPage - 1);
                    } else if (velocityX < -SNAP_VELOCITY
                            && mCurrentPage + 1 < mAdapter.getCount()) {
                        snapToPage(mCurrentPage + 1);
                    } else {
                        snapToDestination();
                    }
                    if (mVelocityTracker != null) {
                        mVelocityTracker.recycle();
                        mVelocityTracker = null;
                    }
                    mScrollX = this.getScrollX();
                    mTouchState = TOUCH_STATE_REST;
                    return true;
                } else if (mIsMove && mTouchState == TOUCH_STATE_REST) {
                    return true;
                }
                break;
        }
        return super.onTouchEvent(event);
    }

    private void snapToDestination() {
        final int PageWidth = getWidth();
        final int whichPage = (mScrollX + (PageWidth / 2)) / PageWidth;
        snapToPage(whichPage);
    }

    public void snapToPage(int whichPage) {
        if (whichPage >= 0 && whichPage < mAdapter.getCount()) {
            pageChange(whichPage);
            final int newX = whichPage * getWidth();
            final int delta = newX - mScrollX;
            mScroller.startScroll(mScrollX, 0, delta, 0, Math.abs(delta) / 2);
            invalidate();
        }
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (l % mWidth == 0 && l == mWidth * mCurrentPage) {
            mScrollEnd = true;
        } else {
            mScrollEnd = false;
        }
    }

    private void pageChange(int whichPage) {
        mCurrentPage = whichPage;
        mPageChange = mCurrentPage != mLastPage;
    }

    private void onPageChange() {
        mLastPage = mCurrentPage;
        mPageChange = false;
        if (mPageChangeListener != null) {
            GalleryIndex galleryIndex = new GalleryIndex();
            galleryIndex.setGallery(mGalleryId);
            galleryIndex.setFrameIndex(mCurrentPage);
            mPageChangeListener.onPageChange(mGallaryData, galleryIndex);
        }
        setFlowIndicatorSelection(mCurrentPage);
    }

    public void scrollToPage(int pageIndex) {
        if (pageIndex >= 0 && pageIndex < mAdapter.getCount()
                && pageIndex != mCurrentPage) {
            pageChange(pageIndex);
            scrollTo(mCurrentPage * mWidth, 0);
            requestLayout();
            mScrollX = mCurrentPage * mWidth;
            if (mCurrentPage == mAdapter.getCount() - 1)
                mScrollEnd = true;
        }
    }

    private void setFlowIndicatorSelection(int index) {
        if (mFlowIndicator != null) {
            mFlowIndicator.setSeletion(index);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (mAdapter != null && mAdapter.getCount() > 0) {
            removeOtherChild();
            addGalleryChild();
            int width = mWidth;
            int height = mHeight;
            final int count = getChildCount();
            for (int i = 0; i < count; i++) {
                final View child = getChildAt(i);
                if (child.getVisibility() != View.GONE) {
                    int childHeight = child.getMeasuredHeight();
                    int tag = (Integer) child.getTag();
                    child.layout(width * tag, (height - childHeight) / 2, width
                            * tag + width, (height - childHeight) / 2
                            + childHeight);
                }
            }
        }
    }

    private void addGalleryChild() {
        addCurrentChild();
        addPreChild();
        addNextChild();
    }

    private void addGalleryChild(int pageIndex) {
        ImageView child = getchildByTag(pageIndex);
        if (child == null) {
            ImageView view = (ImageView) mAdapter.getView(pageIndex,
                    getCachedView(), this);
            addViewInLayout(view, 0, new LayoutParams(
                    LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
            view.measure(View.MeasureSpec.EXACTLY | mWidth,
                    View.MeasureSpec.EXACTLY | mHeight);
        }

    }

    private void addCurrentChild() {
        addGalleryChild(mCurrentPage);
    }

    private void addPreChild() {
        if (mCurrentPage - 1 >= 0) {
            addGalleryChild(mCurrentPage - 1);
        }
    }

    private void addNextChild() {
        if (mCurrentPage + 1 < mAdapter.getCount()) {
            addGalleryChild(mCurrentPage + 1);
        }
    }

    private ImageView getchildByTag(int Page) {
        ImageView imageView = null;
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            ImageView child = (ImageView) getChildAt(i);
            if (child != null) {
                int tag = (Integer) child.getTag();
                if (tag == Page) {
                    imageView = child;
                    break;
                }
            }
        }
        return imageView;
    }

    private void removeOtherChild() {
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            GalleryImageView imageView = (GalleryImageView) getChildAt(i);
            if (imageView != null) {
                int position = (Integer) imageView.getTag();
                if (position != mCurrentPage && position != mCurrentPage - 1
                        && position != mCurrentPage + 1) {
                    removeViewInLayout(imageView);
                    imageView.releaseBitmap();
                    mCacheViews.add(imageView);
                }
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = 0, height = 0;
        int widthMode = View.MeasureSpec.getMode(widthMeasureSpec);
        switch (widthMode) {
            case View.MeasureSpec.UNSPECIFIED:
                width = mWidth;
                break;
            default:
                width = MeasureSpec.getSize(widthMeasureSpec);
        }
        int heightMode = View.MeasureSpec.getMode(heightMeasureSpec);
        switch (heightMode) {
            case View.MeasureSpec.UNSPECIFIED:
                height = mHeight;
                break;
            default:
                height = MeasureSpec.getSize(heightMeasureSpec);
        }
        setMeasuredDimension(width, height);
        final int count = getChildCount();
        for (int i = 0; i < count; i++) {
            getChildAt(i).measure(View.MeasureSpec.EXACTLY | mWidth,
                    View.MeasureSpec.EXACTLY | mHeight);
        }
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            mScrollX = mScroller.getCurrX();
            scrollTo(mScrollX, 0);
            postInvalidate();
        } else {
            if (mScrollEnd && mPageChange) {
                onPageChange();
            }
        }
    }

    public void reset() {
        if (getContext() != null && mScrollBroadcastReceiver != null) {
            getContext().unregisterReceiver(mScrollBroadcastReceiver);
        }
        mAdapter.clear();
        mCacheViews.clear();
        while (getChildCount() > 0) {
            View child = getChildAt(0);
            if (child != null) {
                removeViewInLayout(child);
                GalleryImageView gaImageView = (GalleryImageView) child;
                gaImageView.releaseBitmap();
            }

        }
    }

    public class ScrollBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            int galleryId = intent.getIntExtra(
                    GalleryViewActivity.KEY_GALLERY_ID, 0);
            int pageIndex = intent.getIntExtra(
                    GalleryViewActivity.KEY_GALLERY_PAGEINDEX, 0);
            if (mGalleryId == galleryId) {
                scrollToPage(pageIndex);
            }
        }

    }

    public interface OnGalleryPageChangeListener {
        public void onPageChange(GallaryData gallaryData,
                                 GalleryIndex galleryIndex);
    }
}