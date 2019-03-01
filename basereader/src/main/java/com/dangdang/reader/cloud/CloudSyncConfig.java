package com.dangdang.reader.cloud;

import android.content.Context;
import android.content.SharedPreferences;

public class CloudSyncConfig {

	
	public static final String CLOUD_AUTOSYNC_SWITCH = "cloud_autosync_switch";
	public static final String CLOUD_AUTOSYNC_ONLYWIFI = "cloud_autosync_onlywifi";
	//public static final String CLOUD_SYNC_VERSIONTIME = "cloud_sync_versiontime";
	public static final String CLOUD_SWITCH_DIALOG_TIP = "cloud_autosync_dialogtip";
	public static final String CLOUD_NO_MORE_TIP = "cloud_no_more_dialogtip";
	public static final String CLOUD_NOVEL_PRELOAD = "cloud_novel_preload";
	
	private Context mContext;
	
	public CloudSyncConfig(Context context) {
		this.mContext = context;
	}

	private SharedPreferences getPreferences() {
		final String name = mContext.getPackageName();
		return mContext.getSharedPreferences(name, Context.MODE_PRIVATE);
	}
	
	private SharedPreferences.Editor getEditor(){
		return getPreferences().edit();
	}
	
	/**
	 * 获取自动云同步开关，默认为打开true
	 * @return
	 */
	public boolean getAutoSyncSwitch(){
		SharedPreferences pre = getPreferences();
		return pre.getBoolean(CLOUD_AUTOSYNC_SWITCH, true);
	}
	
	/**
	 * 是否仅在wifi下自动同步, 默认是true
	 * @return
	 */
	public boolean getAutoSyncOnlyWifi(){
		SharedPreferences pre = getPreferences();
		return pre.getBoolean(CLOUD_AUTOSYNC_ONLYWIFI, true);
	}
	
	/**
	 * 
	 */
	public void saveAutoSyncSwitch(boolean tSwitch){
		SharedPreferences.Editor editor = getEditor();
		editor.putBoolean(CLOUD_AUTOSYNC_SWITCH, tSwitch);
		editor.commit();
	}
	
	public boolean getNovelPreload(){
		SharedPreferences pre = getPreferences();
		return pre.getBoolean(CLOUD_NOVEL_PRELOAD, true);
	}
	
	public void setNovelPreload(boolean tSwitch){
		SharedPreferences.Editor editor = getEditor();
		editor.putBoolean(CLOUD_NOVEL_PRELOAD, tSwitch);
		editor.commit();
	}
	
	public void saveAutoSyncOnlyWifi(boolean tSwitch){
		SharedPreferences.Editor editor = getEditor();
		editor.putBoolean(CLOUD_AUTOSYNC_ONLYWIFI, tSwitch);
		editor.commit();
	}
	
	public boolean isAutoSyncDialogTip(){
		SharedPreferences pre = getPreferences();
		return !pre.contains(CLOUD_SWITCH_DIALOG_TIP);
	}
	
	public void saveAutoSyncDialogTip(boolean b){
		SharedPreferences.Editor editor = getEditor();
		editor.putBoolean(CLOUD_SWITCH_DIALOG_TIP, b);
		editor.commit();
	}
	
	/**
	 * 是否不显示dialog提示使用流量
	 * @return
	 */
	public boolean isNoMobileDialogTip(){
		SharedPreferences pre = getPreferences();
		return pre.getBoolean(CLOUD_NO_MORE_TIP, false);
	}
	
	public void saveNoMobileDialogTip(boolean b){
		SharedPreferences.Editor editor = getEditor();
		editor.putBoolean(CLOUD_NO_MORE_TIP, b);
		editor.commit();
	}
	
	/*public long getSyncVersionTime(){
		SharedPreferences pre = getPreferences();
		return pre.getLong(CLOUD_SYNC_VERSIONTIME, 0);
	}
	
	public void saveSyncVersionTime(long versionTime){
		SharedPreferences.Editor editor = getEditor();
		editor.putLong(CLOUD_SYNC_VERSIONTIME, versionTime);
		editor.commit();
	}*/
	
}
