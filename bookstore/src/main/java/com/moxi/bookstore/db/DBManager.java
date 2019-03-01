package com.moxi.bookstore.db;

import android.database.sqlite.SQLiteDatabase;

import com.moxi.bookstore.BookstoreApplication;

/**
 * Created by Administrator on 2016/9/25.
 */

    public class DBManager {
        private static DBManager manager;
        private DBHelp mySQLiteOpenHelper;
        private SQLiteDatabase db;

        /**
         * 私有化构造器
         */
        private DBManager() {
//            if (BookstoreApplication.getContext()==null)return;
            //创建数据库
            mySQLiteOpenHelper =DBHelp.getInstance(BookstoreApplication.getContext());
            if (db == null) {
                db = mySQLiteOpenHelper.getWritableDatabase();
            }
        }

        /**
         * 单例DbManager类
         *
         * @return 返回DbManager对象
         */
        public static synchronized DBManager newInstances() {
            if (manager == null) {
                manager = new DBManager();
            }
            return manager;
        }

        /**
         * 获取数据库的对象
         *
         * @return 返回SQLiteDatabase数据库的对象
         */
        public SQLiteDatabase getDataBase() {
            return db;
        }
}

