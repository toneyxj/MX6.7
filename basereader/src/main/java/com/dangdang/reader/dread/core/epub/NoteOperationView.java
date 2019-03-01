package com.dangdang.reader.dread.core.epub;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;

import com.dangdang.reader.R;
import com.dangdang.reader.dread.config.NoteRect;
import com.dangdang.reader.dread.config.PagePadding;
import com.dangdang.reader.dread.config.ReadConfig;
import com.dangdang.reader.dread.config.TmpRect;
import com.dangdang.reader.dread.core.base.IEpubPageView.DrawingType;
import com.dangdang.reader.dread.core.base.IReaderWidget.DrawPoint;
import com.dangdang.reader.dread.core.epub.NoteHolder.NoteFlag;
import com.dangdang.reader.dread.data.BookNote;
import com.dangdang.reader.dread.view.BaseView;
import com.dangdang.zframework.utils.BitmapUtil;

public class NoteOperationView extends BaseView {
	
	protected DrawWrapper mDrawWrapper;
	
	protected float mMinX;
	protected float mMaxX;
	protected float mMinY;
	protected float mMaxY;
	protected int mRadius;
	
	protected DrawingType mType;
	protected DrawPoint mStartPoint;
	protected DrawPoint mEndPoint;
	protected NoteRect[] mRectss;
	protected Bitmap mNoteBitmap;
	
	protected DrawingType mTmpType;
	protected TmpRect[] mTmpRects;
	
	protected DrawingType mTmpTTSType;
	protected TmpRect[] mTmpTTSRects;
	
	private int mNotePicWidth;
	private int mNotePicHeight;
	
	public NoteOperationView(Context context) {
		super(context);
		init();
	}

