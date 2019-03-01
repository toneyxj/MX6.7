package com.dangdang.reader.dread.font;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.dangdang.zframework.log.LogM;

public abstract class BaseDownloadDb {

private final static LogM logger = LogM.getLog(BaseDownloadDb.class);
	
	protected DownloadDbHelper mDb;
	
	protected BaseDownloadDb(Context context){
		mDb = new DownloadDbHelper(context);
	}
	
	public void closeSqlite(SQLiteDatabase sqlite){
		try {
			if(sqlite != null){
				sqlite.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void closeCursor(Cursor cursor){
		try {
			if(cursor != null){
				cursor.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void printLog(String log) {
		logger.d(false, log);
	}
}
