package com.dangdang.reader.dread.service;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.dangdang.reader.cloud.CloudConstant;
import com.dangdang.reader.cloud.MarkData;
import com.dangdang.reader.cloud.Status;
import com.dangdang.reader.dread.data.BookMark;
import com.dangdang.reader.dread.data.BookMark.Column;
import com.dangdang.reader.dread.data.BookMarkDataWrapper;
import com.dangdang.reader.dread.data.MarkKey;
import com.dangdang.reader.dread.data.ReadInfo;
import com.dangdang.reader.dread.util.DreaderConstants;

public class MarkService extends BaseService {

	private AtomicBoolean mChange = new AtomicBoolean(false);
	
	public MarkService(Context context) {
		super(context);
	}

	public synchronized boolean saveMark(BookMark mark){
		
		printLog(" saveMark elementIndex = " + mark.elementIndex);
		
		boolean ret = false;
		BookMark bmk = mark;
		SQLiteDatabase db = mDB.getWritableDatabase();
		try {
			execInsertSqlNotContainModifyTime(db, bmk);
			ret = true;
			setChange();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			closeSqliteDb(db);
		}
		return ret;
	}
	
	/**
	 * 执行插入书签sql，插入modifyTime
	 * @param db
	 * @param bmk
	 */
	private void execInsertSql(SQLiteDatabase db, BookMark bmk) {
		
		String insert = "INSERT INTO "+ Column.TableName + " (" +
				Column.Pid + ", " + Column.IsBought + "," + 
				Column.BookPath + ", " + Column.ChapterIndex + "," + 
				Column.ElementIndex + ", " + Column.ChapterName + "," + 
				Column.MarkTime + "," + Column.MarkText + "," + 
				Column.ExpColumn1 + "," + Column.ExpColumn2 + "," + 
				Column.ExpColumn3 + "," + Column.ModVersion +
				") values (?,?,?,?,?,?,?,?,?,?,?,?)";
		
		Object[] args = new Object[]{bmk.pId, bmk.isBought, bmk.bookPath, 
				bmk.chapterIndex,bmk.elementIndex, bmk.chapterName, 
				bmk.markTime, bmk.markText, bmk.status, bmk.cloudStatus, bmk.modifyTime, bmk.bookModVersion};//, 
		db.execSQL(insert, args);
	}

	/**
	 * 执行插入书签sql，不插入modifyTime
	 * @param db
	 * @param bmk
	 */
	private void execInsertSqlNotContainModifyTime(SQLiteDatabase db, BookMark bmk) {
		
		String insert = "INSERT INTO "+ Column.TableName + " (" +
				Column.Pid + ", " + Column.IsBought + "," + 
				Column.BookPath + ", " + Column.ChapterIndex + "," + 
				Column.ElementIndex + ", " + Column.ChapterName + "," + 
				Column.MarkTime + "," + Column.MarkText + "," + 
				Column.ExpColumn1 + "," + Column.ExpColumn2 + 
				/*"," + Column.ExpColumn3 + */ "," + Column.ModVersion +
				") values (?,?,?,?,?,?,?,?,?,?,?)";
		
		Object[] args = new Object[]{bmk.pId, bmk.isBought, bmk.bookPath, 
				bmk.chapterIndex,bmk.elementIndex, bmk.chapterName, 
				bmk.markTime, bmk.markText, bmk.status, bmk.cloudStatus, bmk.bookModVersion};//, bmk.modifyTime
		db.execSQL(insert, args);
	}

	/*public synchronized boolean updateMarkById(int id, String content){
		
		boolean ret = false;
		SQLiteDatabase db = mDB.getWritableDatabase();
		try {
			String sql = " update " + Column.TableName + 
					" set " + Column.MarkText + " = ? where " + Column.Id +" = ? ";
			db.execSQL(sql, new Object[]{content, id});
			ret = true;
			setChange();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			closeSqliteDb(db);
		}
		
		return ret;
	}*/
	
