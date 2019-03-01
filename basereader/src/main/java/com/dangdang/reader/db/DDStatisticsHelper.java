package com.dangdang.reader.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DDStatisticsHelper extends SQLiteOpenHelper{
	private static final int DB_VERSION	= 1;
	private static final String	DB_NAME	= "DDClick.db";
	public static final String	DB_TABLE = "click";
	
	public static final String	KEY_ID	= "_id";
	public static final String	KEY_DATA = "data";
	public static final String	KEY_NUM	 = "num";		
	//0 没上传  1上传
	public static final String	KEY_UPLOAD	 = "upload";
	
	private static final String	DB_CREATE = "CREATE TABLE IF NOT EXISTS " + DB_TABLE +
											"('_id' INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL , " +
											KEY_UPLOAD + " INTEGER, " +
											KEY_DATA + " TEXT);";
	
	public DDStatisticsHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		try{
			db.execSQL(DB_CREATE);
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS notes");
		onCreate(db);
	}

}
