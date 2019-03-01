package com.moxi.bookstore.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.moxi.bookstore.modle.BookStoreFile;
import com.moxi.bookstore.utils.MD5;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/9/25.
 */
public class TableOperate {
    private DBManager manager;
    private SQLiteDatabase db;
    static TableOperate mInstance;

    private TableOperate() {
        //创建数据库
        manager = DBManager.newInstances();
        db = manager.getDataBase();
    }

    public static TableOperate getInstance() {
        if (null == mInstance) {
            synchronized (TableOperate.class) {
                if (null == mInstance) {
                    mInstance = new TableOperate();
                }
            }
        }
        return mInstance;
    }
    public String getHaveMD5( ){
        Cursor cursor = db.rawQuery("select  "+TableConfig.E_FILEPATHMD5+" from "+TableConfig.TABLE_NAME+" limit 0,1", null);
        cursor.moveToFirst();
        String md5="md5";
        if (!cursor.isAfterLast()){
            md5=cursor.getString(0);
        }
        cursor.close();
        return md5;
    }
    /**
     * 查询数据库的名，数据库的添加
     *
     * @param tableName 查询的数据库的名字
     */
    public ArrayList<EbookDB> query(String tableName, String orderby) {
        ArrayList<EbookDB> list = new ArrayList();
        Cursor cursor = db.rawQuery("select * from " + tableName + " order by " + orderby, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {

            EbookDB t = new EbookDB();
            t.saleId = cursor.getLong(15);
            t.name = cursor.getString(1);
            t.author = cursor.getString(2);
            t.publisher = cursor.getString(3);
            t.iconUrl = cursor.getString(4);
            t.type = cursor.getString(5);
            t.filePath = cursor.getString(6);
            t.publishtime = cursor.getLong(7);
            t.downloadtime = cursor.getLong(8);
            t.lastreadtime = cursor.getLong(9);
            t.chartcount = cursor.getLong(10);
            t.pagecount = cursor.getInt(11);
            t.flag = cursor.getInt(12);
            t.key = cursor.getString(13);
            t.progress = cursor.getString(14);
            t.bookdesc = cursor.getString(16);
            t.lowestprice = cursor.getDouble(17);
            t.orgprice = cursor.getDouble(18);
            t.filePathMd5 = cursor.getString(19);
            list.add(t);
            cursor.moveToNext();
        }
        cursor.close();

        return list;
    }

    /**
     * 查询所有书籍
     *
     * @param tableName
     * @return
     */
    public ArrayList<EbookDB> queryAll(String tableName) {
        ArrayList<EbookDB> list = new ArrayList();
        Cursor cursor = db.rawQuery("select * from " + tableName, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {

            EbookDB t = new EbookDB();
            t.saleId = cursor.getLong(15);
            t.name = cursor.getString(1);
            t.author = cursor.getString(2);
            t.publisher = cursor.getString(3);
            t.iconUrl = cursor.getString(4);
            t.type = cursor.getString(5);
            t.filePath = cursor.getString(6);
            t.publishtime = cursor.getLong(7);
            t.downloadtime = cursor.getLong(8);
            t.lastreadtime = cursor.getLong(9);
            t.chartcount = cursor.getLong(10);
            t.pagecount = cursor.getInt(11);
            t.flag = cursor.getInt(12);
            t.key = cursor.getString(13);
            t.progress = cursor.getString(14);
            t.bookdesc = cursor.getString(16);
            t.lowestprice = cursor.getDouble(17);
            t.orgprice = cursor.getDouble(18);
            t.filePathMd5 = cursor.getString(19);
            list.add(t);
            cursor.moveToNext();

        }
        cursor.close();

        return list;
    }

    /**
     * 根据条件查询
     *
     * @param tableName
     * @param id
     * @param flag
     * @return
     */
    public ArrayList<EbookDB> query(String tableName, long id, int flag) {

        ArrayList<EbookDB> list = new ArrayList();
        Cursor cursor = db.rawQuery("select * from " + tableName + " where saleId= " + id + " and flag= " + flag, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {

            EbookDB t = new EbookDB();
            t.saleId = cursor.getLong(15);
            t.name = cursor.getString(1);
            t.author = cursor.getString(2);
            t.publisher = cursor.getString(3);
            t.iconUrl = cursor.getString(4);
            t.type = cursor.getString(5);
            t.filePath = cursor.getString(6);
            t.publishtime = cursor.getLong(7);
            t.downloadtime = cursor.getLong(8);
            t.lastreadtime = cursor.getLong(9);
            t.chartcount = cursor.getLong(10);
            t.pagecount = cursor.getInt(11);
            t.flag = cursor.getInt(12);
            t.key = cursor.getString(13);
            t.progress = cursor.getString(14);
            t.bookdesc = cursor.getString(16);
            t.lowestprice = cursor.getDouble(17);
            t.orgprice = cursor.getDouble(18);
            File file=new File(t.filePath);
            if (file.exists()&&file.canRead()&&file.length()>0) {
                list.add(t);
            }else {//失效文件删除
                TableOperate.getInstance().delete(TableConfig.TABLE_NAME, TableConfig.E_FILEPATHMD5, MD5.stringToMD5(t.filePath));
                com.mx.mxbase.utils.StringUtils.deleteFile(t.filePath);
            }
            cursor.moveToNext();

        }
        cursor.close();

        return list;
    }

    /**
     * 判断是否是当当书城书籍
     * @param tableName
     * @param savePath
     * @return
     */
    public boolean checkFilexists (String tableName, String savePath) {
        boolean result = false;
        Cursor cursor;
        String sql = "select COUNT(*) from "+ tableName + " where " + TableConfig.E_FILEPATHMD5 + " = '"+ MD5.stringToMD5(savePath) +"'";
        cursor = db.rawQuery(sql, null);
        if(cursor.moveToNext()){
            int count = cursor.getInt(0);
            if(count>0){
                result = true;
            }
        }
        return result;
    }
    /**
     * 根据保存路径查寻书籍
     *
     * @param tableName 表名
     * @param savePath  保存路径
     * @return 返回书籍对象
     */
    public EbookDB queryByPath(String tableName, String savePath) {
        EbookDB t = null;
        Cursor cursor = db.rawQuery("select * from " + tableName + " where " + TableConfig.E_FILEPATHMD5 + " = '"+ MD5.stringToMD5(savePath) +"'",null);
        cursor.moveToFirst();
        if (!cursor.isAfterLast()) {
            t = new EbookDB();
            t.saleId = cursor.getLong(15);
            t.name = cursor.getString(1);
            t.author = cursor.getString(2);
            t.publisher = cursor.getString(3);
            t.iconUrl = cursor.getString(4);
            t.type = cursor.getString(5);
            t.filePath = cursor.getString(6);
            t.publishtime = cursor.getLong(7);
            t.downloadtime = cursor.getLong(8);
            t.lastreadtime = cursor.getLong(9);
            t.chartcount = cursor.getLong(10);
            t.pagecount = cursor.getInt(11);
            t.flag = cursor.getInt(12);
            t.key = cursor.getString(13);
            t.progress = cursor.getString(14);
        }
        cursor.close();
        return t;
    }


    /**
     * 根据保存路径查寻书籍
     *
     * @param tableName 表名
     * @return 返回书籍对象
     */
    public List<BookStoreFile> queryByDDTable(String tableName) {
        BookStoreFile bookStoreFile;
        List<BookStoreFile> tempList = new ArrayList<>();
        Cursor cursor = db.rawQuery("select * from " + tableName, null);
        tempList.clear();
        while (cursor.moveToNext()) {
            bookStoreFile = new BookStoreFile();
            bookStoreFile.filePath = cursor.getString(6);
            bookStoreFile.photoPath = cursor.getString(4);
            tempList.add(bookStoreFile);
        }
        cursor.close();
        return tempList;
    }

    /**
     * 根据保存路径查寻书籍
     *
     * @param tableName 表名
     * @return 返回书籍对象
     */
    public List<BookStoreFile> queryByDDTableName(String search, String tableName) {
        BookStoreFile bookStoreFile;
        List<BookStoreFile> tempList = new ArrayList<>();
        Cursor cursor = db.rawQuery("select * from " + tableName + " where " + TableConfig.E_NAME + " like '%" + search + "%'", null);
        tempList.clear();
        while (cursor.moveToNext()) {
            bookStoreFile = new BookStoreFile();
            bookStoreFile.filePath = cursor.getString(6);
            bookStoreFile.pathMd5=cursor.getString(19);
            bookStoreFile.photoPath = cursor.getString(4);
            tempList.add(bookStoreFile);
        }
        cursor.close();
        return tempList;
    }

    /**
     * 向数据库插入数据
     *
     * @param tableName 数据库插入数据的数据表
     * @param object    数据库插入的对象
     *                  return -1 失败
     */
    public void insert(String tableName, EbookDB object) {
        object.filePathMd5=MD5.stringToMD5(object.filePath);
        if (searchById(tableName, object.saleId)) {
            uptate(tableName, object);
            return;
        }
        ContentValues value = new ContentValues();
        value.put(TableConfig.E_SAELID, object.saleId);
        value.put(TableConfig.E_NAME, object.name);
        value.put(TableConfig.E_AUTHOR, object.author);
        value.put(TableConfig.E_PUBLISHER, object.publisher);
        value.put(TableConfig.E_ICO, object.iconUrl);
        value.put(TableConfig.E_TYPE, object.type);
        value.put(TableConfig.E_FILEPATH, object.filePath);
        value.put(TableConfig.E_PUBTIME, object.publishtime);
        value.put(TableConfig.E_DOWNTIME, object.downloadtime);
        value.put(TableConfig.E_LASTREADTIME, object.lastreadtime);
        value.put(TableConfig.E_CHARTCOUNT, object.chartcount);
        value.put(TableConfig.E_PAGECOUNT, object.pagecount);
        value.put(TableConfig.E_FLAG, object.flag);
        value.put(TableConfig.E_KEY, object.key);
        value.put(TableConfig.E_PROGRESS, object.progress);
        value.put(TableConfig.E_DESC, object.bookdesc);
        value.put(TableConfig.E_LOWESTPRICE, object.lowestprice);
        value.put(TableConfig.E_ORGPRICE, object.orgprice);
        value.put(TableConfig.E_FILEPATHMD5, object.filePathMd5);
        db.insert(tableName, TableConfig.E_ID, value);

    }

    public boolean searchById(String tablename, long id) {
        boolean isSave = false;
        Cursor cursor = db.rawQuery("select " + TableConfig.E_SAELID + " from " + tablename + " where " + TableConfig.E_SAELID + "=" + id, null);
        if (cursor.moveToNext()) {
            isSave = true;
        }
        if (cursor != null) cursor.close();
        return isSave;
    }

    /**
     * 删除数据 试读
     *
     * @param tableName 删除数据库的表名
     * @param fieldName 删除的字段名
     * @param value     删除的字段的值
     * @return n 删除行数
     */
    public int delete(String tableName, String fieldName, String value) {

        int n = db.delete(tableName, fieldName + "=?", new String[]{value});
        return n;
    }


    /**
     * 更改数据库内容
     *
     * @param tableName 更改数据的数据表
     * @param object    更改的数据
     */
    public void uptate(String tableName, EbookDB object) {
        ContentValues value = new ContentValues();
        value.put(TableConfig.E_SAELID, object.saleId);
        value.put(TableConfig.E_NAME, object.name);
        value.put(TableConfig.E_AUTHOR, object.author);
        value.put(TableConfig.E_PUBLISHER, object.publisher);
        value.put(TableConfig.E_ICO, object.iconUrl);
        value.put(TableConfig.E_TYPE, object.type);
        value.put(TableConfig.E_FILEPATH, object.filePath);
        value.put(TableConfig.E_PUBTIME, object.publishtime);
        value.put(TableConfig.E_DOWNTIME, object.downloadtime);
        value.put(TableConfig.E_LASTREADTIME, object.lastreadtime);
        value.put(TableConfig.E_CHARTCOUNT, object.chartcount);
        value.put(TableConfig.E_PAGECOUNT, object.pagecount);
        value.put(TableConfig.E_FLAG, object.flag);
        value.put(TableConfig.E_KEY, object.key);
        value.put(TableConfig.E_PROGRESS, object.progress);
        value.put(TableConfig.E_DESC, object.bookdesc);
        value.put(TableConfig.E_LOWESTPRICE, object.lowestprice);
        value.put(TableConfig.E_ORGPRICE, object.orgprice);
        value.put(TableConfig.E_FILEPATHMD5, object.filePathMd5);
        db.update(tableName, value, TableConfig.E_SAELID + "=?", new String[]{String.valueOf(object.saleId)});

    }
    public void AddMd5(String tableName, EbookDB object){
        ContentValues value = new ContentValues();
        value.put(TableConfig.E_FILEPATHMD5, MD5.stringToMD5(object.filePath));
       int id= db.update(tableName, value, TableConfig.E_SAELID + "=?", new String[]{String.valueOf(object.saleId)});
    }

    public void updataTime(String saleId, String progress) {
        ContentValues value = new ContentValues();
        value.put(TableConfig.E_LASTREADTIME, String.valueOf(System.currentTimeMillis()));
        value.put(TableConfig.E_PROGRESS, progress);
        db.update(TableConfig.TABLE_NAME, value, TableConfig.E_SAELID + "=?", new String[]{saleId});
    }
}
