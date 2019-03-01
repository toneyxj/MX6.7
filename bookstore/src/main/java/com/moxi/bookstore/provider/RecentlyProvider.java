package com.moxi.bookstore.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.support.annotation.Nullable;

import com.moxi.bookstore.db.EbookDB;
import com.moxi.bookstore.db.SacnReadFileUtils;
import com.moxi.bookstore.db.TableConfig;
import com.moxi.bookstore.db.TableOperate;
import com.mx.mxbase.constant.APPLog;

/**
 * 最近阅读
 * Created by Administrator on 2016/10/19.
 */
public class RecentlyProvider extends ContentProvider {
    private static final int MULTIPLE_PEOPLE = 1;
    private static final UriMatcher uriMatcher;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(BookProviderUtils.PATH_MULTIPLE_KEY_RecentlyProvider, BookProviderUtils.AUTHORITY_KEY_RecentlyProvider, MULTIPLE_PEOPLE);
    }

    @Override
    public boolean onCreate() {
        return false;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        MatrixCursor cursorchange=new MatrixCursor(new String[]{"id","filePath","pathMd5","photoPath",
                "_index","fullPinyin","isDdBook","bookImageUrl","progress"},9);
        Cursor cursor = SacnReadFileUtils.getInstance(getContext()).getBookStoreRecentReading(8);
        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                /** 索引id */
                long id = cursor.getLong(0);
                /** 文件保存路径 */
                String filePath = cursor.getString(1);
                /** 文件图片路径 */
                String pathMd5 = cursor.getString(2);

                String photoPath = cursor.getString(3);
                /** 文件排序索引 */
                long _index = cursor.getInt(4);
                /** 文件全拼 */
                String fullPinyin = cursor.getString(5);
                /**是否为当当书记**/
                int isDdBook = cursor.getInt(6);
                /**书籍图片*/
                String bookImageUrl = cursor.getString(7);
                EbookDB ebookDB = TableOperate.getInstance().queryByPath(TableConfig.TABLE_NAME, filePath);
                String progress = "";
                if (ebookDB != null) progress = ebookDB.getProgress();
                cursorchange.addRow(new Object[]{id, filePath, pathMd5, photoPath, _index, fullPinyin, isDdBook, bookImageUrl, progress});
            }
            cursor.close();
        }

        return cursorchange;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)) {
            case MULTIPLE_PEOPLE:
                return BookProviderUtils.MIME_TYPE_MULTIPLE_KEY;
            default:
                throw new IllegalArgumentException("Unkown uro:" + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        APPLog.e("阅读器删除", selectionArgs.toString());
        for (int i = 0; i < selectionArgs.length; i++) {
            SacnReadFileUtils.getInstance(getContext()).deleteFile(selectionArgs[i]);
        }

        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}
