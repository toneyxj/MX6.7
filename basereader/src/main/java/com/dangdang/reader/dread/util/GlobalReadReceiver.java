package com.dangdang.reader.dread.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.dangdang.zframework.log.LogM;

public class GlobalReadReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		
		String action = intent.getAction();
		if(Intent.ACTION_PACKAGE_REMOVED.equals(action)){
			String packageName = intent.getDataString().substring(8);
			pringLog(" removed package " + packageName);
		}
	}
	
	protected void pringLog(String log){
		LogM.i(getClass().getSimpleName(), log);
	}
	
	protected void pringLogE(String log){
		LogM.e(getClass().getSimpleName(), log);
	}
	
}
