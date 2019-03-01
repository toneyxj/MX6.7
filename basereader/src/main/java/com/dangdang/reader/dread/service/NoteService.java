package com.dangdang.reader.dread.service;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.dangdang.reader.cloud.CloudConstant;
import com.dangdang.reader.cloud.NoteData;
import com.dangdang.reader.cloud.Status;
import com.dangdang.reader.dread.data.BookMark.Column;
import com.dangdang.reader.dread.data.BookNote;
import com.dangdang.reader.dread.data.BookNote.NoteColumn;
import com.dangdang.reader.dread.data.BookNoteDataWrapper;
import com.dangdang.reader.dread.data.NoteKey;
import com.dangdang.reader.dread.data.ReadInfo;
import com.dangdang.reader.dread.util.DreaderConstants;
import com.dangdang.zframework.utils.StringUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

public class NoteService extends BaseService {

	//private AtomicBoolean mChange = new AtomicBoolean(false);
	
	public NoteService(Context context) {
		super(context);
	}

	public synchronized long saveNote(BookNote note) {
		
		long id = -1;
		//boolean result = false;
		SQLiteDatabase db = mDB.getWritableDatabase();
		try {
			printLog(" saveNote start ");
			//db.beginTransaction();
			//for(int i = 0; i < 1000; i++){
				id = execInsertSql(db, note);
			//}
			//db.setTransactionSuccessful();
			printLog(" saveNote end ");
			
			/*String sql = "INSERT INTO " + NoteColumn.TableName + " ( "
					   + NoteColumn.BookId + ", "
					   + NoteColumn.BookPath + ", "
					   + NoteColumn.ChapterName + ", "
					   + NoteColumn.ChapterIndex + ", "
					   + NoteColumn.SourceText + ", "
					   + NoteColumn.NoteStart + ", "
					   + NoteColumn.NoteEnd + ", "
					   + NoteColumn.NoteText + ", "
					   + NoteColumn.NoteTime + ", "
					   + NoteColumn.IsBought 
					   + " ) values (?,?,?,?,?,?,?,?,?,?)";
			
			Object[] obj = new Object[] {note.bookId, note.bookPath, note.chapterName, 
										 note.chapterIndex, note.sourceText, note.noteStart, 
										 note.noteEnd, note.noteText, note.noteTime, note.isBought};
			db.execSQL(sql, obj);*/
			//result = true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			//db.endTransaction();
			closeSqliteDb(db);
		}
		
		return id;
	}

	private long execInsertSql(SQLiteDatabase db, BookNote note) {
		
		ContentValues values = new ContentValues();
		values.put(NoteColumn.BookId, note.bookId);
		values.put(NoteColumn.BookPath, note.bookPath);
		values.put(NoteColumn.ChapterName, note.chapterName);
		values.put(NoteColumn.ChapterIndex, note.chapterIndex);
		values.put(NoteColumn.SourceText, note.sourceText);
		values.put(NoteColumn.NoteStart, note.noteStart);
		values.put(NoteColumn.NoteEnd, note.noteEnd);
		values.put(NoteColumn.NoteText, note.noteText);
		values.put(NoteColumn.NoteTime, note.noteTime);
		values.put(NoteColumn.IsBought, note.isBought);
		values.put(NoteColumn.ExpColumn1, note.status);
		values.put(NoteColumn.ExpColumn2, note.cloudStatus);
		//values.put(NoteColumn.ExpColumn3, note.modifyTime);
		values.put(NoteColumn.ModVersion, note.bookModVersion);
		values.put(NoteColumn.ExpColumn4, note.drawLineColor);
		long id = db.insert(NoteColumn.TableName, null, values);
		
		return id;
	}
	
	/**
	 * 同时笔记的云状态改为未同步
	 * @param note
	 * @return
	 */
	public synchronized boolean updateNote(BookNote note) {
		
		boolean result = false;
		SQLiteDatabase db = mDB.getWritableDatabase();
		try {
			execUpdateSqlNotContainModifyTime(db, note);
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			closeSqliteDb(db);
		}
		
		return result;
	}

