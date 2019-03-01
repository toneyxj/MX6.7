package com.dangdang.reader.dread.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.dangdang.reader.R;
import com.dangdang.reader.dread.holder.GlobalResource;
import com.dangdang.zframework.log.LogM;
import com.dangdang.zframework.utils.BitmapUtil;
import com.dangdang.zframework.utils.DRUiUtility;

public class BatteryView extends View {

	private Bitmap mBatteryBmp; 
	private int mWidth;
	private int mHeight;
	private float mDensity = 1;
	
	private final Paint mFootPaint = new Paint();
	
	public BatteryView(Context context) {
		super(context);
		init();
	}

	public BatteryView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public BatteryView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	private void init() {
		mBatteryBmp = GlobalResource.getBatteryBmp(getContext());//BitmapFactory.decodeResource(getResources(), R.drawable.battery);
		mWidth = mBatteryBmp.getWidth();
		mHeight = mBatteryBmp.getHeight();
		mDensity = DRUiUtility.getDensity();
		
		mFootPaint.setAntiAlias(true);
		mFootPaint.setFlags(mFootPaint.getFlags() & ~Paint.DEV_KERN_TEXT_FLAG);
		mFootPaint.setDither(true);
		mFootPaint.setSubpixelText(true);
		mFootPaint.setColor(getContext().getResources().getColor(R.color.colorBlack));
		//mFootPaint.setTextSize(FOOTER_TEXT_SIZE * mDensity);
		
	}
	
	public void setColor(int color){
		//mFootPaint.setColor(color);
	}
	
	private float mCurBattery = 0.9f;
	
	@Override
	protected void onDraw(Canvas canvas) {
		if(!isAvailable(mBatteryBmp)){
			pringLog(" mBatteryBmp not Available");
			return;
		}
		int bwidth = mWidth;
		int bheight = mHeight;
		int bleft = 0;
		float batterytop = 0;
		canvas.drawBitmap(mBatteryBmp, bleft, batterytop, mFootPaint);
				
		//绘制 剩余电量
		float last = (bwidth - 6 * mDensity) * (1 - mCurBattery);
		float left = bleft + 3.8f * mDensity + last;
		float top = batterytop + 2.7f * mDensity;
		float right = bleft + bwidth - 2.5f * mDensity;
		float bottom = batterytop + bheight - 2.5f * mDensity;
		canvas.drawRect(left, top, right, bottom, mFootPaint);
	}

	public void setBatteryValue(float battery){
		mCurBattery = battery;
		invalidate();
	}
	
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int measuredWidth = mWidth;
		int measuredHeight = mHeight;
		setMeasuredDimension(measuredWidth, measuredHeight);
	}
	
	public void clear(){// global resources, quit read recycle
		//recyle();
		//mBatteryBmp = null;
	}

	protected void recyle() {
		BitmapUtil.recycle(mBatteryBmp);
	}
	
	private boolean isAvailable(Bitmap bitmap){
		return BitmapUtil.isAvailable(bitmap);
	}
	
	protected void pringLog(String log){
		LogM.i(getClass().getSimpleName(), log);
	}
	
	
}
