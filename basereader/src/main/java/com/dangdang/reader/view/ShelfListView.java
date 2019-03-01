package com.dangdang.reader.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.GridView;
import android.widget.ListView;

import java.util.concurrent.atomic.AtomicBoolean;

public class ShelfListView extends ListView {

	private AtomicBoolean isOnMeasure = new AtomicBoolean(false);

	public ShelfListView(Context context) {
		super(context);
		init();
	}

	public ShelfListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public ShelfListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	private void init() {
//		this.setOverScrollMode(GridView.OVER_SCROLL_NEVER);
//		this.setHorizontalFadingEdgeEnabled(false);
//		this.setVerticalFadingEdgeEnabled(false);
		this.setSelector(new ColorDrawable(Color.TRANSPARENT));
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
