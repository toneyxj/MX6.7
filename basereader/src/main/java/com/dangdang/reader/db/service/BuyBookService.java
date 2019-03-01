package com.dangdang.reader.db.service;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.alibaba.fastjson.JSONObject;
import com.dangdang.reader.db.BuyBookDBColumn;
import com.dangdang.reader.db.ShelfBookDBColumn;
import com.dangdang.reader.db.ShelfBookDBHelper;
import com.dangdang.reader.personal.domain.GroupItem;
import com.dangdang.reader.personal.domain.GroupType;
import com.dangdang.reader.personal.domain.ShelfBook;
import com.dangdang.reader.personal.domain.ShelfBook.BookType;
import com.dangdang.reader.personal.domain.ShelfBook.TryOrFull;
import com.dangdang.reader.store.domain.StoreEBook;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

//import android.database.sqlite.SQLiteDatabase;

public class BuyBookService extends ShelfBookService {

	private static volatile BuyBookService mInstance;
	private ShelfBookDBHelper mDB;
	private Hanyu mHanyu;
	
	public synchronized static BuyBookService getInstance(Context context) {
		if (mInstance == null) {
			mInstance = new BuyBookService(context.getApplicationContext());
		}
		return mInstance;
	}

	private BuyBookService(Context context) {
		super(context);
		mDB = ShelfBookDBHelper.getInstance(context);
		mHanyu = new Hanyu();
	}
	
