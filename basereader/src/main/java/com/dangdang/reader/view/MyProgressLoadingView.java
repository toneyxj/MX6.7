package com.dangdang.reader.view;

import android.content.Context;
import android.graphics.Color;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dangdang.reader.R;
import com.dangdang.zframework.utils.UiUtil;
import com.dangdang.zframework.view.ProgressLoadingView;

public class MyProgressLoadingView extends ProgressLoadingView {

	private RoundProgressBar mBar;
	private RotateAnimation animation;
	
	public MyProgressLoadingView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void init(Context context) {
		context=context.getApplicationContext();
		mLoadingView = new RelativeLayout(context);
		
		LinearLayout l = new LinearLayout(context);
		l.setOrientation(LinearLayout.VERTICAL);
		l.setGravity(Gravity.CENTER);
		l.setBackgroundResource(R.drawable.corner_loading);
		int p = UiUtil.dip2px(context, 20);
		l.setPadding(p, p, p, p);
		
		mBar = new RoundProgressBar(context);		
		mBar.setCricleProgressColor(0xffAAAEB6);
		mBar.setCricleColor(Color.TRANSPARENT);
		mBar.setShowText(false);
		mBar.setStartDegree(60);
		mBar.setRoundWidth(UiUtil.dip2px(context, 1.5F));
		mBar.setProgress(90, true);
		int w = UiUtil.dip2px(context, 30);
		LinearLayout.LayoutParams para = new LinearLayout.LayoutParams(w, w);		
		l.addView(mBar, para);
		
		mMessageTV = new TextView(context);
		mMessageTV.setVisibility(View.GONE);
		l.addView(mMessageTV);
		
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT, 
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		params.addRule(RelativeLayout.CENTER_IN_PARENT);
		
		((ViewGroup)mLoadingView).addView(l, params);
		
		ViewGroup.LayoutParams param = mLoadingView.getLayoutParams();
		if(param != null){
			DisplayMetrics dm = context.getResources().getDisplayMetrics();
			param.width = dm.widthPixels;
			param.height = dm.heightPixels;
			mLoadingView.setLayoutParams(param);
		}
		mLoadingView.setBackgroundColor(Color.TRANSPARENT);
		mLoadingView.setClickable(true);
		
		mBar.setLayerType(View.LAYER_TYPE_HARDWARE, null);
		mBar.startAnimation(getRotateAnimation());
	}

	@Override
	public void reset() {
		mBar.clearAnimation();
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
}
