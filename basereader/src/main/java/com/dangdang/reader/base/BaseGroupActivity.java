package com.dangdang.reader.base;

import android.annotation.TargetApi;
import android.app.ActivityGroup;
import android.app.LocalActivityManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.dangdang.reader.R;
import com.dangdang.zframework.log.LogM;
import com.dangdang.zframework.utils.SystemBarTintManager;

public abstract class BaseGroupActivity extends ActivityGroup {

	protected SystemBarTintManager tintManager;
	
	protected abstract void onCreateImpl(Bundle savedInstanceState);

	protected abstract void onDestroyImpl();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initSystemBar();
		onCreateImpl(savedInstanceState);
		
		printLog("[ onCreate TaskId = " + getTaskId() + "]");
	}

	@Override
    protected void onSaveInstanceState(Bundle outState) {
    	
    }
	
	@SuppressWarnings("rawtypes")
	protected String switchActivity(Class clazz, ViewGroup container) {

		final Intent intent = new Intent(this, clazz);
		final String id = clazz.getSimpleName();

		return switchActivity(intent, id, container);
	}

	@SuppressWarnings("deprecation")
	protected String switchActivity(final Intent intent, final String id,
			ViewGroup container) {

		LocalActivityManager localActm = getLocalActivityManager();
		try {
			View inView = localActm.startActivity(id, intent).getDecorView();
			container.removeAllViews();
			container.addView(inView);
		} catch (Exception e) {
			e.printStackTrace();
			localActm.destroyActivity(id, true);
			View inView = localActm.startActivity(id, intent).getDecorView();
			container.removeAllViews();
			container.addView(inView);
		}

		return id;
	}

	protected void onDestroy() {
		super.onDestroy();
		printLog(" onDestroy()");
		onDestroyImpl();

	}

	protected void printLog(String log) {
		LogM.d(getClass().getSimpleName(), log);
	}

	protected void printLogE(String log) {
		LogM.e(getClass().getSimpleName(), log);
	}

	private void initSystemBar() {
		if (!isTransparentSystemBar())
			return;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			setTranslucentStatus(true);
		}		
		tintManager = new SystemBarTintManager(this);
		tintManager.setStatusBarTintEnabled(isFitSystemWindow());
		tintManager.setStatusBarTintResource(getSystemBarColor());
	}

	/**
	 * 是否支持状态栏透明
	 * 
	 * @return
	 */
	public boolean isTransparentSystemBar() {
		return true;
	}

	/**
	 * 否考虑系统窗口布局，若为false，根布局会被状态栏遮住。非纯色的须要返回false，然后通过padding调整布局
	 * 
	 * @return
	 */
	protected boolean isFitSystemWindow() {
		return true;
	}

	/**
	 * 系统状态栏的颜色，默认为主题色，title_bg，如果isFitSystemWindow返回false，这里可能需要返回透明色
	 * 
	 * @return
	 */
	protected int getSystemBarColor() {
		return R.color.green_32; // title_bg;
	}

	@TargetApi(19)
	private void setTranslucentStatus(boolean on) {
		Window win = getWindow();
		WindowManager.LayoutParams winParams = win.getAttributes();
		final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
		if (on) {
			winParams.flags |= bits;
		} else {
			winParams.flags &= ~bits;
		}
		win.setAttributes(winParams);
	}

	/**
	 * 是否支持进入和退出的平移动画
	 * 
	 * @return
	 */
	protected boolean isAnimation() {
		return true;
	}

	@Override
	public void setContentView(int layoutResID) {
		super.setContentView(getContentView(layoutResID));
	}

	private View getContentView(int layoutResID) {
		View view = LayoutInflater.from(this).inflate(layoutResID, null);
		// 透明状态
		if (isTransparentSystemBar() && isFitSystemWindow())
			view.setFitsSystemWindows(true);
		return view;
	}
	
	@Override
	public void sendBroadcast(Intent intent){
		if(intent == null)
			return;
		intent.setPackage(this.getPackageName());
		super.sendBroadcast(intent);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		//添加友盟统计
//		UmengStatistics.onPause(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		//添加友盟统计
//		UmengStatistics.onResume(this);
	}
}
