package com.dangdang.reader.view;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dangdang.reader.R;
import com.dangdang.zframework.utils.UiUtil;
import com.dangdang.zframework.view.pulltorefresh.LoadingLayout;

public class RoundLoadingLayout extends LoadingLayout {

	private RoundProgressBar mBar;
	private RotateAnimation animation;
	
	public RoundLoadingLayout(Context context, int mode, String releaseLabel,
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
		mPromptView.setId(R.id.textview);
		
		int w = UiUtil.dip2px(context, 30);
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(w, w);
		params.addRule(RelativeLayout.CENTER_IN_PARENT);
		
		mBar = new RoundProgressBar(context);		
		mBar.setCricleProgressColor(0xffAAAEB6);
		mBar.setRoundWidth(UiUtil.dip2px(context, 2));
		mBar.setCricleColor(Color.TRANSPARENT);
		mBar.setShowText(false);
		mBar.setStartDegree(60);
		mBar.setLayerType(View.LAYER_TYPE_HARDWARE, null);
		((ViewGroup)mLoadingView).addView(mBar, params);
		
		mRefreshingLabel = refreshingLabel;
		mReleaseLabel = releaseLabel;
		mPullLabel = pullLabel;
		
		ViewGroup.LayoutParams param = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, UiUtil.dip2px(context, 60));
		this.addView(mLoadingView, param);
	}
	
	private RotateAnimation getRotateAnimation(){
		if(animation == null){
			animation = new RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF, 
					0.5f, Animation.RELATIVE_TO_SELF, 0.5f); 
			animation.setInterpolator(new LinearInterpolator());//不停顿  
			animation.setDuration(500);
			animation.setFillAfter(true);
			animation.setRepeatCount(Animation.INFINITE);
			animation.setRepeatMode(Animation.RESTART);
		}
		return animation;
	}
	
	@Override
	public void setRefreshValid(int mode) {
	}
	
	@Override
	public void releaseToRefresh(){
		super.releaseToRefresh();
		mBar.setMax(100);
		mBar.setProgress(91, true);
	}
	
	@Override
	public void pullToRefresh(){
		super.pullToRefresh();
	}
	
	@Override
	public void scrollPullHeader(int current, int max){
		if(current <= max){
			max = (int)(max * 1.1);
			if(mBar.getMax() != max){
				mBar.setMax(max);				
			}
			mBar.setProgress(current, true);
		}		
	}

	@Override
	public void refreshing() {
		super.refreshing();
		mBar.startAnimation(getRotateAnimation());
	}
	
	@Override
	public void reset(){
		super.reset();
		mBar.clearAnimation();
		mBar.setProgress(0, true);
	}
}