	/**
	 * 执行修改笔记sql，包括modifyTime
	 * @param db
	 * @param note
	 */
	private void execUpdateSql(SQLiteDatabase db, BookNote note) {
		String sql = "update " + NoteColumn.TableName + " set " + NoteColumn.NoteTime + " = ? , " 
				   + NoteColumn.NoteText + " = ? , " +  NoteColumn.ExpColumn1 + " = ? , " 
				   + NoteColumn.ExpColumn2 + " = ? , " + NoteColumn.ExpColumn3 + " = ? , "
                   + NoteColumn.ExpColumn4  + " = ? "
				   + " where " + NoteColumn.BookId + " = ? and "
				   + NoteColumn.IsBought + " = ? and "
				   + NoteColumn.ChapterIndex + " = ? and "
				   + NoteColumn.NoteStart + " = ? and " 
				   + NoteColumn.NoteEnd + " = ? and "
				   + NoteColumn.ModVersion + " = ? ";
		
		long noteTime = note.getNoteTime();
		String content = note.getNoteText();
		String status = note.getStatus();
		String cloudStatus = note.getCloudStatus();
		String modifyTime = note.getModifyTime();
		String bookid = note.getBookId();
		int isBought = note.getIsBought();
		int chapterindex = note.getChapterIndex(); 
		int startindex = note.getNoteStart();
		int endindex = note.getNoteEnd();
		String bookModVersion = note.bookModVersion;
		int drawLineColor = note.drawLineColor;
		
		db.execSQL(sql, new Object[]{noteTime, content, status, cloudStatus, modifyTime, drawLineColor, bookid, isBought,
				chapterindex, startindex, endindex, bookModVersion});
	}
	
	/**
	 * 执行修改笔记sql，不包括modifyTime
	 * @param db
	 * @param note
	 */
	private void execUpdateSqlNotContainModifyTime(SQLiteDatabase db, BookNote note) {
		String sql = "update " + NoteColumn.TableName + " set " + NoteColumn.NoteTime + " = ? , " 
				   + NoteColumn.NoteText + " = ? , " +  NoteColumn.ExpColumn1 + " = ? , " 
				   +  NoteColumn.ExpColumn2  + " = ? , " + NoteColumn.ModVersion + " = ? , " + NoteColumn.ExpColumn4 + " = ? "
				   + " where " + NoteColumn.BookId + " = ? and "
				   + NoteColumn.IsBought + " = ? and "
				   + NoteColumn.ChapterIndex + " = ? and "
				   + NoteColumn.NoteStart + " = ? and " 
				   + NoteColumn.NoteEnd + " = ? ";
		
		long noteTime = note.getNoteTime();
		String content = note.getNoteText();
		String status = note.getStatus();
		String cloudStatus = note.getCloudStatus();
		String bookid = note.getBookId();
		int isBought = note.getIsBought();
		int chapterindex = note.getChapterIndex(); 
		int startindex = note.getNoteStart();
		int endindex = note.getNoteEnd();
		String bookModVersion = note.bookModVersion;
		int drawLineColor = note.drawLineColor;
		
		db.execSQL(sql, new Object[]{noteTime, content, status, cloudStatus, bookModVersion, drawLineColor, bookid, isBought,
				chapterindex, startindex, endindex});
	}
	
	/*private void execUpdateSqlAndUpdateCloudStatus(SQLiteDatabase db, BookNote note) {
		String sql = "update " + NoteColumn.TableName + " set " + NoteColumn.NoteTime + " = ? , " 
				   + NoteColumn.NoteText + " = ? , " +  NoteColumn.ExpColumn1 + " = ? , " 
				   + NoteColumn.ExpColumn2 + " = ? , " + NoteColumn.ExpColumn3 + " = ? "
				   + " where " + NoteColumn.BookId + " = ? and "
				   + NoteColumn.IsBought + " = ? and "
				   + NoteColumn.ChapterIndex + " = ? and "
				   + NoteColumn.NoteStart + " = ? and " 
				   + NoteColumn.NoteEnd + " = ? ";
		
		long noteTime = note.getNoteTime();
		String content = note.getNoteText();
		String status = note.getStatus();
		String cloudStatus = note.getCloudStatus();
		String modifyTime = note.getModifyTime();
		String bookid = note.getBookId();
		int isBought = note.getIsBought();
		int chapterindex = note.getChapterIndex(); 
		int startindex = note.getNoteStart();
		int endindex = note.getNoteEnd();
		
		db.execSQL(sql, new Object[]{noteTime, content, status, cloudStatus, modifyTime, bookid, isBought,
				chapterindex, startindex, endindex});
	}*/
	
	
	/*public boolean updateNoteById(int id, String content) {
		
		boolean result = false;
		SQLiteDatabase db = mDB.getWritableDatabase();
		try {
			String sql = "update " + NoteColumn.TableName + " set " + NoteColumn.NoteText
					   + " = ? where " + NoteColumn.Id + " = ?";
			db.execSQL(sql, new Object[]{content, id});
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			closeSqliteDb(db);
		}
		
		return result;
	}*/
	
