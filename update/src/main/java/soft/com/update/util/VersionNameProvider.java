package soft.com.update.util;

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

/**
 * Created by Archer on 16/10/27.
 */
public class VersionNameProvider extends ContentProvider {

    //DB const
    final static int DB_VERSION = 1;
    final static String DB_NAME = "mxUpdate";
    final static String UPDATE_TABLE = "updateversionname";    //table name

    final static String UPDATE_ID = "_id";            //table field _id
    final static String UPDATE_NAME = "updatename";         //table field user name
    final static String UPDATE_CODE = "updatecode";       //table field user session
    final static String APP_NAME = "appname";

    final static String DB_CREATE = "create table " +   //create table script
            UPDATE_TABLE + "(" +
            UPDATE_ID + " integer primary key autoincrement, " +
            UPDATE_NAME + " text, " +
            UPDATE_CODE + " text, "+
            APP_NAME + " text" +
            ");";

    //Uri const
    final static String AUTHORITY = "com.moxi.providers.Update";
    final static String PATH = "mxupdate";

    public final static Uri UPDATE_CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + PATH);

    //MIME-type
    final static String UPDATE_CONTENT_TYPE = "vnd.android.cursor.dir/vnd." + AUTHORITY + "." + PATH;
    final static String UPDATE_CONTENT_ITEMTYPE = "vnd.android.cursor.item/vnd." + AUTHORITY + "." + PATH;

    //UriMatcher
    final static int URI_UPDATES = 1;
    final static int URI_UPDATE_ID = 2;

    private final static UriMatcher uriMatcher;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY, PATH, URI_UPDATES);
        uriMatcher.addURI(AUTHORITY, PATH + "/#", URI_UPDATE_ID);
    }

    @Override
    public boolean onCreate() {
        dbHelper = new DBHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Log.e("query: ", uri.toString());

        //Checking Uri
        switch (uriMatcher.match(uri)) {
            case URI_UPDATES:
                Log.e("URI_USERS", "URI_USERS");
                //Default order by name, unless otherwise specified
                if (TextUtils.isEmpty(sortOrder)) {
                    sortOrder = UPDATE_NAME + " ASC";
                }
                break;
            case URI_UPDATE_ID:
                //Getting id from path
                String id = uri.getLastPathSegment();
                Log.e("USER_ID", "URI_CONTACTS_ID: " + id);
                if (TextUtils.isEmpty(selection)) {
                    //Where _id = id
                    selection = UPDATE_ID + " = " + id;
                } else {
                    selection = UPDATE_ID + " AND " + selection + " = " + id;
                }
                break;
            default:
                throw new IllegalArgumentException("Wrong uri: " + uri);
        }

        db = dbHelper.getWritableDatabase();
        Cursor cursor = db.query(UPDATE_TABLE, projection, selection, selectionArgs, null, null, sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), UPDATE_CONTENT_URI);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        Log.e("getType(", uri.toString() + ")");
        switch (uriMatcher.match(uri)) {
            case URI_UPDATES:
                return UPDATE_CONTENT_TYPE;
            case URI_UPDATE_ID:
                return UPDATE_CONTENT_ITEMTYPE;
        }
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        Log.e("insert uri: ", uri.toString());
        if (uriMatcher.match(uri) != URI_UPDATES) {
            throw new IllegalArgumentException("Wrong uri: " + uri);
        }
        db = dbHelper.getWritableDatabase();
        long rowId = db.insert(UPDATE_TABLE, null, contentValues);
        Uri resultUri = ContentUris.withAppendedId(UPDATE_CONTENT_URI, rowId);
        getContext().getContentResolver().notifyChange(resultUri, null);
        return resultUri;

    }

    @Override
    public int delete(Uri uri, String selection, String[] strings) {
        Log.e("delete uri: ", uri.toString());
        switch (uriMatcher.match(uri)) {
            case URI_UPDATES:
                Log.e("URI_USERS", "URI_USERS");
                selection = null;
                break;
            case URI_UPDATE_ID:
                String id = uri.getLastPathSegment();
                Log.e("URI_USER_ID: ", id + "");
                if (TextUtils.isEmpty(selection)) {
                    selection = UPDATE_ID + " = " + id;
                } else {
                    selection = UPDATE_ID + " AND " + selection + " = " + id;
                }
                break;
            default:
                throw new IllegalArgumentException("Wrong uri: " + uri);
        }
        db = dbHelper.getWritableDatabase();
        int count = db.delete(UPDATE_TABLE, selection, strings);
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        Log.e("update uri: ", uri.toString());
        switch (uriMatcher.match(uri)) {
            case URI_UPDATES:
                Log.e("URI_USERS", "URI_USERS");
                break;
            case URI_UPDATE_ID:
                String id = uri.getLastPathSegment();
                Log.e("URI_USER_ID: ", id + "");
                if (TextUtils.isEmpty(selection)) {
                    selection = UPDATE_ID + " = " + id;
                } else {
                    selection = UPDATE_ID + " AND " + selection + " = " + id;
                }
                break;
            default:
                throw new IllegalArgumentException("Wrong uri: " + uri);
        }
        db = dbHelper.getWritableDatabase();
        int count = db.update(UPDATE_TABLE, values, selection, selectionArgs);
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

        }
    }
}
