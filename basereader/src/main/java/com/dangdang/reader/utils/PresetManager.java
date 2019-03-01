package com.dangdang.reader.utils;

import java.io.File;
import java.io.InputStream;

import android.content.Context;

import com.dangdang.reader.R;
import com.dangdang.zframework.log.LogM;
import com.dangdang.zframework.utils.ZipExecutor.UnZipOperator;

/**
 * 预置数据管理
 * @author luxu
 */
public class PresetManager {

	
	private Context mContext;
	private ConfigManager mConfigManager;
	private boolean mPdfResSucceed;
	
	public PresetManager(Context context){
		mContext = context;
		mConfigManager = new ConfigManager(context);
	}
	
	public void copyPreset(){
		
		if(!isCopyPdfRes()){
		} else {
			printLog(" already copy pdf res ");
		}
		
	}
	
	private boolean isCopyPdfRes(){
		return mConfigManager.isCopyPdfRes();
	}
	
	protected void printLog(String log){
		LogM.i(getClass().getSimpleName(), log);
	}
	
}
