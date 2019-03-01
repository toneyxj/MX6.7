package com.dangdang.reader.db;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabaseLockedException;
import android.database.sqlite.SQLiteOpenHelper;

import com.alibaba.fastjson.JSONObject;
import com.dangdang.reader.personal.domain.GroupType;
import com.dangdang.reader.personal.domain.GroupType.TypeColumn;
import com.dangdang.reader.personal.domain.ShelfBook;
import com.dangdang.reader.utils.Utils;

public class ShelfBookDBHelper extends SQLiteOpenHelper {

	private static final int DB_VERSION = 7;
	private static final String DB_NAME = ShelfBookDBColumn.SHELF_DB_TABLE_NAME + ".db";
	private static ShelfBookDBHelper mInstance;
	private Context mContext;
	
	private AtomicInteger mOpenCounter = new AtomicInteger(0);
	private SQLiteDatabase mDatabase;
	
	public static synchronized ShelfBookDBHelper getInstance(Context context){
		if(mInstance == null)
			mInstance = new ShelfBookDBHelper(context);
		return mInstance;
	}
	
	private ShelfBookDBHelper(Context context) {
		super(context, getDBName(), null, DB_VERSION);
		mContext = context.getApplicationContext();
	}
	
	@Override
	public synchronized SQLiteDatabase getWritableDatabase() {
//		LogM.l("open db");
        if(mOpenCounter.incrementAndGet() == 1 || mDatabase == null || !mDatabase.isOpen()) {
            // Opening new database
        	while(true){
        		try{
        			mDatabase = super.getWritableDatabase();
        			break;
        		}catch(SQLiteDatabaseLockedException e){
        			try{
        				Thread.sleep(100);
        			}catch(Throwable ex){
        				ex.printStackTrace();
        			}
        		}
        	}            
        }
        return mDatabase;
    }
	
	@Override
	public synchronized SQLiteDatabase getReadableDatabase() {
		return getWritableDatabase();
    }
	
	public synchronized void closeSqlite(SQLiteDatabase sqlite) {
//		LogM.l("close db");
//        if(mOpenCounter.decrementAndGet() == 0) {
//            // Closing database
//            mDatabase.close();
//        }
    }
	
	private static String getDBName(){
		String name = DB_NAME;
		return name;
	}

	public synchronized void release(){		
		if(mInstance != null){
			try{
				if(mDatabase != null && mDatabase.isOpen())
					mDatabase.close();
				this.close();
			}catch(Exception e){
				e.printStackTrace();
			}
			mInstance = null;
		}
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		try{
			// 书架分组关系表
			String sql = GroupType.createBookTypeTable(GroupType.TypeColumn.SHELF_TYPE_TABLE);
			db.execSQL(sql);
			
			// 平台类型关系表
			sql = GroupType.createBookTypeTable(GroupType.TypeColumn.UNDOWN_TYPE_TABLE);
			db.execSQL(sql);
			
			// 书架
			sql = ShelfBook.createShelfTable();
			db.execSQL(sql);
			
			// 登录账号
			sql = UserDBColumn.createUserTable();
			db.execSQL(sql);

			// 云书架已购
			sql = BuyBookDBColumn.createBuyTable();
			db.execSQL(sql);
		
			// 云书架已购类型（试读，全本，借阅，赠书，小说）
			sql = BuyBookDBColumn.createCategoryTable(BuyBookDBColumn.CATEGORY_TABLE);
			db.execSQL(sql);
			
			// 云书架已购分类 (财经，诗歌，小说，散文。。。)
			sql = BuyBookDBColumn.createCategoryTable(BuyBookDBColumn.TYPE_TABLE);
			db.execSQL(sql);
									
						
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	class Item{
		String bookId;
		String bookType;
		String bookJson;
		
		Item(String id, String type, String json){
			bookId = id;
			bookType = type;
			bookJson = json;
		}
	}

	/**
	 * 把BookType从json里拿出来，赠书需要作为查询主键
	 * @param db
	 */
	private void updateBuyBookType(SQLiteDatabase db){
		List<Item> list = new LinkedList<Item>();
		String booksql = "select " + BuyBookDBColumn.BOOK_ID + "," 
				+ BuyBookDBColumn.BOOK_JSON + " from " + BuyBookDBColumn.BUY_TABLE;
		Cursor cursor = null;
		try{
			cursor = db.rawQuery(booksql, null);
			while (cursor != null && cursor.moveToNext()) {
				String bookId = cursor.getString(0);
				String json = cursor.getString(1);
				try{
					JSONObject obj = JSONObject.parseObject(json);
					String type = obj.remove("bookType").toString();
					Item item = new Item(bookId, type, obj.toJSONString());
					list.add(item);
				}catch(Exception e){
					continue;
				}
			}
			if(cursor != null)
				cursor.close();
			for(Item item : list){
				ContentValues values = new ContentValues();
				values.put(BuyBookDBColumn.BOOK_JSON, item.bookJson);
				values.put(BuyBookDBColumn.ExpColumn1, item.bookType);				
				db.update(BuyBookDBColumn.BUY_TABLE, values, BuyBookDBColumn.BOOK_ID + "=?", new String[]{item.bookId});
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		switch(oldVersion){
		case 1:
		case 2:
			Utils.resetRefreshTime(mContext);
			db.execSQL("DROP TABLE IF EXISTS " + ShelfBookDBColumn.SHELF_DB_TABLE_NAME);
			db.execSQL("DROP TABLE IF EXISTS " + TypeColumn.SHELF_TYPE_TABLE);
			db.execSQL("DROP TABLE IF EXISTS " + TypeColumn.UNDOWN_TYPE_TABLE);		
			break;
		case 3:
		case 4:
		case 5:
			break;
		case 6:
			updateBuyBookType(db);
			break;
		}
		
		db.execSQL("DROP TABLE IF EXISTS undownbook");
		db.execSQL("DROP TABLE IF EXISTS hidebook");
		
		onCreate(db);
	}
}