	public synchronized boolean deleteBookNoteByBookId(String bookid, int isBought) {
		
		boolean result = false;
		SQLiteDatabase db = mDB.getWritableDatabase();
		try {
			String sql = "delete from " + NoteColumn.TableName + " where " + NoteColumn.BookId + " = ? and"
					+ NoteColumn.IsBought + " = ? ";
			db.execSQL(sql, new Object[]{bookid, isBought});
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			closeSqliteDb(db);
		}
		
		return result;
	}
	
	public synchronized boolean deleteBookNote(String bookid, int isBought, int chapterindex, int startindex, int endindex) {
		
		boolean result = false;
		SQLiteDatabase db = mDB.getWritableDatabase();
		try {
			execDeleteSql(db, bookid, isBought, chapterindex, startindex, endindex);
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			closeSqliteDb(db);
		}
		
		return result;
	}

	private void execDeleteSql(SQLiteDatabase db, String bookid, int isBought,
			int chapterindex, int startindex, int endindex) {
		
		String sql = "delete from " + NoteColumn.TableName + " where " 
				   + NoteColumn.BookId + " = ? AND "
				   + NoteColumn.IsBought + " = ? AND "
				   + NoteColumn.ChapterIndex + " = ? AND "
				   + NoteColumn.NoteStart + " = ? AND " 
				   + NoteColumn.NoteEnd + " = ? ";
		db.execSQL(sql, new Object[]{bookid, isBought, chapterindex, startindex, endindex});
	}
	
	public synchronized boolean deleteBookNoteById(int id) {
		boolean result = false;
		SQLiteDatabase db = mDB.getWritableDatabase();
		try {
			String sql = "delete from " + NoteColumn.TableName + " where " + NoteColumn.Id + " = ?";
			db.execSQL(sql, new Object[]{id});
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			closeSqliteDb(db);
		}
		return result;
	}
	
	private BookNote getBookNote(Cursor cursor) {
		
		BookNote note = new BookNote();
		note.id = cursor.getInt(cursor.getColumnIndex(NoteColumn.Id));
		note.bookId = cursor.getString(cursor.getColumnIndex(NoteColumn.BookId));
		note.bookPath = cursor.getString(cursor.getColumnIndex(NoteColumn.BookPath));
		note.chapterName = cursor.getString(cursor.getColumnIndex(NoteColumn.ChapterName));
		note.chapterIndex = cursor.getInt(cursor.getColumnIndex(NoteColumn.ChapterIndex));
		note.sourceText = cursor.getString(cursor.getColumnIndex(NoteColumn.SourceText));
		note.noteStart = cursor.getInt(cursor.getColumnIndex(NoteColumn.NoteStart));
		note.noteEnd = cursor.getInt(cursor.getColumnIndex(NoteColumn.NoteEnd));
		note.noteText = cursor.getString(cursor.getColumnIndex(NoteColumn.NoteText));
		note.noteTime = cursor.getLong(cursor.getColumnIndex(NoteColumn.NoteTime));
		note.isBought = cursor.getInt(cursor.getColumnIndex(NoteColumn.IsBought));
		note.bookModVersion = cursor.getString(cursor.getColumnIndex(NoteColumn.ModVersion));
		if (note.bookModVersion == null || note.bookModVersion.isEmpty())
			note.bookModVersion = DreaderConstants.BOOK_MODIFY_VERSION;
		
		/*note.status = cursor.getString(cursor.getColumnIndex(NoteColumn.ExpColumn1));
		note.cloudStatus = cursor.getString(cursor.getColumnIndex(NoteColumn.ExpColumn2));
		note.modifyTime = cursor.getString(cursor.getColumnIndex(NoteColumn.ExpColumn3));*/
		
		/**
		 * TODO 兼容老数据，增加默认值
		 */
		String status = cursor.getString(cursor.getColumnIndex(NoteColumn.ExpColumn1));
		if(TextUtils.isEmpty(status)){
			status = String.valueOf(Status.COLUMN_NEW);//默认为插入状态
		}
		note.status = status;
		
		String cloudStatus = cursor.getString(cursor.getColumnIndex(NoteColumn.ExpColumn2));
		if(TextUtils.isEmpty(cloudStatus)){
			cloudStatus = String.valueOf(Status.CLOUD_NO);//默认为未传到云端
		}
		note.cloudStatus = cloudStatus;
		
		String modifyTime = cursor.getString(cursor.getColumnIndex(NoteColumn.ExpColumn3));
		if(TextUtils.isEmpty(modifyTime)){
			modifyTime = String.valueOf(0);
		}
		note.modifyTime = modifyTime;

		String strDrawLineColor = cursor.getString(cursor.getColumnIndex(NoteColumn.ExpColumn4));
		if(TextUtils.isEmpty(strDrawLineColor)){
			note.drawLineColor = BookNote.NOTE_DRAWLINE_COLOR_RED;//默认为插入状态
		}
		else
			note.drawLineColor = StringUtil.parseInt(strDrawLineColor, BookNote.NOTE_DRAWLINE_COLOR_RED);

		return note;
	}

