package com.moxi.bookstore.db;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;

import com.moxi.bookstore.asy.ScanReadFile;
import com.moxi.bookstore.modle.BookStoreFile;
import com.moxi.bookstore.utils.MD5;
import com.mx.mxbase.constant.APPLog;
import com.mx.mxbase.utils.PinyinUtils;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;
import static org.litepal.crud.DataSupport.findBySQL;

/**
 * 阅读文件保存
 * Created by xj on 2017/6/15.
 */

public class SacnReadFileUtils {
    // 初始化类实列
    private static SacnReadFileUtils instatnce = null;

    public static SacnReadFileUtils getInstance(Context context) {
        if (instatnce == null) {
            synchronized (SacnReadFileUtils.class) {
                if (instatnce == null) {
                    instatnce = new SacnReadFileUtils();
                    instatnce.initScan(context);
                }
            }
        }
        return instatnce;
    }

    /**
     * 小型数据库读取
     */
    private SharedPreferences preferences;
    /**
     * 小型数据库写入
     */
    private SharedPreferences.Editor editor;
    private static String TABLE = "BookStoreFile";
    private static final String sqlSlect = "id,filePath,pathMd5,photoPath,_index,fullPinyin,isDdBook,bookImageUrl,startAssic";
//    private List<String> paths;
    private List<ScanReadFile.ScanReadListner> scanListners = new ArrayList<>();

    private void initScan(Context context) {
        // 初始化小型数据库的读写
        preferences = context.getSharedPreferences("scanRead", MODE_PRIVATE);
        editor = preferences.edit();
//        paths = PathUtils.getExtSDCardPathList(context);
    }

    private long getaddIndex() {
        long index = preferences.getLong("addIndex", 0);
        index++;
        editor.putLong("addIndex", index);
        editor.commit();
        return index;
    }

    private long getUpdateIndex() {
        long index = preferences.getLong("upIndex", 0);
        index--;
        editor.putLong("upIndex", index);
        editor.commit();
        return index;
    }
    public boolean getFirstSetting(){
        return preferences.getBoolean("settingFiles", true);
    }
    public void SetFirstSetting(){
        editor.putBoolean("settingFiles", false);
        editor.commit();
    }

    private ContentValues getContentValues(BookStoreFile model) {
        ContentValues values = new ContentValues();
        values.put("filePath", model.filePath);
        values.put("photoPath", model.photoPath);
        values.put("_index", model._index);
        return values;
    }

    /**
     * 获取文件个数
     *
     * @param size 获取文件个数，小于等于0代表获取所有书籍
     * @return 返回文件集合
     */
    public Cursor getBookStoreRecentReading(int size) {
        String sql = "select " + sqlSlect + " from " + TABLE + " order by _index ASC limit 0," + size;
        return findBySQL(sql);
    }

