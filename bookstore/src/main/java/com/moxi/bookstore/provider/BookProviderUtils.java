package com.moxi.bookstore.provider;

import android.net.Uri;

import com.moxi.bookstore.db.TableConfig;

/**
 * Created by Administrator on 2016/10/10.
 */
public class BookProviderUtils {
    public static final String TABLE_NAME= TableConfig.TABLE_NAME;
    public static final String MIME_DIR_PREFIX = "vnd.android.cursor.dir";
    public static final String MIME_ITEM_PREFIX = "vnd.android.cursor.item";
    public static final String MIME_ITEM = "/"+TABLE_NAME;

    public static final String MIME_TYPE_SINGLE = MIME_ITEM_PREFIX + "/" + MIME_ITEM ;
    public static final String MIME_TYPE_MULTIPLE = MIME_DIR_PREFIX + "/" + MIME_ITEM ;

    public static final String AUTHORITY = "com.moxi.bookstore.provider";
    public static final String PATH_SINGLE = "BookRack/#";
    public static final String PATH_MULTIPLE = "BookRack";
    public static final String CONTENT_URI_STRING = "content://" + AUTHORITY + "/" + PATH_MULTIPLE;
    public static final Uri CONTENT_URI = Uri.parse(CONTENT_URI_STRING);

    //publicKey
    public static final String PATH_MULTIPLE_KEY = "publicKey";
    public static final String AUTHORITY_KEY = "com.moxi.bookstore.provider.Key";
    public static final String PATH_MULTIPLE_KEY_RecentlyProvider = "Recently";
    public static final String AUTHORITY_KEY_RecentlyProvider = "com.moxi.bookstore.provider.RecentlyProvider";
    public static final String MIME_TYPE_MULTIPLE_KEY = MIME_DIR_PREFIX ;
//
//    public static final String KEY_ID = "_id";
//    public static final String KEY_NAME = "name";
//    public static final String KEY_AGE = "age";
//    public static final String KEY_HEIGHT = "height";

}