	private void init() {
		mDrawWrapper = new DrawWrapper();
		mDrawWrapper.init();
		
		mNoteBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.notes);
		mNotePicWidth = mNoteBitmap.getWidth();
		mNotePicHeight = mNoteBitmap.getHeight();
	}

	@Override
	public void initScreenReleatedParamsInner() {
		super.initScreenReleatedParamsInner();
		
		int sWidth = getScreenWidth();
		int sHeight = getScreenHeight();
		ReadConfig readConfig = ReadConfig.getConfig();
		PagePadding paddingRect = readConfig.getPaddingRect(getContext());
		
		mMinX = paddingRect.getPaddingLeft();
		mMaxX = sWidth - paddingRect.getPaddingLeft();
		mMinY = paddingRect.getPaddingTop();
		mMaxY = sHeight - paddingRect.getPaddingBottom();
		
		mRadius = (int)(3 * getDensity());
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		
		if(hasDrawNote()){
			if(mType == DrawingType.Line){
				drawLine(canvas);
			} else if(mType == DrawingType.Shadow){
				drawShadow(canvas);
			}
		}
		
		if(hasTmpRects()){
			for(int i = 0, len = mTmpRects.length; i < len; i++){
				drawOneTmpRect(canvas, mTmpRects[i], mTmpType);
			}
		}
		
		if(hasTmpTTSRects()){
			for(int i = 0, len = mTmpTTSRects.length; i < len; i++){
				drawOneTmpRect(canvas, mTmpTTSRects[i], mTmpTTSType);
			}
		}
	}

	protected void drawOneTmpRect(Canvas canvas, TmpRect tmpRect, DrawingType type) {
		if(tmpRect == null){
			return;
		}
		Rect[] rects = tmpRect.getRects();
		if(isArrEmpty(rects)){
			printLog(" drawTmpRect rects is null ");
			return;
		}
		
		for(int i = 0, len = rects.length; i < len; i++){
			Rect rect = rects[i];
			
			int startX = rect.left;//Rect(214, 473 - 240, 499)
			int endX = rect.right;
			int startY = rect.top;
			int endY = rect.bottom;
			if(type == DrawingType.ShadowSearch){
				mDrawWrapper.drawRectBySearch(canvas, startX, startY, endX, endY, tmpRect.isCurrent());
			} else if(type == DrawingType.ShadowTTS){
				mDrawWrapper.fillTmpRectangle(canvas, startX, startY, endX, endY);
			}
		}
	}
	
	protected void drawLine(Canvas canvas) {
		for(int j = 0, len0 = mRectss.length; j < len0; j++){
			try {
				NoteRect nRect = mRectss[j];
				Rect[] rects = nRect.getRects();
				if(isArrEmpty(rects)){
					printLogE(" drawLine rects is null ");
					continue;
				}
				
				int lastEndX = 0;
				int lastEndY = 0;
				for(int i = 0, len = rects.length; i < len; i++){
					Rect rect = rects[i];
					
					int startX = rect.left;//Rect(214, 473 - 240, 499)
					int endX = rect.right;
					int startY = rect.top;
					int endY = rect.bottom;
					float gap = getGap(rect);
					int color = ReadConfig.NOTE_DRAWLINE_COLOR[0];
					if (nRect.getDrawLineColor() < ReadConfig.NOTE_DRAWLINE_COLOR.length
							&& nRect.getDrawLineColor() >= 0) {
						color = ReadConfig.NOTE_DRAWLINE_COLOR[nRect.getDrawLineColor()];
					}
					mDrawWrapper.setLineColor(color);
					mDrawWrapper.drawUnderLine(canvas, startX, endY + gap, endX, endY + gap);
					
					if(i == len - 1){
						lastEndX = endX;
						lastEndY = endY;
					}
				}
				
				if(nRect.isHasNote()){
					lastEndX = getAdapterMaxX(lastEndX);
					float noteBmpX = lastEndX - mNotePicWidth * 1 / 3;
					float noteBmpY = lastEndY - mNotePicHeight / 4;
					mDrawWrapper.drawNoteBitmap(canvas, mNoteBitmap, noteBmpX, noteBmpY);
					
					saveNotePicRect(nRect, noteBmpX, noteBmpY);
				} else {
					delNotePicRect(nRect);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void saveNotePicRect(NoteRect nRect, float noteBmpX, float noteBmpY) {
		NoteHolder.getHolder().addPicRect(nRect.getChapterIndex(), 
				nRect.getPageIndexInChapter(), 
				nRect.getFlag(), 
				mNotePicWidth, mNotePicHeight, 
				noteBmpX, noteBmpY);
	}
	
	private void delNotePicRect(NoteRect nRect){
		int chapterIndex = nRect.getChapterIndex();
		int pageIndexInChapter = nRect.getPageIndexInChapter();
		NoteFlag flag = nRect.getFlag();
		NoteHolder.getHolder().deleteNoteRect(chapterIndex, pageIndexInChapter, flag);
	}

	private int getAdapterMaxX(float endX) {
		float lastX = mMaxX;
		int result = (endX >= lastX) ? (int)lastX : (int)endX;
		return result;
	}
	
	protected void drawShadow(Canvas canvas) {
		
		Rect firstRect = null;
		Rect lastRect = null;
		
		NoteRect nRect = mRectss[0];
		Rect[] rects = nRect.getRects();
		if(isArrEmpty(rects)){
			printLog(" drawShadow rects is null ");
			return ;
		}
		
		for(int i = 0, len = rects.length; i < len; i++){
			Rect rect = rects[i];
			
			int startX = rect.left;//Rect(214, 473 - 240, 499)
			int endX = rect.right;
			int startY = rect.top;
			int endY = rect.bottom;
			
			if(mType == DrawingType.Line){
				final float gap = getGap(rect);
				mDrawWrapper.drawUnderLine(canvas, startX, endY + gap, endX, endY + gap);
			} else if(mType == DrawingType.Shadow){
				mDrawWrapper.fillRectangle(canvas, startX, startY, endX, endY);
			}
			if(i == 0){
				firstRect = rect;
			} 
			if(i == len - 1){
				lastRect = rect;
			}
		}
		
		if(mType == DrawingType.Shadow){
			int radius = mRadius;
			if(isDrawCursor(mStartPoint)){
				Point p = new Point(firstRect.left, firstRect.top);//Point(480, 1381)
				int height = firstRect.bottom - firstRect.top;//60
				drawCursor(canvas, radius, p, height, true);
			}
			if(isDrawCursor(mEndPoint)){
				Point p = new Point(lastRect.right, lastRect.top);//mEndPoint.getPoint();  Point(600, 1381)
				int height = lastRect.bottom - lastRect.top;
				drawCursor(canvas, radius, p, height, false);
			}
		}
	}
	
	public boolean isCursor(){
		return mStartPoint != null || mEndPoint != null;
	}
	
	private float getGap(Rect rect){
		float gap = rect.height()*0.15f;
		
		float min = getDensity();
		float lineSpacing = ReadConfig.getConfig().getLineSpacing();
		lineSpacing = lineSpacing > 2 ? 2 : lineSpacing;
		float max = lineSpacing*getDensity()*4;
		
		if(lineSpacing <= 0){
			gap = min;
		} /*else {
			gap = max;
		}*/
		gap = gap < min ? min : gap;
		gap = gap > max ? max : gap;
		return gap;
	}

	private void drawCursor(Canvas canvas, int radius, Point p, int height, boolean left) {
		final int topx = p.x;
		final int topy = p.y;
		final int bottomx = p.x;
		final int bottomy = p.y + height + (int)(getDensity()*2);
		mDrawWrapper.drawCursor(canvas, topx, topy, bottomx, bottomy, radius, left);
	}
	
	private boolean isDrawCursor(DrawPoint dPoint){
		return dPoint != null && dPoint.isDraw();
	}
	
	protected boolean hasRects(){
		return hasTmpTTSRects() || hasTmpRects() || hasDrawNote();
	}
	
	protected boolean hasTmpTTSRects(){
		return mTmpTTSType != null && !isArrEmpty(mTmpTTSRects);
	}
	
	protected boolean hasTmpRects(){
		return mTmpType != null && !isArrEmpty(mTmpRects);
	}
	
	protected boolean hasDrawNote(){
		return mType != null && !isArrEmpty(mRectss);
	}
	
	public void drawRects(DrawingType type, DrawPoint start, DrawPoint end, NoteRect... rectss){
		mType = type;
		mStartPoint = start;
		mEndPoint = end;
		mRectss = rectss;
	}
	
	public void drawTmpSearchRects(DrawingType type, TmpRect... rectss){
		mTmpType = type;
		mTmpRects = rectss;
	}
	
	public void drawTmpTTSRects(DrawingType type, TmpRect... rectss){
		mTmpTTSType = type;
		mTmpTTSRects = rectss;
	}
	
	public void reset(){
		printLogD(" reset() ntt ");
		resetNote();
		resetTmp();
		resetTTSTmp();
	}

	private void resetNote() {
		mType = null;
		mStartPoint = null;
		mEndPoint = null;
		mRectss = null;
	}
	
	private void resetTmp(){
		mTmpType = null;
		mTmpRects = null;
	}
	
	private void resetTTSTmp(){
		mTmpTTSType = null;
		mTmpTTSRects = null;
	}
	
	public void clear(){
		reset();
		BitmapUtil.recycle(mNoteBitmap);
		mNoteBitmap = null;
	}
	
	private boolean isArrEmpty(Object[] arr){
		return arr == null || arr.length == 0;
	}
	
}
