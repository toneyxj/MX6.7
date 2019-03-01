package com.dangdang.reader.dread.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.dangdang.zframework.log.LogM;

public class ReadWidgetParent extends FrameLayout {

	private OnSizeChangedListener mSizeChangeListener;
	
	public ReadWidgetParent(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public ReadWidgetParent(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ReadWidgetParent(Context context) {
		super(context);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		
		printLog(" onSizeChanged " + w + "," + h + "," + oldw + ", " + oldh);
		if(w == 0 || h == 0 || oldw == 0 || oldh == 0){
			return;
		}
		if(w != oldw || h != oldh){
			if(mSizeChangeListener != null){
				mSizeChangeListener.onSizeChanged(w, h, oldw, oldh);
			}
		}
	}
	
	public void setOnSizeChangeListener(OnSizeChangedListener l){
		mSizeChangeListener = l;
	}
	
	protected void printLog(String log){
		LogM.i(getClass().getSimpleName(), log);
	}
	
	protected void printLogD(String log){
		LogM.d(getClass().getSimpleName(), log);
	}
	
	protected void printLogE(String log){
		LogM.e(getClass().getSimpleName(), log);
	}
	
	protected void printLogV(String log){
		LogM.v(getClass().getSimpleName(), log);
	}
	
	protected void printLogW(String log){
		LogM.w(getClass().getSimpleName(), log);
	}
	
	
	public static interface OnSizeChangedListener {
		
		public void onSizeChanged(int w, int h, int oldw, int oldh);
		
	}

}