	public synchronized boolean deleteBookMark(String pId, int elementIndex,
			int chapterIndex, int isBought){
		
		boolean ret = false;
		SQLiteDatabase db = mDB.getWritableDatabase();
		try {
			execDeleteSql(db, pId, elementIndex, chapterIndex, isBought);
			ret = true;
			setChange();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			closeSqliteDb(db);
		}
		
		return ret;
	}

	private void execDeleteSql(SQLiteDatabase db, String pId, int elementIndex, int chapterIndex,
			int isBought) {
		String delete = " DELETE FROM "+ Column.TableName  + " where " + 
				Column.Pid +" = ? AND "+ 
				Column.ElementIndex +" = ? AND " + 
				Column.ChapterIndex  + " = ? AND "+ 
				Column.IsBought +" = ? " ;
		db.execSQL(delete, new Object[]{pId, elementIndex, chapterIndex, isBought});
	}
	
	
	/**
	 * @param pId
	 * @param isBought
	 * @param chapterIndex
	 * @param startIndex 
	 * @param endIndex
	 * @return
	 */
	public synchronized boolean deleteBookMark(String pId, int isBought, 
			int chapterIndex, int startIndex, int endIndex){
		
		printLog(" deleteBookMark startIndex = " + startIndex + ", endIndex = " + endIndex);
		
		boolean ret = false;
		SQLiteDatabase db = mDB.getWritableDatabase();
		try {
			String delete = " DELETE FROM "+ Column.TableName  + " WHERE " + 
					Column.Pid +" = ? AND "+ 
					Column.IsBought +" = ? AND "+ 
					Column.ChapterIndex +" = ? AND "+ 
					Column.ElementIndex +" >= ? AND " +  
					Column.ElementIndex +" <= ? ";
			db.execSQL(delete, new Object[]{pId, isBought, chapterIndex, startIndex, endIndex});
			ret = true;
			setChange();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			closeSqliteDb(db);
		}
		
		return ret;
	}
	
	public synchronized boolean deleteBookMark(String pId){
		
		boolean ret = false;
		SQLiteDatabase db = mDB.getWritableDatabase();
		try {
			String delete = " DELETE FROM "+ Column.TableName + " where " + 
					Column.Pid +" = ? ";
			db.execSQL(delete, new Object[]{pId});
			ret = true;
			setChange();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			closeSqliteDb(db);
		}
		
		return ret;
	}
	
	public synchronized void deleteAllBookMarks(){
		SQLiteDatabase db = mDB.getWritableDatabase();
		try {
			String sql = " delete from " + Column.TableName;
			db.execSQL(sql);
			setChange();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			closeSqliteDb(db);
		}
	}

	/*public synchronized boolean checkExist(String pId, int isBought, 
			String chapterPath, int elementIndex){
		
		boolean ret = false;
		Cursor cursor = null;
		try {
			String sql = "SELECT * FROM "+ Column.TableName + " WHERE "+ 
					Column.Pid +" = ? AND "+ 
					Column.ChapterPath +" = ? AND "+ 
					Column.ElementIndex +" = ? AND " +  
					Column.IsBought +" = ? ";
			
			cursor = mDB.getReadableDatabase().rawQuery(sql, 
					new String[]{pId, chapterPath, 
					String.valueOf(elementIndex), String.valueOf(isBought)});
			if (cursor != null) {
				ret = (cursor.getCount() > 0);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			closeCursor(cursor);
		}
		
		return ret;
	}*/
	
