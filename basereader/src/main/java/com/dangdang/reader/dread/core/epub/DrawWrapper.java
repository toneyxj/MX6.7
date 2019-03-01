package com.dangdang.reader.dread.core.epub;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

import com.dangdang.reader.dread.config.ReadConfig;
import com.dangdang.zframework.log.LogM;
import com.dangdang.zframework.utils.DRUiUtility;

public class DrawWrapper {

	private final static int COLOR_NOTE_RGB = Color.rgb(255, 23, 23);
	private final static int COLOR_NOTE_ARGB = Color.argb(100, 255, 23, 23);
	private final static int COLOR_SEARCH_ARGB_DEEP = Color.argb(110, 255, 23,
			23);
	
	private final static int COLOR_SEARCH_ARGB_TTS = Color.argb(90, 250, 210,
			132);
	
	private final static int COLOR_SEARCH_ARGB = Color.argb(60, 255, 144, 3);

	private final Paint mPaint = new Paint();
	private final Paint mUnderLinePaint = new Paint();
	private final Paint mFillPaint = new Paint();
	private final Paint mLinePaint = new Paint();

	private final Paint mFillSearchPaint = new Paint();
	private final Paint mFillTTSPaint = new Paint();

	private float mDensity = 1f;

	public DrawWrapper() {
		super();
	}

	public void init() {
		mDensity = DRUiUtility.getDensity();

		mUnderLinePaint.setStyle(Paint.Style.STROKE);
		mUnderLinePaint.setStrokeWidth(1.5f * mDensity);
		mUnderLinePaint.setColor(COLOR_NOTE_RGB);

		mFillPaint.setColor(COLOR_NOTE_ARGB);
		mFillSearchPaint.setColor(COLOR_SEARCH_ARGB_DEEP);
		
		mFillTTSPaint.setColor(COLOR_SEARCH_ARGB_TTS);

		mLinePaint.setColor(COLOR_NOTE_RGB);
		mLinePaint.setAntiAlias(true);
		// mLinePaint.setStrokeWidth(4);
		mLinePaint.setStrokeWidth((int) (2.5 * mDensity));
		mLinePaint.setStyle(Paint.Style.FILL_AND_STROKE);

	}

	public void drawUnderLine(Canvas canvas, float startx, float starty,
			float endx, float endy) {
		canvas.drawLine(startx, starty, endx, endy, mUnderLinePaint);
	}

	public void drawNoteBitmap(Canvas canvas, Bitmap bitmap, float bmpX,
			float bmpY) {
		canvas.drawBitmap(bitmap, bmpX, bmpY, mUnderLinePaint);
	}

	public void fillRectangle(Canvas canvas, int startx, int starty, int endx,
			int enxy) {

		/*
		 * if (x1 < x0) { int swap = x1; x1 = x0; x0 = swap; } if (y1 < y0) {
		 * int swap = y1; y1 = y0; y0 = swap; }
		 */
		canvas.drawRect(startx, starty, endx + 1, enxy + 1, mFillPaint);
	}
	
	public void fillTmpRectangle(Canvas canvas, int startx, int starty, int endx,
			int enxy) {

		canvas.drawRect(startx, starty, endx + 1, enxy + 1, mFillTTSPaint);
	}

	public void drawRectBySearch(Canvas canvas, int startx, int starty,
			int endx, int enxy, boolean deep) {
		mFillSearchPaint.setColor(deep ? COLOR_SEARCH_ARGB_DEEP
				: COLOR_SEARCH_ARGB);
		canvas.drawRect(startx, starty, endx + 1, enxy + 1, mFillSearchPaint);
	}

	public void drawCursor(Canvas canvas, int topx, int topy, int bottomx,
			int bottomy, int radius, boolean left) {

		RectF rectf = new RectF();
		if (left) {
			rectf.left = topx - radius;
			rectf.top = topy - 2 * radius;
			rectf.right = topx + radius;
			rectf.bottom = topy;
		} else {
			rectf.left = bottomx - radius;
			rectf.top = bottomy;
			rectf.right = bottomx + radius;
			rectf.bottom = bottomy + 2 * radius;
		}
		canvas.drawLine(topx, topy, bottomx, bottomy, mLinePaint);
		canvas.drawOval(rectf, mLinePaint);
	}

	public Bitmap getBackgroundBitmap(int width, int height) {
		Bitmap bitmap = null;
		try {
			bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
			Canvas canvas = new Canvas(bitmap);
			int bgColor = getBackground();
			ReadConfig config = ReadConfig.getConfig();
			if (config.isImgBg()) {
				drawBackground(canvas, config, width, height);
			} else {
				mPaint.setColor(bgColor);
				canvas.drawRect(0, 0, width, height, mPaint);
			}
			printLog(" testbitmap create page bitmap " + bitmap);
		} catch (Throwable e) {
			LogM.e(" newbmp 1 error " + e);
			System.gc();
			System.gc();
		}

		return bitmap;
	}

	public void drawBackground(Canvas canvas, ReadConfig config, int width, int height) {
		Bitmap bitmapBg = config.getImgBg();
		if (bitmapBg != null) {
			final int w = bitmapBg.getWidth();
			final int h = bitmapBg.getHeight();
			for (int cw = 0, iw = 1; cw < width; cw += w, ++iw) {
				for (int ch = 0, ih = 1; ch < height; ch += h, ++ih) {
					canvas.drawBitmap(bitmapBg, cw, ch, mPaint);
				}
			}
		} else {
			LogM.e(getClass().getName(), "bitmap get error");
			mPaint.setColor(getReaderOtherBgColor());
			canvas.drawRect(0, 0, width, height, mPaint);
		}
	}

	public int getBackground() {
		return ReadConfig.getConfig().getReaderBgColor();
		// return Color.rgb(246, 245, 241);
	}

	public int getReaderOtherBgColor() {
		return ReadConfig.getConfig().getReaderOtherBgColor();
	}

	public void setLineColor(int color) {
		mUnderLinePaint.setColor(color);
	}

	protected void printLog(String log) {
		LogM.i(getClass().getSimpleName(), log);
	}
}
