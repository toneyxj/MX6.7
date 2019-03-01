/**
 * 
 */
package com.dangdang.reader.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.dangdang.reader.R;
import com.dangdang.reader.utils.SystemLib;
import com.dangdang.reader.utils.Utils;
import com.dangdang.zframework.view.DDTextView;

public class StarRate extends DDTextView {
	
	public static final String TAG = "StarRate";
	public static final int MAX_STARS = 5;

	Bitmap mHollowBitmap;
	Bitmap mSolidBitmap;
	int mStarGap;
	boolean mFocusable = false;
	
	public StarRate(Context context, AttributeSet attrs) {
		super(context, attrs);
		TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.uiframework_StarRate, 0, 0);
		BitmapDrawable hollowBitmap = (BitmapDrawable)ta.getDrawable(R.styleable.uiframework_StarRate_path_hollow);
		BitmapDrawable solidBitmap = (BitmapDrawable)ta.getDrawable(R.styleable.uiframework_StarRate_path_solid);
		try {
			if (hollowBitmap == null) {
				hollowBitmap = (BitmapDrawable) Utils.getDrawableResource(context, R.drawable.jw_star_hollow);
			}
			if (solidBitmap == null) {
				solidBitmap = (BitmapDrawable) Utils.getDrawableResource(context, R.drawable.jw_star_solid);
			}
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
		}
		
		if (hollowBitmap == null || solidBitmap == null) {
			return;
		}
		mStarGap = (int)ta.getDimension(R.styleable.uiframework_StarRate_star_gap, 0);
		mHollowBitmap = hollowBitmap.getBitmap();
		mSolidBitmap = solidBitmap.getBitmap();
		ta.recycle();
	}

	public void setHollowAndSolidBitmap(Bitmap hollow, Bitmap solid) {
		mHollowBitmap = hollow;
		mSolidBitmap = solid;
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		if (mSolidBitmap == null || mHollowBitmap == null) {
			return;
		}
		int left = 0;
		if(getText() == null) {
			mStars = 0;
		} else {
			mStars = SystemLib.stringToInt(getText().toString());
		}
		for(int i = 0; i < MAX_STARS; ++i) {
			if(i < mStars) {
				canvas.drawBitmap(mSolidBitmap, left, 0, null);
			} else {
				canvas.drawBitmap(mHollowBitmap, left, 0, null);
			}
			left += mHollowBitmap.getWidth();
			left += mStarGap;
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		if (mHollowBitmap == null) {
			setMeasuredDimension(0, 0);
			return;
		}
		int measuredWidth = MeasureSpec.getSize(widthMeasureSpec);
		int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		if(MeasureSpec.AT_MOST == widthMode || MeasureSpec.UNSPECIFIED == widthMode) {
			measuredWidth = mHollowBitmap.getWidth() * MAX_STARS + mStarGap * (MAX_STARS - 1);
		}

		int measuredHeight = mHollowBitmap.getHeight();
		setMeasuredDimension(measuredWidth, measuredHeight);
	}
	int mStars;
	public void setStar(int stars) {
		mStars = stars;
		setText("" + mStars);
		invalidate();
	}
	public int getStar() {
		return mStars;
	}
	
	public void setStarFocusable(boolean focusable) {
		mFocusable = focusable;
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if(!mFocusable) {
			return false;
		}
		switch(event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			float clickX = event.getX();
			int left = 0;
			int star = 0;
			for(int i = 0; i < MAX_STARS; ++i) {
				if(left > clickX) {
					break;
				}
				++star;
				left += mHollowBitmap.getWidth();
				left += mStarGap;
			}
			if(star > MAX_STARS) {
				star = MAX_STARS;
			}
			setStar(star);
			break;
		}
		return true;
	}
}
