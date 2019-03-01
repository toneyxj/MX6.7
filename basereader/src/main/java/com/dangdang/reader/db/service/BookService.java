package com.dangdang.reader.db.service;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.dangdang.reader.Constants;
import com.dangdang.reader.db.ShelfBookDBColumn;
import com.dangdang.reader.db.ShelfBookDBHelper;
import com.dangdang.reader.personal.domain.GroupType;
import com.dangdang.reader.personal.domain.GroupType.TypeColumn;
import com.dangdang.reader.personal.domain.ShelfBaseBook;

import org.json.JSONObject;

public abstract class BookService {

	protected ShelfBookDBHelper mDB;

	public BookService(Context context) {
		mDB = ShelfBookDBHelper.getInstance(context);
	}

	protected ContentValues getContentValues(ShelfBaseBook info) {
		ContentValues values = new ContentValues();
		values.put(ShelfBookDBColumn.BOOK_ID, info.getMediaId());
		values.put(ShelfBookDBColumn.BOOK_NAME, info.getTitle());
		values.put(ShelfBookDBColumn.BOOK_JSON, info.getBookJson());
		values.put(ShelfBookDBColumn.USER_ID, info.getUserId());
		values.put(ShelfBookDBColumn.USER_NAME, info.getUserName());
		values.put(ShelfBookDBColumn.AUTHOR, info.getAuthorPenname());
		
		return values;
	}

	protected JSONObject getBook(Cursor cursor, ShelfBaseBook info, boolean withDesc) {
		info.setId(cursor.getInt(cursor.getColumnIndex(ShelfBookDBColumn.ID)));
		info.setMediaId(cursor.getString(cursor
				.getColumnIndex(ShelfBookDBColumn.BOOK_ID)));
		info.setAuthorPenname(cursor.getString(cursor
				.getColumnIndex(ShelfBookDBColumn.AUTHOR)));
		String str = cursor.getString(cursor
				.getColumnIndex(ShelfBookDBColumn.BOOK_JSON));
		info.setBookJson(str);
		String name = cursor.getString(cursor.getColumnIndex(ShelfBookDBColumn.BOOK_NAME));
		JSONObject obj = null;
		try {
			obj = new JSONObject(str);
			info.setBookSize(obj.optLong(Constants.JSON_SIZE, 0));
			info.setSaleId(obj.optString(Constants.JSON_SALEID, ""));
			if(withDesc)
				info.setDescs(obj.optString(Constants.JSON_DESC, ""));
			if (name == null || name.length() == 0) {
				name = obj.optString("bookName", "");
			}
		} catch (Exception e) {
			e.printStackTrace();
			obj = null;
		}
		info.setTitle(name);
		info.setUserId(cursor.getString(cursor
				.getColumnIndex(ShelfBookDBColumn.USER_ID)));
		info.setUserName(cursor.getString(cursor
				.getColumnIndex(ShelfBookDBColumn.USER_NAME)));

		info.setExpColumn1(cursor.getString(cursor
				.getColumnIndex(ShelfBookDBColumn.ExpColumn1)));
		info.setExpColumn2(cursor.getString(cursor
				.getColumnIndex(ShelfBookDBColumn.ExpColumn2)));
		info.setExpColumn3(cursor.getString(cursor
				.getColumnIndex(ShelfBookDBColumn.ExpColumn3)));
		// 获取类别id
		GroupType type = new GroupType();
		type.setId(cursor.getInt(cursor.getColumnIndex(ShelfBookDBColumn.GROUP_ID)));
		info.setGroupType(type);

		return obj;
	}

	protected void getBookType(SQLiteDatabase sqlite, String sql, ShelfBaseBook book) throws SQLException{
		GroupType type = getBookType(sqlite, sql);
		book.setCategorys(type.getName());
		book.setGroupType(type);
	}
	
	private GroupType getBookType(SQLiteDatabase sqlite, String sql) throws SQLException{
		GroupType type = null;
		Cursor cursor = sqlite.rawQuery(sql, null);		
		if (cursor != null && cursor.moveToFirst()) {
			type = getBookType(cursor);
		}
		closeCursor(cursor);
		if(type == null)
			type = new GroupType();
		return type;
	}
	
	protected GroupType getBookType(Cursor cursor){
		GroupType type = new GroupType();
		type.setId(cursor.getInt(cursor.getColumnIndex(TypeColumn.ID)));
		type.setName(cursor.getString(cursor.getColumnIndex(TypeColumn.NAME)));
		type.setCreateTime(cursor.getLong(cursor.getColumnIndex(TypeColumn.CREATE_TIME)));
		return type;
	}
	
	protected void closeCursor(Cursor cursor) {
		try {
			if (cursor != null) {
				cursor.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	protected void closeSqlite(SQLiteDatabase sqlite){
		try{
			if(mDB != null)
				mDB.closeSqlite(sqlite);
		}catch(Exception e){
			e.printStackTrace();
		}		
	}
}
