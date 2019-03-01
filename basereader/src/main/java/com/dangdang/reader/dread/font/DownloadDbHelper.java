/**
 *   file_name: DbHelper.java
 *   create_at: 2011-11-6 下午09:26:09

 */

package com.dangdang.reader.dread.font;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.dangdang.zframework.log.LogM;

/**
 *
 *
 */
public class DownloadDbHelper extends SQLiteOpenHelper {

	private final static LogM logger = LogM.getLog(DownloadDbHelper.class);

	public static final int DBVER = 1;
	public static final String DBNAME = "dddownload.db";

	public DownloadDbHelper(Context context) {
		super(context, DBNAME, null, DBVER);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String sql = FontDownload.CREATE_SQL;
		db.execSQL(sql);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}

	public void printLog(String log) {
		logger.i(false, log);
	}

}
