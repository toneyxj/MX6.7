/**
 * 
 */
package com.dangdang.reader.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class UnbindingService extends Service {

	public static final String BROADCAST_RECEIVER_UNBINDING_USERNAME = "action_username";
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		
		if(intent == null){
			return;
		}
		
		final String username = intent.getStringExtra(BROADCAST_RECEIVER_UNBINDING_USERNAME);
		if (username == null) {
			return;
		}
		handleFontRelate();
		
		/**
		 * 解绑不再删书
		 *	
		new Thread() {
			public void run() {
				//删除 新 数据(sd 卡中 ddReader 目录下的数据)
				File file = DangdangFileManager.getUserBookPath(getApplicationContext(), username);
				DangdangFileManager.recurrenceDeleteFile(file);
				//删除 老数据 (sd 卡中 dangdang 目录下的数据)
				File oldfile = DangdangFileManager.getOldUserBookPath(getApplicationContext(), username);
				DangdangFileManager.recurrenceDeleteFile(oldfile);
			}
		} .start();
		 */
	}
	
	
	private void handleFontRelate() {
		/*try {
			Context ctx = getApplicationContext();
			LoginAndBindAfterHandle handle = new LoginAndBindAfterHandle(ctx);
			handle.afterUnBind();
		} catch (Exception e) {
			e.printStackTrace();
		}*/
	}
}
