package com.moxi.bookstore.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;

import com.moxi.bookstore.db.DBHelp;
import com.moxi.bookstore.db.TableConfig;
import com.mx.mxbase.constant.APPLog;

/**
 * 书籍对外接口
 * Created by Administrator on 2016/10/10.
 */
public class BookProvider extends ContentProvider{
//    private static final String sqlSlect = "id,searchContent,time";

    private static final int MULTIPLE_PEOPLE = 1;
    private static final int SINGLE_PEOPLE = 2;
    private static final UriMatcher uriMatcher ;

    private DBHelp dbHelp;
    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(BookProviderUtils.AUTHORITY, BookProviderUtils.PATH_MULTIPLE, MULTIPLE_PEOPLE);
        uriMatcher.addURI(BookProviderUtils.AUTHORITY, BookProviderUtils.PATH_SINGLE, SINGLE_PEOPLE);
    }

    @Override
    public boolean onCreate() {
        this.dbHelp = DBHelp.getInstance(this.getContext());
        return dbHelp==null?false:true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        //设置默认排序方式
        if (sortOrder==null)
        sortOrder=TableConfig.E_PUBLISHER+" DESC";

        Cursor cursor=null;
        SQLiteDatabase db = dbHelp.getReadableDatabase();
        switch (uriMatcher.match(uri)){
           case  MULTIPLE_PEOPLE:
               cursor=db.query(TableConfig.TABLE_NAME, projection, selection, selectionArgs,
                       null, null, sortOrder);
               break ;
           case  SINGLE_PEOPLE:
               long id = ContentUris.parseId(uri);
               String where =TableConfig.E_ID+ "=" + id;
               if (selection != null && !"".equals(selection)) {
                   where = selection + " and " + where;
               }
               cursor= db.query(TableConfig.TABLE_NAME, projection, where, selectionArgs, null,
                       null, sortOrder);
               break ;
           default:
               APPLog.e("com.moxi.bookstore.provider.BookProvider","未匹配传入参数");
               return null;
        }
        return cursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)){
            case MULTIPLE_PEOPLE:
                return BookProviderUtils.MIME_TYPE_MULTIPLE;
            case SINGLE_PEOPLE:
                return BookProviderUtils.MIME_TYPE_SINGLE;
            default:
                throw new IllegalArgumentException("Unkown uro:"+uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = dbHelp.getWritableDatabase();
        switch (uriMatcher.match(uri)) {
            case MULTIPLE_PEOPLE:
                // 特别说一下第二个参数是当name字段为空时，将自动插入一个NULL。
                long rowid = db.insert(TableConfig.TABLE_NAME, TableConfig.E_ID, values);
                Uri insertUri = ContentUris.withAppendedId(uri, rowid);// 得到代表新增记录的Uri
                this.getContext().getContentResolver().notifyChange(uri, null);

                return insertUri;
            default:
                throw new IllegalArgumentException("Unkwon Uri:" + uri.toString());
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = dbHelp.getWritableDatabase();
        int count = 0;
        switch (uriMatcher.match(uri)) {
            case MULTIPLE_PEOPLE:
                count = db.delete(TableConfig.TABLE_NAME, selection, selectionArgs);
                return count;
            case SINGLE_PEOPLE:
                long id = ContentUris.parseId(uri);
                String where =TableConfig.E_ID+ "=" + id;
                if (selection != null && !"".equals(selection)) {
                    where = selection + " and " + where;
                }
                count = db.delete(TableConfig.TABLE_NAME, where, selectionArgs);
                return count;
            default:
                throw new IllegalArgumentException("Unkwon Uri:" + uri.toString());
        }
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase db = dbHelp.getWritableDatabase();
        int count = 0;
        switch (uriMatcher.match(uri)) {
            case MULTIPLE_PEOPLE:
                count = db.update(TableConfig.TABLE_NAME, values, selection, selectionArgs);
                return count;
            case SINGLE_PEOPLE:
                long id = ContentUris.parseId(uri);
                String where =TableConfig.E_ID+ "=" + id;
                if (selection != null && !"".equals(selection)) {
                    where = selection + " and " + where;
                }
                count = db.update(TableConfig.TABLE_NAME, values, where, selectionArgs);
                return count;
            default:
                throw new IllegalArgumentException("Unkwon Uri:" + uri.toString());
        }
    }
}
