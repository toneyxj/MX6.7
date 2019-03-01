package com.dangdang.reader.dread.view;

import java.lang.ref.WeakReference;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;

import com.dangdang.reader.R;
import com.dangdang.zframework.utils.DRUiUtility;

public class LineSelectionView extends View {

	public final static int SELECT_DIR = 1;
	public final static int SELECT_MARK = 2;
	public final static int SELECT_NOTE = 3;

	private int mScreenWidth = 0;
	private int mWidth = 0;
	private int mHeight = 12;
	private float mDensity = 1f;
	private float mArrowWidth = 20;
	private float mLine = 3;
	private int mPreSelection = -1;
	
	private Paint mPaint = null;
	private Path mPath = null;
	
	private onSelectListener mSelectListener;
	private Handler mHandler;
	
	public LineSelectionView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public LineSelectionView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public LineSelectionView(Context context) {
		super(context);
		init();
	}
	
	private void init() {
		mHandler = new MyHandler(this);
		int color = getResources().getColor(R.color.list_dmn_devider);
		
		mDensity = DRUiUtility.getDensity();
		mScreenWidth = DRUiUtility.getScreenWith();
		mScreenWidth = (int) (mScreenWidth - 0.2*mScreenWidth);
		mWidth = (int) (mScreenWidth - 20*mDensity*2);//TODO parent store_ebook_pay_activity padingleft、padingright
		mHeight = (int) (mHeight*mDensity);
		mLine = mDensity <= 0 ? 1 : mDensity;
		mArrowWidth = mDensity <= 0 ? mArrowWidth : mDensity*mArrowWidth;
		
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setColor(color);
		mPaint.setStrokeWidth(mLine);
		
		mPath = new Path();
		
	}
	
	/**
	 * SELECT_DIR
	 * SELECT_MARK
	 * SELECT_NOTE
	 * @param selectPos
	 */
	public void setSelection(int selectPos){
		
		float relativeEndX = selectionRelativeX(selectPos);
		if(relativeEndX == -1 || selectPos == mPreSelection){
			return;
		}
		
		final float sy = mHeight - mLine;
		final float sx = 0;
		final float halfArrowWidth = mArrowWidth / 2;
		
		mPath.reset();
		mPath.moveTo(sx, sy);
		mPath.lineTo(relativeEndX - halfArrowWidth, sy);
		mPath.lineTo(relativeEndX, 0);
		mPath.lineTo(relativeEndX + halfArrowWidth, sy);
		mPath.lineTo(mWidth, sy);
		invalidate();
		
		mPreSelection = selectPos;
	}
	
	/**
	 * SELECT_DIR
	 * SELECT_MARK
	 * SELECT_NOTE
	 * @param selectPos
	 */
	public void setStepSelection(int selectPos){
		
		float relativeEndX = selectionRelativeX(selectPos);
		if(relativeEndX == -1 || selectPos == mPreSelection){
			return;
		}
		final float startX = selectionRelativeX(mPreSelection);
		final float endX = relativeEndX;
		fromStartXToEndX(startX, endX, selectPos);
		
		mPreSelection = selectPos;
	}

	private void fromStartXToEndX(final float startX, final float endX, 
			final int selectPosition) {
		
		new Thread() {
			@Override
			public void run() {
				
				final float sy = mHeight - mLine;
				final float sx = 0;
				final float halfArrowWidth = mArrowWidth / 2;
				
				final int time = 10;
				float relativeTemX = startX;
				for(int i = 1; i <= time; i++){
					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					mPath.reset();
					if(endX > startX){
						relativeTemX = startX + i*((endX - startX) / time);
					} else {
						relativeTemX = endX + (time - i)*((startX - endX) / time);
					}
					
					mPath.moveTo(sx, sy);
					mPath.lineTo(relativeTemX - halfArrowWidth, sy);
					mPath.lineTo(relativeTemX, 0);
					mPath.lineTo(relativeTemX + halfArrowWidth, sy);
					mPath.lineTo(mWidth, sy);
					postInvalidate();
				}
				
				Message msg = mHandler.obtainMessage();
				msg.what = 0;
				msg.arg1 = selectPosition;
				mHandler.sendMessage(msg);
				
			}
		}.start();
	}

	private float selectionRelativeX(int selectPos) {
		float relativeX = -1;
		switch (selectPos) {
		case SELECT_DIR:
			relativeX = (mWidth / 3) / 2;
			break;
		case SELECT_MARK:
			relativeX = mWidth / 2;
			break;
		case SELECT_NOTE:
			relativeX = mWidth - (mWidth / 3) / 2;
			break;
		}
		return relativeX;
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		canvas.drawPath(mPath, mPaint);
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		setMeasuredDimension(mScreenWidth, mHeight);
	}
	
	public void setOnSelectListener(onSelectListener l){
		this.mSelectListener = l;
	}
	
	public int getPosition(){
		return mPreSelection;
	}
	
	private static class MyHandler extends Handler {
		private final WeakReference<LineSelectionView> mFragmentView;

		MyHandler(LineSelectionView view) {
			this.mFragmentView = new WeakReference<LineSelectionView>(view);
		}

		@Override
		public void handleMessage(Message msg) {
			LineSelectionView service = mFragmentView.get();
			if (service != null) {
				super.handleMessage(msg);
				try {
					final int selectPosition = msg.arg1;
					if(service.mSelectListener != null){
						service.mSelectListener.onSelect(selectPosition);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public interface onSelectListener{
		
		public void onSelect(int selectPosition);
		
	}
	
	
}