	/**
	 * @param pId
	 * @param isBought
	 * @param chapterIndex
	 * @param startIndex 不包括startIndex
	 * @param endIndex
	 * @return
	 */
	public synchronized boolean checkExist(String pId, int isBought, 
			int chapterIndex, int startIndex, int endIndex, String bookModVersion){
		
		boolean ret = false;
		Cursor cursor = null;
		try {
			String sql = "SELECT * FROM "+ Column.TableName + " WHERE "+ 
					Column.Pid +" = ? AND "+ 
					Column.IsBought +" = ? AND "+ 
					Column.ExpColumn1 +" != ? AND "+ 
					Column.ChapterIndex +" = ? AND "+ 
					Column.ElementIndex +" >= ? AND " +  
					Column.ElementIndex +" <= ? AND " +
					Column.ModVersion +" = ? ";
			
			final String status = String.valueOf(Status.COLUMN_DELETE);
			cursor = mDB.getReadableDatabase().rawQuery(sql, 
					new String[]{pId, String.valueOf(isBought), status, String.valueOf(chapterIndex), 
					String.valueOf(startIndex), String.valueOf(endIndex), bookModVersion});
			if (cursor != null) {
				ret = (cursor.getCount() > 0);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			closeCursor(cursor);
		}
		
		return ret;
	}
	
	
	/*public Cursor getBookMarks(String pId, int isBought){
		
		Cursor cursor = null;
		SQLiteDatabase db = mDB.getReadableDatabase();
		try {
			String sql = "SELECT * FROM "+ Column.TableName +" WHERE "+
					Column.Pid +" = ?  AND "+ 
					Column.IsBought +" = ? ORDER BY "+ Column.MarkTime +" DESC";
			
			cursor = db.rawQuery(sql, new String[]{pId, String.valueOf(isBought)});
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			closeCursor(cursor);
			closeSqliteDb(db);
		}
		
		return cursor;
	}*/
	
	
	public synchronized List<BookMark> getBookMarkList(String pId, int isBought){
		
		List<BookMark> results = new ArrayList<BookMark>();
		SQLiteDatabase db = mDB.getReadableDatabase();
		Cursor cursor = null;
		try {
			String sql = "SELECT * FROM "+ Column.TableName +" WHERE "+
					Column.Pid +" = ?  AND "+ 
					Column.IsBought +" = ? AND "+
					Column.ExpColumn1 + "!= ? ORDER BY "+ Column.ChapterIndex+","+Column.ElementIndex +" ASC";
			cursor = db.rawQuery(sql, new String[]{pId, String.valueOf(isBought), String.valueOf(Status.COLUMN_DELETE)});
			
			BookMark bookmark = null;
			while(cursor.moveToNext()){
				bookmark = getBookMark(cursor);
				results.add(bookmark);
			} 
			reSetChange();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			closeCursor(cursor);
			closeSqliteDb(db);
		}
		
		return results;
	}

	public synchronized List<BookMarkDataWrapper> getBookMarkWrapperList(String pId, int isBought) {

		List<BookMarkDataWrapper> results = new ArrayList<BookMarkDataWrapper>();
		SQLiteDatabase db = mDB.getReadableDatabase();
		Cursor cursor = null;
		try {
			String sql = "SELECT * FROM " + Column.TableName + " WHERE " + Column.Pid + " = ?  AND " + Column.IsBought + " = ? AND " + Column.ExpColumn1
					+ "!= ? ORDER BY " + Column.ChapterIndex + "," + Column.ElementIndex + " ASC";
			cursor = db.rawQuery(sql, new String[] { pId, String.valueOf(isBought), String.valueOf(Status.COLUMN_DELETE) });
			int lastChapterIndex = -1;
			BookMark bookmark = null;
			while (cursor.moveToNext()) {
				bookmark = getBookMark(cursor);
				if (bookmark.chapterIndex != lastChapterIndex) {
					lastChapterIndex = bookmark.chapterIndex;
					bookmark.isChapterHead = true;
					BookMarkDataWrapper chapterName = new BookMarkDataWrapper();
					chapterName.chapterName = bookmark.chapterName;
					chapterName.chapterIndex = bookmark.chapterIndex;
					results.add(chapterName);
				}
				BookMarkDataWrapper data = new BookMarkDataWrapper();
				data.data = bookmark;
				results.add(data);
			}
			reSetChange();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			closeCursor(cursor);
			closeSqliteDb(db);
		}

		return results;
	}

	private BookMark getBookMark(Cursor cursor) {
		BookMark bookmark = new BookMark();
		bookmark.id = cursor.getInt(cursor.getColumnIndex(Column.Id));
		bookmark.pId = cursor.getString(cursor.getColumnIndex(Column.Pid));
		bookmark.isBought = cursor.getInt(cursor.getColumnIndex(Column.IsBought));
		bookmark.bookPath = cursor.getString(cursor.getColumnIndex(Column.BookPath));
		bookmark.chapterIndex = cursor.getInt(cursor.getColumnIndex(Column.ChapterIndex));
		bookmark.elementIndex = cursor.getInt(cursor.getColumnIndex(Column.ElementIndex));
		bookmark.markText = cursor.getString(cursor.getColumnIndex(Column.MarkText));
		bookmark.markTime = cursor.getLong(cursor.getColumnIndex(Column.MarkTime));
		bookmark.chapterName = cursor.getString(cursor.getColumnIndex(Column.ChapterName));
		bookmark.bookModVersion = cursor.getString(cursor.getColumnIndex(Column.ModVersion));
		if (bookmark.bookModVersion == null || bookmark.bookModVersion.isEmpty())
			bookmark.bookModVersion = DreaderConstants.BOOK_MODIFY_VERSION;
		
		/**
		 * TODO 兼容老数据，增加默认值
		 */
		String status = cursor.getString(cursor.getColumnIndex(Column.ExpColumn1));
		if(TextUtils.isEmpty(status)){
			status = String.valueOf(Status.COLUMN_NEW);
		}
		bookmark.status = status;
		
		String cloudStatus = cursor.getString(cursor.getColumnIndex(Column.ExpColumn2));
		if(TextUtils.isEmpty(cloudStatus)){
			cloudStatus = String.valueOf(Status.CLOUD_NO);
		}
		bookmark.cloudStatus = cloudStatus;
		
		String modifyTime = cursor.getString(cursor.getColumnIndex(Column.ExpColumn3));
		if(TextUtils.isEmpty(modifyTime)){
			modifyTime = String.valueOf(0);
		}
		bookmark.modifyTime = modifyTime;
		/*bookmark.status = cursor.getString(cursor.getColumnIndex(Column.ExpColumn1));
		bookmark.cloudStatus = cursor.getString(cursor.getColumnIndex(Column.ExpColumn2));
		bookmark.modifyTime = cursor.getString(cursor.getColumnIndex(Column.ExpColumn3));*/
		
		return bookmark;
	}
	
	public synchronized Map<MarkKey, BookMark> getBookMarks(String pId, int isBought){
		
		Map<MarkKey, BookMark> bookMarks = new Hashtable<MarkKey, BookMark>();
		SQLiteDatabase db = mDB.getReadableDatabase();
		Cursor cursor = null;
		try {
			String sql = "SELECT * FROM "+ Column.TableName +" WHERE "+
					Column.Pid +" = ?  AND "+ 
					Column.IsBought +" = ? ORDER BY "+ Column.ChapterIndex+","+Column.ElementIndex +" ASC";
			cursor = db.rawQuery(sql, new String[]{pId, String.valueOf(isBought)});
			
			BookMark bookmark = null;
			while(cursor.moveToNext()){
				bookmark = getBookMark(cursor);
				bookMarks.put(new MarkKey(bookmark.pId, bookmark.bookModVersion, bookmark.chapterIndex, bookmark.elementIndex), bookmark);
			} 
			reSetChange();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			closeCursor(cursor);
			closeSqliteDb(db);
		}
		
		return bookMarks;
	}
	
	/**
	 * 获取未同步的书签列表
	 * 拼成json串
	 * @param pId
	 * @param isBought
	 * @param userId
	 * @return
	 */
	public synchronized MarkData getNotSyncBookMarks(String pId, int isBought, String userId){
		
		MarkData markData = new MarkData();
		SQLiteDatabase db = mDB.getReadableDatabase();
		Cursor cursor = null;
		try {
			String sql = "SELECT * FROM "+ Column.TableName +" WHERE "+
					Column.Pid +" = ?  AND "+ Column.IsBought +" = ?  AND "+ 
					Column.ExpColumn2 +" != ? ";
			
			String cloudStatus = String.valueOf(Status.CLOUD_YES);
			cursor = db.rawQuery(sql, new String[]{pId, String.valueOf(isBought), cloudStatus});
			
			//List<BookMark> marks = new ArrayList<BookMark>();
			BookMark bookMark = null;
			JSONArray jsonArrs = new JSONArray();
			JSONObject josnObj = null;
			while(cursor.moveToNext()){
				bookMark = getBookMark(cursor);
				josnObj = convertCloudJson(bookMark, userId, pId);
				
				if(josnObj != null){
					//marks.add(bookMark);
					jsonArrs.put(josnObj);
				}
			}
			//markData.setMarks(marks);
			markData.setMarksJson(jsonArrs);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			closeCursor(cursor);
			closeSqliteDb(db);
		}
		return markData;
	}
	
	private JSONObject convertCloudJson(BookMark bookMark, String userId, String productId){
		
		JSONObject jsonObj = new JSONObject();
		try {
			long operateTime = 0;
			long modifyTime = 0;
			try {
				operateTime = bookMark.getMarkTime() / 1000;
				if(!TextUtils.isEmpty(bookMark.getModifyTime())){
					modifyTime = Long.valueOf(bookMark.getModifyTime()) / 1000;
				}
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			jsonObj.put(CloudConstant.JSONK_MARK_CUSTID, userId);
			jsonObj.put(CloudConstant.JSONK_MARK_PRODUCTID, productId);
			jsonObj.put(CloudConstant.JSONK_MARK_CHAPTERINDEX, bookMark.getChapterIndex());
			jsonObj.put(CloudConstant.JSONK_MARK_ELEMENTINDEX, bookMark.getElementIndex());
			jsonObj.put(CloudConstant.JSONK_MARK_OPERATETIME, operateTime);
			jsonObj.put(CloudConstant.JSONK_MARK_MODIFYTIME, modifyTime);
			jsonObj.put(CloudConstant.JSONK_MARK_STATUS, bookMark.getStatus());
			jsonObj.put(CloudConstant.JSONK_MARK_MARKINFO, bookMark.getMarkText());
			jsonObj.put(CloudConstant.JSONK_MARK_BOOKMODVERSION, bookMark.getBookModVersion());
		} catch (JSONException e) {
			e.printStackTrace();
			jsonObj = null;
		}
		
		return jsonObj;
	}
	
	
	/**
	 * 将书（pid + isBought）的某章(chapterIndex)的范围为(startElementIndex - endElementIndex)的状态改为expColumn1
	 * 同时云状态置为非同步，不更改modifyTime
	 * @param pid
	 * @param isBought
	 * @param chapterIndex
	 * @param startElementIndex
	 * @param endElementIndex
	 * @param status 增、删、改（应该只有增、删）
	 */
	public synchronized void updateMarkStatus(String pid, String modVersion, int isBought, int chapterIndex, 
			int startElementIndex, int endElementIndex, int status, long operateTime){
		
		SQLiteDatabase db = mDB.getWritableDatabase();
		try {
			String sql = "update " + Column.TableName + " set " + Column.MarkTime + " = ?, " +
					Column.ExpColumn1 + " = ?, " + Column.ExpColumn2 + " = ?, " + Column.ModVersion + " = ? where " +
					Column.Pid +" = ? AND "+ 
					Column.IsBought +" = ? AND "+ 
					Column.ChapterIndex +" = ? AND "+ 
					Column.ElementIndex +" >= ? AND " +  
					Column.ElementIndex +" <= ? " ;
			
			final long tOperateTime = operateTime;
			final String tStatus = String.valueOf(status);
			final String tCloudStatus = String.valueOf(Status.CLOUD_NO);
			//final long tModifyTime = operateTime;
			final String tPid = pid;
			final int tIsBought = isBought;
			final int tChapterIndex = chapterIndex;
			final int tStartIndex = startElementIndex;
			final int tEndIndex = endElementIndex;
			db.execSQL(sql, new Object[]{tOperateTime, tStatus, tCloudStatus, modVersion,  
					tPid, tIsBought, tChapterIndex, tStartIndex, tEndIndex});
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			closeSqliteDb(db);
		}
	}
		
	/**
	 * 将书（pid + isBought）的某章(chapterIndex)的位置elementIndex对应纪录 状态改为expColumn1
	 * 同时云状态置为非同步
	 * @param pid
	 * @param isBought
	 * @param chapterIndex
	 * @param elementIndex
	 * @param status 增、删、改
	 */
	/*public synchronized void updateMarkStatus(String pid, int isBought, int chapterIndex, int elementIndex, int status, long operateTime){
		
		SQLiteDatabase db = mDB.getWritableDatabase();
		try {
			String sql = "update " + Column.TableName + " set " + Column.MarkTime + " = ?, " +
					Column.ExpColumn1 + " = ?, " + Column.ExpColumn2 + " = ?, " + Column.ExpColumn3 + " = ? where " +
					Column.Pid +" = ? AND "+ 
					Column.IsBought +" = ? AND "+ 
					Column.ChapterIndex +" = ? AND "+ 
					Column.ElementIndex +" = ? ";
			
			final long tOperateTime = operateTime;
			final String tStatus = String.valueOf(status);
			final String tCloudStatus = String.valueOf(Status.CLOUD_NO);
			final long tModifyTime = operateTime;
			final String tPid = pid;
			final int tIsBought = isBought;
			final int tChapterIndex = chapterIndex;
			final int tElementIndex = elementIndex;
			
			db.execSQL(sql, new Object[]{tOperateTime, tStatus, tCloudStatus, tModifyTime, 
					tPid, tIsBought, tChapterIndex, tElementIndex});
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			closeSqliteDb(db);
		}
	}*/
	
	public synchronized boolean performMergeMarkResult(final List<BookMark> marks){
		
		printLog(" performMergeMarkResult start ");
		boolean result = false;
		SQLiteDatabase db = mDB.getWritableDatabase();
		try {
			db.beginTransaction();
			BookMark mark = null;
			for(int i = 0, len = marks.size(); i < len; i++){
				
				mark = marks.get(i);
				//考虑同步状态？mark.getCloudStatus()
				printLog(" performMergeMark new Status = " + mark.getStatus() + ",CloudStatus = " + mark.getCloudStatus());
				
				String status = mark.getStatus();
				if(isNewStatus(status)){
					execInsertSql(db, mark);
				} else if(isUpdateStatus(status)){
					
					ContentValues values = new ContentValues();
					values.put(Column.ExpColumn1, mark.getStatus());
					values.put(Column.ExpColumn2, mark.getCloudStatus());
					
					String whereClause = Column.Pid +" = ? AND "+ Column.ElementIndex +" = ? AND " + 
							Column.ChapterIndex  + " = ? AND "+ Column.IsBought +" = ? ";
					String[] whereArgs = new String[]{mark.getpId(), String.valueOf(mark.getElementIndex()), 
						String.valueOf(mark.getChapterIndex()), String.valueOf(mark.getIsBought())};
					
					db.update(Column.TableName, values, whereClause, whereArgs);
					
				} else if(isDeleteStatus(status)){
					String pid = mark.getpId();
					int elementIndex = mark.getElementIndex();
					int chapterIndex = mark.getChapterIndex();
					int isBought = ReadInfo.BSTATUS_FULL;//mark.getIsBought();
					execDeleteSql(db, pid, elementIndex, chapterIndex, isBought);
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
		printLog(" performMergeMarkResult end ");
		return result;
	}
	
	/**
	 * 把marks的云状态都更改为cloudStatus
	 * @param marks
	 * @param cloudStatus Status.CLOUD_YES,Status.CLOUD_NO
	 * @return
	 */
	/*public synchronized boolean updateMarksCloudStatus(final List<BookMark> marks, final int cloudStatus){
		
		printLog(" updateMarkListStatus start ");
		boolean result = false;
		SQLiteDatabase db = mDB.getWritableDatabase();
		try {
			db.beginTransaction();
			BookMark mark = null;
			for(int i = 0, len = marks.size(); i < len; i++){
				mark = marks.get(i);
				//考虑同步状态？mark.getCloudStatus()
				printLog(" updateMarkList Status = " + mark.getStatus() + ",CloudStatus = " + mark.getCloudStatus());
				
				String sql = "update " + Column.TableName + " set " + Column.ExpColumn2 + 
						" = ? where " + Column.Id +" = ? ";
				
				String tCloudStatus = String.valueOf(cloudStatus);
				int markId = mark.getId();
				db.execSQL(sql, new Object[]{tCloudStatus, markId});
			}
			db.setTransactionSuccessful();
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.endTransaction();
			closeSqliteDb(db);
		}
		printLog(" updateMarkListStatus end ");
		return result;
	}*/
	
	/**
	 * 把marks的云状态都更改为cloudStatus, 同时修改modifyTime
	 * @param marks
	 * @param cloudStatus Status.CLOUD_YES,Status.CLOUD_NO
	 * @return
	 */
	public synchronized boolean updateMarksCloudStatus(final JSONArray marks, final int cloudStatus, final long modifyTime){
		
		printLog(" updateMarkListStatus start ");
		boolean result = false;
		SQLiteDatabase db = mDB.getWritableDatabase();
		try {
			db.beginTransaction();
			JSONObject mark = null;
			for(int i = 0, len = marks.length(); i < len; i++){
				mark = marks.getJSONObject(i);
				//考虑同步状态？mark.getCloudStatus()
				printLog(" updateMarkList Status = " + mark.optString("status"));
				
				
				String sql = "update " + Column.TableName + " set " + Column.ExpColumn2 + " = ?, " + 
						Column.ExpColumn3 + " = ?, " +
						Column.ModVersion + " = ? where " +
						Column.Pid +" = ? AND "+ 
						Column.IsBought +" = ? AND "+ 
						Column.ChapterIndex +" = ? AND "+ 
						Column.ElementIndex +" = ? ";
				
				String tCloudStatus = String.valueOf(cloudStatus);
				long tModifyTime = modifyTime;
				String productId = mark.optString(CloudConstant.JSONK_MARK_PRODUCTID);
				int isBought = ReadInfo.BSTATUS_FULL;
				int chapterIndex = mark.optInt(CloudConstant.JSONK_MARK_CHAPTERINDEX);
				int elementIndex = mark.optInt(CloudConstant.JSONK_MARK_ELEMENTINDEX);
				String modVersion = mark.optString(CloudConstant.JSONK_MARK_BOOKMODVERSION);
				
				db.execSQL(sql, new Object[]{tCloudStatus, tModifyTime, modVersion, productId, isBought, chapterIndex, elementIndex});
			}
			db.setTransactionSuccessful();
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.endTransaction();
			closeSqliteDb(db);
		}
		printLog(" updateMarkListStatus end ");
		return result;
	}

	private void setChange() {
		mChange.set(true);
	}
	
	private void reSetChange(){
		mChange.set(false);
	}
	
	public boolean isChange(){
		return mChange.get();
	}
	
	/**
	 * 复制某本书的试读书签到全本
	 * @param bookid
	 * @param tryStatus
	 * @param fullStatus
	 */
	public void copyTryMarkToFull(String bookid, int tryStatus, int fullStatus){
		SQLiteDatabase db = mDB.getWritableDatabase();
		Cursor cursor = null;
		try {
			String sql = "select * from " + Column.TableName + " where "
					   + Column.Pid + " = ? AND "
					   + Column.IsBought + " = ? ";
			cursor = db.rawQuery(sql, new String[]{bookid, String.valueOf(fullStatus)});
			if(cursor.getCount() > 0){
				printLog(" already exist full mark data ");
				return;
			}
			
			db.beginTransaction();
			cursor = db.rawQuery(sql, new String[]{bookid, String.valueOf(tryStatus)});
			BookMark mark = null;
			while (cursor.moveToNext()) {
				mark = getBookMark(cursor);
				mark.setIsBought(fullStatus);
				execInsertSqlNotContainModifyTime(db, mark);
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
	
	public void moveTryMarkToFull(String bookid, int tryStatus, int fullStatus){
		SQLiteDatabase db = mDB.getWritableDatabase();
		Cursor cursor = null;
		try {
			db.beginTransaction();
			String updatesql = " update " + Column.TableName + 
					" set " + Column.IsBought + " = ? where " 
					+ Column.Pid + " = ? AND "
					+ Column.IsBought + " = ? ";
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
	
}
