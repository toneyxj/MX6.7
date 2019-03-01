package com.dangdang.reader.dread.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.SeekBar;

public class ReadSeekBar extends SeekBar {

	public ReadSeekBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public ReadSeekBar(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ReadSeekBar(Context context) {
		super(context);
	}

	
	@Override
	protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}
	
	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		if(olCallback != null){
			olCallback.callback(0);
		}
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			printLog("ACTION_DOWN x:" + event.getX());
			break;
		case MotionEvent.ACTION_MOVE:
			printLog("ACTION_MOVE x:" + event.getX());
			break;
		case MotionEvent.ACTION_UP:
			printLog("ACTION_UP x:" + event.getX());
			break;
		}
		return super.onTouchEvent(event);
	}
	
	private void printLog(String log){
		Log.i(this.getClass().getSimpleName(), log);
	}
	
	private OnLayoutCallBack olCallback;
	
	public OnLayoutCallBack getOlCallback() {
		return olCallback;
	}

	public void setOlCallback(OnLayoutCallBack olCallback) {
		this.olCallback = olCallback;
	}

	public interface OnLayoutCallBack{
		public void callback(int width);
	}
}
