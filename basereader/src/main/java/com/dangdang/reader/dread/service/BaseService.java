package com.dangdang.reader.dread.service;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.dangdang.reader.cloud.Status;
import com.dangdang.reader.db.ReaderDBHelper;

public abstract class BaseService {
	
	protected ReaderDBHelper mDB = null;
	
	public BaseService(Context context){
	
		mDB = new ReaderDBHelper(context);
		
	}
	
	public ReaderDBHelper getDB(){
		return mDB;
	}
	

	public void closeDB(){
		if(mDB != null){
			try {
				mDB.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public void closeSqliteDb(SQLiteDatabase sqlDb){
		if(sqlDb != null){
			try {
				sqlDb.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
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
	
	protected boolean isNewStatus(String status){
		return Status.isNew(Integer.valueOf(status));
	}
	
	protected boolean isUpdateStatus(String status){
		return Status.isUpdate(Integer.valueOf(status));
	}
	
	protected boolean isDeleteStatus(String status){
		return Status.isDelete(Integer.valueOf(status));
	}
	
	public void printLog(String log){
		//LogM.i(getClass().getSimpleName(), log);
	}
	
}
