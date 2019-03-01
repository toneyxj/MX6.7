package com.dangdang.reader.view;

import android.content.Context;
import android.graphics.Canvas;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dangdang.reader.R;
import com.dangdang.reader.utils.DangdangConfig;
import com.dangdang.zframework.log.LogM;
import com.dangdang.zframework.view.jazzylistview.JazzyListView;

public class MoreJazzyListView extends JazzyListView implements OnScrollListener {
    private ProgressBar moreProgressBar;
    private TextView mLoadMoreView;
    private View moreView, bottomView;
    private OnLoadListener mLoadListener;
    private String mLoadCompleteTipStr;
    private OnScrollListener mCustomOnScrollListener;

    public MoreJazzyListView(Context context) {
        super(context);
        init(context);
    }

    public MoreJazzyListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context.getApplicationContext());
        setOnScrollListener(this);

        view = inflater.inflate(R.layout.more_list_footer, null);
        bottomView = view.findViewById(R.id.bottom);
        moreView = view.findViewById(R.id.load_more_layout);
        moreProgressBar = (ProgressBar) moreView
                .findViewById(R.id.load_more_progress);
        mLoadMoreView = (TextView) moreView.findViewById(R.id.load_more);
        moreView.setVisibility(View.GONE);
        addFooterView(view, null, false);
    }

    /**
     * 设置加载完成提示语
     *
     * @param tip
     */
    public void setLoadCompleteText(String tip) {
        mLoadCompleteTipStr = tip;
    }

    /**
     * 设置加载完成提示语
     *
     * @param resId
     */
    public void setLoadCompleteText(int resId) {
        try {
            setLoadCompleteText(getResources().getString(resId));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void setBottomVisible(boolean visible) {
        if (visible)
            bottomView.setVisibility(View.VISIBLE);
        else
            bottomView.setVisibility(View.GONE);
    }

    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (mCustomOnScrollListener != null)
            mCustomOnScrollListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
    }

    public void removeMyFooterView() {
        if (getFooterViewsCount() > 0 && view != null) {
            view.setVisibility(View.GONE);
        }
    }

    private boolean isNearby = false;
    private View view;

    public void setNearby() {
        isNearby = true;
    }

    public void onScrollStateChanged(AbsListView view, int scrollState) {
        try {

            if (scrollState == SCROLL_STATE_IDLE) {
                final int lastVsbPosition = getLastVisiblePosition();

                final int count = getCount();
                final int pageSize = DangdangConfig.PageSize;

                if (count >= pageSize && lastVsbPosition == (count - 1)
                        && count > 0) {
                    onLoad();
                } else if (isNearby && lastVsbPosition == (count - 1) && count > 0) {
                    onLoad();
                } else {
                    if (mLoadListener != null && mLoadListener.isFinished()) {
                        onLoadComplete();
                    }
                }
            }
            if (mCustomOnScrollListener != null)
                mCustomOnScrollListener.onScrollStateChanged(view, scrollState);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setOnLoadListener(OnLoadListener loadListener) {
        this.mLoadListener = loadListener;
    }

    public void onLoad() {
        if (mLoadListener != null) {

            if (mLoadListener.isFinished()) {
                onLoadComplete();
            } else {

                mLoadListener.onLoad();
                moreView.setVisibility(VISIBLE);
                mLoadMoreView.setText(R.string.listview_footer_loading);
                moreProgressBar.setVisibility(VISIBLE);

            }
        }
        printLog(" onLoad() ");
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        try {
            super.dispatchDraw(canvas);
        } catch (Exception e) {
            LogM.e(e.toString());
        } catch (StackOverflowError e) {
            LogM.e(e.toString());
        }
    }

    @Override
    protected void layoutChildren() {
        try {
            super.layoutChildren();
        } catch (Exception e) {
            try {
                ListAdapter adapter = this.getAdapter();
                if (adapter != null && adapter instanceof BaseAdapter) {
                    ((BaseAdapter) adapter).notifyDataSetChanged();
                }
            } catch (Exception ex) {
                LogM.e(e.toString());
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        try {
            return super.onTouchEvent(ev);
        } catch (Exception e) {
            LogM.e(e.toString());
            return true;
        } catch (OutOfMemoryError e) {
            LogM.e(e.toString());
            return true;
        }
    }

    public void onLoadComplete() {
        moreView.setVisibility(View.VISIBLE);
        moreProgressBar.setVisibility(View.GONE);
        if (!TextUtils.isEmpty(mLoadCompleteTipStr)) {
            mLoadMoreView.setText(mLoadCompleteTipStr);
        } else {
            mLoadMoreView.setText(R.string.listview_footer_loaded);
        }

    }

    public void onLoadFailed() {

        moreProgressBar.setVisibility(View.GONE);
        mLoadMoreView.setText(R.string.listview_footer_load_fail);

    }

    public void setLoading() {

        moreView.setVisibility(VISIBLE);
        mLoadMoreView.setText(R.string.listview_footer_loading);

    }

    public void removeLoadMore() {

        hideLoadMore();

    }

    public void hideLoadMore() {
        moreView.setVisibility(View.INVISIBLE);
    }

    /**
     * footer隐藏+状态提示
     * @param isAll 是否隐藏状态提示
     */
    public void hideLoadMore(boolean isAll) {
        hideLoadMore();
        if (isAll)
            mLoadMoreView.setVisibility(View.GONE);
    }

    private void printLog(String msg) {
        LogM.d(getClass().getSimpleName(), msg);
    }

    public void setLoadMoreTVColor(int color) {
        mLoadMoreView.setTextColor(color);
    }

    public void setBottomViewBackgroundColor(int color) {
        view.setBackgroundColor(color);
    }

    public interface OnLoadListener {

        public void onLoad();

        public boolean isFinished();
    }

    public void setCustomOnScrollListener(OnScrollListener onScrollListener) {
        mCustomOnScrollListener = onScrollListener;
    }
}
