package com.moxi.haierc.hjbook.hjutils;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.moxi.haierc.hjbook.hjdata.HJBookData;
import com.moxi.haierc.hjbook.hjdata.HJPrePathDir;
import com.mx.mxbase.utils.FileUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by King on 2017/8/29.
 */

public class ScanBookUtils {
    public static ScanBookUtils instance;
    public static SQLiteDatabase db;

    public static ScanBookUtils getInstance(Context context) {
        if (instance == null) {
            AssetsDatabaseManager.initManager(context.getApplicationContext());
            AssetsDatabaseManager mg = AssetsDatabaseManager.getManager();
//            db = mg.getDatabase("/mnt/sdcard/", "hjBook.db");
            db = mg.getDatabase("hjBook.db");
            return new ScanBookUtils();
        } else {
            return instance;
        }
    }

    private String selectBook = "id,prePath,filePath,photoPath,openTime,fullPinyin";

    /**
     * 按阅读时间排序
     *
     * @param number
     * @param bookType
     * @return
     */
    public List<HJBookData> getBookData(int number, String bookType) {
        List<HJBookData> listBook = new ArrayList<>();
        String sql;
        if (number == 0) {
            sql = "select " + selectBook + " from HJBookData where prePath = '" + bookType + "' order by fullPinyin ASC";//order by openTime DESC,fullPinyin ASC
        } else {
            sql = "select " + selectBook + " from HJBookData" + " order by openTime DESC,fullPinyin ASC limit 0," + number;
        }
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast() && (cursor.getString(1) != null)) {
                HJBookData info = getBook(cursor);
                listBook.add(info);
                cursor.moveToNext();
            }
            cursor.close();
        }
        return listBook;
    }

    private HJBookData getBook(Cursor cursor) {
        long id = cursor.getLong(0);
        String prePath = cursor.getString(1);
        String filePath = cursor.getString(2);
        String photoPath = cursor.getString(3);
        long openTime = cursor.getLong(4);
        HJBookData model = new HJBookData();
        model.id = id;
        model.prePath = prePath;
        model.filePath = filePath;
        model.photoPath = photoPath;
        model.openTime = openTime;
        return model;
    }

    /**
     * 获取外层目录
     *
     * @param number
     * @return
     */
    private String selectDir = "id,dirName,prePath,photoPath,number";

    public List<HJPrePathDir> getPreDir(int number) {
        List<HJPrePathDir> listDir = new ArrayList<>();
        String sql;
        if (number == 0) {
            sql = "select " + selectDir + " from HJPrePathDir" + " order by sortindex DESC,fullPinyin ASC";
        } else {
            sql = "select " + selectDir + " from HJPrePathDir" + " order by fullPinyin ASC limit 0," + number;
        }
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor != null) {
            listDir.clear();
            cursor.moveToFirst();
            while (!cursor.isAfterLast() && (cursor.getString(1) != null)) {
                HJPrePathDir info = getPreDirModel(cursor);
                listDir.add(info);
                cursor.moveToNext();
            }
            cursor.close();
        }
//        listDir.add(0, null);
        return listDir;
    }

    /**
     * 更新最近阅读时间
     *
     * @param id
     */
    public void updateBookReadTime(long id) {
        String sql = "update HJBookData set openTime = '" + System.currentTimeMillis() + "' where id = " + id;
        db.execSQL(sql);
    }

    private HJPrePathDir getPreDirModel(Cursor cursor) {
        long id = cursor.getLong(0);
        String dirName = cursor.getString(1);
        String prePath = cursor.getString(2);
        String photoPath = cursor.getString(3);
        int number = cursor.getInt(4);
        HJPrePathDir model = new HJPrePathDir();
        model.id = id;
        model.dirName = dirName;
        model.prePath = prePath;
        model.photoPath = photoPath;
        model.number = number;
        return model;
    }
}
