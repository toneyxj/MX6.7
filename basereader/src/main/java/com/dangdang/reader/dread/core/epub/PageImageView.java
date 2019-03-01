package com.dangdang.reader.dread.core.epub;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;

import com.dangdang.reader.dread.config.NoteRect;
import com.dangdang.reader.dread.config.ReadConfig;
import com.dangdang.reader.dread.config.TmpRect;
import com.dangdang.reader.dread.core.base.IEpubPageView.DrawingType;
import com.dangdang.reader.dread.core.base.IReaderWidget.DrawPoint;
import com.dangdang.reader.dread.core.epub.EpubPageView.IUpdateMagnifListener;
import com.dangdang.zframework.log.LogM;
import com.dangdang.zframework.utils.BitmapUtil;

public class PageImageView extends NoteOperationView {

	private Bitmap mSrcBitmap;
	private Bitmap mFullBitmap;
	private Bitmap mTmpBitmap;
	private Paint mPaint = new Paint();

	private DrawPoint mCurrent;
	private IUpdateMagnifListener mMagnifListener;

	public PageImageView(Context context) {
		super(context);
		mPaint.setAntiAlias(true);
	}

	/*
	 * @Override public boolean isOpaque() { return true; }
	 */

	@Override
	protected void onDraw(Canvas canvas) {
		if (!isUseable(mSrcBitmap)) {
			printLog(" onDraw isUseable=false");
			drawBg(canvas);
			return;
		}
		
		if (isCursor()) {
			if (mTmpBitmap == null) {
				mTmpBitmap = getBitmap();
				printLog(" testbitmap create Tmp " + mTmpBitmap);
			} else {
				mTmpBitmap.eraseColor(Color.TRANSPARENT);
			}
			if (mTmpBitmap == null) {
				printLogE(" tmpbmp is null ");
				drawNoCursor(canvas);
				return;
			}
			
			Canvas tmpCav = new Canvas(mTmpBitmap);
			Bitmap contentBmp = (mFullBitmap != null) ? mFullBitmap : mSrcBitmap;
			tmpCav.drawBitmap(contentBmp, 0, 0, mPaint);
			super.onDraw(tmpCav);
			canvas.drawBitmap(mTmpBitmap, 0, 0, mPaint);

			if (mCurrent != null) {
				onUpdateMagnif(mTmpBitmap, mCurrent.getPoint());
			}
			/*
			 * canvas.drawBitmap(mFullBitmap, 0, 0, mPaint);
			 * super.onDraw(canvas);
			 */
		} else {
			drawNoCursor(canvas);
		}
		/*
		 * canvas.drawBitmap(mSrcBitmap, 0, 0, mPaint); super.onDraw(canvas);
		 */
	}