	/*public List<BookNote> getBookNoteListByBookId(String bookid) {
		
		List<BookNote> result = new ArrayList<BookNote>();
		SQLiteDatabase db = mDB.getReadableDatabase();
		Cursor cursor = null;
		try {
			String sql = "select * from " + NoteColumn.TableName + " where "
					   + NoteColumn.BookId + " = ? order by " + NoteColumn.ChapterIndex+","+ NoteColumn.NoteStart + " asc, "+NoteColumn.NoteTime+ " desc ";
			cursor = db.rawQuery(sql, new String[]{bookid});
			printLog(" getBookNoteListByBookId getCount = " + cursor.getCount());
			while (cursor.moveToNext()) {
				result.add(getBookNote(cursor));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			closeCursor(cursor);
			closeSqliteDb(db);
		}
		return result;
	}*/
	
	public BookNote getBookNoteById(int id) {
		BookNote note = null;
		SQLiteDatabase db = mDB.getReadableDatabase();
		Cursor cursor = null;
		try {
			String sql = "select * from " + NoteColumn.TableName + " where " + NoteColumn.Id + " = " + id;
			cursor = db.rawQuery(sql, null);
			if (cursor != null && cursor.moveToNext()) {
				note = getBookNote(cursor);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			closeCursor(cursor);
			closeSqliteDb(db);
		}
		return note;
	}
	
	public synchronized List<BookNote> getBookNoteListByBookId(String bookid, int isbought) {
		
		printLog(" getBookNoteList start ");
		List<BookNote> result = new ArrayList<BookNote>();
		SQLiteDatabase db = mDB.getReadableDatabase();
		Cursor cursor = null;
		try {
			String sql = "select * from " + NoteColumn.TableName + " where "
					   + NoteColumn.BookId + " = ? AND "
					   + NoteColumn.IsBought + " = ? AND "
					   + NoteColumn.ExpColumn1 + " != ? order by " + NoteColumn.ChapterIndex+","+ NoteColumn.NoteStart + " asc, "+NoteColumn.NoteTime+ " desc ";
			cursor = db.rawQuery(sql, new String[]{bookid, String.valueOf(isbought), String .valueOf(Status.COLUMN_DELETE)});
			while (cursor.moveToNext()) {
				result.add(getBookNote(cursor));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			closeCursor(cursor);
			closeSqliteDb(db);
		}
		printLog(" getBookNoteList end result.size() = " + result.size());
		return result;
	}

	public synchronized List<BookNoteDataWrapper> getBookNoteWrapperListByBookId(String bookid, int isbought) {

		List<BookNoteDataWrapper> result = new ArrayList<BookNoteDataWrapper>();
		SQLiteDatabase db = mDB.getReadableDatabase();
		Cursor cursor = null;
		try {
			String sql = "select * from " + NoteColumn.TableName + " where " + NoteColumn.BookId + " = ? AND " + NoteColumn.IsBought + " = ? AND "
					+ NoteColumn.ExpColumn1 + " != ? order by " + NoteColumn.ChapterIndex + "," + NoteColumn.NoteStart + " asc, " + NoteColumn.NoteTime
					+ " desc ";
			cursor = db.rawQuery(sql, new String[] { bookid, String.valueOf(isbought), String.valueOf(Status.COLUMN_DELETE) });
			int lastChapterIndex = -1;
			while (cursor.moveToNext()) {
				BookNote note = getBookNote(cursor);
				if (note.chapterIndex != lastChapterIndex) {
					lastChapterIndex = note.chapterIndex;
					note.isChapterHead = true;
					BookNoteDataWrapper chapterName = new BookNoteDataWrapper();
					chapterName.chapterName = note.chapterName;
					chapterName.chapterIndex = note.chapterIndex;
					result.add(chapterName);
				}
				BookNoteDataWrapper data = new BookNoteDataWrapper();
				data.data = note;
				result.add(data);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			closeCursor(cursor);
			closeSqliteDb(db);
		}
		printLog(" getBookNoteWrapperList end result.size() = " + result.size());
		return result;
	}

	public synchronized int getBookNoteCount(String bookid, int isbought){
		
		SQLiteDatabase db = mDB.getReadableDatabase();
		Cursor cursor = null;
		int count = 0;
		try {
			String sql = "select * from " + NoteColumn.TableName + " where "
					   + NoteColumn.BookId + " = ? AND "
					   + NoteColumn.IsBought + " = ? ";
			cursor = db.rawQuery(sql, new String[]{bookid, String.valueOf(isbought)});
			count = cursor.getCount();
			printLog(" getBookNoteCount " + count);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			closeCursor(cursor);
			closeSqliteDb(db);
		}
		return count;
	}
	
	/**
	 * 获取未同步的笔记列表
	 * 拼成json串
	 * @param bookid
	 * @param isbought
	 * @param userId
	 * @return
	 */
	public synchronized NoteData getNotSyncBookNotes(String bookid, int isbought, String userId){
		
		NoteData noteData = new NoteData();
		SQLiteDatabase db = mDB.getReadableDatabase();
		Cursor cursor = null;
		try {
			String sql = "select * from " + NoteColumn.TableName + " where "
					   + NoteColumn.BookId + " = ? AND "
					   + NoteColumn.IsBought + " = ? AND "
					   + Column.ExpColumn2 +" != ? ";
			String cloudStatus = String.valueOf(Status.CLOUD_YES);
			cursor = db.rawQuery(sql, new String[]{bookid, String.valueOf(isbought), cloudStatus});
			
			//List<BookNote> notes = new ArrayList<BookNote>();
			BookNote bookNote = null;
			JSONArray jsonArr = new JSONArray();
			JSONObject jsonObj = null;
			while (cursor.moveToNext()) {
				bookNote = getBookNote(cursor);
				jsonObj = convertCloudJson(bookNote, userId, bookid);
				
				if(jsonObj != null){
					jsonArr.put(jsonObj);
					//notes.add(bookNote);
				}
			}
			//noteData.setNotes(notes);
			noteData.setNotesJson(jsonArr);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			closeCursor(cursor);
			closeSqliteDb(db);
		}
		return noteData;
	}
	
	/**
	 *  获取未同步的笔记列表拼成Json串(个人中心删除笔记)
	 * @return
	 */
//	public synchronized NoteData getNotSyncBookNotes(PersonalBookNoteInfo bookNote, String userId){
//		NoteData noteData = new NoteData();
//		JSONArray jsonArr = new JSONArray();
//		JSONObject jsonObj = new JSONObject();
//		try {
//			long operateTime = 0;
//			long modifyTime = 0;
//			try {
//				operateTime = bookNote.getClientOperateTime()/ 1000;
//				if(!TextUtils.isEmpty(bookNote.getModifyTime()+"")){
//					modifyTime = Long.valueOf(bookNote.getModifyTime()) / 1000;
//				}
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//			jsonObj.put(CloudConstant.JSONK_NOTE_CUSTID, userId);
//			jsonObj.put(CloudConstant.JSONK_NOTE_PRODUCTID, bookNote.getProductId());
//			jsonObj.put(CloudConstant.JSONK_NOTE_CHAPTERINDEX, bookNote.getChaptersIndex());
//			jsonObj.put(CloudConstant.JSONK_NOTE_START_ELEMENTINDEX, bookNote.getCharacterStartIndex());
//			jsonObj.put(CloudConstant.JSONK_NOTE_END_ELEMENTINDEX, bookNote.getCharacterEndIndex());
//			jsonObj.put(CloudConstant.JSONK_NOTE_STATUS, String.valueOf(Status.COLUMN_DELETE));
//			jsonObj.put(CloudConstant.JSONK_NOTE_NOTEINFO, bookNote.getNoteInfo());
//			jsonObj.put(CloudConstant.JSONK_NOTE_CALLOUTINFO, bookNote.getCallOutInfo());
//			jsonObj.put(CloudConstant.JSONK_NOTE_OPERATETIME, operateTime);
//			jsonObj.put(CloudConstant.JSONK_NOTE_MODIFYTIME, modifyTime);
//			jsonObj.put(CloudConstant.JSONK_NOTE_BOOKMODVERSION, bookNote.getBookModVersion());
//			jsonObj.put(CloudConstant.JSONK_NOTE_DRAWLINECOLOR, bookNote.getDrawLineColor());
//
//			if(jsonObj != null){
//				jsonArr.put(jsonObj);
//				//notes.add(bookNote);
//			}
//
//			noteData.setNotesJson(jsonArr);
//
//		} catch (JSONException e) {
//			e.printStackTrace();
//			jsonObj = null;
//		}
//		return noteData;
//	}
	
	private synchronized JSONObject convertCloudJson(BookNote bookNote, String userId, String productId){
		
		JSONObject jsonObj = new JSONObject();
		try {
			/**
			 * 服务端要求时间单位为秒. 
			 */
			long operateTime = 0;
			long modifyTime = 0;
			try {
				operateTime = bookNote.getNoteTime() / 1000;
				if(!TextUtils.isEmpty(bookNote.getModifyTime())){
					modifyTime = Long.valueOf(bookNote.getModifyTime()) / 1000;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			jsonObj.put(CloudConstant.JSONK_NOTE_CUSTID, userId);
			jsonObj.put(CloudConstant.JSONK_NOTE_PRODUCTID, productId);
			jsonObj.put(CloudConstant.JSONK_NOTE_CHAPTERINDEX, bookNote.getChapterIndex());
			jsonObj.put(CloudConstant.JSONK_NOTE_START_ELEMENTINDEX, bookNote.getNoteStart());
			jsonObj.put(CloudConstant.JSONK_NOTE_END_ELEMENTINDEX, bookNote.getNoteEnd());
			jsonObj.put(CloudConstant.JSONK_NOTE_STATUS, bookNote.getStatus());
			jsonObj.put(CloudConstant.JSONK_NOTE_NOTEINFO, bookNote.getNoteText());
			jsonObj.put(CloudConstant.JSONK_NOTE_CALLOUTINFO, bookNote.getSourceText());
			jsonObj.put(CloudConstant.JSONK_NOTE_OPERATETIME, operateTime);
			jsonObj.put(CloudConstant.JSONK_NOTE_MODIFYTIME, modifyTime);
			jsonObj.put(CloudConstant.JSONK_NOTE_BOOKMODVERSION, bookNote.getBookModVersion());
			jsonObj.put(CloudConstant.JSONK_NOTE_DRAWLINECOLOR, bookNote.getDrawLineColor());
			
		} catch (JSONException e) {
			e.printStackTrace();
			jsonObj = null;
		}
		
		return jsonObj;
	}
	
	public synchronized Map<NoteKey, BookNote> getBookNotes(String bookid, int isbought){
		
		Map<NoteKey, BookNote> bookNotes = new Hashtable<NoteKey, BookNote>();
		SQLiteDatabase db = mDB.getReadableDatabase();
		Cursor cursor = null;
		try {
			String sql = "select * from " + NoteColumn.TableName + " where "
					   + NoteColumn.BookId + " = ? AND "
					   + NoteColumn.IsBought + " = ? order by " + NoteColumn.ChapterIndex+","+ NoteColumn.NoteStart + " asc, "+NoteColumn.NoteTime+ " desc ";
			cursor = db.rawQuery(sql, new String[]{bookid, String.valueOf(isbought)});
			BookNote bookNote = null;
			while (cursor.moveToNext()) {
				bookNote = getBookNote(cursor);
				bookNotes.put(getNoteKey(bookNote), bookNote);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			closeCursor(cursor);
			closeSqliteDb(db);
		}
		return bookNotes;
	}
	
	private NoteKey getNoteKey(BookNote bookNote){
		
		NoteKey key = new NoteKey();
		key.setProductId(bookNote.bookId);
		key.setChapterIndex(bookNote.chapterIndex);
		key.setBookModVersion(bookNote.getBookModVersion());
		key.setStartElementIndex(bookNote.noteStart);
		key.setEndElementIndex(bookNote.noteEnd);
		
		return key;
	}
	
	/**
	 * 修改笔记状态
	 * 同时云状态置为非同步
	 * @param noteId
	 * @param status 增、删、改
	 * @return
	 */
	public synchronized boolean updateNoteStatus(final int noteId, final int status, final long nowTime){
		
		boolean result = false;
		SQLiteDatabase db = mDB.getWritableDatabase();
		try {
			String sql = "update " + NoteColumn.TableName + " set " + NoteColumn.NoteTime + " = ? , " 
					   + NoteColumn.ExpColumn1 + " = ? , " + NoteColumn.ExpColumn2 + " = ? , " + NoteColumn.ExpColumn3 
					   + " = ? where " + NoteColumn.Id + " = ? ";
			
			long noteTime = nowTime;
			long modifyTime = nowTime;
			String cloudStatus = String.valueOf(Status.CLOUD_NO);
			db.execSQL(sql, new Object[]{noteTime, status, cloudStatus, modifyTime, noteId});
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			closeSqliteDb(db);
		}
		return result;
	}
	
	/**
	 * 修改到数据库的notes的云状态都为已同步
	 * @param notes
	 * @return
	 */
	public synchronized boolean performMergeNoteResult(final List<BookNote> notes, Map<NoteKey, BookNote> cacheNotes){
		
		boolean result = false;
		
		SQLiteDatabase db = mDB.getWritableDatabase();
		try {
			db.beginTransaction();
			final List<BookNote> mergeNotesResult = notes;
			BookNote bookNote = null;
			for(int i = 0, len = mergeNotesResult.size(); i < len; i++){
				
				bookNote = mergeNotesResult.get(i);
				printLog(" performMergeNote status = " + bookNote.getStatus());
				String status = bookNote.getStatus();
				if(isNewStatus(status)){
					long id = execInsertSql(db, bookNote);
					NoteKey noteKey = NoteKey.convert(bookNote);
					BookNote cacheNote = cacheNotes.get(noteKey);
					if(cacheNote != null){
						cacheNote.id = (int) id;//设置主键id，点击笔记小按钮时有用
						//cacheNotes.put(noteKey, value);
					}
					
				} else if(isUpdateStatus(status)){
					execUpdateSql(db, bookNote);
					//execUpdateSqlAndUpdateCloudStatus(db, bookNote);
				} else if(isDeleteStatus(status)){
					
					String bookid = bookNote.getBookId();
					int isBought = ReadInfo.BSTATUS_FULL;
					int chapterIndex = bookNote.getChapterIndex();
					int startIndex = bookNote.getNoteStart();
					int endIndex = bookNote.getNoteEnd();
					
					execDeleteSql(db, bookid, isBought, chapterIndex, startIndex, endIndex);
				}
			}
			db.setTransactionSuccessful();
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.endTransaction();
			closeSqliteDb(db);
		}
		return result;
	}
	
	/**
	 * 更改marks的云状态为cloudStatus
	 * @param cloudStatus Status.CLOUD_YES,Status.CLOUD_NO
	 * @return
	 */
	public synchronized boolean updateNotesCloudStatus(final List<BookNote> notes, final int cloudStatus){
		
		boolean result = false;
		
		SQLiteDatabase db = mDB.getWritableDatabase();
		try {
			db.beginTransaction();
			final List<BookNote> mergeNotesResult = notes;
			BookNote bookNote = null;
			for(int i = 0, len = mergeNotesResult.size(); i < len; i++){
				
				bookNote = mergeNotesResult.get(i);
				printLog(" updateNotesCloud status = " + bookNote.getStatus());
				
				String sql = "update " + NoteColumn.TableName + " set " + NoteColumn.ExpColumn2 + 
						" = ? where " + NoteColumn.Id +" = ? ";
				
				String tCloudStatus = String.valueOf(cloudStatus);
				int noteId = bookNote.getId();
				db.execSQL(sql, new Object[]{tCloudStatus, noteId});
			}
			db.setTransactionSuccessful();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.endTransaction();
			closeSqliteDb(db);
		}
		return result;
	} 
	
	/**
	 * 更改marks的云状态为cloudStatus, 同时修改modifyTime
	 * @param cloudStatus Status.CLOUD_YES,Status.CLOUD_NO
	 * @return
	 */
	public synchronized boolean updateNotesCloudStatus(final JSONArray notes, final int cloudStatus, final long modifyTime){
		
		printLog(" updateNotesCloudStatus start ");
		boolean result = false;
		SQLiteDatabase db = mDB.getWritableDatabase();
		try {
			db.beginTransaction();
			JSONObject bookNote = null;
			for(int i = 0, len = notes.length(); i < len; i++){
				
				bookNote = notes.getJSONObject(i);
				printLog(" updateNotesCloud status = " + bookNote.optString(CloudConstant.JSONK_NOTE_STATUS));
				
				String sql = "update " + NoteColumn.TableName + " set " + NoteColumn.ExpColumn2 + " = ?," 
				   + NoteColumn.ExpColumn3 + " = ? where " + NoteColumn.BookId + " = ? and "
				   + NoteColumn.IsBought + " = ? and "
				   + NoteColumn.ChapterIndex + " = ? and "
				   + NoteColumn.NoteStart + " = ? and " 
				   + NoteColumn.NoteEnd + " = ? ";
				
				String tCloudStatus = String.valueOf(cloudStatus);
				long tModifyTime = modifyTime;
				String productId = bookNote.optString(CloudConstant.JSONK_NOTE_PRODUCTID);
				int isBought = ReadInfo.BSTATUS_FULL;
				int chapterIndex = bookNote.optInt(CloudConstant.JSONK_NOTE_CHAPTERINDEX);
				int startElementIndex = bookNote.optInt(CloudConstant.JSONK_NOTE_START_ELEMENTINDEX);
				int endElementIndex = bookNote.optInt(CloudConstant.JSONK_NOTE_END_ELEMENTINDEX);
				db.execSQL(sql, new Object[]{tCloudStatus, tModifyTime, productId, isBought, chapterIndex, startElementIndex, endElementIndex});
			}
			db.setTransactionSuccessful();
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.endTransaction();
			closeSqliteDb(db);
		}
		printLog(" updateNotesCloudStatus end ");
		return result;
	} 
	
	public void copyTryNoteToFull(String bookid, int tryStatus, int fullStatus){
		SQLiteDatabase db = mDB.getWritableDatabase();
		Cursor cursor = null;
		try {
			String sql = "select * from " + NoteColumn.TableName + " where "
					   + NoteColumn.BookId + " = ? AND "
					   + NoteColumn.IsBought + " = ? ";
			
			cursor = db.rawQuery(sql, new String[]{bookid, String.valueOf(fullStatus)});
			if(cursor.getCount() > 0){
				printLog(" already exist full note data ");
				return;
			}
			db.beginTransaction();
			cursor = db.rawQuery(sql, new String[]{bookid, String.valueOf(tryStatus)});
			BookNote bookNote = null;
			while (cursor.moveToNext()) {
				bookNote = getBookNote(cursor);
				bookNote.setIsBought(fullStatus);
				execInsertSql(db, bookNote);
			}
			db.setTransactionSuccessful();
			db.endTransaction();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			closeCursor(cursor);
			closeSqliteDb(db);
		}
	}
	
	public void moveTryNoteToFull(String bookid, int tryStatus, int fullStatus){
		SQLiteDatabase db = mDB.getWritableDatabase();
		Cursor cursor = null;
		try {
			db.beginTransaction();

			String updatesql = " update " + NoteColumn.TableName + 
					" set " + NoteColumn.IsBought + " = ? where " 
					+ NoteColumn.BookId + " = ? AND "
					+ NoteColumn.IsBought + " = ? ";
			db.execSQL(updatesql, new Object[]{fullStatus, bookid, tryStatus});
			
			db.setTransactionSuccessful();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.endTransaction();
			closeCursor(cursor);
			closeSqliteDb(db);
		}
	}
	

	/*private void setChange() {
		mChange.set(true);
	}
	
	private void reSetChange(){
		mChange.set(false);
	}
	
	public boolean isChange(){
		return mChange.get();
	}*/
	
}
