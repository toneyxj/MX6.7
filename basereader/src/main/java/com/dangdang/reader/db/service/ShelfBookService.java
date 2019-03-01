package com.dangdang.reader.db.service;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.SparseArray;

import com.dangdang.reader.Constants;
import com.dangdang.reader.DDApplication;
import com.dangdang.reader.R;
import com.dangdang.reader.db.ShelfBookDBColumn;
import com.dangdang.reader.db.ShelfBookDBHelper;
import com.dangdang.reader.dread.data.ReadInfo;
import com.dangdang.reader.handle.DownloadBookHandle;
import com.dangdang.reader.personal.DataUtil;
import com.dangdang.reader.personal.domain.GroupItem;
import com.dangdang.reader.personal.domain.GroupType;
import com.dangdang.reader.personal.domain.GroupType.TypeColumn;
import com.dangdang.reader.personal.domain.ShelfBook;
import com.dangdang.reader.personal.domain.ShelfBook.BookType;
import com.dangdang.reader.personal.domain.ShelfBook.MonthlyPaymentType;
import com.dangdang.reader.personal.domain.ShelfBook.TryOrFull;
import com.dangdang.reader.utils.DangdangFileManager;
import com.dangdang.reader.utils.InbuildBooks;
import com.dangdang.zframework.network.download.DownloadConstant.Status;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class ShelfBookService extends BookService {

    private static volatile ShelfBookService mInstance;
    private ShelfBookDBHelper mDB;
    protected Context mContext;
    private String TAG;

    public synchronized static ShelfBookService getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new ShelfBookService(context.getApplicationContext());
        }
        return mInstance;
    }

    protected ShelfBookService(Context context) {
        super(context);
        TAG = this.getClass().getName();
        mContext = context;
        mDB = ShelfBookDBHelper.getInstance(context);
    }

    public void release() {
        if (mDB != null) {
            try {
                mDB.release();
                mDB = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (mInstance != null)
            mInstance = null;
    }

    private ShelfBook getShelfBook(Cursor cursor, HashSet<Long> set, boolean withDesc, boolean withJson) {
        if (null==cursor){
            return null;
        }
        ShelfBook info = new ShelfBook();
        JSONObject obj = super.getBook(cursor, info, withDesc);

        info.setBookKey(cursor.getBlob(cursor.getColumnIndex(ShelfBookDBColumn.BOOK_KEY)));
        info.setBookFinish(cursor.getInt(cursor.getColumnIndex(ShelfBookDBColumn.BOOK_FINISH)));
        info.setImport(cursor.getInt(cursor.getColumnIndex(ShelfBookDBColumn.LOCAL_IMPORT)) == 1);
        int tmp = cursor.getInt(cursor.getColumnIndex(ShelfBookDBColumn.BOOK_TYPE));
        info.setBookType(BookType.valueOf(tmp));
        tmp = cursor.getInt(cursor.getColumnIndex(ShelfBookDBColumn.IS_FOLLOW));
        info.setFollow(tmp == 1);
        info.setReadProgress(cursor.getString(cursor.getColumnIndex(ShelfBookDBColumn.READ_PROGRESS)));
        long time = cursor.getLong(cursor.getColumnIndex(ShelfBookDBColumn.LAST_TIME));
        if (time == 0) {
            time = System.currentTimeMillis() / 2;
            if (set != null) {
                while (true) {
                    if (set.contains(time)) {
                        ++time;
                        continue;
                    }
                    set.add(time);
                    break;
                }
            }
        }
        info.setLastTime(time);
        info.setTotalTime(cursor.getString(cursor.getColumnIndex(ShelfBookDBColumn.TOTAL_TIME)));
        info.setDownloadStatus(Status.UNSTART);

        // 包月信息
        tmp = cursor.getInt(cursor.getColumnIndex(ShelfBookDBColumn.MONTHLY_PAYMENT_TYPE));
        info.setMonthlyPaymentType(MonthlyPaymentType.valueOf(tmp));
        info.setDeadline(cursor.getLong(cursor.getColumnIndex(ShelfBookDBColumn.MONTHLY_END_TIME)));
        info.setGroupId(cursor.getInt(cursor.getColumnIndex(ShelfBookDBColumn.LOCAL_GROUP_ID)));

        info.setTryOrFull(TryOrFull.valueOf(cursor.getInt(cursor.getColumnIndex(ShelfBookDBColumn.TRY_OR_FULL))));
        info.setOverDue(cursor.getInt(cursor.getColumnIndex(ShelfBookDBColumn.OVER_DUE)));

        info.setBookStructDatas(cursor.getBlob(cursor.getColumnIndex(ShelfBookDBColumn.BOOK_STRUCT)));
        info.setTotalTime(cursor.getString(cursor.getColumnIndex(ShelfBookDBColumn.TOTAL_TIME)));

        String str = cursor.getString(cursor.getColumnIndex(ShelfBookDBColumn.BOOK_DIR));
        if (TextUtils.isEmpty(str)) {// || !DownloadBookHandle.isValidDownloadUsername(str)){
            if (info.getTryOrFull().ordinal() > 1)
                str = DownloadBookHandle.getHandle(mContext).getBookDest(true, info.getMediaId(), info.getBookType()).getParent();
            else
                str = DownloadBookHandle.getHandle(mContext).getBookDest(false, info.getMediaId(), info.getBookType()).getParent();
        }
        info.setBookDir(str.trim());

        if (obj != null)
            verifyShlefInfo(info, obj, withJson);

        return info;
    }

    private void verifyShlefInfo(ShelfBook info, JSONObject obj, boolean withJson) {
        if (DangdangFileManager.isImportBook(info.getMediaId())) { // 如果是pdf和txt书籍
        } else {
            try {
                info.setPublishDate(obj.optString(Constants.JSON_DATE, ""));
                info.setCoverPic(obj.optString(Constants.JSON_COVER, ""));
                info.setLocalLastIndexOrder(obj.optInt(Constants.JSON_LOCAL, 0));
                info.setServerLastIndexOrder(obj.optInt(Constants.JSON_SERVER, info.getLocalLastIndexOrder()));
                info.setDown(obj.optInt(Constants.JSON_DOWN_STATUS, 0) == 1 ? true : false);
                if (info.getMonthlyPaymentType() != MonthlyPaymentType.DEFAULT_VALUE) {
                }

                if (info.getTryOrFull().ordinal() == 4) { // 借阅
                    info.setBorrowStartTime(obj.getLong(Constants.BORROW_BEGIN_DATE));
                    info.setBorrowTotalTime(obj.getLong(Constants.BORROW_DURATION));
                    info.setCanBorrow(obj.optBoolean(Constants.BORROW_APPEND, true));
                }
                info.setIsOthers(obj.optBoolean(Constants.OTHERS, false));
                info.setStealPercent(obj.optInt(Constants.STEAL_PERCENT, 100));
                info.setPreload(obj.optBoolean(Constants.JSON_PRELOAD, false));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (!withJson)
            info.setBookJson("");
    }

    /**
     * 按阅读时间排序的所有书
     *
     * @return
     */
    public ArrayList<ShelfBook> getAllShelfBookList() {
        synchronized (this) {
            ArrayList<ShelfBook> list = new ArrayList<ShelfBook>();
            SQLiteDatabase sqlite = mDB.getWritableDatabase();
            Cursor cursor = null;
            sqlite.beginTransaction();
            try {
                String booksql = "select * from " + ShelfBookDBColumn.SHELF_TABLE_NAME + " order by " + ShelfBookDBColumn.LAST_TIME
                        + " desc";
                String typesql = "select " + TypeColumn.ID + ", " + TypeColumn.NAME + ", " + TypeColumn.CREATE_TIME + " from "
                        + TypeColumn.UNDOWN_TYPE_TABLE + " where " + TypeColumn.ID + "=";
                cursor = sqlite.rawQuery(booksql, null);
                HashSet<Long> set = new HashSet<Long>();
                while (cursor != null && cursor.moveToNext()) {
                    //获取书架所有的书修改为附加BookJson数据
                    ShelfBook book = getShelfBook(cursor, set, true, true);
                    getBookType(sqlite, typesql + book.getGroupType().getId(), book);
                    list.add(book);
                }
                sqlite.setTransactionSuccessful();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                sqlite.endTransaction();
                closeCursor(cursor);
                closeSqlite(sqlite);
            }

            return list;
        }
    }
    /**
     * 获取书架上的所有包月的书
     *
     * @return
     */
    public ArrayList<ShelfBook> getAllShelfMonthlyBookList() {
        synchronized (this) {
            ArrayList<ShelfBook> list = new ArrayList<ShelfBook>();
            SQLiteDatabase sqlite = mDB.getWritableDatabase();
            Cursor cursor = null;
            sqlite.beginTransaction();
            try {
                String booksql = "select * from " + ShelfBookDBColumn.SHELF_TABLE_NAME + " where "+ ShelfBookDBColumn.TRY_OR_FULL+" = "+TryOrFull.MONTH_FULL.ordinal()+ " order by " + ShelfBookDBColumn.LAST_TIME
                        + " desc";
                String typesql = "select " + TypeColumn.ID + ", " + TypeColumn.NAME + ", " + TypeColumn.CREATE_TIME + " from "
                        + TypeColumn.UNDOWN_TYPE_TABLE + " where " + TypeColumn.ID + "=";
                cursor = sqlite.rawQuery(booksql, null);
                HashSet<Long> set = new HashSet<Long>();
                while (cursor != null && cursor.moveToNext()) {
                    //获取书架所有的书修改为附加BookJson数据
                    ShelfBook book = getShelfBook(cursor, set, true, true);
                    getBookType(sqlite, typesql + book.getGroupType().getId(), book);
                    list.add(book);
                }
                sqlite.setTransactionSuccessful();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                sqlite.endTransaction();
                closeCursor(cursor);
                closeSqlite(sqlite);
            }

            return list;
        }
    }
    /**
     * 获取 书架所有 分组
     *
     * @return
     */
    public List<GroupType> getAllBookGroup() {
        synchronized (this) {
            List<GroupType> list = new ArrayList<GroupType>();
            SQLiteDatabase sqlite = mDB.getReadableDatabase();
            Cursor cursor = null;
            try {
                String typesql = "select " + TypeColumn.ID + ", " + TypeColumn.NAME + ", " + TypeColumn.CREATE_TIME + " from "
                        + TypeColumn.SHELF_TYPE_TABLE + " order by " + TypeColumn.CREATE_TIME + " desc";
                cursor = sqlite.rawQuery(typesql, null);
                while (cursor != null && cursor.moveToNext()) {
                    GroupType type = getBookType(cursor);
                    list.add(type);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                closeCursor(cursor);
                closeSqlite(sqlite);
            }
            return list;
        }
    }

    public ArrayList<GroupItem> getGroupList() {
        synchronized (this) {
            ArrayList<Integer> empty = new ArrayList<Integer>();
            ArrayList<GroupItem> result = new ArrayList<GroupItem>();
            List<GroupType> mBookGroupList = getAllBookGroup();
            ArrayList<ShelfBook> list = getAllShelfBookList();
            for (GroupType group : mBookGroupList) {
                ArrayList<ShelfBook> tmp = new ArrayList<ShelfBook>();
                int id = group.getId();
                int len = list.size();
                for (int j = len - 1; j >= 0; j--) {
                    ShelfBook item = list.get(j);
                    int cid = item.getGroupId();
                    if (id == cid) {
                        tmp.add(0, item);
                        list.remove(j);
                    }
                }
                if (!tmp.isEmpty()) {
                    GroupItem item = new GroupItem(group, tmp);
                    result.add(item);
                } else
                    empty.add(id);
            }
            // 删空分组
            if (!empty.isEmpty()) {
                deleteGroupByIds(empty);
                empty.clear();
            }
            // 未分组列表
            GroupType type = new GroupType();
            type.setId(Constants.UNKNOW_TYPE);
            type.setName(mContext.getString(R.string.bookshelf_no_group));
            GroupItem item = new GroupItem(type, list);
            result.add(0, item);

            return result;
        }
    }

    /**
     * 批量保存 外部导入的 txt 或 其它类型的书籍（pdf等）
     *
     * @param list
     */
    public void saveInputShelfBookList(List<ShelfBook> list) {
        synchronized (this) {
            SQLiteDatabase sqlite = mDB.getWritableDatabase();
            sqlite.beginTransaction();
            try {
                List<ShelfBook> tmp = new ArrayList<ShelfBook>();
                // 保存书籍
                for (ShelfBook info : list) {
                    long rowId = saveShelfBook(sqlite, info);
                    if (rowId == -1)
                        tmp.add(info);
                }
                list.removeAll(tmp);
                sqlite.setTransactionSuccessful();
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                sqlite.endTransaction();
                closeSqlite(sqlite);
            }
        }
    }

    /**
     * 根据 bookId 删除记录
     *
     * @param sqlite
     * @param bid
     * @throws SQLException
     */
    private void deleteBookInBookShelf(SQLiteDatabase sqlite, String bid) throws SQLException {
        String sql = " delete from " + ShelfBookDBColumn.SHELF_TABLE_NAME + " where " + ShelfBookDBColumn.BOOK_ID + "=?";
        sqlite.execSQL(sql, new Object[]{bid});
    }

    public void deleteOneBook(String bid) {
        synchronized (this) {
            SQLiteDatabase sqlite = mDB.getWritableDatabase();
            try {
                deleteBookInBookShelf(sqlite, bid);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                closeSqlite(sqlite);
            }
        }
    }

    public void deleteMultiShelfBook(List<ShelfBook> list) {
        synchronized (this) {
            SQLiteDatabase sqlite = null;
            try {
                sqlite = mDB.getWritableDatabase();
                for (ShelfBook info : list) {
                    if (!info.isSelect())
                        continue;
                    // 先删除 书架上的书籍数据
                    deleteBookInBookShelf(sqlite, info.getMediaId());
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                closeSqlite(sqlite);
            }
        }
    }

    public void deleteMultiShelfBookById(List<ShelfBook> list) {
        synchronized (this) {
            SQLiteDatabase sqlite = null;
            try {
                sqlite = mDB.getWritableDatabase();
                for (ShelfBook info : list) {
                    deleteBookInBookShelf(sqlite, info.getMediaId());
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                closeSqlite(sqlite);
            }
        }
    }

    /**
     * 保存一本书
     *
     * @param sqlite
     * @param info
     * @return
     * @throws SQLException
     */
    private long saveShelfBook(SQLiteDatabase sqlite, ShelfBook info) throws SQLException {
        setBookJson(info);
        ContentValues values = getContentValues(info);
        if (info.getTryOrFull() == TryOrFull.TRY || info.getTryOrFull() == TryOrFull.INNER_TRY) {
            values.put(ShelfBookDBColumn.USER_ID, "");
            values.put(ShelfBookDBColumn.USER_NAME, "");
        }
        values.put(ShelfBookDBColumn.BOOK_DIR, info.getBookDir());
        values.put(ShelfBookDBColumn.BOOK_KEY, info.getBookKey());
        values.put(ShelfBookDBColumn.BOOK_FINISH, info.getBookFinish());
        values.put(ShelfBookDBColumn.TOTAL_TIME, info.getTotalTime());
        BookType bookType = info.getBookType();
        if (bookType == null) {
            values.put(ShelfBookDBColumn.BOOK_TYPE, BookType.BOOK_TYPE_NOT_NOVEL.getValue());
        } else {
            values.put(ShelfBookDBColumn.BOOK_TYPE, info.getBookType().getValue());
        }
        values.put(ShelfBookDBColumn.READ_PROGRESS, info.getReadProgress());
        values.put(ShelfBookDBColumn.LAST_TIME, info.getLastTime());

        if (info.isImport())
            values.put(ShelfBookDBColumn.LOCAL_IMPORT, 1);
        else
            values.put(ShelfBookDBColumn.LOCAL_IMPORT, 0);

        if (info.getMonthlyPaymentType() == null) {
            values.put(ShelfBookDBColumn.MONTHLY_PAYMENT_TYPE, 0);
        } else {
            values.put(ShelfBookDBColumn.MONTHLY_PAYMENT_TYPE, info.getMonthlyPaymentType().ordinal());
        }
        values.put(ShelfBookDBColumn.MONTHLY_END_TIME, info.getDeadline());
        if (info.isFollow())
            values.put(ShelfBookDBColumn.IS_FOLLOW, 1);
        else
            values.put(ShelfBookDBColumn.IS_FOLLOW, 0);

        values.put(ShelfBookDBColumn.TRY_OR_FULL, info.getTryOrFull().ordinal());
        values.put(ShelfBookDBColumn.OVER_DUE, info.getOverDue());

        values.put(ShelfBookDBColumn.TOTAL_TIME, info.getTotalTime());

        // 平台分类信息
        String name = info.getGroupType().getName();
        if (!TextUtils.isEmpty(name)) {
            Cursor cursor = sqlite.query(TypeColumn.UNDOWN_TYPE_TABLE, new String[]{TypeColumn.ID}, TypeColumn.NAME + "=?",
                    new String[]{name}, null, null, null);
            int id;
            if (cursor != null && cursor.moveToFirst()) {
                id = cursor.getInt(0);
            } else {
                ContentValues value = new ContentValues();
                value.put(TypeColumn.CREATE_TIME, System.currentTimeMillis());
                value.put(TypeColumn.NAME, info.getGroupType().getName());
                id = (int) sqlite.insert(TypeColumn.UNDOWN_TYPE_TABLE, null, value);
            }
            closeCursor(cursor);
            info.getGroupType().setId(id);
            values.put(ShelfBookDBColumn.GROUP_ID, id);
        } else
            values.put(ShelfBookDBColumn.GROUP_ID, Constants.UNKNOW_TYPE);
        long result = sqlite.insert(ShelfBookDBColumn.SHELF_TABLE_NAME, null, values);
//        info.setBookJson("");
        if (result != -1)
            info.setId(result);

        return result;
    }

    protected void setBookJson(ShelfBook info) {
        try {
            JSONObject json;
            try {
                json = new JSONObject(info.getBookJson());
            } catch (Exception e) {
                json = new JSONObject();
            }
            json.put(Constants.JSON_DATE, info.getPublishDate());
            json.put(Constants.JSON_SALEID, info.getSaleId());
            json.put(Constants.JSON_COVER, info.getCoverPic());
            json.put(Constants.JSON_DESC, info.getDescs());
            json.put(Constants.JSON_LOCAL, info.getLocalLastIndexOrder());
            json.put(Constants.JSON_SERVER, info.getServerLastIndexOrder());
            json.put(Constants.JSON_PRELOAD, info.isPreload());

            json.put(Constants.BORROW_BEGIN_DATE, info.getBorrowStartTime());
            json.put(Constants.BORROW_DURATION, info.getBorrowTotalTime());
            json.put(Constants.BORROW_APPEND, info.canBorrow());
            json.put(Constants.OTHERS, info.getIsOthers());
            json.put(Constants.STEAL_PERCENT, info.getStealPercent());


            info.setBookJson(json.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据存储路径删除
     *
     * @param dir
     * @return
     */
    public boolean deleteBookByDir(String dir) {
        synchronized (this) {
            SQLiteDatabase db = null;
            try {
                db = mDB.getWritableDatabase();
                String[] args = {String.valueOf(dir)};
                int result = db.delete(ShelfBookDBColumn.SHELF_TABLE_NAME, ShelfBookDBColumn.BOOK_DIR + "=?", args);
                if (result > 0)
                    return true;
            } catch (Exception e) {

            } finally {
                closeSqlite(db);
            }
            return false;
        }
    }

    private void updateGroupInfo(ShelfBook book, int id, SQLiteDatabase sqlite, long time) {
        try {
            ContentValues values = new ContentValues();
            values.put(ShelfBookDBColumn.LAST_TIME, time);
            values.put(ShelfBookDBColumn.LOCAL_GROUP_ID, id);
            sqlite.update(ShelfBookDBColumn.SHELF_TABLE_NAME, values, ShelfBookDBColumn.BOOK_ID + "=?",
                    new String[]{book.getMediaId()});
            book.setGroupId(id);
            book.setLastTime(time);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateGroup(List<ShelfBook> list, int id) {
        synchronized (this) {
            SQLiteDatabase db = null;
            try {
                db = mDB.getWritableDatabase();
                updateGroup(db, list, id, true);
            } catch (Exception e) {

            } finally {
                closeSqlite(db);
            }
        }
    }

    private void updateGroup(SQLiteDatabase db, List<ShelfBook> list, int id, boolean updateGroup) {
        long time = System.currentTimeMillis() + list.size() + 10;
        for (ShelfBook book : list) {
            updateGroupInfo(book, id, db, time--);
        }
        if (!updateGroup)
            return;
        ContentValues values = new ContentValues();
        values.put(TypeColumn.CREATE_TIME, System.currentTimeMillis());
        db.update(TypeColumn.SHELF_TYPE_TABLE, values, TypeColumn.ID + "=?", new String[]{String.valueOf(id)});
    }

    /**
     * 插入新的 类别
     *
     * @param type 类别名称
     * @return 返回 新插入类别所对应 自增id
     */
    public int createGroup(String type, long createtime) {
        synchronized (this) {
            int rowId = -1;
            SQLiteDatabase db = null;
            try {
                db = mDB.getWritableDatabase();
                rowId = createGroup(db, type, createtime).get("id");
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                closeSqlite(db);
            }
            return rowId;
        }
    }

    private HashMap<String, Integer> createGroup(SQLiteDatabase db, String type, long createtime) {
        HashMap<String, Integer> map = new HashMap<String, Integer>();
        Cursor cursor = db.query(TypeColumn.SHELF_TYPE_TABLE, new String[]{TypeColumn.ID}, TypeColumn.NAME + "=?",
                new String[]{type}, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(0);
            closeCursor(cursor);
            ContentValues values = new ContentValues();
            values.put(TypeColumn.CREATE_TIME, createtime);
            db.update(TypeColumn.SHELF_TYPE_TABLE, values, TypeColumn.ID + "=?", new String[]{String.valueOf(id)});
            map.put("id", id);
            map.put("exist", 1);
            return map;
        }
        closeCursor(cursor);
        ContentValues values = new ContentValues();
        values.put(TypeColumn.NAME, type);
        values.put(TypeColumn.CREATE_TIME, createtime);
        int id = (int) db.insert(TypeColumn.SHELF_TYPE_TABLE, null, values);
        map.put("id", id);
        map.put("exist", 0);

        return map;
    }

    public boolean isGroupExist(String name) {
        synchronized (this) {
            SQLiteDatabase db = null;
            Cursor cursor = null;
            try {
                db = mDB.getWritableDatabase();
                cursor = db.query(TypeColumn.SHELF_TYPE_TABLE, new String[]{TypeColumn.ID}, TypeColumn.NAME + "=?",
                        new String[]{name}, null, null, null);
                if (cursor != null && cursor.moveToFirst()) {
                    return true;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                closeCursor(cursor);
                closeSqlite(db);
            }
            return false;
        }
    }

    /**
     * 根据id 删除分组
     */
    public void deleteGroupById(int id) {
        synchronized (this) {
            SQLiteDatabase sqlite = null;
            try {
                sqlite = mDB.getWritableDatabase();
                deleteGroupItem(sqlite, id);
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                closeSqlite(sqlite);
            }
        }
    }

    public void deleteGroupByIds(ArrayList<Integer> list) {
        synchronized (this) {
            SQLiteDatabase sqlite = null;
            try {
                sqlite = mDB.getWritableDatabase();
                for (int id : list) {
                    deleteGroupItem(sqlite, id);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                closeSqlite(sqlite);
            }
        }
    }

    public void deleteGroupByItems(List<GroupItem> list) {
        synchronized (this) {
            SQLiteDatabase sqlite = null;
            try {
                sqlite = mDB.getWritableDatabase();
                for (GroupItem item : list) {
                    deleteGroupItem(sqlite, item.type.getId());
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                closeSqlite(sqlite);
            }
        }
    }

    private void deleteGroupItem(SQLiteDatabase sqlite, int id) {
        sqlite.delete(TypeColumn.SHELF_TYPE_TABLE, TypeColumn.ID + "=?", new String[]{String.valueOf(id)});
    }

    /**
     * 下载一本书，如果本地已有，返回本地记录
     *
     * @param book
     * @return
     */
    public ShelfBook saveOneBook(ShelfBook book) {
        if (book.getBookType() == BookType.BOOK_TYPE_NOT_NOVEL)
            return saveOneReaderBook(book);
        else
            return saveOneNovelBook(book);
    }

    /**
     * 下载一本原创书，如果本地已有，返回本地记录
     *
     * @param book
     * @return
     */
    private ShelfBook saveOneNovelBook(ShelfBook book) {
        synchronized (this) {
            SQLiteDatabase sqlite = null;
            try {
                sqlite = mDB.getWritableDatabase();
                ShelfBook item = getShelfBookById(sqlite, book.getMediaId(), false);
                if (item != null) {
                    if (item.getBookType() == BookType.BOOK_TYPE_IS_FULL_NO && book.getBookType() == BookType.BOOK_TYPE_IS_FULL_YES
                            || item.getTryOrFull() == TryOrFull.TRY && book.getTryOrFull() == TryOrFull.FULL
                            || item.getTryOrFull() == TryOrFull.MONTH_FULL && book.getTryOrFull() == TryOrFull.FULL
                            || item.getTryOrFull() == TryOrFull.TRY && book.getTryOrFull() == TryOrFull.GIFT_FULL
                            || item.getTryOrFull() == TryOrFull.MONTH_FULL && book.getTryOrFull() == TryOrFull.GIFT_FULL
                            || item.getTryOrFull() == TryOrFull.GIFT_FULL && book.getTryOrFull() == TryOrFull.FULL) {
                        book.setReadProgress(item.getReadProgress());
                        book.setGroupId(item.getGroupId());
                        book.setGroupType(item.getGroupType());
                        deleteBookInBookShelf(sqlite, item.getMediaId());
                        DataUtil.getInstance(mContext).deleteFile(book, true);
                        saveShelfBook(sqlite, book);
                        book.setUpdate(true);
                        return book;
                    } else
                        return item;
                }
                saveShelfBook(sqlite, book);
                return null;
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                closeSqlite(sqlite);
            }
            return null;
        }
    }

    private ShelfBook saveOneReaderBook(ShelfBook book) {
        synchronized (this) {
            SQLiteDatabase sqlite = null;
            try {
                sqlite = mDB.getWritableDatabase();

                ShelfBook old = getShelfBookById(sqlite, book.getMediaId(), true);

                if (old != null) {
                    switch (old.getTryOrFull()) {
                        case MONTH_FULL:
                        case BORROW_FULL:
                            // 借阅更新为全本(赠书或已购)
                            if (book.getTryOrFull() == TryOrFull.FULL || book.getTryOrFull() == TryOrFull.GIFT_FULL) {
                                ContentValues values = new ContentValues();
                                values.put(ShelfBookDBColumn.TRY_OR_FULL, book.getTryOrFull().ordinal());
                                sqlite.update(ShelfBookDBColumn.SHELF_TABLE_NAME, values, ShelfBookDBColumn.BOOK_ID + "=?", new String[]{book.getMediaId()});
                                book = getShelfBookById(sqlite, book.getMediaId(), true);
                                book.setUpdate(true);
                                return book;
                            }
                            // 借阅更新为包或者之前就是包月，更新包月时间
                            else if (book.getTryOrFull() == TryOrFull.MONTH_FULL && (old.getTryOrFull() == TryOrFull.BORROW_FULL||old.getTryOrFull() == TryOrFull.MONTH_FULL)) {
                                ContentValues values = new ContentValues();
                                values.put(ShelfBookDBColumn.TRY_OR_FULL, TryOrFull.MONTH_FULL.ordinal());
                                //更新包月时间,
                                values.put(ShelfBookDBColumn.MONTHLY_END_TIME,book.getDeadline());
                                //更新包月信息同步时间，频道id，频道name
                                values.put(ShelfBookDBColumn.BOOK_JSON,book.getBookJson());
                                sqlite.update(ShelfBookDBColumn.SHELF_TABLE_NAME, values, ShelfBookDBColumn.BOOK_ID + "=?", new String[]{book.getMediaId()});
                                book = getShelfBookById(sqlite, book.getMediaId(), true);
                                book.setUpdate(true);
                                return book;
                            }
                            // 续借，更新借阅时间
                            else if (book.getTryOrFull() == TryOrFull.BORROW_FULL &&
                                    book.getBorrowStartTime() + book.getBorrowTotalTime() > old.getBorrowStartTime() + old.getBorrowTotalTime()) {
                                JSONObject json;
                                try {
                                    json = new JSONObject(old.getBookJson());
                                } catch (Exception e) {
                                    json = new JSONObject();
                                }
                                json.put(Constants.BORROW_BEGIN_DATE, book.getBorrowStartTime());
                                json.put(Constants.BORROW_DURATION, book.getBorrowTotalTime());

                                ContentValues values = new ContentValues();
                                values.put(ShelfBookDBColumn.BOOK_JSON, json.toString());
                                values.put(ShelfBookDBColumn.OVER_DUE, 0);
                                sqlite.update(ShelfBookDBColumn.SHELF_TABLE_NAME, values, ShelfBookDBColumn.BOOK_ID + "=?", new String[]{book.getMediaId()});
                                book = getShelfBookById(sqlite, book.getMediaId(), false);
                                book.setUpdate(true);
                                ((DDApplication) mContext.getApplicationContext()).removeValueFromSet(book.getMediaId());
                                return book;
                            }
                            // 试读覆盖过期的借阅
//						else if(book.getTryOrFull() == TryOrFull.TRY && DataUtil.getInstance(mContext).getLastTime(old) < 0){
//							book.setReadProgress(old.getReadProgress());
//							book.setGroupId(old.getGroupId());
//							book.setGroupType(old.getGroupType());
//							deleteBookInBookShelf(sqlite, old.getMediaId());
//							DataUtil.getInstance(mContext).deleteFile(book, true);
//							saveShelfBook(sqlite, book);
//							book.setUpdate(true);
//							return book;
//						}
                            break;
                        case GIFT_FULL:
                            // 赠书更新为已购
                            if (book.getTryOrFull() == TryOrFull.FULL) {
                                ContentValues values = new ContentValues();
                                values.put(ShelfBookDBColumn.TRY_OR_FULL, book.getTryOrFull().ordinal());
                                sqlite.update(ShelfBookDBColumn.SHELF_TABLE_NAME, values, ShelfBookDBColumn.BOOK_ID + "=?", new String[]{book.getMediaId()});
                                book = getShelfBookById(sqlite, book.getMediaId(), false);
                                book.setUpdate(true);
                                return book;
                            }
                            break;
                        case FULL:
                        case INNER_FULL:
                            break;
                        case INNER_TRY:
                        case TRY:
                            //覆盖试读
                            if (book.getTryOrFull() != TryOrFull.TRY) {
                                book.setReadProgress(old.getReadProgress());
                                book.setGroupId(old.getGroupId());
                                book.setGroupType(old.getGroupType());
                                deleteBookInBookShelf(sqlite, old.getMediaId());
                                DataUtil.getInstance(mContext).deleteFile(book, true);
                                saveShelfBook(sqlite, book);
                                book.setUpdate(true);
                                return book;
                            }
                            break;
                        default:
                            break;
                    }
                    return old;
                } else {
                    saveShelfBook(sqlite, book);
                    return null;
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                closeSqlite(sqlite);
            }
            return null;
        }
    }

    /**
     * 按ID取电子书
     *
     * @return
     */
    protected ShelfBook getShelfBookById(SQLiteDatabase sqlite, String bookId, boolean withJson) {
        Cursor cursor = null;
        try {
            String booksql = "select * from " + ShelfBookDBColumn.SHELF_TABLE_NAME + " where " + ShelfBookDBColumn.BOOK_ID + "=? or "
                    + ShelfBookDBColumn.BOOK_ID + "=?";
            String typesql = "select " + TypeColumn.ID + ", " + TypeColumn.NAME + ", " + TypeColumn.CREATE_TIME + " from "
                    + TypeColumn.UNDOWN_TYPE_TABLE + " where " + TypeColumn.ID + "=";
            cursor = sqlite.rawQuery(booksql, new String[]{bookId, InbuildBooks.PUBLIC_KEY_PREFIX + "_" + bookId});
            if (cursor != null && cursor.moveToNext()) {
                ShelfBook book = getShelfBook(cursor, null, true, withJson);
                getBookType(sqlite, typesql + book.getGroupType().getId(), book);
                return book;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeCursor(cursor);
        }
        return null;
    }

    /**
     * 按ID取电子书
     *
     * @return
     */
    public ShelfBook getShelfBookById(String bookId) {
        synchronized (this) {
            SQLiteDatabase sqlite = null;
            try {
                sqlite = mDB.getWritableDatabase();
                ShelfBook item = getShelfBookById(sqlite, bookId, false);
                return item;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 查询所有未同步的书
     *
     * @return
     */
    public List<ShelfBook> getShelfBookNoSyncReadingTime(String userId) {
        synchronized (this) {
            SQLiteDatabase sqlite = mDB.getWritableDatabase();
            Cursor cursor = null;
            JSONObject jsonObj;
            long timeLong;

//			[{"productId":1111,"readingTime":1001},{"productId":2222,"readingTime":9999}]
//			StringBuilder builder = new StringBuilder();
            List<ShelfBook> shelfBookList = new ArrayList<ShelfBook>();
            ShelfBook shelfBook;
            try {
                String booksql = "select * from " + ShelfBookDBColumn.SHELF_TABLE_NAME + " where "
                        + ShelfBookDBColumn.ExpColumn1 + " is null or " + ShelfBookDBColumn.ExpColumn1 + "<>1 ";//and "+ShelfBookDBColumn.USER_ID+" = '"+userId+"' 针对书不区分用户，只要是当当的书都上传
                cursor = sqlite.rawQuery(booksql, null);
                if (cursor != null) {
                    while (cursor.moveToNext()) {
                        String mediaId = cursor.getString(cursor.getColumnIndex(ShelfBookDBColumn.BOOK_ID));
                        String readTimes = cursor.getString(cursor.getColumnIndex(ShelfBookDBColumn.TOTAL_TIME));
                        if (!TextUtils.isEmpty(readTimes) && !DangdangFileManager.isImportBook(mediaId)) {
                            shelfBook = new ShelfBook();
                            shelfBook.setMediaId(mediaId);
                            shelfBook.setTotalTime(readTimes);
                            shelfBookList.add(shelfBook);
                        }

                    }
                }
                closeCursor(cursor);
                return shelfBookList;
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
     * 批量置空阅读时长
     *
     * @param shelfBooks
     */
    public void resetShelfBookSyncReadingTime(List<ShelfBook> shelfBooks) {
        synchronized (this) {
            if (shelfBooks == null || shelfBooks.size() == 0)
                return;
            for (ShelfBook book : shelfBooks) {
                ShelfBook info = getShelfBookById(book.getMediaId());
                if (info == null)
                    return;
                String readTimes = info.getTotalTime();
                JSONObject jsonObj;
                long timeLong = 0;
                long startReadTime = 0;
                long endTime = 0;
                if (readTimes != null) {
                    try {
                        jsonObj = new JSONObject(readTimes);
                        startReadTime = jsonObj.optLong(ReadInfo.JSONK_READ_START_TIME, 0);
                        endTime = jsonObj.optLong(ReadInfo.JSONK_READ_END_TIME, 0);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        startReadTime = 0;
                        endTime = 0;
                    }
                }
                try {
                    JSONObject jObj = new JSONObject();
                    jObj.put(ReadInfo.JSONK_READ_START_TIME, startReadTime);
                    jObj.put(ReadInfo.JSONK_READ_PAUSE_TIME, timeLong);
                    jObj.put(ReadInfo.JSONK_READ_END_TIME, endTime);
                    updateBookReadTime(book.getMediaId(), jObj.toString(), 1);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    /**
     * 按ID取电子书
     *
     * @return
     */
    protected TryOrFull getShelfBookTryOrFullById(SQLiteDatabase sqlite, String bookId) {
        Cursor cursor = null;
        try {
            String booksql = "select " + ShelfBookDBColumn.TRY_OR_FULL + " from " +
                    ShelfBookDBColumn.SHELF_TABLE_NAME + " where " + ShelfBookDBColumn.BOOK_ID + "=?";
            cursor = sqlite.rawQuery(booksql, new String[]{bookId});
            if (cursor != null && cursor.moveToNext()) {
                TryOrFull type = TryOrFull.valueOf(cursor.getInt(cursor.getColumnIndex(ShelfBookDBColumn.TRY_OR_FULL)));
                return type;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeCursor(cursor);
        }
        return null;
    }

    /**
     * 记录电子书证书
     *
     * @param book
     */
    public void saveBookKey(ShelfBook book) {
        saveBookKey(book.getMediaId(), book.getBookKey());
    }

    public void saveBookKey(String bookId, byte[] bookKey) {
        synchronized (this) {
            SQLiteDatabase sqlite = null;
            try {
                sqlite = mDB.getWritableDatabase();
                ContentValues values = new ContentValues();
                values.put(ShelfBookDBColumn.BOOK_KEY, bookKey);
                sqlite.update(ShelfBookDBColumn.SHELF_TABLE_NAME, values, ShelfBookDBColumn.BOOK_ID + "=?", new String[]{bookId});
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                closeSqlite(sqlite);
            }
        }
    }

    /**
     * 记录全本大小
     */
    public void updateBookSize(String bookId, long bookSize) {
        synchronized (this) {
            SQLiteDatabase sqlite = null;
            Cursor cursor = null;
            try {
                sqlite = mDB.getWritableDatabase();
                cursor = sqlite.query(ShelfBookDBColumn.SHELF_TABLE_NAME, new String[]{ShelfBookDBColumn.BOOK_JSON},
                        ShelfBookDBColumn.BOOK_ID + "=?", new String[]{bookId}, null, null, null);
                if (cursor != null && cursor.moveToFirst()) {
                    String str = cursor.getString(cursor.getColumnIndex(ShelfBookDBColumn.BOOK_JSON));
                    JSONObject json;
                    try {
                        json = new JSONObject(str);
                    } catch (Exception e) {
                        json = new JSONObject();
                    }
                    json.put(Constants.JSON_SIZE, bookSize);

                    ContentValues values = new ContentValues();
                    values.put(ShelfBookDBColumn.BOOK_JSON, json.toString());
                    sqlite.update(ShelfBookDBColumn.SHELF_TABLE_NAME, values, ShelfBookDBColumn.BOOK_ID + "=?", new String[]{bookId});
                }
            } catch (Exception e) {

            } finally {
                closeCursor(cursor);
                closeSqlite(sqlite);
            }
        }
    }

    /**
     * 设置下载完成标志
     */
    public void setDownloadFinish(String bookId) {
        synchronized (this) {
            SQLiteDatabase sqlite = null;
            try {
                sqlite = mDB.getWritableDatabase();
                ContentValues values = new ContentValues();
                values.put(ShelfBookDBColumn.BOOK_FINISH, 1);
                sqlite.update(ShelfBookDBColumn.SHELF_TABLE_NAME, values, ShelfBookDBColumn.BOOK_ID + "=?", new String[]{bookId});
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                closeSqlite(sqlite);
            }
        }
    }

    /**
     * 取(未)追更书列表
     *
     * @return
     */
    public List<ShelfBook> getFollowList(boolean isFollow) {
        synchronized (this) {
            ArrayList<ShelfBook> list = new ArrayList<ShelfBook>();
            SQLiteDatabase sqlite = null;
            Cursor cursor = null;
            try {
                sqlite = mDB.getWritableDatabase();
                int tmp = 0;
                if (isFollow)
                    tmp = 1;
                String booksql = "select * from " + ShelfBookDBColumn.SHELF_TABLE_NAME + " where " + ShelfBookDBColumn.IS_FOLLOW + "="
                        + tmp + " and " + ShelfBookDBColumn.BOOK_TYPE + "=" + BookType.BOOK_TYPE_IS_FULL_NO.getValue() + " order by "
                        + ShelfBookDBColumn.LAST_TIME + " desc";
                String typesql = "select " + TypeColumn.ID + ", " + TypeColumn.NAME + ", " + TypeColumn.CREATE_TIME + " from "
                        + TypeColumn.UNDOWN_TYPE_TABLE + " where " + TypeColumn.ID + "=";
                cursor = sqlite.rawQuery(booksql, null);
                HashSet<Long> set = new HashSet<Long>();
                while (cursor != null && cursor.moveToNext()) {
                    ShelfBook book = getShelfBook(cursor, set, true, false);
                    getBookType(sqlite, typesql + book.getGroupType().getId(), book);
                    list.add(book);
                }
            } catch (Exception e) {

            } finally {
                closeCursor(cursor);
                closeSqlite(sqlite);
            }

            return list;
        }
    }

    /**
     * 更新是否追更
     *
     * @param list
     * @param isFollow
     */
    public void updateFollowStatus(List<ShelfBook> list, boolean isFollow) {
        synchronized (this) {
            SQLiteDatabase sqlite = null;
            try {
                sqlite = mDB.getWritableDatabase();
                int tmp = 0;
                if (isFollow)
                    tmp = 1;
                for (ShelfBook book : list) {
                    book.setFollow(isFollow);
                    ContentValues values = new ContentValues();
                    values.put(ShelfBookDBColumn.IS_FOLLOW, tmp);
                    sqlite.update(ShelfBookDBColumn.SHELF_TABLE_NAME, values, ShelfBookDBColumn.BOOK_ID + "=?",
                            new String[]{book.getMediaId()});
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                closeSqlite(sqlite);
            }
        }
    }

    /**
     * 更新阅读时间和阅读进度
     *
     * @param bookId
     * @param progress
     * @return
     */
    public HashMap<String, Long> updateLastTime(String bookId, String progress, byte[] key, boolean isFollow, boolean preload, int index) {
        synchronized (this) {
            HashMap<String, Long> map = new HashMap<String, Long>();
            long time = System.currentTimeMillis();
            map.put("time", time);
            SQLiteDatabase sqlite = null;
            Cursor cursor = null;
            long groupId = Constants.UNKNOW_TYPE;
            try {
                sqlite = mDB.getWritableDatabase();
                ContentValues values = new ContentValues();

                cursor = sqlite.query(ShelfBookDBColumn.SHELF_TABLE_NAME, new String[]{ShelfBookDBColumn.BOOK_JSON, ShelfBookDBColumn.LOCAL_GROUP_ID},
                        ShelfBookDBColumn.BOOK_ID + "=?", new String[]{bookId}, null, null, null);
                if (cursor != null && cursor.moveToFirst()) {
                    String str = cursor.getString(0);
                    JSONObject json;
                    try {
                        json = new JSONObject(str);
                    } catch (Exception e) {
                        json = new JSONObject();
                    }
                    // 查数据库，得到书所属分组
                    groupId = cursor.getInt(1);
                    if (isFollow) { // 如果是追更书，更新本地章节数
                        int server = json.optInt(Constants.JSON_SERVER, 0);
                        if (index > server) {
                            server = index;
                            json.put(Constants.JSON_SERVER, index);
                        }
                        json.put(Constants.JSON_LOCAL, server);
                    }
                    json.put(Constants.JSON_PRELOAD, preload);
                    values.put(ShelfBookDBColumn.BOOK_JSON, json.toString());
                }
                // 更新阅读进度、阅读时间等
                values.put(ShelfBookDBColumn.LAST_TIME, time);
                values.put(ShelfBookDBColumn.READ_PROGRESS, progress);
                if (key != null)
                    values.put(ShelfBookDBColumn.BOOK_KEY, key);

                if (isFollow)
                    values.put(ShelfBookDBColumn.IS_FOLLOW, 1);
                else
                    values.put(ShelfBookDBColumn.IS_FOLLOW, 0);
                sqlite.update(ShelfBookDBColumn.SHELF_TABLE_NAME, values, ShelfBookDBColumn.BOOK_ID + "=?", new String[]{bookId});

                // 更新分组操作时间
                if (groupId == Constants.UNKNOW_TYPE) {
                    map.put("groupId", groupId);
                    return map;
                }
                values = new ContentValues();
                values.put(TypeColumn.CREATE_TIME, time);
                sqlite.update(TypeColumn.SHELF_TYPE_TABLE, values, TypeColumn.ID + "=?", new String[]{String.valueOf(groupId)});
            } catch (Exception e) {

            } finally {
                closeCursor(cursor);
                closeSqlite(sqlite);
            }
            map.put("groupId", groupId);
            return map;
        }
    }

    public void quickGroup(SparseArray<GroupItem> array) {
        synchronized (this) {
            SQLiteDatabase sqlite = null;
            try {
                sqlite = mDB.getWritableDatabase();
                int len = array.size();
                for (int i = 0; i < len; i++) {
                    GroupItem item = array.valueAt(i);
                    if (item.type.getId() == Constants.UNKNOW_TYPE)
                        continue;
                    long time = System.currentTimeMillis();
                    HashMap<String, Integer> map = createGroup(sqlite, item.type.getName(), time);
                    int id = map.get("id");
                    updateGroup(sqlite, item.list, id, false);
                    item.type.setCreateTime(time);
                    item.type.setId(id);
                    if (map.get("exist") == 1)
                        item.isNew = false;
                    else
                        item.isNew = true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                closeSqlite(sqlite);
            }
        }
    }

    /**
     * 搜索推荐列表
     *
     * @return
     */
    public List<ShelfBook> getSearchTipList() {
        synchronized (this) {
            ArrayList<ShelfBook> list = new ArrayList<ShelfBook>();
            SQLiteDatabase sqlite = null;
            Cursor cursor = null;
            try {
                sqlite = mDB.getWritableDatabase();
                String booksql = "select * from " + ShelfBookDBColumn.SHELF_TABLE_NAME + " order by " + ShelfBookDBColumn.LAST_TIME
                        + " desc limit 0,3";
                String typesql = "select " + TypeColumn.ID + ", " + TypeColumn.NAME + ", " + TypeColumn.CREATE_TIME + " from "
                        + TypeColumn.UNDOWN_TYPE_TABLE + " where " + TypeColumn.ID + "=";
                cursor = sqlite.rawQuery(booksql, null);
                HashSet<Long> set = new HashSet<Long>();
                while (cursor != null && cursor.moveToNext()) {
                    ShelfBook book = getShelfBook(cursor, set, true, false);
                    getBookType(sqlite, typesql + book.getGroupType().getId(), book);
                    list.add(book);
                }
            } catch (Exception e) {

            } finally {
                closeCursor(cursor);
                closeSqlite(sqlite);
            }
            return list;
        }
    }

    /**
     * 是否包含搜索关键词
     *
     * @param book
     * @param word
     * @return
     */
    private boolean isValidate(String title, String author, String word) {
        try {
            word = word.toLowerCase(Locale.CHINA);
            if (title != null && title.toLowerCase(Locale.CHINA).contains(word))
                return true;
            if (author != null && author.toLowerCase(Locale.CHINA).contains(word))
                return true;
        } catch (Exception e) {

        }
        return false;
    }

    /**
     * 搜索结果列表
     *
     * @param keyword
     * @return
     */
    public List<ShelfBook> getSearchList(String keyword) {
        synchronized (this) {
            ArrayList<ShelfBook> list = new ArrayList<ShelfBook>();
            SQLiteDatabase sqlite = null;
            Cursor cursor = null;
            try {
                sqlite = mDB.getWritableDatabase();
                String booksql = "select * from " + ShelfBookDBColumn.SHELF_TABLE_NAME + " order by " + ShelfBookDBColumn.LAST_TIME
                        + " desc";
                String typesql = "select " + TypeColumn.ID + ", " + TypeColumn.NAME + ", " + TypeColumn.CREATE_TIME + " from "
                        + TypeColumn.UNDOWN_TYPE_TABLE + " where " + TypeColumn.ID + "=";
                cursor = sqlite.rawQuery(booksql, null);
                HashSet<Long> set = new HashSet<Long>();
                while (cursor != null && cursor.moveToNext()) {
                    String title = cursor.getString(cursor.getColumnIndex(ShelfBookDBColumn.BOOK_NAME));
                    String author = cursor.getString(cursor.getColumnIndex(ShelfBookDBColumn.AUTHOR));
                    if (isValidate(title, author, keyword)) {
                        ShelfBook book = getShelfBook(cursor, set, true, false);
                        getBookType(sqlite, typesql + book.getGroupType().getId(), book);
                        list.add(book);
                    }
                }
            } catch (Exception e) {

            } finally {
                closeCursor(cursor);
                closeSqlite(sqlite);
            }
            return list;
        }
    }

    public void updateServerMax(List<ShelfBook> list) {
        synchronized (this) {
            SQLiteDatabase sqlite = null;
            try {
                sqlite = mDB.getWritableDatabase();
                for (ShelfBook book : list) {
                    ShelfBook dbShelfBook = getShelfBookById(sqlite, book.getMediaId(), true);

                    JSONObject json;
                    try {
                        json = new JSONObject(dbShelfBook.getBookJson());
                    } catch (Exception e) {
                        json = new JSONObject();
                    }
                    json.put(Constants.JSON_SERVER, book.getServerLastIndexOrder());

                    ContentValues values = new ContentValues();
                    values.put(ShelfBookDBColumn.BOOK_JSON, json.toString());
                    values.put(ShelfBookDBColumn.BOOK_TYPE, book.getBookType().getValue());
                    sqlite.update(ShelfBookDBColumn.SHELF_TABLE_NAME, values, ShelfBookDBColumn.BOOK_ID + "=?",
                            new String[]{book.getMediaId()});
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                closeSqlite(sqlite);
            }
        }
    }

    /**
     * 重置全本下载状态
     */
    public long updateBookFinish(String bookId) {
        synchronized (this) {
            SQLiteDatabase sqlite = null;
            long time = System.currentTimeMillis();
            try {
                sqlite = mDB.getWritableDatabase();
                ContentValues values = new ContentValues();
                values.put(ShelfBookDBColumn.BOOK_FINISH, 0);
                values.put(ShelfBookDBColumn.LAST_TIME, time);
                sqlite.update(ShelfBookDBColumn.SHELF_TABLE_NAME, values, ShelfBookDBColumn.BOOK_ID + "=?", new String[]{bookId});
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                closeSqlite(sqlite);
            }
            return time;
        }
    }

    /**
     * 更新包月过期时间
     *
     * @param type
     * @param time
     */
    public void updateBorrowTime(MonthlyPaymentType type, long time) {
        synchronized (this) {
            SQLiteDatabase sqlite = null;
            try {
                sqlite = mDB.getWritableDatabase();
                ContentValues values = new ContentValues();
                values.put(ShelfBookDBColumn.MONTHLY_END_TIME, time);
                sqlite.update(ShelfBookDBColumn.SHELF_TABLE_NAME, values, ShelfBookDBColumn.MONTHLY_PAYMENT_TYPE + "=?",
                        new String[]{String.valueOf(type.ordinal())});
            } catch (Exception e) {

            } finally {
                closeSqlite(sqlite);
            }
        }
    }

    private void updateBorrowTime(SQLiteDatabase sqlite, ShelfBook book) {
        Cursor cursor = null;
        try {
            cursor = sqlite.query(ShelfBookDBColumn.SHELF_TABLE_NAME, new String[]{ShelfBookDBColumn.BOOK_JSON},
                    ShelfBookDBColumn.BOOK_ID + "=?", new String[]{book.getMediaId()}, null, null, null);
            if (cursor != null && cursor.moveToNext()) {
                String str = cursor.getString(0);
                JSONObject json;
                try {
                    json = new JSONObject(str);
                } catch (Exception e) {
                    json = new JSONObject();
                }
                json.put(Constants.BORROW_BEGIN_DATE, book.getBorrowStartTime());
                json.put(Constants.BORROW_DURATION, book.getBorrowTotalTime());
                ContentValues values = new ContentValues();
                values.put(ShelfBookDBColumn.BOOK_JSON, json.toString());
                sqlite.update(ShelfBookDBColumn.SHELF_TABLE_NAME, values, ShelfBookDBColumn.BOOK_ID + "=?",
                        new String[]{book.getMediaId()});
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeCursor(cursor);
        }
    }

    public void updateBorrowTime(List<ShelfBook> list) {
        synchronized (this) {
            SQLiteDatabase sqlite = null;
            try {
                sqlite = mDB.getWritableDatabase();
                sqlite.beginTransaction();
                for (ShelfBook book : list) {
                    updateBorrowTime(sqlite, book);
                }
                sqlite.setTransactionSuccessful();
            } catch (Exception e) {

            } finally {
                if (sqlite != null)
                    sqlite.endTransaction();
                closeSqlite(sqlite);
            }
        }
    }

    /**
     * 包月购买后
     *
     * @param mediaId
     */
    public void updateBorrowBookToBought(String mediaId) {
        synchronized (this) {
            SQLiteDatabase sqlite = null;
            try {
                sqlite = mDB.getWritableDatabase();
                ContentValues values = new ContentValues();
                values.put(ShelfBookDBColumn.MONTHLY_PAYMENT_TYPE, MonthlyPaymentType.DEFAULT_VALUE.ordinal());
                values.put(ShelfBookDBColumn.MONTHLY_END_TIME, 0);
                sqlite.update(ShelfBookDBColumn.SHELF_TABLE_NAME, values, ShelfBookDBColumn.BOOK_ID + "=?", new String[]{mediaId});
            } catch (Exception e) {

            } finally {
                closeSqlite(sqlite);
            }
        }
    }

    /**
     * 更新分组名
     */
    public long updateGroupName(int groupId, String name) {
        if (groupId <= 0) {
            return 0;
        }
        synchronized (this) {
            long time = System.currentTimeMillis();
            SQLiteDatabase sqlite = mDB.getWritableDatabase();
            try {
                sqlite = mDB.getWritableDatabase();
                ContentValues values = new ContentValues();
                values.put(GroupType.TypeColumn.CREATE_TIME, time);
                values.put(GroupType.TypeColumn.NAME, name);
                sqlite.update(GroupType.TypeColumn.SHELF_TYPE_TABLE, values, GroupType.TypeColumn.ID + "=?", new String[]{groupId + ""});
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                closeSqlite(sqlite);
            }
            return time;
        }
    }

    public void updateDownStatus(ShelfBook shelfBook) {
        if (shelfBook == null) {
            return;
        }
        synchronized (this) {
            SQLiteDatabase sqlite = null;
            try {
                sqlite = mDB.getWritableDatabase();
                JSONObject json;
                try {
                    json = new JSONObject(shelfBook.getBookJson());
                } catch (Exception e) {
                    json = new JSONObject();
                }
                json.put(Constants.JSON_DOWN_STATUS, shelfBook.isDown() ? 1 : 0);
                ContentValues values = new ContentValues();
                values.put(ShelfBookDBColumn.BOOK_JSON, json.toString());
                sqlite.update(ShelfBookDBColumn.SHELF_TABLE_NAME, values, ShelfBookDBColumn.BOOK_ID + "=?",
                        new String[]{shelfBook.getMediaId()});
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                closeSqlite(sqlite);
            }
        }
    }

    public HashSet<String> getImportBookPathSet() {
        HashSet<String> set = new HashSet<String>();
        synchronized (this) {
            SQLiteDatabase sqlite = null;
            Cursor cursor = null;
            try {
                sqlite = mDB.getWritableDatabase();
                cursor = sqlite.query(ShelfBookDBColumn.SHELF_TABLE_NAME,
                        new String[]{ShelfBookDBColumn.BOOK_DIR}, ShelfBookDBColumn.LOCAL_IMPORT + "=?",
                        new String[]{"1"}, null, null, null);
                while (cursor != null && cursor.moveToNext()) {
                    String str = cursor.getString(0);
                    set.add(str);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                closeCursor(cursor);
                closeSqlite(sqlite);
            }
            return set;
        }
    }

//    /**
//     * 获取当当登录账户信息列表
//     *
//     * @return
//     */
//    public List<DangUserInfo> getUserInfoList() {
//        List<DangUserInfo> list = new LinkedList<DangUserInfo>();
//        SQLiteDatabase sqlite = null;
//        Cursor cursor = null;
//        synchronized (this) {
//            try {
//                sqlite = mDB.getWritableDatabase();
//                String booksql = "select * from " + UserDBColumn.TABLE_NAME
//                        + " where " + UserDBColumn.LOGIN_TYPE + "=?"
//                        + " order by " + UserDBColumn.LAST_TIME + " desc limit 0,5";
//                cursor = sqlite.rawQuery(booksql, new String[]{String.valueOf(LoginType.DD.ordinal())});
//                while (cursor != null && cursor.moveToNext()) {
//                    DangUserInfo item = getUserInfo(cursor);
//                    list.add(item);
//                }
//            } catch (Exception e) {
//                LogM.e(TAG, e.toString());
//            } finally {
//                closeCursor(cursor);
//                closeSqlite(sqlite);
//            }
//        }
//
//        return list;
//    }
//
//    private DangUserInfo getUserInfo(Cursor cursor) {
//        DangUserInfo info = new DangUserInfo();
//        info.id = cursor.getString(cursor.getColumnIndex(UserDBColumn.USER_ID));
//        info.name = cursor.getString(cursor.getColumnIndex(UserDBColumn.NICK_NAME));
//        info.nameAll = cursor.getString(cursor.getColumnIndex(UserDBColumn.NICK_NAME_ALL));
//        info.ddAccount = cursor.getString(cursor.getColumnIndex(UserDBColumn.USER_NAME));
//        info.token = cursor.getString(cursor.getColumnIndex(UserDBColumn.TOKEN));
//        info.head = cursor.getString(cursor.getColumnIndex(UserDBColumn.HEADIMG));
//        info.sex = cursor.getInt(cursor.getColumnIndex(UserDBColumn.SEX));
//        info.loginType = LoginType.valueOf(cursor.getInt(cursor.getColumnIndex(UserDBColumn.LOGIN_TYPE)));
//
//        info.telephone = cursor.getString(cursor.getColumnIndex(UserDBColumn.PHONE));
//        info.gold = cursor.getLong(cursor.getColumnIndex(UserDBColumn.GOLD));
//        info.silver = cursor.getLong(cursor.getColumnIndex(UserDBColumn.SILVER));
//        info.friend = cursor.getLong(cursor.getColumnIndex(UserDBColumn.FRIEND));
//        info.info = cursor.getString(cursor.getColumnIndex(UserDBColumn.INFO));
//        info.level = cursor.getInt(cursor.getColumnIndex(UserDBColumn.LEVEL));
//        info.honor = cursor.getString(cursor.getColumnIndex(UserDBColumn.ExpColumn3));
//        String channelOwner = cursor.getString(cursor.getColumnIndex(UserDBColumn.ExpColumn1));
//        info.channelOwner = StringParseUtil.parseInt(channelOwner, 0);
//        try {
//            JSONObject exp2 = new JSONObject(cursor.getString(cursor.getColumnIndex(UserDBColumn.ExpColumn2)));
//            info.barOwnerLevel = exp2.getInt("barOwnerLevel");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        int createBar = cursor.getInt(cursor.getColumnIndex(UserDBColumn.CREATEBAR));
//        int rewardHead = cursor.getInt(cursor.getColumnIndex(UserDBColumn.REWARDHEAD));
//        int rewardIntroduct = cursor.getInt(cursor.getColumnIndex(UserDBColumn.REWARDINTRODUCT));
//        int rewardNick = cursor.getInt(cursor.getColumnIndex(UserDBColumn.REWARDNICKNAME));
//
//        info.createBar = createBar == 0 ? false : true;
//        info.rewardHead = rewardHead == 0 ? false : true;
//        info.rewardIntroduct = rewardIntroduct == 0 ? false : true;
//        info.rewardNickName = rewardNick == 0 ? false : true;
//
//        return info;
//    }
//
//    /**
//     * 删除登录账户信息
//     *
//     * @param info
//     */
//    public void deleteUserInfo(DangUserInfo info) {
//        SQLiteDatabase sqlite = null;
//        synchronized (this) {
//            try {
//                sqlite = mDB.getWritableDatabase();
//                sqlite.delete(UserDBColumn.TABLE_NAME, UserDBColumn.USER_ID + "=?", new String[]{info.id});
//            } catch (Exception e) {
//                LogM.e(TAG, e.toString());
//            } finally {
//                closeSqlite(sqlite);
//            }
//        }
//    }
//
//    /**
//     * 记录登录账户信息
//     *
//     * @param info
//     */
//    public void saveUserInfo(DangUserInfo info) {
//        SQLiteDatabase sqlite = null;
//        synchronized (this) {
//            try {
//                sqlite = mDB.getWritableDatabase();
//
//                ContentValues values = new ContentValues();
//                values.put(UserDBColumn.LAST_TIME, System.currentTimeMillis());
//                values.put(UserDBColumn.HEADIMG, info.head);
//                values.put(UserDBColumn.LOGIN_TYPE, info.loginType.ordinal());
//                values.put(UserDBColumn.USER_NAME, info.ddAccount);
//                int result = sqlite.update(UserDBColumn.TABLE_NAME, values, UserDBColumn.USER_ID + "=?", new String[]{info.id});
//                if (result <= 0) {
//                    values.put(UserDBColumn.SEX, info.sex);
//                    values.put(UserDBColumn.USER_ID, info.id);
//                    values.put(UserDBColumn.TOKEN, info.token);
//                    values.put(UserDBColumn.NICK_NAME, info.name);
//                    values.put(UserDBColumn.NICK_NAME_ALL, info.nameAll);
//
//                    values.put(UserDBColumn.EMAIL, info.email);
//                    values.put(UserDBColumn.PHONE, info.telephone);
//                    values.put(UserDBColumn.REGIST, info.registerDate);
//                    values.put(UserDBColumn.VIP, info.isVip);
//                    values.put(UserDBColumn.CREATEBAR, info.createBar);
//                    values.put(UserDBColumn.REWARDHEAD, info.rewardHead);
//                    values.put(UserDBColumn.REWARDINTRODUCT, info.rewardIntroduct);
//                    values.put(UserDBColumn.REWARDNICKNAME, info.rewardNickName);
//
//                    values.put(UserDBColumn.GOLD, info.gold);
//                    values.put(UserDBColumn.SILVER, info.silver);
//                    values.put(UserDBColumn.FRIEND, info.friend);
//                    values.put(UserDBColumn.INFO, info.info);
//                    values.put(UserDBColumn.LEVEL, info.level);
//                    values.put(UserDBColumn.ExpColumn3, info.honor);
//
//                    sqlite.insert(UserDBColumn.TABLE_NAME, null, values);
//                }
//            } catch (Exception e) {
//                LogM.e(TAG, e.toString());
//            } finally {
//                closeSqlite(sqlite);
//            }
//        }
//    }
//
//    /**
//     * 记录登录账户信息
//     *
//     * @param info
//     */
//    public void updateUserInfo(DangUserInfo info) {
//        SQLiteDatabase sqlite = null;
//        synchronized (this) {
//            try {
//                sqlite = mDB.getWritableDatabase();
//
//                ContentValues values = new ContentValues();
//                values.put(UserDBColumn.LAST_TIME, System.currentTimeMillis());
//                values.put(UserDBColumn.HEADIMG, info.head);
//                values.put(UserDBColumn.SEX, info.sex);
//                values.put(UserDBColumn.GOLD, info.gold);
//                values.put(UserDBColumn.SILVER, info.silver);
//                values.put(UserDBColumn.FRIEND, info.friend);
//                values.put(UserDBColumn.LEVEL, info.level);
//                values.put(UserDBColumn.NICK_NAME, info.name);
//                values.put(UserDBColumn.NICK_NAME_ALL, info.nameAll);
//                values.put(UserDBColumn.INFO, info.info);
//                values.put(UserDBColumn.CREATEBAR, info.createBar);
//                values.put(UserDBColumn.REWARDHEAD, info.rewardHead);
//                values.put(UserDBColumn.REWARDINTRODUCT, info.rewardIntroduct);
//                values.put(UserDBColumn.REWARDNICKNAME, info.rewardNickName);
//                values.put(UserDBColumn.ExpColumn1, info.channelOwner);
//                JSONObject jsonObject = new JSONObject();
//                jsonObject.put("barOwnerLevel", info.barOwnerLevel);
//                values.put(UserDBColumn.ExpColumn2, jsonObject.toString());
//                values.put(UserDBColumn.ExpColumn3, info.honor);
//                sqlite.update(UserDBColumn.TABLE_NAME, values, UserDBColumn.USER_ID + "=?", new String[]{info.id});
//
//            } catch (Exception e) {
//                LogM.e(TAG, e.toString());
//            } finally {
//                closeSqlite(sqlite);
//            }
//        }
//    }
//
//    public DangUserInfo getUserInfo(String id, LoginType type) {
//        SQLiteDatabase sqlite = null;
//        Cursor cursor = null;
//        synchronized (this) {
//            try {
//                sqlite = mDB.getWritableDatabase();
//                String booksql = "select * from " + UserDBColumn.TABLE_NAME
//                        + " where " + UserDBColumn.USER_ID + "=?"
//                        + " and " + UserDBColumn.LOGIN_TYPE + "=?";
//
//                cursor = sqlite.rawQuery(booksql, new String[]{id, String.valueOf(type.ordinal())});
//                if (cursor != null && cursor.moveToFirst()) {
//                    DangUserInfo item = getUserInfo(cursor);
//                    return item;
//                }
//            } catch (Exception e) {
//                LogM.e(TAG, e.toString());
//            } finally {
//                closeCursor(cursor);
//                closeSqlite(sqlite);
//            }
//        }
//        return null;
//    }

    /**
     * 更新 当前书籍 阅读时间
     *
     * @param bid
     * @param readTime
     * @return
     */
    public boolean updateBookReadTime(String bid, String readTime) {
        synchronized (this) {
            boolean ret = false;
            SQLiteDatabase sqlite = mDB.getWritableDatabase();
            try {
                String sql = "update " + ShelfBookDBColumn.SHELF_TABLE_NAME + " set "
                        + ShelfBookDBColumn.TOTAL_TIME + "=?" + " where "
                        + ShelfBookDBColumn.BOOK_ID + "=?";
                sqlite.execSQL(sql, new Object[]{readTime, bid});
                ret = true;
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                closeSqlite(sqlite);
            }
            return ret;
        }
    }

    /**
     * 用于更新阅读时长
     *
     * @param bid
     * @param readTime
     * @param syncState
     * @return
     */
    public boolean updateBookReadTime(String bid, String readTime, int syncState) {
        synchronized (this) {
            boolean ret = false;
            SQLiteDatabase sqlite = mDB.getWritableDatabase();
            try {
                String sql = "update " + ShelfBookDBColumn.SHELF_TABLE_NAME + " set "
                        + ShelfBookDBColumn.TOTAL_TIME + "=? , " + ShelfBookDBColumn.ExpColumn1 + "=?" + " where "
                        + ShelfBookDBColumn.BOOK_ID + "=?";
                sqlite.execSQL(sql, new Object[]{readTime, syncState, bid});
                ret = true;
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                closeSqlite(sqlite);
            }
            return ret;
        }
    }

    /**
     * 更新书过期状态
     *
     * @param set
     */
    public void updateOverDue(Set<String> set) {
        if (set.isEmpty())
            return;
        SQLiteDatabase sqlite = mDB.getWritableDatabase();
        try {
            Iterator<String> it = set.iterator();
            while (it.hasNext()) {
                String str = it.next();
                String sql = "update " + ShelfBookDBColumn.SHELF_TABLE_NAME + " set "
                        + ShelfBookDBColumn.OVER_DUE + "=1" + " where "
                        + ShelfBookDBColumn.BOOK_ID + "=?";
                sqlite.execSQL(sql, new Object[]{str});
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeSqlite(sqlite);
        }
    }

    /**
     * 取最后阅读的一本书
     *
     * @return
     */
    public List<ShelfBook> getLastBook(int num) {
        if (num < 1)
            return null;
        synchronized (this) {
            SQLiteDatabase sqlite = null;
            Cursor cursor = null;
            try {
                sqlite = mDB.getWritableDatabase();
                String booksql = "select * from " + ShelfBookDBColumn.SHELF_TABLE_NAME + " order by " + ShelfBookDBColumn.LAST_TIME
                        + " desc";
                String typesql = "select " + TypeColumn.ID + ", " + TypeColumn.NAME + ", " + TypeColumn.CREATE_TIME + " from "
                        + TypeColumn.UNDOWN_TYPE_TABLE + " where " + TypeColumn.ID + "=";
                cursor = sqlite.rawQuery(booksql, null);
                HashSet<Long> set = new HashSet<Long>();
                /**
                 * 跳过没读和过期的书
                 */
                List<ShelfBook> books = new ArrayList<ShelfBook>();
                while (cursor != null && cursor.moveToNext()) {
                    String str = cursor.getString(cursor.getColumnIndex(ShelfBookDBColumn.READ_PROGRESS));
                    if (TextUtils.isEmpty(str))
                        continue;
                    ShelfBook book = null;
                    book = getShelfBook(cursor, set, true, false);
//					if(!DataUtil.getInstance(mContext).checkBorrowValide(book, null, false))
//						continue;
                    getBookType(sqlite, typesql + book.getGroupType().getId(), book);
                    books.add(book);
                    if (books.size() >= num)
                        break;
                }
                return books;
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                closeCursor(cursor);
                closeSqlite(sqlite);
            }
            return null;
        }
    }
}
