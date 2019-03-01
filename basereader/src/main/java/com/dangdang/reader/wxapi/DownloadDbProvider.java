package com.dangdang.reader.wxapi;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.dangdang.reader.domain.ShelfDownload;
import com.dangdang.reader.dread.font.DownloadDbHelper;
import com.dangdang.reader.utils.DangdangConfig;

public class DownloadDbProvider extends ContentProvider {

	public static final String AUTHORITY = DangdangConfig.ParamsType.mPagekageName+".wxapi.DownloadDbProvider";
	private static UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
	private static final int DOWNLOADS = 1;
	private static final int DOWNLOAD = 2;
	private DownloadDbHelper downloadDbHelper;
	static {
		matcher.addURI(AUTHORITY, ShelfDownload.TABLE_NAME, DOWNLOADS);
		matcher.addURI(AUTHORITY, ShelfDownload.TABLE_NAME + "/#", DOWNLOAD);
	}

	@Override
	public boolean onCreate() {
		downloadDbHelper = new DownloadDbHelper(getContext());
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		SQLiteDatabase db = downloadDbHelper.getReadableDatabase();
		switch (matcher.match(uri)) {
		case DOWNLOADS:
			return db.query(ShelfDownload.TABLE_NAME, projection, selection,
					selectionArgs, null, null, sortOrder);

		default:
			throw new IllegalArgumentException("Unkwon Uri:" + uri.toString());
		}
	}

	@Override
	public String getType(Uri uri) {
		switch (matcher.match(uri)) {
		case DOWNLOADS:
			return "vnd.android.cursor.dir/" + ShelfDownload.TABLE_NAME;

		case DOWNLOAD:
			return "vnd.android.cursor.item/" + ShelfDownload.TABLE_NAME;

		default:
			throw new IllegalArgumentException("Unkwon Uri:" + uri.toString());
		}
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		SQLiteDatabase db = downloadDbHelper.getWritableDatabase();
		switch (matcher.match(uri)) {
		case DOWNLOADS:
			long rowid = db.insert(ShelfDownload.TABLE_NAME, null, values);
			return ContentUris.withAppendedId(uri, rowid);

		default:
			throw new IllegalArgumentException("Unkwon Uri:" + uri.toString());
		}
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		SQLiteDatabase db = downloadDbHelper.getWritableDatabase();
		int count = 0;
		switch (matcher.match(uri)) {
		case DOWNLOADS:
			count = db.delete(ShelfDownload.TABLE_NAME, selection, selectionArgs);
			return count;

		default:
			throw new IllegalArgumentException("Unkwon Uri:" + uri.toString());
		}
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		SQLiteDatabase db = downloadDbHelper.getWritableDatabase();
		int count = 0;
		switch (matcher.match(uri)) {
		case DOWNLOADS:
			count = db.update(ShelfDownload.TABLE_NAME, values, selection, selectionArgs);
			return count;
		default:
			throw new IllegalArgumentException("Unkwon Uri:" + uri.toString());
		}
	}

}
