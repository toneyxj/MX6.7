package com.dangdang.reader.dread.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;

import com.dangdang.reader.dread.config.ReadConfig;
import com.dangdang.reader.dread.core.epub.EpubPageView.IUpdateMagnifListener;
import com.dangdang.reader.dread.holder.GlobalResource;
import com.dangdang.zframework.utils.BitmapUtil;
import com.dangdang.zframework.utils.DRUiUtility;


public class MagnifView extends BaseView implements IUpdateMagnifListener {
	
	private static final float FACTOR = 1f;
	
	private int mRadius = 80;
	/**
	 * 边框厚度
	 */
	private int mFrameWidth = 5;
	private int mFrameHeight = 5;
	private int mHandGlassGap = 40;
	
	private int mMinX = 0;
	private int mMinY = 0;
	private int mMaxX = 0;
	private int mMaxY = 0;
	
	private float mClassX = 0;
	private float mClassY = 0;
	
	private float mDensity = 1f;
	private int mWidth = 0;
	
	private ShapeDrawable mDrawable;
	private Bitmap mGlassBmp;
	private Matrix mMatrix = new Matrix();
	private Paint mPaint = new Paint();
	
	public MagnifView(Context context) {
		super(context);
		
		mDensity = DRUiUtility.getDensity();
		mWidth = DRUiUtility.getScreenWith();
		mGlassBmp = GlobalResource.getMagnifClassBmp(getContext());//BitmapFactory.decodeResource(getResources(), R.drawable.magnifying_glass);
		mFrameWidth = (int)(6*mDensity);
		mFrameHeight = mFrameWidth;
		mRadius = (mGlassBmp.getWidth() / 2) - mFrameWidth;
		mHandGlassGap = (int) (40*mDensity);
		
		int paddingLeft = (int) ReadConfig.getConfig().getPaddingLeft();
		mMinX = paddingLeft;
		mMaxX = mWidth - paddingLeft;
		mMinY = mFrameWidth;
		mMaxY = mWidth - mMinY;
		
		//Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.guide_2);
		//initShader(bmp);
	}
	
	//private Bitmap mSbmp;
	private void initShader(Bitmap source) {
		//recycle(mSbmp);
		
		//int sbWidth = (int)(source.getWidth()*FACTOR);
		//int sbHeight = (int)(source.getHeight()*FACTOR);
		//printLog(" initShader " + sbWidth + ", " + sbHeight);
		//mSbmp = Bitmap.createScaledBitmap(source, sbWidth, sbHeight, true);
		
		BitmapShader shader = new BitmapShader(source, TileMode.CLAMP, TileMode.CLAMP);
		mDrawable = new ShapeDrawable(new OvalShape());
		mDrawable.getPaint().setShader(shader);
		//mDrawable.setBounds(0, 0, mRadius*2, mRadius*2);
	}	
	
	@Override
	public void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		/**
		 * 隐藏放大镜功能
		 */
//		if(mDrawable != null){
//			canvas.drawBitmap(mGlassBmp, mClassX, mClassY, mPaint);
//			mDrawable.draw(canvas);
//		}
	}
	
	public void updateMagnif(Bitmap bitmap, final int x, final int y){
		
		initShader(bitmap);
		
		//画shader的起始位置
		float dx = mRadius - x*FACTOR;
		float dy = mRadius - y*FACTOR;
		mMatrix.setTranslate(dx, dy);
		
		//printLog(" updateMagnif dx=" + dx + ", dy=" + dy + ", radisu=" + mRadius + ", x=" + x + ", y=" + y + ", mMinY=" + mMinY);
		
		mDrawable.getPaint().getShader().setLocalMatrix(mMatrix);
		//bounds，就是那个圆的外切矩形
		int dia = 2*mRadius;
		
		int left = x - mRadius;
		int top = y - dia - mHandGlassGap;
		/*if(left < mMinX){
			left = mMinX;
		} else if((left + dia) > mMaxX){
			left = mMaxX - dia;
		}*/
		if(top < mMinY){
			top = mMinY + dia;
			top = (top < (y + mRadius)) ? (top + mRadius) : top;
		}
		
		int right = left + dia;
		int bottom = top + dia;//y - mHandGlassGap;
		mDrawable.setBounds(left, top, right, bottom);
		
		mClassX = (left - mFrameWidth + mDensity);
		mClassY = (top - mFrameHeight + mDensity);
		
		mClassX-=getEr();
		
		/*printLogE(" updateMagnif left=" + left + ", top=" + top 
				+ ", right=" + right + ", bottom=" + bottom);*/
		
		//invalidate();
	}
	
	private float getEr(){
		float er = 0.5f;
		try {
			er = (mDensity / 2f);
			er = er <= 0 ? 0.5f : er;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return er;
	}

	public void reset(){
		//recycle(mSbmp);
		//mSbmp = null;
	}
	
	public void clear(){
		recycle(mGlassBmp);
		mGlassBmp = null;
		
		reset();
	}
	
	private void recycle(Bitmap bitmap){
		BitmapUtil.recycle(bitmap);
	}
}
