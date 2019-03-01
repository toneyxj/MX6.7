/**
 * 
 */
package com.dangdang.reader.service;

import java.io.File;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.dangdang.reader.utils.DangdangFileManager;

public class DeleteLocalFileService extends Service {

	public static final String DELETE_LOCAL_FILE = "delete_local_files";
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		
		/*if(intent == null || intent.getAction() == null){
			return;
		}
		if (intent.getAction().equals(DELETE_LOCAL_FILE)) {//删除书籍
			final List<String> dirlist = intent.getStringArrayListExtra(BookShelfUtil.DEL_LOCAL_FILE_LIST);
			new Thread() {
				public void run() {
					for (int i = 0; i < dirlist.size(); i++) {
						deleteBook(dirlist.get(i));
					}
				}
			} .start();
		}*/
	}
	
	private void deleteBook(String dir) {
		DangdangFileManager.deleteBook(new File(dir));
	}
	
}