	public void release(){
		if(mDB != null){
			try{
				mDB.release();
				mDB = null;
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		if(mInstance != null)
			mInstance = null;
	}
	
	class CategoryHolder{
		int num;
		long time;
	}

	private HashMap<String, Object> deleteOneBuyBook(ShelfBook book, SQLiteDatabase sqlite, HashSet<String> set){
		HashMap<String, Object> map = new HashMap<String, Object>();
		Cursor cursor = sqlite.query(BuyBookDBColumn.BUY_TABLE, new String[]{BuyBookDBColumn.RELATION_TYPE, BuyBookDBColumn.IS_HIDE}, 
				BuyBookDBColumn.BOOK_ID + "=?", new String[]{book.getMediaId()}, null, null, null);
		if(cursor != null && cursor.moveToFirst()){
			String type = cursor.getString(0);
			int hide = cursor.getInt(1);
			set.add(book.getMediaId());
			sqlite.delete(BuyBookDBColumn.BUY_TABLE, BuyBookDBColumn.BOOK_ID + "=?", new String[]{book.getMediaId()});
			if(hide == 1)
				map.put("int", -100);
			else{
				map.put("int", -1);
				map.put("type", type);
			}
		}else
			map.put("int", -100);
		if (cursor != null)
			cursor.close();
		return map;
	}

	private ShelfBook getBook(StoreEBook item, long time){
		try{
			ShelfBook book = new ShelfBook();
			book.setBuyType("1001");
			book.setAuthorPenname(item.getAuthorPenname());
			book.setCoverPic(item.getCoverPic());
			book.setDescs(item.getDescs());
			book.setMediaId(item.getMediaId());
			book.setMediaType(item.getMediaType());
			book.setSaleId(item.getSaleId());
			book.setTitle(item.getTitle());
			book.setBookSize(item.getFileSize());
			book.setLastTime(time);
			book.setHide(0);
			if(book.getMediaType() == 1){
				if(item.getIsFull() == 1)
					book.setBookType(BookType.BOOK_TYPE_IS_FULL_YES);
				else
					book.setBookType(BookType.BOOK_TYPE_IS_FULL_NO);
				book.setAuthorityType(item.getIsWholeAuthority() + "");
			}else{
				// 出版物 和 支持设备  两个字段一起用
				book.setAuthorityType("1");
				book.setBookType(BookType.BOOK_TYPE_NOT_NOVEL);
				book.isValid = 1;
			}
			book.setCategoryIds(item.getCategoryIds());
			String tmp = item.getCategorys();
			if(TextUtils.isEmpty(tmp))
				tmp = "未分组";
			book.setCategorys(tmp);

			GroupType type = new GroupType();
			type.setName(book.getCategorys());
			book.setGroupType(type);

			return book;
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}

	private long getLastBookTime(){
		synchronized (this) {
			SQLiteDatabase sqlite = null;
			Cursor cursor = null;
			try{
				sqlite = mDB.getWritableDatabase();
				String bookSql = "select " + BuyBookDBColumn.LAST_TIME + " from " + BuyBookDBColumn.BUY_TABLE
						+ " order by " + BuyBookDBColumn.LAST_TIME
						+ " desc limit " + 0 + "," + 1;
				cursor = sqlite.rawQuery(bookSql, null);
				if (cursor != null && cursor.moveToNext()) {
					return cursor.getLong(0);
				}
				return 0;
			}catch (Throwable e){
				e.printStackTrace();
				return 0;
			}finally {
				closeCursor(cursor);
				closeSqlite(sqlite);
			}
		}
	}

	/**
	 * 记录一本购买
	 * @param book
	 */
	public void saveStoreEBook(StoreEBook book){
		if(book == null)
			return;
		List<StoreEBook> list = new ArrayList<StoreEBook>();
		list.add(book);
		saveStoreEBooks(list);
	}

	/**
	 * 记录批量购买
	 * @param list
	 */
	public void saveStoreEBooks(List<StoreEBook> list){
		if(list == null)
			return;
		long time = getLastBookTime();
		List<ShelfBook> tmp = new LinkedList<ShelfBook>();
		for(StoreEBook book : list){
			ShelfBook item = getBook(book, ++time);
			if(item == null)
				continue;
			tmp.add(item);
		}
		if(!tmp.isEmpty())
			saveBuyBooks(tmp);
	}

	public List<String> saveBuyBooks(List<ShelfBook> list){
		List<String> result = new ArrayList<String>();
		HashMap<String, CategoryHolder> map = new HashMap<String, CategoryHolder>();
		HashMap<String, CategoryHolder> map2 = new HashMap<String, CategoryHolder>();
		synchronized (this) {
			SQLiteDatabase sqlite = null;
			try{
				sqlite = mDB.getWritableDatabase();
				HashSet<String> set = new HashSet<String>();
				sqlite.beginTransaction();
				for(ShelfBook book : list){
					int tmp;
					if("9999".equals(book.getBuyType())){
						set.add(book.getMediaId());
						HashMap<String, Object> m = deleteOneBuyBook(book, sqlite, set);
						tmp = (Integer) m.get("int");
						if(tmp == -1)
							book.setBuyType((String) m.get("type"));
					}else{
						set.remove(book.getMediaId());
						tmp = saveOneBuyBook(sqlite, book, result);
					}
					if(tmp == -100)
						continue;
					
					CategoryHolder holder = map.get(book.getBuyType());
					if(holder == null){
						holder = new CategoryHolder();
						holder.num = tmp;
						holder.time = book.getLastTime();
					}else{
						holder.num += tmp;
						if(book.getLastTime() > holder.time)
							holder.time = book.getLastTime();
					}
					map.put(book.getBuyType(), holder);
					
					
					holder = map2.get(book.getGroupType().getName());
					if(holder == null){
						holder = new CategoryHolder();
						holder.num = tmp;
						holder.time = book.getLastTime();
					}else{
						holder.num += tmp;
						if(book.getLastTime() > holder.time)
							holder.time = book.getLastTime();
					}
					map2.put(book.getGroupType().getName(), holder);
				}
//				DataUtil.getInstance(mContext).deleteBooksById(set);
				
				Iterator<Entry<String, CategoryHolder>> iter = map.entrySet().iterator();
				while (iter.hasNext()) {
					Map.Entry<String, CategoryHolder> entry = (Map.Entry<String, CategoryHolder>) iter.next();
					String key = entry.getKey();
					CategoryHolder holder = entry.getValue();
										
					Cursor cursor = sqlite.query(BuyBookDBColumn.CATEGORY_TABLE, new String[] { BuyBookDBColumn.BOOK_NUM,
							BuyBookDBColumn.CREATE_TIME }, BuyBookDBColumn.CATEGORY + "=?",
							new String[] { key }, null, null, null);
					if (cursor != null && cursor.moveToFirst()) {
						int num = cursor.getInt(0);
						num += holder.num;			
						if(num < 0)
							num = 0;
						ContentValues values = new ContentValues();
						values.put(BuyBookDBColumn.BOOK_NUM, num);
						long time = cursor.getLong(1);
						if(time < holder.time)
							values.put(BuyBookDBColumn.CREATE_TIME, holder.time);
						sqlite.update(BuyBookDBColumn.CATEGORY_TABLE, values, BuyBookDBColumn.CATEGORY + "=?", new String[]{key});
					} else {
						ContentValues value = new ContentValues();
						value.put(BuyBookDBColumn.CATEGORY, key);
						if(holder.num < 0)
							holder.num = 0;
						value.put(BuyBookDBColumn.BOOK_NUM, holder.num);
						value.put(BuyBookDBColumn.CREATE_TIME, holder.time);
						sqlite.insert(BuyBookDBColumn.CATEGORY_TABLE, null, value);
					}
					if (cursor != null)
						cursor.close();
				}
				
				iter = map2.entrySet().iterator();
				while (iter.hasNext()) {
					Map.Entry<String, CategoryHolder> entry = (Map.Entry<String, CategoryHolder>) iter.next();
					String key = entry.getKey();
					CategoryHolder holder = entry.getValue();
										
					Cursor cursor = sqlite.query(BuyBookDBColumn.TYPE_TABLE, new String[] { BuyBookDBColumn.BOOK_NUM,
							BuyBookDBColumn.CREATE_TIME }, BuyBookDBColumn.CATEGORY + "=?",
							new String[] { key }, null, null, null);
					if (cursor != null && cursor.moveToFirst()) {
						int num = cursor.getInt(0);
						num += holder.num;						
						if(num < 0)
							num = 0;
						ContentValues values = new ContentValues();
						values.put(BuyBookDBColumn.BOOK_NUM, num);
						long time = cursor.getLong(1);
						if(time < holder.time)
							values.put(BuyBookDBColumn.CREATE_TIME, holder.time);
						sqlite.update(BuyBookDBColumn.TYPE_TABLE, values, BuyBookDBColumn.CATEGORY + "=?", new String[]{key});
					} else {
						ContentValues value = new ContentValues();
						value.put(BuyBookDBColumn.CATEGORY, key);
						if(holder.num < 0)
							holder.num = 0;
						value.put(BuyBookDBColumn.BOOK_NUM, holder.num);
						value.put(BuyBookDBColumn.CREATE_TIME, holder.time);
						sqlite.insert(BuyBookDBColumn.TYPE_TABLE, null, value);
					}
					if (cursor != null)
						cursor.close();
				}
				sqlite.setTransactionSuccessful();
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				if(sqlite != null)
					sqlite.endTransaction();
				closeSqlite(sqlite);
			}
		}
		return result;
	}	
	
	private int saveOneBuyBook(SQLiteDatabase sqlite, ShelfBook book, List<String> list){
		Cursor cursor = null;
		try{
			cursor = sqlite.query(BuyBookDBColumn.BUY_TABLE, new String[]{BuyBookDBColumn.IS_HIDE, BuyBookDBColumn.AUTHORITY_TYPE}, 
					BuyBookDBColumn.BOOK_ID + "=?", new String[]{book.getMediaId()}, null, null, null);
			if(cursor != null && cursor.moveToNext()){
				ContentValues values = new ContentValues();
				try{
					String type = cursor.getString(1);
					if(!book.getAuthorityType().equals(type))
						values.put(BuyBookDBColumn.AUTHORITY_TYPE, book.getAuthorityType());
				}catch(Exception e){
					e.printStackTrace();
				}				
				int hide = cursor.getInt(0);
				if(hide != book.isHide()){					
					values.put(BuyBookDBColumn.IS_HIDE, book.isHide());					
					sqlite.update(BuyBookDBColumn.BUY_TABLE, values, BuyBookDBColumn.BOOK_ID + "=?", new String[]{book.getMediaId()});
					if(book.isHide() == 0)
						return 1;
					else
						return -1;
				}else{
					if(values.size() > 0)
						sqlite.update(BuyBookDBColumn.BUY_TABLE, values, BuyBookDBColumn.BOOK_ID + "=?", new String[]{book.getMediaId()});
					return -100;
				}
			}		
			
			if(saveBuyBook(sqlite, book) == -1)
				return -100;
			
			TryOrFull old = getShelfBookTryOrFullById(sqlite, book.getMediaId());
			if (old != null) {
				// 更新书架数据库，借阅 包月 --> 赠书
				if("1004".equals(book.getBuyType())){
					if(old == TryOrFull.MONTH_FULL || old == TryOrFull.BORROW_FULL){
						ContentValues values = new ContentValues();
						values.put(ShelfBookDBColumn.TRY_OR_FULL, TryOrFull.GIFT_FULL.ordinal());
						sqlite.update(ShelfBookDBColumn.SHELF_TABLE_NAME, values, ShelfBookDBColumn.BOOK_ID + "=?", new String[]{book.getMediaId()});
						list.add(book.getMediaId());
					}
				}
				// 更新书架数据库，借阅 包月 赠书 --> 已购
				else{
					if(old == TryOrFull.MONTH_FULL || old == TryOrFull.BORROW_FULL || old == TryOrFull.GIFT_FULL){
						ContentValues values = new ContentValues();
						//TODO liupan  吧这里的变成赠书改为变为已购
						values.put(ShelfBookDBColumn.TRY_OR_FULL, TryOrFull.FULL.ordinal());
						sqlite.update(ShelfBookDBColumn.SHELF_TABLE_NAME, values, ShelfBookDBColumn.BOOK_ID + "=?", new String[]{book.getMediaId()});
						list.add(book.getMediaId());
					}
				}
			}	
			if(book.isHide() == 0)
				return 1;
			else
				return 0;
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			closeCursor(cursor);
		}
		return -100;
	}
	
	/**
	 * 保存一本书
	 * 
	 * @param sqlite
	 * @param info
	 * @return
	 * @throws SQLException
	 */
	private long saveBuyBook(SQLiteDatabase sqlite, ShelfBook info) throws SQLException {
		ContentValues values = new ContentValues();
		values.put(BuyBookDBColumn.BOOK_ID, info.getMediaId());
		values.put(BuyBookDBColumn.BOOK_NAME, info.getTitle());
		values.put(BuyBookDBColumn.AUTHOR, info.getAuthorPenname());
		values.put(BuyBookDBColumn.AUTHORITY_TYPE, info.getAuthorityType());
		values.put(BuyBookDBColumn.BOOK_SIZE, info.getBookSize());
		values.put(BuyBookDBColumn.COVER_URL, info.getCoverPic());
		values.put(BuyBookDBColumn.USER_ID, info.getUserId());
		values.put(BuyBookDBColumn.USER_NAME, info.getUserName());
		values.put(BuyBookDBColumn.RELATION_TYPE, info.getBuyType());
		values.put(BuyBookDBColumn.LAST_TIME, info.getLastTime());
		values.put(BuyBookDBColumn.IS_HIDE, info.isHide());
		values.put(BuyBookDBColumn.ExpColumn1, info.getBookType().getValue());
		JSONObject json = JSONObject.parseObject("{}");
		json.put("desc", info.getDescs());
		json.put("saleId", info.getSaleId());
		values.put(BuyBookDBColumn.BOOK_JSON, json.toJSONString());
		
        String strPinyin = mHanyu.getStringPinYin(info.getTitle());        
		values.put(BuyBookDBColumn.BOOK_NAME_PINYIN, strPinyin);
		values.put(BuyBookDBColumn.TYPE_ID, info.getGroupType().getName());
		
		long result = sqlite.insert(BuyBookDBColumn.BUY_TABLE, null, values);
		info.setBookJson("");
		if (result != -1)
			info.setId(result);

		return result;
	} 

	/**
	 * 获取所有非赠送的已购，给赠书用
	 * @return
	 */
	public List<ShelfBook> getAllBuyBookList(){
		String booksql = "select * from " + BuyBookDBColumn.BUY_TABLE
				+ " where " + BuyBookDBColumn.RELATION_TYPE + "<>'1004'"
				+ " and " + BuyBookDBColumn.AUTHORITY_TYPE + "<>'2'"
				+ " order by " + BuyBookDBColumn.LAST_TIME
				+ " desc";
		return getBuyBookList(booksql);
	}
	
	public List<ShelfBook> getBuyBookList(int type, int len, int size, int isHide, String category){
		String booksql = null;
		switch(type){
		case 0:
			booksql = "select * from " + BuyBookDBColumn.BUY_TABLE
				+ " where " + BuyBookDBColumn.IS_HIDE + "=" + isHide
				+ " order by " + BuyBookDBColumn.LAST_TIME
				+ " desc limit " + len + "," + size;
			break;
		case 1:
			booksql = "select * from " + BuyBookDBColumn.BUY_TABLE
				+ " where " + BuyBookDBColumn.IS_HIDE + "=" + isHide
				+ " order by " + BuyBookDBColumn.BOOK_NAME_PINYIN
				+ " asc limit " + len + "," + size;
			break;
		case 2:
			booksql = "select * from " + BuyBookDBColumn.BUY_TABLE
				+ " where " + BuyBookDBColumn.IS_HIDE + "=" + isHide
				+ " and " + BuyBookDBColumn.RELATION_TYPE + "='" + category + "'"
				+ " order by " + BuyBookDBColumn.LAST_TIME
				+ " desc limit " + len + "," + size;
			break;
		}
		if(TextUtils.isEmpty(booksql))
			return null;
		
		return getBuyBookList(booksql);
	}
	
	private List<ShelfBook> getBuyBookList(String booksql){
		synchronized (this) {
			List<ShelfBook> list = new LinkedList<ShelfBook>();
			SQLiteDatabase sqlite = mDB.getWritableDatabase();
			Cursor cursor = null;
			try{
				cursor = sqlite.rawQuery(booksql, null);
				while (cursor != null && cursor.moveToNext()) {
					ShelfBook book = getBuyBook(cursor);
					list.add(book);
				}
				return list;
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				closeCursor(cursor);
				closeSqlite(sqlite);
			}
		}
		return null;
	}
	
	public List<String> getBuyBookIdList(){
		synchronized (this) {
			List<String> list = new LinkedList<String>();
			SQLiteDatabase sqlite = mDB.getWritableDatabase();
			Cursor cursor = null;
			try{
				cursor = sqlite.query(BuyBookDBColumn.BUY_TABLE, new String[]{BuyBookDBColumn.BOOK_ID},
						BuyBookDBColumn.RELATION_TYPE + "=?", new String[]{"2000"}, null, null, null);
				while (cursor != null && cursor.moveToNext()) {
					list.add(cursor.getString(0));
				}
				return list;
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				closeCursor(cursor);
				closeSqlite(sqlite);
			}
		}
		return null;
	}
	
	private ShelfBook getBuyBook(Cursor cursor) {
		ShelfBook book = new ShelfBook();
		book.setMediaId(cursor.getString(cursor.getColumnIndex(BuyBookDBColumn.BOOK_ID)));
		book.setTitle(cursor.getString(cursor.getColumnIndex(BuyBookDBColumn.BOOK_NAME)));
		book.setAuthorPenname(cursor.getString(cursor.getColumnIndex(BuyBookDBColumn.AUTHOR)));
		book.setAuthorityType(cursor.getString(cursor.getColumnIndex(BuyBookDBColumn.AUTHORITY_TYPE)));
		book.setBookSize(cursor.getLong(cursor.getColumnIndex(BuyBookDBColumn.BOOK_SIZE)));
		book.setCoverPic(cursor.getString(cursor.getColumnIndex(BuyBookDBColumn.COVER_URL)));
		book.setUserId(cursor.getString(cursor.getColumnIndex(BuyBookDBColumn.USER_ID)));
		book.setUserName(cursor.getString(cursor.getColumnIndex(BuyBookDBColumn.USER_NAME)));
		book.setBuyType(cursor.getString(cursor.getColumnIndex(BuyBookDBColumn.RELATION_TYPE)));
		book.setLastTime(cursor.getLong(cursor.getColumnIndex(BuyBookDBColumn.LAST_TIME)));
		book.setHide(cursor.getInt(cursor.getColumnIndex(BuyBookDBColumn.IS_HIDE)));
		try{
			book.setBookType(BookType.valueOf(cursor.getInt(cursor.getColumnIndex(BuyBookDBColumn.ExpColumn1))));
		}catch(Exception e){
			book.setBookType(BookType.BOOK_TYPE_NOT_NOVEL);
		}		

		String str = cursor.getString(cursor.getColumnIndex(BuyBookDBColumn.BOOK_JSON));
		try{
			JSONObject json = JSONObject.parseObject(str);
			book.setDescs(json.getString("desc"));
			book.setSaleId(json.getString("saleId"));
		}catch(Exception e){
			e.printStackTrace();
		}
		
		GroupType type = new GroupType();
		type.setName(cursor.getString(cursor.getColumnIndex(BuyBookDBColumn.TYPE_ID)));
		book.setGroupType(type);
		if("1004".equals(book.getBuyType()))
			book.setTryOrFull(TryOrFull.GIFT_FULL);
		else if(book.getBookType() != BookType.BOOK_TYPE_NOT_NOVEL){
			if(book.getBookType() == BookType.BOOK_TYPE_IS_FULL_YES && "1".equals(book.getAuthorityType()))
				book.setTryOrFull(TryOrFull.FULL);
			else
				book.setTryOrFull(TryOrFull.TRY);
		}else
			book.setTryOrFull(TryOrFull.FULL);
		
		return book;
	}
	
	public void clearData(){
		synchronized (this) {
			SQLiteDatabase sqlite = mDB.getWritableDatabase();
			try{
				sqlite.delete(BuyBookDBColumn.BUY_TABLE, null, null);
				sqlite.delete(BuyBookDBColumn.CATEGORY_TABLE, null, null);
				sqlite.delete(BuyBookDBColumn.TYPE_TABLE, null, null);
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				closeSqlite(sqlite);
			}
		}
	}
	
	public List<GroupItem> getCategoryList(){
		List<GroupItem> list = new ArrayList<GroupItem>();
		synchronized (this) {
			SQLiteDatabase sqlite = mDB.getWritableDatabase();
			Cursor cursor = null;
			try{
				sqlite.beginTransaction();
				String booksql = "select * from " + BuyBookDBColumn.CATEGORY_TABLE 
						+ " where " + BuyBookDBColumn.BOOK_NUM + ">0"
						+ " order by " + BuyBookDBColumn.CREATE_TIME
						+ " desc";
				cursor = sqlite.rawQuery(booksql, null);
				while (cursor != null && cursor.moveToNext()) {
					GroupItem group = getGroupItem(cursor);
					list.add(group);
				}
				closeCursor(cursor);
				
				for(GroupItem group : list){
					booksql = "select " + BuyBookDBColumn.COVER_URL + " from " + BuyBookDBColumn.BUY_TABLE 
							+ " where " + BuyBookDBColumn.RELATION_TYPE + "=? and "
							+ BuyBookDBColumn.IS_HIDE + "=0"
							+ " order by " + BuyBookDBColumn.LAST_TIME + " desc limit 0,4";// + group.type.getId();
					cursor = sqlite.rawQuery(booksql, new String[]{ group.type.getName() });
					while (cursor != null && cursor.moveToNext()) {
						ShelfBook book = new ShelfBook();
						book.setCoverPic(cursor.getString(0));
						group.list.add(book);
					}					
				}
				sqlite.setTransactionSuccessful();
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				if(sqlite != null)
					sqlite.endTransaction();
				closeCursor(cursor);
				closeSqlite(sqlite);
			}
		}
		return list;
	}
	
	private GroupItem getGroupItem(Cursor cursor){
		GroupType type = new GroupType();
		type.setCreateTime(cursor.getLong(cursor.getColumnIndex(BuyBookDBColumn.CREATE_TIME)));
		type.setName(cursor.getString(cursor.getColumnIndex(BuyBookDBColumn.CATEGORY)));
		type.setId(cursor.getInt(cursor.getColumnIndex(BuyBookDBColumn.BOOK_NUM)));
		List<ShelfBook> list = new ArrayList<ShelfBook>();
		GroupItem item = new GroupItem(type, list);
		return item;
	}
	
	public List<ShelfBook> getBuyBookById(List<String> ids){
		synchronized (this) {
			List<ShelfBook> list = new LinkedList<ShelfBook>();
			SQLiteDatabase sqlite = null;
			Cursor cursor = null;
			try{
				sqlite = mDB.getWritableDatabase();
				sqlite.beginTransaction();
				for(String id : ids){
					String booksql = "select * from " + BuyBookDBColumn.BUY_TABLE
							+ " where " + BuyBookDBColumn.BOOK_ID + "=" + id;
					cursor = sqlite.rawQuery(booksql, null);
					if (cursor != null && cursor.moveToNext()) {
						ShelfBook book = getBuyBook(cursor);
						list.add(book);
					}
					closeCursor(cursor);
				}
				sqlite.setTransactionSuccessful();
				return list;
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				if(sqlite != null)
					sqlite.endTransaction();
				closeCursor(cursor);
				closeSqlite(sqlite);
			}
		}
		return null;
	}

	/**
	 * 分页获取所有已购，添加攻略时使用
	 * @param len  索引
	 * @param limit size
	 * @return
	 */
	public List<ShelfBook> getAllBuyBookList(int len, int limit) {
//		String booksql;
//		if (last_time == 0) {
//			booksql = "select * from " + BuyBookDBColumn.BUY_TABLE
//					+ " order by " + BuyBookDBColumn.LAST_TIME + " desc"
//					+ " limit 0," + limit;
//		} else {
//			booksql = "select * from " + BuyBookDBColumn.BUY_TABLE
//					+ " where " + BuyBookDBColumn.LAST_TIME + " < " + last_time
//					+ " order by " + BuyBookDBColumn.LAST_TIME + " desc"
//					+ " limit 0," + limit;
//		}
		String booksql = "select * from " + BuyBookDBColumn.BUY_TABLE
				+ " order by " + BuyBookDBColumn.LAST_TIME + " desc"
				+ " limit " + len + "," + limit;
		return getBuyBookList(booksql);
	}

	/**
	 * 搜索已购，分页，根据 LAST_TIME
	 * @param keyword   关键字
	 * @param len  索引
	 * @param limit  SIZE
	 * @return
	 */
	public List<ShelfBook> searchBuyBookByKeyword(String keyword, int len, int limit) {
		synchronized (this) {
			List<ShelfBook> list = new LinkedList<ShelfBook>();
			SQLiteDatabase sqlite = mDB.getWritableDatabase();
			Cursor cursor = null;
			try {
				String booksql = "select * from " + BuyBookDBColumn.BUY_TABLE + " where "
						+ BuyBookDBColumn.BOOK_NAME + " like '%" + keyword + "%' or "
						+ BuyBookDBColumn.AUTHOR + " like '%" + keyword + "%'"
						+ " order by " + BuyBookDBColumn.LAST_TIME + " desc"
						+ " limit " + len + "," + limit;
				cursor = sqlite.rawQuery(booksql, null);
				if (cursor != null) {
					while (cursor.moveToNext()) {
						ShelfBook book = getBuyBook(cursor);
						list.add(book);
					}
				}
				closeCursor(cursor);
				return list;
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				closeCursor(cursor);
				closeSqlite(sqlite);
			}
		}
		return null;
	}

	/**
	 * 搜索非赠送的已购，给赠书用
	 * @return
	 */
	public List<ShelfBook> searchBuyBookToGiveByKeyword(String keyword) {
		synchronized (this) {
			List<ShelfBook> list = new LinkedList<ShelfBook>();
			SQLiteDatabase sqlite = mDB.getWritableDatabase();
			Cursor cursor = null;
			try {
				String booksql = "select * from " + BuyBookDBColumn.BUY_TABLE + " where "
						+ BuyBookDBColumn.RELATION_TYPE + "<>'1004'" + " and "
						+ BuyBookDBColumn.AUTHORITY_TYPE + "<>'2'" + " and " + "("
						+ BuyBookDBColumn.BOOK_NAME + " like '%" + keyword + "%' or "
						+ BuyBookDBColumn.AUTHOR + " like '%" + keyword + "%' )";
				cursor = sqlite.rawQuery(booksql, null);
				if (cursor != null) {
					while (cursor.moveToNext()) {
						ShelfBook book = getBuyBook(cursor);
						list.add(book);
					}
				}
				closeCursor(cursor);
				return list;
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				closeCursor(cursor);
				closeSqlite(sqlite);
			}
		}
		return null;
	}

	public List<ShelfBook> searchBuyBookByKeyword(String keyword) {
		synchronized (this) {
			List<ShelfBook> list = new LinkedList<ShelfBook>();
			SQLiteDatabase sqlite = mDB.getWritableDatabase();
			Cursor cursor = null;
			try {
				/*String booksql = "select * from " + BuyBookDBColumn.BUY_TABLE + " where "
						+ BuyBookDBColumn.BOOK_NAME + " like '%" + keyword + "%' or "
						+ BuyBookDBColumn.AUTHOR + " like '%" + keyword + "%' or "
						+ BuyBookDBColumn.BOOK_JSON + " like '%" + keyword + "%'";*/
				String booksql = "select * from " + BuyBookDBColumn.BUY_TABLE + " where "
						+ BuyBookDBColumn.BOOK_NAME + " like '%" + keyword + "%' or "
						+ BuyBookDBColumn.AUTHOR + " like '%" + keyword + "%'";
				cursor = sqlite.rawQuery(booksql, null);
				if (cursor != null) {
					while (cursor.moveToNext()) {
						ShelfBook book = getBuyBook(cursor);
						list.add(book);
					}
				}
				closeCursor(cursor);
				return list;
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				closeCursor(cursor);
				closeSqlite(sqlite);
			}
		}
		return null;
	}
}
