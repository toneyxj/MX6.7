package com.dangdang.reader.dread.view;

import android.content.Context;
import android.view.View;

import com.dangdang.reader.dread.config.ReadConfig;
import com.dangdang.zframework.log.LogM;
import com.dangdang.zframework.utils.DRUiUtility;

public abstract class BaseView extends View {

	private int mScreenWidth;
	private int mScreenHeight;
	private float mDensity = 1f;
	
	public BaseView(Context context) {
		super(context);
		initScreenReleatedParams();
	}

	final public void initScreenReleatedParams(){
		final ReadConfig readConfig = ReadConfig.getConfig();
		mScreenWidth = readConfig.getReadWidth();
		mScreenHeight = readConfig.getReadHeight();
		mDensity = DRUiUtility.getDensity();
		initScreenReleatedParamsInner();
	}
	
	public void initScreenReleatedParamsInner(){
		
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
