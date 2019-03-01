package com.dangdang.reader.dread.font;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.dangdang.zframework.log.LogM;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DownloadDb extends BaseDownloadDb {

	public static final String BOOKNAME_SIGN="(电子书)";

	public static enum DType{
		BOOK,
		BOOK_TRY,
		BOOK_FULL,
		FONT_FREE,
		FONT_CHARGE
	}
	
	public DownloadDb(Context context) {
		super(context);
	}

	public boolean saveDownload(String indentityId, String url, String saveDir, 
			long progress, long totalSize, String status, 
			String data, String user, DType type) {
		synchronized (BaseDownloadDb.class) {
			LogM.l("saveDownload");
			SQLiteDatabase sqlite = null;
			boolean ret = false;
			try {
				sqlite = mDb.getWritableDatabase();
				exeDeleteOne(sqlite, indentityId);
				
				String insert = " insert into " + FontDownload.TABLE_NAME + "(" + FontDownload.INDENTITY_ID + ","
						+ FontDownload.URL + "," + FontDownload.SAVE_DIR + "," + FontDownload.PROGRESS + ","
						+ FontDownload.TOTALSIZE + "," + FontDownload.STATUS + "," + FontDownload.TIME + ","
						+ FontDownload.EXTEN + "," + FontDownload.USER + "," + FontDownload.TYPE 
						+ ") values(?,?,?,?,?,?,?,?,?,?) ";

				long time = new Date().getTime();
				sqlite.execSQL(insert, new Object[] { indentityId, url, saveDir, 
						progress, totalSize, status, time, data , user, type.name()});
				ret = true;
			} catch (Exception e) {
				e.printStackTrace();
				ret = false;
			} finally {
				closeSqlite(sqlite);
			}
			printLog("[saveDownload: ret="+ ret +"]");
			return ret;
		}
	}
	
	public List<FontDownload> getDownloadList(String publicUser, String user, String status, DType type){
		synchronized (BaseDownloadDb.class) {
			printLog("getDownloadList");
			SQLiteDatabase sqlite = null;
			List<FontDownload> results = new ArrayList<FontDownload>();
			FontDownload sDownload = null;
			Cursor cursor = null;
			try {
				sqlite = mDb.getReadableDatabase();
				
				String select = "";
				if(TextUtils.isEmpty(publicUser)){
					select = " select * from " + FontDownload.TABLE_NAME + " where " 
							+ FontDownload.USER + " = ? and " 
							+ FontDownload.STATUS + " = ? and " 
							+ FontDownload.TYPE + " = ? " ;
					cursor = sqlite.rawQuery(select, new String[] { user, status, type.name()});
				} else {
					select = " select * from " + FontDownload.TABLE_NAME + " where (" 
							+ FontDownload.USER + " = ? or " 
							+ FontDownload.USER + " = ? ) and " 
							+ FontDownload.STATUS + " = ? and " 
							+ FontDownload.TYPE + " = ? " ;
					cursor = sqlite.rawQuery(select, new String[] { publicUser, user, status, type.name()});
				}

				while(cursor.moveToNext()){
					sDownload = setDownloadData(cursor);
					results.add(sDownload);
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				closeCursor(cursor);
				closeSqlite(sqlite);
			}
			printLog("[getDownloadList3: results="+ results +"]");
			return results;
		}
	}
	
	public List<FontDownload> getDownloadList(String status, DType type){
		synchronized (BaseDownloadDb.class) {
			printLog("getDownloadList");
			SQLiteDatabase sqlite = null;
			List<FontDownload> results = new ArrayList<FontDownload>();
			FontDownload sDownload = null;
			Cursor cursor = null;
			try {
				sqlite = mDb.getReadableDatabase();
				
				String select = " select * from " + FontDownload.TABLE_NAME + " where " 
						+ FontDownload.STATUS + " = ? and " 
						+ FontDownload.TYPE + " = ? " ;
				cursor = sqlite.rawQuery(select, new String[] {status, type.name()});

				while(cursor.moveToNext()){
					sDownload = setDownloadData(cursor);
					results.add(sDownload);
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				closeCursor(cursor);
				closeSqlite(sqlite);
			}
			printLog("[getDownloadList2: results="+ results +"]");
			return results;
		}
	}

	public FontDownload getDownload(String indentityId) {
		synchronized (BaseDownloadDb.class) {
			LogM.l("getDownload");
			SQLiteDatabase sqlite = null;
			FontDownload sDownload = null;
			Cursor cursor = null;
			try {
				sqlite = mDb.getReadableDatabase();
				
				String select = " select * from " + FontDownload.TABLE_NAME + " where " + FontDownload.INDENTITY_ID
						+ " = ?";
				cursor = sqlite.rawQuery(select, new String[] { indentityId });

				if (cursor.moveToLast()) {
					sDownload = setDownloadData(cursor);
				}
				printLog("[getDownload: sDownload="+ sDownload + ", count=" + cursor.getCount() + "]");
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				closeCursor(cursor);
				closeSqlite(sqlite);
			}
			
			return sDownload;
		}
	}

	private FontDownload setDownloadData(Cursor cursor) {
		FontDownload sDownload;
		sDownload = new FontDownload();
		sDownload.id = cursor.getInt(cursor.getColumnIndex(FontDownload.ID));
		sDownload.indentityId = cursor.getString(cursor.getColumnIndex(FontDownload.INDENTITY_ID));
		sDownload.url = cursor.getString(cursor.getColumnIndex(FontDownload.URL));
		sDownload.saveDir = cursor.getString(cursor.getColumnIndex(FontDownload.SAVE_DIR));
		sDownload.progress = cursor.getLong(cursor.getColumnIndex(FontDownload.PROGRESS));
		sDownload.totalSize = cursor.getLong(cursor.getColumnIndex(FontDownload.TOTALSIZE));
		sDownload.status = cursor.getString(cursor.getColumnIndex(FontDownload.STATUS));
		sDownload.timeStamp = cursor.getLong(cursor.getColumnIndex(FontDownload.TIME));
		sDownload.bookData = cursor.getString(cursor.getColumnIndex(FontDownload.EXTEN));
		sDownload.userName = cursor.getString(cursor.getColumnIndex(FontDownload.USER));
		sDownload.type = cursor.getString(cursor.getColumnIndex(FontDownload.TYPE));
		return sDownload;
	}
	
	public String getDownloadProductName(String indentityId){
		synchronized (BaseDownloadDb.class) {
			LogM.l("getDownloadProductName");
			SQLiteDatabase sqlite = null;
			Cursor cursor = null;
			String name = "";
			try {
				sqlite = mDb.getReadableDatabase();
				
				String select = " select * from " + FontDownload.TABLE_NAME + " where " + FontDownload.INDENTITY_ID
						+ " = ?";
				cursor = sqlite.rawQuery(select, new String[] { indentityId });
				String jsonStr = null;
				if (cursor.moveToFirst()) {
					jsonStr = cursor.getString(cursor.getColumnIndex(FontDownload.EXTEN));
				}
				if(!TextUtils.isEmpty(jsonStr)){
					JSONObject json = new JSONObject(jsonStr);
					name = json.optString("bookName", "");
					int pos = name.lastIndexOf(BOOKNAME_SIGN);
					if (pos != -1) {
						name = name.substring(0, pos);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				closeCursor(cursor);
				closeSqlite(sqlite);
			}
			printLog("[getDownloadProductName: name="+ name +"]");
			return name;
		}
	}
	
	public String getStatus(String url){
		synchronized (BaseDownloadDb.class) {
			LogM.l("getStatus");
			SQLiteDatabase sqlite = null;
			String result = null;
			Cursor cursor = null;
			try {
				sqlite = mDb.getReadableDatabase();
				
				String select = " select * from " + FontDownload.TABLE_NAME + " where " + FontDownload.URL + " = ? ";
				cursor = sqlite.rawQuery(select, new String[]{url});
				if (cursor.moveToFirst()) {
					result = cursor.getString(cursor.getColumnIndex(FontDownload.STATUS));
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				closeCursor(cursor);
				closeSqlite(sqlite);
			}
//			printLog("[getStatus: result="+ result +"]");
			return result;
		}
	}
	
	public String getStatusByIndentityId(String indentityId){
		synchronized (BaseDownloadDb.class) {
			//LogM.l("getStatusByIndentityId");
			SQLiteDatabase sqlite = null;
			String result = null;
			Cursor cursor = null;
			try {
				sqlite = mDb.getReadableDatabase();
				
				String select = " select * from " + FontDownload.TABLE_NAME + " where " + FontDownload.INDENTITY_ID + " = ? ";
				cursor = sqlite.rawQuery(select, new String[]{indentityId});
				if (cursor.moveToFirst()) {
					result = cursor.getString(cursor.getColumnIndex(FontDownload.STATUS));
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				closeCursor(cursor);
				closeSqlite(sqlite);
			}
//			printLog("[getStatus: result="+ result +"]");
			return result;
		}
	}

	public boolean updateProgress(String url, long progress) {
		synchronized (BaseDownloadDb.class) {
			LogM.l("updateProgress");
			SQLiteDatabase sqlite = null;
			boolean ret = false;
			try {
				sqlite = mDb.getWritableDatabase();
				
				String update = " update " + FontDownload.TABLE_NAME + " set " + FontDownload.PROGRESS + " = ? where "
						+ FontDownload.URL + " = ? ";
				sqlite.execSQL(update, new Object[] { progress, url });
				ret = true;
			} catch (SQLException e) {
				e.printStackTrace();
				ret = false;
			}finally {
				closeSqlite(sqlite);
			}
//			printLog("[updateProgress: ret="+ ret +"]");
			return ret;
		}
	}
	
	public boolean updateProgressById(String indentityId, long progress) {
		synchronized (BaseDownloadDb.class) {
			LogM.l("updateProgressById");
			SQLiteDatabase sqlite = null;
			boolean ret = false;
			try {
				sqlite = mDb.getWritableDatabase();
				
				String update = " update " + FontDownload.TABLE_NAME + " set " + FontDownload.PROGRESS + " = ? where "
						+ FontDownload.INDENTITY_ID + " = ? ";
				sqlite.execSQL(update, new Object[] { progress, indentityId });
				ret = true;
			} catch (Exception e) {
				e.printStackTrace();
				ret = false;
			}finally {
				closeSqlite(sqlite);
			}
//			printLog("[updateProgress: ret="+ ret +"]");
			return ret;
		}
	}

	public boolean updateStatus(String url, String status) {
		synchronized (BaseDownloadDb.class) {
			LogM.l("updateStatus");
			SQLiteDatabase sqlite = null;
			boolean ret = false;
			try {
				sqlite = mDb.getWritableDatabase();
				
				String update = "update " + FontDownload.TABLE_NAME + " set " + FontDownload.STATUS + " = ? where "
						+ FontDownload.URL + " = ? ";
				sqlite.execSQL(update, new Object[] { status, url });
				ret = true;
			} catch (SQLException e) {
				e.printStackTrace();
				ret = false;
			} finally {
				closeSqlite(sqlite);
			}
//			printLog("[updateStatus: ret="+ ret +"]");
			return ret;
		}
	}
	
	public boolean updateStatusById(String indentityId, String status) {
		synchronized (BaseDownloadDb.class) {
			LogM.l("updateStatusById");
			SQLiteDatabase sqlite = null;
			boolean ret = false;
			try {
				sqlite = mDb.getWritableDatabase();
				
				String update = "update " + FontDownload.TABLE_NAME + " set " + FontDownload.STATUS + " = ? where "
						+ FontDownload.INDENTITY_ID + " = ? ";
				sqlite.execSQL(update, new Object[] { status, indentityId });
				ret = true;
			} catch (SQLException e) {
				e.printStackTrace();
				ret = false;
			} finally {
				closeSqlite(sqlite);
			}
//			printLog("[updateStatus: ret="+ ret +"]");
			return ret;
		}
	}

	public boolean updateAllStatusToOther(String status[], String otherStatus) {
		synchronized (BaseDownloadDb.class) {
			LogM.l("updateAllStatusToOther");
			SQLiteDatabase sqlite = null;
			boolean ret = false;
			try {
				sqlite = mDb.getWritableDatabase();
				
				String update = "update " + FontDownload.TABLE_NAME + " set " + FontDownload.STATUS + " = ? where "
						+ FontDownload.STATUS + " = ? or " + FontDownload.STATUS + " = ? ";
				sqlite.execSQL(update, new Object[] {otherStatus, status[0], status[1]});
				ret =  true;
			} catch (SQLException e) {
				e.printStackTrace();
				ret = false;
			} finally {
				closeSqlite(sqlite);
			}
			printLog("[updateAllStatusToOther: ret="+ ret +"]");
			return ret;
		}
	}
	
	public boolean updateTotalSize(String url, long totalSize) {
		synchronized (BaseDownloadDb.class) {
			LogM.l("updateTotalSize");
			SQLiteDatabase sqlite = null;
			boolean ret = false;
			try {
				sqlite = mDb.getWritableDatabase();
				
				String update = "update " + FontDownload.TABLE_NAME + " set " + FontDownload.TOTALSIZE + " = ? where "
						+ FontDownload.URL + " = ? ";
				sqlite.execSQL(update, new Object[] { totalSize, url });
				ret =  true;
			} catch (SQLException e) {
				e.printStackTrace();
				ret = false;
			} finally {
				closeSqlite(sqlite);
			}
			printLog("[updateTotalSize: ret="+ ret +"]");
			return ret;
		}
	}

	public boolean deleteDownloadShelf(String indentityId, boolean b) {
		synchronized (BaseDownloadDb.class) {
			LogM.l("deleteDownloadShelf");
			SQLiteDatabase sqlite = null;
			boolean ret = false;
			try {
				sqlite = mDb.getWritableDatabase();
				
				/*String delete = " delete from " + FontDownload.TABLE_NAME + " where " + FontDownload.INDENTITY_ID
						+ " = ?";
				sqlite.execSQL(delete, new Object[] { indentityId });*/
				exeDeleteOne(sqlite, indentityId);
				ret =  true;
			} catch (SQLException e) {
				e.printStackTrace();
				ret = false;
			} finally {
				closeSqlite(sqlite);
			}
			printLog("[deleteDownloadShelf: ret="+ ret +"]");
			return ret;
		}
	}
	
	public boolean deleteDownloadShelf(String url) {
		synchronized (BaseDownloadDb.class) {
			LogM.l("deleteDownloadShelf");
			SQLiteDatabase sqlite = null;
			boolean ret = false;
			try {
				sqlite = mDb.getWritableDatabase();
				
				String delete = " delete from " + FontDownload.TABLE_NAME + " where " + FontDownload.URL
						+ " = ?";
				sqlite.execSQL(delete, new Object[] { url });
				ret = true;
			} catch (SQLException e) {
				e.printStackTrace();
				ret = false;
			} finally {
				closeSqlite(sqlite);
			}
			printLog("[deleteDownloadShelf2: ret="+ ret +"]");
			return ret;
		}
	}

	public void deleteAllDownload(){
		synchronized (BaseDownloadDb.class) {
			LogM.l("deleteAllDownload");
			SQLiteDatabase sqlite = null;
			boolean ret = false;
			try {
				sqlite = mDb.getWritableDatabase();
				
				String delete = " delete from " + FontDownload.TABLE_NAME;
				sqlite.execSQL(delete);
				ret = true;
			} catch (SQLException e) {
				e.printStackTrace();
				ret = false;
			} finally {
				closeSqlite(sqlite);
			}
			printLog("[deleteAllDownload: ret="+ ret +"]");
		}
	}
	
	public void deleteDownloadByNotDType(DType dt){
		synchronized (BaseDownloadDb.class) {
			LogM.l("deleteDownloadByNotDType");
			SQLiteDatabase sqlite = null;
			boolean ret = false;
			try {
				sqlite = mDb.getWritableDatabase();
				
				String delete = " delete from " + FontDownload.TABLE_NAME;
				if(dt != null){
					if(dt == DType.FONT_FREE){
						delete += (" where " + FontDownload.TYPE + " != ?");
						sqlite.execSQL(delete, new Object[]{dt.name()});
					}
				} else {
					sqlite.execSQL(delete);
				}
				
				ret = true;
			} catch (SQLException e) {
				e.printStackTrace();
				ret = false;
			} finally {
				closeSqlite(sqlite);
			}
			printLog("[deleteAllDownload: ret="+ ret +"]");
		}
	}
	
	private void exeDeleteOne(SQLiteDatabase sqlite, String indentityId){
		String delete = " delete from " + FontDownload.TABLE_NAME + " where " + FontDownload.INDENTITY_ID
				+ " = ?";
		sqlite.execSQL(delete, new Object[] { indentityId });
	}
}
