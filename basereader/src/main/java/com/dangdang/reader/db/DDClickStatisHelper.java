package com.dangdang.reader.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DDClickStatisHelper extends SQLiteOpenHelper{
	
	private static final int DB_VERSION	= 1;
	private static final String	DB_NAME	= "DDClickNew.db";
	public static final String	DB_TABLE = "ddclick";
	
	public static final String	COLUMN_ID	= "_id";
	public static final String	COLUMN_DATA = "data";
	//0 没上传  1上传
	public static final String	COLUMN_UPLOADSTATUS	 = "uploadstatus";
	public static final String COLUMN_EXPCLOUM1 = "column1";
	
	/**
	 * 1上传
	 */
	public static final int UPLOADSTATUS_YES = 1;
	/**
	 * 0 没上传
	 */
	public static final int UPLOADSTATUS_NO = 0;
	
	private static final String	DB_CREATE = "CREATE TABLE IF NOT EXISTS " + DB_TABLE +
											"('_id' INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL , " +
											COLUMN_UPLOADSTATUS + " INTEGER, " +
											COLUMN_DATA + " TEXT, " +
											COLUMN_EXPCLOUM1 + " TEXT);";
	
	public DDClickStatisHelper(Context context) {
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
		
	}

}
