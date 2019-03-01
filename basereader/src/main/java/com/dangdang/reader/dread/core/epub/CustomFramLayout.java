package com.dangdang.reader.dread.core.epub;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.FrameLayout;

public class CustomFramLayout extends FrameLayout {
	private Rect mRect;
	private Paint mRectPaint;

	public CustomFramLayout(Context context) {
		super(context);
	}

	public CustomFramLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public void init(Rect rect, Paint paint) {
		this.mRect = rect;
		this.mRectPaint = paint;
	}

	@Override
	protected void dispatchDraw(Canvas canvas) {
		if (mRect != null) {
			canvas.drawRect(mRect, mRectPaint);
		}
		super.dispatchDraw(canvas);
	}
}
