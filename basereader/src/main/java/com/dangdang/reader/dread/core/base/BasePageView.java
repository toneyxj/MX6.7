package com.dangdang.reader.dread.core.base;

import android.content.Context;
import android.graphics.Color;
import android.view.ViewGroup;

import com.dangdang.reader.R;
import com.dangdang.reader.dread.config.ReadConfig;
import com.dangdang.reader.dread.holder.PageState;
import com.dangdang.zframework.log.LogM;
import com.dangdang.zframework.utils.DRUiUtility;

public abstract class BasePageView extends ViewGroup {
	
	protected int mScreenWidth;
	protected int mScreenHeight;
	
	private float mDensity = 1;
	private int mColorDay = Color.BLACK;
	private int mColorNight = Color.WHITE;
	
	private final int mPageStateKey = R.string.app_name;
	
	private ReadConfig mReadConfig;
	
	public BasePageView(Context context) {
		super(context);
		preinit();
	}
	
	public void preinit() {
		initScreenReleateParams();
		mReadConfig = ReadConfig.getConfig(); 
		mDensity = DRUiUtility.getDensity();
		mColorDay = getResources().getColor(R.color.colorBlack);
		mColorNight = mColorDay;
		
		updatePageStyle();
	}

	public void initScreenReleateParams() {
		final ReadConfig readConfig = ReadConfig.getConfig();
		mScreenWidth = readConfig.getReadWidth();
		mScreenHeight = readConfig.getReadHeight();
	}

	public abstract void animChangeAfter();
	
	public void clear(){
		
	}
	
	public void reset(){
		
	}
	
	public void repaintFooter(){
		
	}
	
	public void updatePageStyle(){
		setBackgroundColor(getBgColor());
	}
	
	protected int getForeColor() {
		return isNightMode() ? mColorNight : mColorDay;
	}

	protected boolean isNightMode() {
		return mReadConfig.isNightMode();
	}
	
	protected int getBgColor(){
		
		return mReadConfig.getReaderOtherBgColor();
	}
	
	public int getScreenWidth(){
		return mScreenWidth;
	}
	
	public int getScreenHeight(){
		return mScreenHeight;
	}
	
	public float getDensity(){
		return mDensity;
	}
	
	public int getColorDay() {
		return mColorDay;
	}

	public int getColorNight() {
		return mColorNight;
	}

	public ReadConfig getReadConfig() {
		return mReadConfig;
	}
	
	public void setPageState(PageState state){
		setTag(mPageStateKey, state);
	}
	
	public void resetPageState(){
		setTag(mPageStateKey, null);
	}
	
	public PageState getPageState(){
		Object tag = getTag(mPageStateKey);
		if(tag == null){
			return null;
		}
		return (PageState) tag;
	}

	public void printLog(String msg){
		LogM.i(getClass().getSimpleName(), msg);
	}
	
	public void printLogD(String msg){
		LogM.d(getClass().getSimpleName(), msg);
	}
	
	public void printLogV(String msg){
		LogM.v(getClass().getSimpleName(), msg);
	}
	
	public void printLogW(String msg){
		LogM.w(getClass().getSimpleName(), msg);
	}
	
	public void printLogE(String msg){
		LogM.e(getClass().getSimpleName(), msg);
	}

}
