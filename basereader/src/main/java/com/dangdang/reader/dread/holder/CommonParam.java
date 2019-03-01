package com.dangdang.reader.dread.holder;

import com.dangdang.reader.utils.Utils;

public class CommonParam {

	private static CommonParam mCommonParam;

	private float mBatteryValue;
	private String mCurTime;
	private boolean mRefreshTime = true;
	
	private CommonParam() {
		mBatteryValue = 1f;
		mCurTime = Utils.getCurrentTime();
	}
	
	public synchronized static CommonParam getInstance() {
		if (mCommonParam == null) {
			mCommonParam = new CommonParam();
		}
		return mCommonParam;
	}
	
	public boolean ismRefreshTime() {
		return mRefreshTime;
	}

	public void setmRefreshTime(boolean mRefreshTime) {
		this.mRefreshTime = mRefreshTime;
	}

	public float getmBatteryValue() {
		return mBatteryValue;
	}

	public void setmBatteryValue(float mBatteryValue) {
		this.mBatteryValue = mBatteryValue;
	}

	public String getmCurTime() {
		return mCurTime;
	}

	public void setmCurTime(String mCurTime) {
		this.mCurTime = mCurTime;
	}
	
}
