package com.dangdang.reader.dread.service;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.dangdang.reader.DDApplication;
import com.dangdang.reader.dread.data.ReadTimes;

import java.util.ArrayList;
import java.util.List;

/**
 * 阅读时段
 * Created by Yhyu on 2016/1/20.
 */
public class ReadTimesService extends BaseService {
    public ReadTimesService() {
        super(DDApplication.getApplication());
    }

    public synchronized boolean addReadTimes(ReadTimes readTimes) {
        boolean result = false;
        SQLiteDatabase db = mDB.getWritableDatabase();
        try {
            db.insert(ReadTimes.ReadTimesColumn.TableName, ReadTimes.ReadTimesColumn.COLUMN_PRODUCTID, getContentValues(readTimes));
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeSqliteDb(db);
        }
        return result;
    }

    public synchronized boolean delReadTimes(String productId) {
        boolean result = false;
        SQLiteDatabase db = mDB.getWritableDatabase();
        try {
            db.delete(ReadTimes.ReadTimesColumn.TableName, ReadTimes.ReadTimesColumn.COLUMN_PRODUCTID +
                    "=? and " + ReadTimes.ReadTimesColumn.COLUMN_USERID + "=? ", new String[]{productId, getUserId()});
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeSqliteDb(db);
        }
        return result;
    }

    public synchronized boolean delReadTimesAll() {
        boolean result = false;
        SQLiteDatabase db = mDB.getWritableDatabase();
        try {
            db.delete(ReadTimes.ReadTimesColumn.TableName, ReadTimes.ReadTimesColumn.COLUMN_USERID + "=? ", new String[]{getUserId()});
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeSqliteDb(db);
        }
        return result;
    }

    public synchronized List<ReadTimes> getReadTimes(String productId) {
        SQLiteDatabase db = mDB.getWritableDatabase();
        List<ReadTimes> readTimesList = new ArrayList<ReadTimes>();
        Cursor cursor = null;
        try {
            cursor = db.query(ReadTimes.ReadTimesColumn.TableName,
                    new String[]{ReadTimes.ReadTimesColumn.COLUMN_PRODUCTID, ReadTimes.ReadTimesColumn.COLUMN_STARTTIME, ReadTimes.ReadTimesColumn.COLUMN_ENDTIME}
                    , ReadTimes.ReadTimesColumn.COLUMN_PRODUCTID + "=? and " + ReadTimes.ReadTimesColumn.COLUMN_USERID + "=? ",
                    new String[]{productId, getUserId()}, null, null, null);
            while (cursor != null && cursor.moveToNext()) {
                ReadTimes readTimes = getReadTimes(cursor);
                readTimesList.add(readTimes);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeCursor(cursor);
            closeSqliteDb(db);
        }
        return readTimesList;
    }

    public synchronized List<ReadTimes> getReadTimesAll() {
        SQLiteDatabase db = mDB.getWritableDatabase();
        List<ReadTimes> readTimesList = new ArrayList<ReadTimes>();
        Cursor cursor = null;
        try {
            cursor = db.query(ReadTimes.ReadTimesColumn.TableName,
                    new String[]{ReadTimes.ReadTimesColumn.COLUMN_PRODUCTID, ReadTimes.ReadTimesColumn.COLUMN_STARTTIME, ReadTimes.ReadTimesColumn.COLUMN_ENDTIME}
                    , ReadTimes.ReadTimesColumn.COLUMN_USERID + "=? ",
                    new String[]{getUserId()}, null, null, null);
            while (cursor != null && cursor.moveToNext()) {
                ReadTimes readTimes = getReadTimes(cursor);
                readTimesList.add(readTimes);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeCursor(cursor);
            closeSqliteDb(db);
        }
        return readTimesList;
    }

    private ContentValues getContentValues(ReadTimes readTimes) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(ReadTimes.ReadTimesColumn.COLUMN_PRODUCTID, readTimes.getProductId());
        contentValues.put(ReadTimes.ReadTimesColumn.COLUMN_STARTTIME, readTimes.getStartTime());
        contentValues.put(ReadTimes.ReadTimesColumn.COLUMN_ENDTIME, readTimes.getEndTime());
        contentValues.put(ReadTimes.ReadTimesColumn.COLUMN_USERID, getUserId());
        return contentValues;
    }

    private ReadTimes getReadTimes(Cursor cursor) {
        ReadTimes readTimes = new ReadTimes();
        readTimes.setProductId(cursor.getString(cursor.getColumnIndex(ReadTimes.ReadTimesColumn.COLUMN_PRODUCTID)));
        readTimes.setStartTime(cursor.getLong(cursor.getColumnIndex(ReadTimes.ReadTimesColumn.COLUMN_STARTTIME)));
        readTimes.setEndTime(cursor.getLong(cursor.getColumnIndex(ReadTimes.ReadTimesColumn.COLUMN_ENDTIME)));
        return readTimes;
    }

    private String getUserId() {
        String userid = "undefine";
//        DangUserInfo userInfo = DataUtil.getInstance(DDApplication.getApplication()).getCurrentUser();
//        if (userInfo != null)
//            userid = userInfo.id;
        return userid;
    }
}
