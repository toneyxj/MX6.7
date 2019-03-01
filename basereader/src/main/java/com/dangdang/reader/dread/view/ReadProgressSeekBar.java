package com.dangdang.reader.dread.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.SeekBar;

import com.dangdang.zframework.log.LogM;

public class ReadProgressSeekBar extends SeekBar {

	public ReadProgressSeekBar(Context context) {
		super(context);
	}

	public ReadProgressSeekBar(Context context, AttributeSet attrs) {
		super(context, attrs, android.R.style.Widget_ProgressBar_Horizontal);
	}

	public ReadProgressSeekBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, android.R.style.Widget_ProgressBar_Horizontal);
	}

	private Drawable mThumb;
	private boolean mClick = true;

	@Override
	public void setThumb(Drawable thumb) {
		super.setThumb(thumb);
		mThumb = thumb;
	}

	public Drawable getSeekBarThumb() {
		return mThumb;
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if(!mClick){
			return true;
		}
		return super.onTouchEvent(event);
	}
	
	public void setClick(){
		mClick = true;
	}
	
	public void setNoClick(){
		mClick = false;
	}
	
	protected void printLog(String log){
		LogM.i(getClass().getSimpleName(), log);
	}
	
	protected void printLogE(String log){
		LogM.e(getClass().getSimpleName(), log);
	}
	
}