package com.dangdang.reader.dread.core.base;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;

import com.dangdang.reader.dread.config.ReadConfig;
import com.dangdang.reader.dread.core.base.IReaderController.DAnimType;
import com.dangdang.reader.dread.core.epub.DrawWrapper;
import com.dangdang.reader.dread.data.ReadInfo;

public abstract class OtherPageView extends BasePageView {
	
	protected int mShapeWidth = 5;
	private int bookType;
	
	public OtherPageView(Context context) {
		super(context);
		preInit();
	}
	
	private void preInit() {
		animChangeAfter();
	}

	@Override
	public void animChangeAfter() {
		DAnimType animType = ReadConfig.getConfig().getAnimationTypeNew();
		if(animType == DAnimType.Slide){
			mShapeWidth = (int)(5*getDensity());
		} else {
			mShapeWidth = 0;
		}
	}

	private DrawWrapper mDrawWrapper;
	@Override
	protected void dispatchDraw(Canvas canvas) {

		try {
			final ReadConfig readConfig = ReadConfig.getConfig();
			if (readConfig.isImgBg()) {
				if (mDrawWrapper == null) {
					mDrawWrapper = new DrawWrapper();
				}
				int width = readConfig.getReadWidth();
				int height = readConfig.getReadHeight();
				mDrawWrapper.drawBackground(canvas, readConfig, width, height);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		super.dispatchDraw(canvas);
	}
	
	public int getBookType() {
		return bookType;
	}

	public void setBookType(int bookType) {
		this.bookType = bookType;
	}
	
	public boolean isFullBook(){
		return ReadInfo.isFullBook(getBookType());
	}

	public int getShapeWidth() {
		return mShapeWidth;
	}

	public void setShapeWidth(int shapeWidth) {
		this.mShapeWidth = shapeWidth;
	}

	protected void sendBroadcast(Intent intent){
		if(intent == null)
			return;
		intent.setPackage(getContext().getPackageName());
		getContext().sendBroadcast(intent);
	}
	
}