    /**
     * 获取文件个数
     *
     * @param size 获取文件个数，小于等于0代表获取所有书籍
     * @return 返回文件集合
     */
    public List<BookStoreFile> getBookStoreSize(int size) {
        List<BookStoreFile> fils = new ArrayList<>();
        String table;
        if (size > 0) {
            table = "select " + sqlSlect + " from " + TABLE + " order by _index ASC limit 0," + size;
        } else {
            table = "select " + sqlSlect + " from " + TABLE + "  order by _index ASC";
        }
        Cursor cursor = findBySQL(table);
        if (cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast() && (cursor.getString(1) != null)) {
                BookStoreFile info = getModel(cursor);
                if (info!=null) {
                    fils.add(info);
                }
                cursor.moveToNext();
            }
            cursor.close();
        }
        return fils;
    }


    /**
     * 根据拼音排序查询数据库
     *
     * @param size 查询数量
     *             _CI(CS) 是否区分大小写，CI不区分，CS区分
     *             _AI(AS) 是否区分重音，AI不区分，AS区分
     *             _KI(KS) 是否区分假名类型,KI不区分，KS区分
     *             _WI(WS) 是否区分宽度 WI不区分，WS区分
     * @return
     */
    public List<BookStoreFile> getBookStoreSizeOrderByPinyin(int size) {
        List<BookStoreFile> files = new ArrayList<>();
        String sqlBook;
        if (size > 0) {
            sqlBook = "select " + sqlSlect + " from " + TABLE + " order by fullPinyin ASC limit 0," + size;
        } else {
            sqlBook = "select " + sqlSlect + " from " + TABLE + " order by fullPinyin ASC";
        }
        Cursor cursor = findBySQL(sqlBook);
        if (cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast() && (cursor.getString(1) != null)) {
                BookStoreFile info = getModel(cursor);
                if (info!=null) {
                    files.add(info);
                }
                cursor.moveToNext();
            }
            cursor.close();
        }
        return files;
    }

    /**
     * 获取文件个数
     *
     * @param size 获取文件个数，小于等于0代表获取所有书籍
     * @return 返回文件集合
     */
    public List<BookStoreFile> getSearchBookStoreSize(String search, int size) {
        List<BookStoreFile> fils = new ArrayList<>();
        String table;
        if (size > 0) {
            table = "select " + sqlSlect + " from " + TABLE + " where filePath like '%" + search + "%' order by _index ASC limit 0," + size;
        } else {
            table = "select " + sqlSlect + " from " + TABLE + " where filePath like '%" + search + "%' order by _index ASC";
        }
        Cursor cursor = findBySQL(table);
        if (cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast() && (cursor.getString(1) != null)) {
                BookStoreFile info = getModel(cursor);
                if (info!=null) {
                    fils.add(info);
                }
                cursor.moveToNext();
            }
            cursor.close();
        }
        return fils;
    }

    /**
     * 查询当当书库
     *
     * @param size
     * @return
     */
    public List<BookStoreFile> searchDDBookStore(int size) {
        List<BookStoreFile> fils = new ArrayList<>();
        String sql;
        if (size > 0) {
            sql = "select " + sqlSlect + " from " + TABLE + " where isDdBook = '1' order by _index ASC limit 0," + size;
        } else {
            sql = "select " + sqlSlect + " from " + TABLE + " where isDdBook = '1'  order by _index ASC";
        }
        Cursor cursor = findBySQL(sql);
        if (cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast() && (cursor.getString(1) != null)) {
                BookStoreFile info = getModel(cursor);
                if (info!=null) {
                    fils.add(info);
                }
                cursor.moveToNext();
            }
            cursor.close();
        }
        return fils;
    }

    /**
     * 查询当当书库
     *
     * @param size
     * @return
     */
    public List<BookStoreFile> searchDDBookStoreByFullPinYin(int size) {
        List<BookStoreFile> fils = new ArrayList<>();
        String sql;
        if (size > 0) {
            sql = "select " + sqlSlect + " from " + TABLE + " where isDdBook = '1' order by fullPinyin ASC limit 0," + size;
        } else {
            sql = "select " + sqlSlect + " from " + TABLE + " where isDdBook = '1'  order by fullPinyin ASC";
        }
        Cursor cursor = findBySQL(sql);
        if (cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast() && (cursor.getString(1) != null)) {
                BookStoreFile info = getModel(cursor);
                if (info!=null) {
                    fils.add(info);
                }
                cursor.moveToNext();
            }
            cursor.close();
        }
        return fils;
    }

    /**
     * 保存文件集合
     *
     * @param model 处理的数据模型
     * @return 是否处理成功
     */
    public boolean saveMode(BookStoreFile model) {
        boolean isSucess = false;
        if (!isExits(model.filePath)) {
            model._index = getaddIndex();
            model.pathMd5 = MD5.stringToMD5(model.filePath);
            String temp = model.getName().substring(0, model.getName().lastIndexOf("."));
            model.setFullPinyin(PinyinUtils.getPingYin(temp).toString());
            model.startAssic=getStartAssic(model.getName());

            EbookDB ebookDB = TableOperate.getInstance().queryByPath(TableConfig.TABLE_NAME, model.filePath);

            if (TableOperate.getInstance().checkFilexists(TableConfig.TABLE_NAME, model.filePath)) {
                model.setDdBook(1);
                model.setBookImageUrl(ebookDB.getIconUrl());
            } else {
                model.setDdBook(0);
            }
            isSucess = model.save();
        }
        return isSucess;
    }

    /**
     * 保存文件集合
     *
     * @param FilePath 文件存储路径
     * @return 是否处理成功
     */
    public BookStoreFile saveMode(String FilePath) {
        APPLog.e("saveMode-FilePath",FilePath);
        if (!isExits(FilePath)) {
            BookStoreFile model = new BookStoreFile();
            model.filePath = FilePath;
            model._index = getaddIndex();
            String temp = model.getName().substring(0, model.getName().lastIndexOf("."));
            model.setFullPinyin(PinyinUtils.getPingYin(temp).toString());

            model.startAssic=getStartAssic(model.getName());
            model.pathMd5=MD5.stringToMD5(FilePath);
            EbookDB ebookDB = TableOperate.getInstance().queryByPath(TableConfig.TABLE_NAME, model.filePath);

            if (ebookDB!=null) {
                model.setDdBook(1);
                model.setBookImageUrl(ebookDB.getIconUrl());
            } else {
                model.setDdBook(0);
            }
            model.save();
            return model;
        }

        return null;
    }
    private String getStartAssic(String name){
            int end=subStartAscii(name);
            if (end>0) {
                return name.substring(0, end);
            }
        return "";
    }

    private int subStartAscii(String str){
//        int index=str.lastIndexOf(".");
//        if (index>0) {
//            str = str.substring(0, index);
//        }
        int end=0;
        for (int i = 0; i < str.length(); i++) {
            int chr=str.charAt(i);
            if (chr>=33&&chr<=126){
                end++;
            }else {
                break;
            }
        }
        if (end>1) {
            int chr = str.charAt(end - 1);
            if (chr>=58){
                end--;
            }
        }
        return end;
    }
    public void updateIndex(BookStoreFile model) {
        long id = isExitsLong(model.filePath);
        if (id != -1) {
            ContentValues values = new ContentValues();
            values.put("_index", getUpdateIndex());
            DataSupport.update(BookStoreFile.class, values, id);
        } else {
            model._index = getUpdateIndex();
            String temp = model.getName().substring(0, model.getName().lastIndexOf("."));
            model.setFullPinyin(PinyinUtils.getPingYin(temp).toString());

            model.startAssic=getStartAssic(model.getName());
            model.pathMd5=MD5.stringToMD5(model.filePath);
            EbookDB ebookDB = TableOperate.getInstance().queryByPath(TableConfig.TABLE_NAME, model.filePath);

            if (TableOperate.getInstance().checkFilexists(TableConfig.TABLE_NAME, model.filePath)) {
                model.setDdBook(1);
                model.setBookImageUrl(ebookDB.getIconUrl());
            } else {
                model.setDdBook(0);
            }
            model.save();
        }
    }

    public void updateIndex(String filePath) {
        BookStoreFile model = new BookStoreFile();
        model.filePath = filePath;
        long id = isExitsLong(model.filePath);
        if (id != -1) {
            ContentValues values = new ContentValues();
            values.put("_index", getUpdateIndex());
            DataSupport.update(BookStoreFile.class, values, id);
        } else {
            model._index = getUpdateIndex();
            String temp = model.getName().substring(0, model.getName().lastIndexOf("."));
            model.setFullPinyin(PinyinUtils.getPingYin(temp).toString());

            model.startAssic=getStartAssic(model.getName());
            model.pathMd5=MD5.stringToMD5(filePath);
            EbookDB ebookDB = TableOperate.getInstance().queryByPath(TableConfig.TABLE_NAME, model.filePath);

            if (TableOperate.getInstance().checkFilexists(TableConfig.TABLE_NAME, model.filePath)) {
                model.setDdBook(1);
                model.setBookImageUrl(ebookDB.getIconUrl());
            } else {
                model.setDdBook(0);
            }
            model.save();
        }
    }

    /**
     * 是否存在书籍
     *
     * @param filePath 文件路径
     * @return
     */
    public boolean isExits(String filePath) {
//        File file=new File(filePath);
//        if (!file.exists()||file.getName().startsWith("."))return true;
        String md5= MD5.stringToMD5(filePath);
        APPLog.e(filePath,md5);
        boolean is = false;
        String table = "select COUNT(*) from " + TABLE + " where pathMd5='"+md5+"'";

        Cursor cur = DataSupport.findBySQL(table);
        if (cur != null && cur.moveToNext()) {
            is = cur.getInt(0) != 0;
        }
        cur.close();
        APPLog.e("isExits="+filePath,is);
        return is;
    }

    public void deleteFile(String filePath) {
        long id = isExitsLong(filePath);
        if (id >= 0)
            DataSupport.delete(BookStoreFile.class, id);
        TableOperate.getInstance().delete(TableConfig.TABLE_NAME, TableConfig.E_FILEPATHMD5, MD5.stringToMD5(filePath));
    }
    public void clearDb(){
        DataSupport.deleteAll(BookStoreFile.class,"isDdBook=0");
    }

    /**
     * 是否存在书籍
     *
     * @param filePath 文件路径
     * @return
     */
    public long isExitsLong(String filePath) {
        String md5= MD5.stringToMD5(filePath);
        long is = -1;
        String table = "select id from " + TABLE + " where pathMd5='"+md5+"'";
        Cursor cur = findBySQL(table);
        if (cur != null && cur.moveToNext()) {
            is = cur.getInt(0);
        }
        cur.close();
        return is;
    }

    private BookStoreFile getModel(Cursor cursor) {
        /** 索引id */
        long id = cursor.getLong(0);
        /** 文件保存路径 */
        String filePath = cursor.getString(1);
//        File file=new File(filePath);
//        if (!file.exists()||!file.canRead()) {
//            deleteFile(filePath);
//            return null;
//        }
        String pathMd5 = cursor.getString(2);
        /** 文件图片路径 */
        String photoPath = cursor.getString(3);
        /** 文件排序索引 */
        long _index = cursor.getInt(4);
        /** 文件全拼 */
        String fullPinyin = cursor.getString(5);
        /**是否为当当书记**/
        int isDdBook = cursor.getInt(6);
        /**书籍图片*/
        String bookImageUrl = cursor.getString(7);
        String startAssic = cursor.getString(8);

        BookStoreFile model = new BookStoreFile();
        model.id = id;
        model.filePath = filePath;
        model.pathMd5 = pathMd5;
        model.photoPath = photoPath;
        model._index = _index;
        model.fullPinyin = fullPinyin;
        model.isDdBook = isDdBook;
        model.bookImageUrl = bookImageUrl;
        model.startAssic = startAssic;
        return model;
    }

    private ScanReadFile scanReadFile;

    public void SearchBooks(Context context, ScanReadFile.ScanReadListner listner) {
        addListener(listner);
        if (scanReadFile == null) {
            scanReadFile = new ScanReadFile(scanReadListner);
            new Thread(scanReadFile).start();
        }
    }

    private ScanReadFile.ScanReadListner scanReadListner = new ScanReadFile.ScanReadListner() {
        @Override
        public void onScanReadEnd() {
            for (int i = 0; i < scanListners.size(); i++) {
                scanListners.get(i).onScanReadEnd();
            }
            APPLog.e("onScan-结束了");
            scanReadFile = null;
        }

        @Override
        public void onScanReadFile(BookStoreFile file) {
            APPLog.e("onScan", file.filePath);
            for (int i = 0; i < scanListners.size(); i++) {
                scanListners.get(i).onScanReadFile(file);
            }
        }
    };

    private void addListener(ScanReadFile.ScanReadListner listner) {
        if (listner != null && !scanListners.contains(listner)) {
            scanListners.add(listner);
        }
    }

    public void removeListener(ScanReadFile.ScanReadListner listner) {
        if (listner != null)
            scanListners.remove(listner);
    }

    public void ondestory() {
        scanListners.clear();
        if (scanReadFile != null) {
            scanReadFile.setRun(false);
        }
    }
}
