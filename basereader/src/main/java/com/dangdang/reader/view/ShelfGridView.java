package com.dangdang.reader.view;

import java.util.concurrent.atomic.AtomicBoolean;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.GridView;

public class ShelfGridView extends GridView {

	private AtomicBoolean isOnMeasure = new AtomicBoolean(false);
	private boolean isSetScrollListener = false;
	protected boolean isIdle = true;

	public ShelfGridView(Context context) {
		super(context);
		init();
	}

	public ShelfGridView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public ShelfGridView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	private void init() {
		this.setOverScrollMode(GridView.OVER_SCROLL_NEVER);
		this.setHorizontalFadingEdgeEnabled(false);
		this.setVerticalFadingEdgeEnabled(false);
		this.setSelector(new ColorDrawable(Color.TRANSPARENT));
	}
	
	public boolean isIdle(){
		return isIdle;
	}
	
	public void setIdle(boolean bo){
		isIdle = bo;
	}
	
	@Override
	public void setOnScrollListener(OnScrollListener l) {
		super.setOnScrollListener(l);
		if(l == null)
			isSetScrollListener = false;
		else
			isSetScrollListener = true;
    }

	public boolean isSetScrollListener(){
		return isSetScrollListener;
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		try {
			return super.onTouchEvent(ev);
		} catch (Exception e) {
			e.printStackTrace();
			return true;
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
			return true;
		}
	}

	@Override
	protected void layoutChildren() {
		try {
			super.layoutChildren();
		} catch (Exception e) {
			e.printStackTrace();
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		isOnMeasure.set(true);
		try {
			super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		} catch (Exception e) {
			e.printStackTrace();
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void dispatchDraw(Canvas canvas) {
		try {
			super.dispatchDraw(canvas);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		isOnMeasure.set(false);
		super.onLayout(changed, l, t, r, b);
	}
	
	public boolean isOnMeasure(){
		return isOnMeasure.get();
	}
}