	protected void drawBg(Canvas canvas) {
		try {
			final ReadConfig readConfig = ReadConfig.getConfig();
			if (readConfig.isImgBg()) {
				if (mDrawWrapper == null) {
					mDrawWrapper = new DrawWrapper();
				}
				int width = getScreenWidth();
				int height = getScreenHeight();
				mDrawWrapper.drawBackground(canvas, readConfig, width, height);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void drawNoCursor(Canvas canvas) {
		resetTmpBitmap();
		if (hasRects()) {
			drawAndSelected(canvas);
		} else {
			drawNoSelected(canvas);
		}
	}

	protected void drawNoSelected(Canvas canvas) {
		resetFullBitmap();
		canvas.drawBitmap(mSrcBitmap, 0, 0, mPaint);
	}

	private Bitmap getBitmap() {
		Bitmap bitmap = null;
		try {
			final int width = getWidth();
			final int height = getHeight();
			bitmap = Bitmap.createBitmap(width, height, Config.RGB_565);
		} catch (OutOfMemoryError e) {
			LogM.e(" newbmp 2 OutOfMemoryError ");
			System.gc();
			System.gc();
		}
		return bitmap;
	}

	@SuppressLint("WrongCall")
	private void drawAndSelected(Canvas canvas) {
		if (mFullBitmap == null) {
			mFullBitmap = getBitmap();
			printLog(" testbitmap create full " + mFullBitmap);
		} else {
			mFullBitmap.eraseColor(Color.TRANSPARENT);
		}
		
		if(mFullBitmap != null){
			Canvas tmpCav = new Canvas(mFullBitmap);
			tmpCav.drawBitmap(mSrcBitmap, 0, 0, mPaint);
			super.onDraw(tmpCav);
			canvas.drawBitmap(mFullBitmap, 0, 0, mPaint);
		} else {
			printLogE(" fullbmp is null");
			drawNoSelected(canvas);
		}
	}

	/**
	 * @param bitmap
	 * @param current
	 *            可能是null
	 * @param type
	 * @param rectss
	 *            可能是null
	 */
	public void updatePageInner(Bitmap bitmap, DrawingType type,
			NoteRect... rectss) {
		mSrcBitmap = bitmap;
		type = type == null ? DrawingType.Line : type;
		drawRects(type, null, null, rectss);

		invalidate();
	}

	/*
	 * public void setImageBitmap(Bitmap bm) { mSrcBitmap = bm; invalidate(); }
	 */

	public void doDrawing(DrawingType type, DrawPoint start, DrawPoint end,
			DrawPoint current, int drawLineColor, Rect[]... rectss) {
		if(type != null && type == DrawingType.ShadowTTS){
			super.drawTmpTTSRects(type, convertTmpRect(rectss));
		} else {
			super.drawRects(type, start, end, convertRect(drawLineColor, rectss));
		}
		mCurrent = current;

		/*
		 * Bitmap bitmap = (mTmpBitmap != null) ? mTmpBitmap : mFullBitmap;
		 * if(bitmap != null && current != null && !current.isNull()){
		 * onUpdateMagnif(bitmap, current.getPoint()); } else {
		 * printLogW(" doDrawing no no no ... "); }
		 */
	}

	private NoteRect[] convertRect(int drawLineColor, Rect[]... rectss) {
		NoteRect[] nRects = new NoteRect[rectss.length];
		for (int i = 0, len = rectss.length; i < len; i++) {
			NoteRect nRect = new NoteRect();
			nRect.setRects(rectss[i]);
			nRect.setDrawLineColor(drawLineColor);
			nRects[i] = nRect;
		}
		return nRects;
	}
	
	private TmpRect[] convertTmpRect(Rect[]... rectss) {
		TmpRect[] nRects = new TmpRect[rectss.length];
		for (int i = 0, len = rectss.length; i < len; i++) {
			TmpRect nRect = new TmpRect();
			nRect.setRects(rectss[i]);
			nRects[i] = nRect;
		}
		return nRects;
	}

	public int drawFinish(DrawingType type, DrawPoint current) {

		printLog(" drawFinish ");
		hideMagnif();

		return 0;
	}

	public void reset() {
		resetInner();
		invalidate();
	}

	private void resetInner() {
		mSrcBitmap = null;
		resetFullBitmap();
		resetTmpBitmap();
		super.reset();
	}

	protected void resetFullBitmap() {
		if (mFullBitmap != null) {
			recycle(mFullBitmap);
			printLog(" testbitmap recycle Full " + mFullBitmap);
			mFullBitmap = null;
		}
	}

	protected void resetTmpBitmap() {
		if (mTmpBitmap != null) {
			recycle(mTmpBitmap);
			printLog(" testbitmap recycle tmp " + mTmpBitmap);
			mTmpBitmap = null;
		}
	}

	private boolean isUseable(Bitmap bitmap) {
		return BitmapUtil.isAvailable(bitmap);
	}

	private void recycle(Bitmap bitmap) {
		BitmapUtil.recycle(bitmap);
	}

	public void clear() {
		super.clear();
		resetInner();
	}

	private void onUpdateMagnif(Bitmap bitmap, Point point) {
		if (mMagnifListener != null && point != null) {
			mMagnifListener.updateMagnif(bitmap, point.x, point.y);
		}
	}

	private void hideMagnif() {
		mCurrent = null;
	}

	public void setMagnifListener(IUpdateMagnifListener l) {
		this.mMagnifListener = l;
	}

}
