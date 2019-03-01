package com.dangdang.reader.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dangdang.reader.DDApplication;
import com.dangdang.reader.R;
import com.dangdang.reader.utils.Utils;
import com.dangdang.zframework.view.DDTextView;
import com.dangdang.zframework.view.pulltorefresh.LoadingLayout;
import com.dangdang.zframework.view.pulltorefresh.PullToRefreshBase;
import com.dangdang.zframework.view.pulltorefresh.PullToRefreshListView;

public class MyPullToRefreshListView extends PullToRefreshListView {

	private int mode = MODE_PULL_DOWN_TO_REFRESH;
	
	private ProgressBar moreProgressBar;
	private TextView mLoadMoreView;
	private View moreView, mFooterView;
	private boolean isLoading = false;
	private TextView mFinish = null;
	private IScrollListener mListener;
	
	private boolean isAddFloatView = false;
	private int mFloatBottom;
	private View mFloatView;
	
	public MyPullToRefreshListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
        setDisableScrollingWhileRefreshing(false);
	}

	public MyPullToRefreshListView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	protected LoadingLayout getDownLoadingLayout(Context context) {
		String pullLabel = "";//context.getString(R.string.pull_to_refresh_pull_label);
		String releaseLabel = "";//context.getString(R.string.pull_to_refresh_release_label);
		String refreshingLabel = "";//context.getString(R.string.pull_to_refresh_refreshing_label);
		return new RoundLoadingLayout(context, MODE_PULL_DOWN_TO_REFRESH, releaseLabel, pullLabel, refreshingLabel);
	}
	
	protected LoadingLayout getUpLoadingLayout(Context context) {
		String pullLabel = context.getString(R.string.pull_up_to_refresh_pull_label);
		String releaseLabel = context.getString(R.string.pull_up_to_refresh_release_label);
		String refreshingLabel = context.getString(R.string.pull_to_refresh_refreshing_label);
		return new BottomLoadingLayout(context, MODE_PULL_UP_TO_REFRESH, releaseLabel, pullLabel, refreshingLabel);
	}
	
	private void onLoad(){
		if(mode == PullToRefreshBase.MODE_BOTH || mode == PullToRefreshBase.MODE_PULL_UP_TO_REFRESH){
			if(onRefreshListener != null && !isLoading){
				isLoading = true;
				//打开这里，刷新时不能滑动
				//setRefreshingInternal(false);
				
				if(mFooterView != null){
					moreView.setVisibility(VISIBLE);
					mLoadMoreView.setText(R.string.listview_footer_loading);
					moreProgressBar.setVisibility(VISIBLE);
				}
				
				onRefreshListener.onPullUpRefresh();
			}
		}
	}
	
	
	public void showFinish(){
		if (mFinish == null) {
			Context context = DDApplication.getApplication().getApplicationContext();
			mFinish = new DDTextView(context);
			mFinish.setBackgroundColor(Color.WHITE);
			mFinish.setText(R.string.listview_footer_loaded);
			mFinish.setTextColor(Utils.getColorResource(context, R.color.c));
			mFinish.setGravity(Gravity.CENTER);
			mFinish.setHeight(Utils.dip2px(context, 40));
		}
		if (mListView.getFooterViewsCount() == 1) {
			mListView.addFooterView(mFinish, null, false);
		}
	}
	public void hideFinish(){
		if (mListView != null && mFinish != null) {
			mListView.removeFooterView(mFinish);
		}
	}
	
	private void addFooter(boolean bo){
		if(bo){
			if(mFooterView != null)
				return;
			
			LayoutInflater inflater = LayoutInflater.from(getContext().getApplicationContext());
			mFooterView = inflater.inflate(R.layout.more_list_footer, null);
			mFooterView.setBackgroundColor(Color.WHITE);
			
			moreView = mFooterView.findViewById(R.id.load_more_layout);
			moreProgressBar = (ProgressBar) moreView
					.findViewById(R.id.load_more_progress);
			mLoadMoreView = (TextView) moreView.findViewById(R.id.load_more);
			moreView.setVisibility(View.GONE);
			
			mListView.addFooterView(mFooterView, null, false);
		}else{
			if(mFooterView == null)
				return;
			
			mListView.removeFooterView(mFooterView);
			mFooterView = null;
		}
	}
	
	@Override
	public void onRefreshComplete() {
		super.onRefreshComplete();
		isLoading = false;
		if(moreView != null){
			moreView.setVisibility(View.GONE);
		}
	}
	
	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		if (scrollState == SCROLL_STATE_IDLE) {
			final int lastVsbPosition = mListView.getLastVisiblePosition();

			final int count = mListView.getCount();
			if (lastVsbPosition == (count - 1) && count > 0) {
				onLoad();
			}

			if(mListener!=null){
				mListener.onScrollStateChanged(view, scrollState);
			}
		}
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		if(mListener != null){
			mListener.onScroll(view,firstVisibleItem,visibleItemCount,totalItemCount);
		}
		super.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
		showFloatView(view, firstVisibleItem);
	}

	@Override
	public void changeMode(int mode){
		this.mode = mode;
		if(mode == PullToRefreshBase.MODE_PULL_UP_TO_REFRESH)
			super.changeMode(PullToRefreshBase.MODE_NONE);

		if(mode == PullToRefreshBase.MODE_BOTH || mode == PullToRefreshBase.MODE_PULL_UP_TO_REFRESH)
			addFooter(true);
		else
			addFooter(false);
	}

	public interface IScrollListener{
		void onScrollStateChanged(AbsListView view, int scrollState);
		void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount);
	}

	public void setScrollListener(IScrollListener listener){
		mListener = listener;
	}
	
	public void addFloatView(boolean bo, int bottom, View view){
		isAddFloatView = bo;
		if(!isAddFloatView){
			mFloatView = null;
		}else{
			mFloatView = view;
			mFloatBottom = bottom;
		}
	}
	
	private void showFloatView(AbsListView view, int firstVisibleItem){
		if(!isAddFloatView || mFloatView == null)
			return;
		if(firstVisibleItem == 0){
			View v = view.getChildAt(0);
			if(v != null && v.getBottom() <= mFloatBottom){
				mFloatView.setVisibility(View.VISIBLE);
			}else
				mFloatView.setVisibility(View.GONE);
		}else if(mFloatView.getVisibility() != View.VISIBLE)
			mFloatView.setVisibility(View.VISIBLE);
	}

	@Override
	protected ListView createRefreshableView(Context context, AttributeSet attrs) {
		this.mListView = new ShelfListView(context);
		this.mListView.setCacheColorHint(0);
		this.mListView.setDivider((Drawable) null);
		this.mListView.setOnScrollListener(this);
		this.mListView.setVerticalFadingEdgeEnabled(false);
		this.mListView.setOverScrollMode(2);
		return this.mListView;
	}
}
