package com.dangdang.reader.dread.core.base;

import android.content.Context;
import android.graphics.Canvas;
import android.view.View;
import android.view.ViewGroup;

import com.dangdang.reader.dread.config.ReadConfig;
import com.dangdang.reader.dread.core.base.IReaderController.DPageIndex;
import com.dangdang.reader.dread.core.epub.DrawWrapper;
import com.dangdang.reader.dread.core.epub.GalleryView.OnGalleryPageChangeListener;
import com.dangdang.zframework.log.LogM;
import com.dangdang.zframework.utils.DRUiUtility;

public abstract class BaseReaderWidget extends ViewGroup implements IReaderWidget {
	
	private int mScreenWidth;
	private int mScreenHeight;
	private float mDensity;

	public BaseReaderWidget(Context context) {
		super(context);
		initScreenReleateParams();
	}
	
	public void initScreenReleateParams() {
		final ReadConfig readConfig = ReadConfig.getConfig();
		mScreenWidth = readConfig.getReadWidth();
		mScreenHeight = readConfig.getReadHeight();
		mDensity = DRUiUtility.getDensity();
	}

	public abstract BasePageView getCurrentView();
	
	public abstract View getOrCreateChild(int current, DPageIndex pageIndex);
	
	public abstract boolean isFirstPage();
	
	public abstract boolean isLastPage();
	
	public abstract void animChangeAfter();
	
	public abstract boolean isAnimFinish();
	
	public OnGalleryPageChangeListener getGalleryPageListener(){
		return null;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
	}

	@Override
	public boolean addViewInLayout(View child, int index,
			LayoutParams params, boolean preventRequestLayout) {
		return super.addViewInLayout(child, index, params, preventRequestLayout);
	}
	
	private DrawWrapper mDrawWrapper;
	@Override
	protected void dispatchDraw(Canvas canvas) {
		//printLog(" ReaderWidget dispatchDraw ");
		if (ReadConfig.getConfig().isImgBg()) {
			if(mDrawWrapper == null){
				mDrawWrapper = new DrawWrapper();
			}
			int width = getScreenWidth();
			int height = getScreenHeight();
			mDrawWrapper.drawBackground(canvas, ReadConfig.getConfig(), width, height);
		}
		super.dispatchDraw(canvas);
	}
	
	public void removeLongClick(){
		
	}
	
	public void onSizeChange(){
		
	}
	
	public void resetMorePointer(){//TODO tmp ?
		
	}
	
	public int getScreenWidth() {
		return mScreenWidth;
	}

	public int getScreenHeight() {
		return mScreenHeight;
	}
	
	public float getDensity() {
		return mDensity;
	}

	protected void printLog(String log){
		LogM.i(getClass().getSimpleName(), log);
	}
	
	protected void printLogD(String log){
		LogM.d(getClass().getSimpleName(), log);
	}
	
	protected void printLogE(String log){
		LogM.e(getClass().getSimpleName(), log);
	}
	
	protected void printLogV(String log){
		LogM.v(getClass().getSimpleName(), log);
	}
	
	protected void printLogW(String log){
		LogM.w(getClass().getSimpleName(), log);
	}
	
	
}
