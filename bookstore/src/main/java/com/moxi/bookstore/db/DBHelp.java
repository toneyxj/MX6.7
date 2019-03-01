package com.moxi.bookstore.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.mx.mxbase.constant.APPLog;

/**
 * Created by Administrator on 2016/9/25.
 */
public class DBHelp extends SQLiteOpenHelper {

    private static DBHelp mInstance;
    /**
     *
     * 此构造函数用来 创建库和表 （第一次执行时，如果没有库、表，将创建库、表，以后将不再创建）
     * @param context  上下文对象
     * @param name 数据库名称
     * @param factory 创建Cursor的工厂类。参数是为了可以自定义Cursor创建
     * @param version 数据库版本号
     */
    private DBHelp(Context context, String name, SQLiteDatabase.CursorFactory factory,
                  int version) {
        super(context, name, factory, version);
    }

    /**操作数据时，（增删改查）
     *
     * @param context  上下文对象
     */
    private DBHelp(Context context) {
        this(context, TableConfig.DATABASE_NAME, null, TableConfig.DATABASE_VERSION);
        APPLog.e("create database");

    }

    /**需要根据版本号来实现修改
     *
     *此构造函数用来 修改库和表 （修改现有的库、表）
     * @param context  上下文对象
     * @param version 数据库版本号
     */
    private DBHelp(Context context ,int version) {
        this(context,TableConfig.DATABASE_NAME,null,version);
    }

    public static  synchronized DBHelp getInstance(Context context){
        if (null==mInstance)

            mInstance=new DBHelp(context);
        return mInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        APPLog.e("当第一次运行或者修改数据库时，将会自动执行");

        String createTableSQL = "create table " + TableConfig.TABLE_NAME + "("
                +TableConfig.E_ID        + " integer  primary key autoincrement,"
                +TableConfig.E_NAME     + " text,"
                +TableConfig.E_AUTHOR + " text,"
                +TableConfig.E_PUBLISHER + " text,"
                +TableConfig.E_ICO        + " text,"
                +TableConfig.E_TYPE         + " int,"
                +TableConfig.E_FILEPATH     + " text,"
                +TableConfig.E_PUBTIME + " integer,"
                +TableConfig.E_DOWNTIME + " integer,"
                +TableConfig.E_LASTREADTIME + " integer,"
                +TableConfig.E_CHARTCOUNT + " integer,"
                +TableConfig.E_PAGECOUNT + " int,"
                +TableConfig.E_FLAG + " int,"
                +TableConfig.E_KEY + " text,"
                +TableConfig.E_PROGRESS + " text,"
                +TableConfig.E_SAELID + " integer,"
                +TableConfig.E_DESC + " text,"
                +TableConfig.E_LOWESTPRICE + " double,"
                +TableConfig.E_ORGPRICE + " double,"
                +TableConfig.E_FILEPATHMD5     + " text"
                +")";
        db.execSQL(createTableSQL); // 执行SQL 需要使用execSQL（）
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        APPLog.e("onUpgrade","oldVersion="+oldVersion+"\tnewVersion"+newVersion);

        if (oldVersion==1&&newVersion==2) {
            //版本为2时添加字段
            db.execSQL("ALTER TABLE "+TableConfig.TABLE_NAME+" ADD COLUMN " + TableConfig.E_FILEPATHMD5 + " text");
            APPLog.e(" 版本为2时添加字段");
        }else {
            String sql = " DROP TABLE IF EXISTS " + TableConfig.TABLE_NAME;
            db.execSQL(sql);
            onCreate(db);

        }

    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        super.onOpen(db);
        System.out.println("当数据库打开时，执行");
    }



}
