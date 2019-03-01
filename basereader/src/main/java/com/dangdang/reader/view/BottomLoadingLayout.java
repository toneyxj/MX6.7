package com.dangdang.reader.view;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dangdang.reader.R;
import com.dangdang.zframework.utils.UiUtil;
import com.dangdang.zframework.view.pulltorefresh.LoadingLayout;

public class BottomLoadingLayout extends LoadingLayout {

	private ProgressBar mBar;
	
	public BottomLoadingLayout(Context context, int mode, String releaseLabel,
			String pullLabel, String refreshingLabel) {

		super(context, mode, releaseLabel, pullLabel, refreshingLabel);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void init(Context context, int mode, String releaseLabel,
			String pullLabel, String refreshingLabel) {
		
		// TODO Auto-generated method stub
		mLoadingView = new RelativeLayout(context);

		mPromptView = new TextView(context);
		mPromptView.setTextColor(0xff969696);
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		params.addRule(RelativeLayout.CENTER_IN_PARENT);
		int tmp = UiUtil.dip2px(context, 10);
		params.setMargins(tmp, 0, tmp, 0);
		mPromptView.setId(R.id.textview);
		((ViewGroup)mLoadingView).addView(mPromptView, params);
		
		tmp = UiUtil.dip2px(context, 20);
		mBar = new ProgressBar(context);
		params = new RelativeLayout.LayoutParams(tmp, tmp);
		params.addRule(RelativeLayout.CENTER_VERTICAL);
		params.addRule(RelativeLayout.LEFT_OF, R.id.textview);
		((ViewGroup)mLoadingView).addView(mBar, params);
		
		mRefreshingLabel = refreshingLabel;
		mReleaseLabel = releaseLabel;
		mPullLabel = pullLabel;
		
		mBar.setVisibility(View.GONE);			
		
		ViewGroup.LayoutParams param = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, UiUtil.dip2px(context, 60));
		this.addView(mLoadingView, param);		
	}

	@Override
	public void setRefreshValid(int arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void refreshing() {
		super.refreshing();
		mBar.setVisibility(View.VISIBLE);
	}
	
	@Override
	public void reset(){
		super.reset();
		mBar.setVisibility(View.GONE);
	}
}
