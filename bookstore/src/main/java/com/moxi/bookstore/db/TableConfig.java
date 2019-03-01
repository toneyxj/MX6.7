package com.moxi.bookstore.db;

import android.os.Environment;

import java.io.File;

/**
 * Created by Administrator on 2016/9/25.
 */
public class TableConfig {

    public final static String DATABASE_NAME="EbookDB.db"; //数据库名称
    public final static int DATABASE_VERSION=2; //数据库默认版本
    public final static String TABLE_NAME="Ebook"; //数据表名称
    public final static String E_ID="id";  // 列名
    public final static String E_NAME="name";
    public final static String E_ICO="iconUrl";
    public final static String E_TYPE="type";
    public final static String E_FILEPATH="filePath";
    public final static String E_FILEPATHMD5="filePathMd5";
    public final static String E_DOWNTIME="downloadtime";
    public final static String E_LASTREADTIME="lastreadtime";
    public final static String E_CHARTCOUNT="chartcount";
    public final static String E_PAGECOUNT="pagecount";
    public final static String E_FLAG="flag";
    public final static String E_AUTHOR="author";
    public final static String E_PUBLISHER="publisher";
    public final static String E_PUBTIME="publishtime";
    public final static String E_KEY="key";
    public final static String E_PROGRESS="progress";
    public final static String E_SAELID="saleId";
    public final static String E_DESC="bookdesc";
    public final static String E_LOWESTPRICE="lowestprice";
    public final static String E_ORGPRICE="orgprice";
    public final static String E_DOWNLOAD_DIR= Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+"DDBooks";
}
