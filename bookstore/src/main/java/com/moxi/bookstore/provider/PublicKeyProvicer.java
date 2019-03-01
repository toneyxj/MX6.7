package com.moxi.bookstore.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.support.annotation.Nullable;

import com.dangdang.reader.dread.util.DrmWrapUtil;
import com.mx.mxbase.constant.APPLog;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * Created by Administrator on 2016/10/11.
 */
public class PublicKeyProvicer extends ContentProvider{
    private static final int MULTIPLE_PEOPLE = 1;
    private static final UriMatcher uriMatcher ;
    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(BookProviderUtils.AUTHORITY_KEY, BookProviderUtils.PATH_MULTIPLE_KEY, MULTIPLE_PEOPLE);
    }
    @Override
    public boolean onCreate() {
        return false;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        String key=null;
        try {
             key= URLDecoder.decode(DrmWrapUtil.getPublicKey(), "UTF-8");
            APPLog.e("key="+key);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        MatrixCursor cursor=null;
        if (key!=null){
         cursor=new MatrixCursor(new String[]{"key"},1);
        cursor.addRow(new Object[] { key });
        }
        return cursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)){
            case MULTIPLE_PEOPLE:
                return BookProviderUtils.MIME_TYPE_MULTIPLE_KEY;
            default:
                throw new IllegalArgumentException("Unkown uro:"+uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}
