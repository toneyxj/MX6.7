package com.mx.user.mxcontentprovider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.mx.mxbase.constant.APPLog;

/**
 * Created by Archer on 16/10/8.
 */
public class MXUserContentProvider extends ContentProvider {
    //DB const
    final static int DB_VERSION = 8;
    final static String DB_NAME = "mxUSER";
    final static String USER_TABLE = "user";    //table name

    final static String USER_ID = "_id";            //table field _id
    final static String USER_NAME = "username";         //table field user name
    final static String USER_SESSION = "appsession";       //table field user session
    final static String USER_TOKEN = "usertoken";
    final static String USER_B_ID = "userid";

    final static String DB_CREATE = "create table " +
            USER_TABLE + "(" +
            USER_ID + " integer primary key autoincrement, " +
            USER_NAME + " text, " +
            USER_SESSION + " text, " +
            USER_TOKEN + " text," +
            USER_B_ID + " text" +
            ");";

    //删除临时表
    private String DROP_TABLE = "drop table user";

    //Uri const
    final static String AUTHORITY = "com.moxi.providers.MoxiUser";
    final static String PATH = "mxuser";

    public final static Uri USER_CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + PATH);

    //MIME-type
    final static String USER_CONTENT_TYPE = "vnd.android.cursor.dir/vnd." + AUTHORITY + "." + PATH;
    final static String USER_CONTENT_ITEMTYPE = "vnd.android.cursor.item/vnd." + AUTHORITY + "." + PATH;

    //UriMatcher
    final static int URI_USERS = 1;
    final static int URI_USER_ID = 2;

    private final static UriMatcher uriMatcher;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY, PATH, URI_USERS);
        uriMatcher.addURI(AUTHORITY, PATH + "/#", URI_USER_ID);
    }

    @Override
    public boolean onCreate() {
        Log.e("onCreate--", "onCreate");
        dbHelper = new DBHelper(getContext());
        return true;
    }

    //Reading cursor
    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        APPLog.e("query: ", uri.toString());
        //Checking Uri
        switch (uriMatcher.match(uri)) {
            case URI_USERS:
                APPLog.e("URI_USERS", "URI_USERS");
                //Default order by name, unless otherwise specified
                if (TextUtils.isEmpty(sortOrder)) {
                    sortOrder = USER_NAME + " ASC";
                }
                break;
            case URI_USER_ID:
                //Getting id from path
                String id = uri.getLastPathSegment();
                Log.e("USER_ID", "URI_CONTACTS_ID: " + id);
                if (TextUtils.isEmpty(selection)) {
                    //Where _id = id
                    selection = USER_ID + " = " + id;
                } else {
                    selection = USER_ID + " AND " + selection + " = " + id;
                }
                break;
            default:
                throw new IllegalArgumentException("Wrong uri: " + uri);
        }

        db = dbHelper.getWritableDatabase();
        Cursor cursor = db.query(USER_TABLE, projection, selection, selectionArgs, null, null, sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), USER_CONTENT_URI);
        APPLog.e("cursor",cursor);
        return cursor;
    }

    //Getting MIME-type
    @Nullable
    @Override
    public String getType(Uri uri) {
        Log.e("getType(", uri.toString() + ")");
        switch (uriMatcher.match(uri)) {
            case URI_USERS:
                return USER_CONTENT_TYPE;
            case URI_USER_ID:
                return USER_CONTENT_ITEMTYPE;
        }
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        Log.e("insert uri: ", uri.toString());
        if (uriMatcher.match(uri) != URI_USERS) {
            throw new IllegalArgumentException("Wrong uri: " + uri);
        }
        db = dbHelper.getWritableDatabase();
        long rowId = db.insert(USER_TABLE, null, values);
        Uri resultUri = ContentUris.withAppendedId(USER_CONTENT_URI, rowId);
        getContext().getContentResolver().notifyChange(resultUri, null);
        return resultUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        Log.e("delete uri: ", uri.toString());
        switch (uriMatcher.match(uri)) {
            case URI_USERS:
                Log.e("URI_USERS", "URI_USERS");
                selection = null;
                break;
            case URI_USER_ID:
                String id = uri.getLastPathSegment();
                Log.e("URI_USER_ID: ", id + "");
                if (TextUtils.isEmpty(selection)) {
                    selection = USER_ID + " = " + id;
                } else {
                    selection = USER_ID + " AND " + selection + " = " + id;
                }
                break;
            default:
                throw new IllegalArgumentException("Wrong uri: " + uri);
        }
        db = dbHelper.getWritableDatabase();
        int count = db.delete(USER_TABLE, selection, selectionArgs);
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        Log.e("update uri: ", uri.toString());
        switch (uriMatcher.match(uri)) {
            case URI_USERS:
                Log.e("URI_USERS", "URI_USERS");
                break;
            case URI_USER_ID:
                String id = uri.getLastPathSegment();
                Log.e("URI_USER_ID: ", id + "");
                if (TextUtils.isEmpty(selection)) {
                    selection = USER_ID + " = " + id;
                } else {
                    selection = USER_ID + " AND " + selection + " = " + id;
                }
                break;
            default:
                throw new IllegalArgumentException("Wrong uri: " + uri);
        }
        db = dbHelper.getWritableDatabase();
        int count = db.update(USER_TABLE, values, selection, selectionArgs);
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    DBHelper dbHelper;
    SQLiteDatabase db;

    private class DBHelper extends SQLiteOpenHelper {

        public DBHelper(Context context) {
            super(context, DB_NAME, null, DB_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DB_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            if (newVersion > oldVersion) {
                db.execSQL(DROP_TABLE);
                db.execSQL(DB_CREATE);
            }
        }
    }
}
