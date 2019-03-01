package com.dangdang.reader.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.dangdang.reader.cloud.Status;
import com.dangdang.reader.dread.data.BookMark;
import com.dangdang.reader.dread.data.BookNote;
import com.dangdang.reader.dread.data.BookNote.NoteColumn;
import com.dangdang.reader.dread.data.ReadInfo;
import com.dangdang.reader.dread.data.ReadTimes;
import com.dangdang.reader.dread.util.DreaderConstants;
import com.dangdang.reader.utils.ConfigManager;
import com.dangdang.reader.utils.DDAllShelfBookId;
import com.dangdang.zframework.log.LogM;

import java.util.List;

public class ReaderDBHelper extends SQLiteOpenHelper {


    private final static LogM logger = LogM.getLog(ReaderDBHelper.class);
    /**
     * 3.6以前数据库版本是2
     */
    public static final int DBVER_2 = 2;
    /**
     * 3.6版本是3
     */
    public static final int DBVER_3 = 3;
    /**
     * 3.7版本是4
     */
    public static final int DBVER_4 = 4;

    /**
     * 4.8.8(不包含)以后，5.1.0开始 版本是5，书签笔记增加modversion字段
     */
    public static final int DBVER_5 = 5;

    private static final int DBVER = DBVER_5;
    public static final String DBNAME = "newreader.db";

    private Context mContext;

    public ReaderDBHelper(Context context) {
        super(context, DBNAME, null, DBVER);
        mContext = context;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(BookMark.CreateSql);
        db.execSQL(BookNote.CreateNoteSql);
        db.execSQL(ReadTimes.CreateReadTimesSql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        //db.execSQL("DROP TABLE IF EXISTS " + BookMark.Column.TableName);
        //db.execSQL("DROP TABLE IF EXISTS " + BookNote.NoteColumn.TableName);
        //onCreate(db);

        printLog(" onUpgrade oldVersion = " + oldVersion + ", newVersion = " + newVersion);
        if (oldVersion == DBVER_2 && newVersion >= DBVER_3) {

            /**
             * 修改笔记区分试读、全本
             */
            //DDAllShelfBookId ddsb = DDAllShelfBookId.getInstance();
            List<String> list = DDAllShelfBookId.getAllBookIdList();
            for (int i = 0; i < list.size(); i++) {
                final String sql = "update " + NoteColumn.TableName
                        + " set " + NoteColumn.IsBought
                        + " = ? where " + NoteColumn.BookId + "= ?";
                db.execSQL(sql, new Object[]{ReadInfo.BSTATUS_FULL, list.get(i)});
            }
            DDAllShelfBookId.clearAllBookId();
            ConfigManager manager = new ConfigManager(mContext);
            manager.setNoteFullFlag(false);
        }
        if (newVersion == DBVER_4) {

            /**
             * 修改书签老数据的status为新增状态、cloudStatus为非同步
             */
            final String updateMarkSql = "update " + BookMark.Column.TableName + " set " +
                    BookMark.Column.ExpColumn1 + " = ? , " + BookMark.Column.ExpColumn2 + " = ?";
            db.execSQL(updateMarkSql, new Object[]{Status.COLUMN_NEW, Status.CLOUD_NO});

            /**
             * 修改笔记老数据的status为新增状态、cloudStatus为非同步
             * 由于之前存的笔记endindex位置与ios不一致，我们不包含endindex(ios包含)，所以将原来的noteEnd值全部减1, 并修改画线等逻辑。与ios保持一致
             */
            final String updateNoteSql = "update " + NoteColumn.TableName
                    + " set " + NoteColumn.NoteEnd + " = " + NoteColumn.NoteEnd + "-1"
                    + ", " + NoteColumn.ExpColumn1 + " = ? , " + NoteColumn.ExpColumn2 + " = ?";
            db.execSQL(updateNoteSql, new Object[]{Status.COLUMN_NEW, Status.CLOUD_NO});

        }


        if (oldVersion <= DBVER_4 && newVersion >= DBVER_5) {
            //不支持多条alter
            final String alterNoteSql1 = "alter TABLE " + NoteColumn.TableName + " add " + NoteColumn.ModVersion + " VARCHAR DEFAULT " + DreaderConstants.BOOK_MODIFY_VERSION;
            final String alterNoteSql2 = "alter TABLE " + NoteColumn.TableName + " add " + NoteColumn.ExpColumn4 + " VARCHAR ";
            final String alterNoteSql3 = "alter TABLE " + NoteColumn.TableName + " add " + NoteColumn.ExpColumn5 + " VARCHAR ";
            final String alterNoteSql4 = "alter TABLE " + NoteColumn.TableName + " add " + NoteColumn.ExpColumn6 + " VARCHAR ";
            db.execSQL(alterNoteSql1);
            db.execSQL(alterNoteSql2);
            db.execSQL(alterNoteSql3);
            db.execSQL(alterNoteSql4);

            final String alterMarkSql1 = "alter TABLE " + BookMark.Column.TableName + " add " + BookMark.Column.ModVersion + " VARCHAR DEFAULT " + DreaderConstants.BOOK_MODIFY_VERSION;
            final String alterMarkSql2 = "alter TABLE " + BookMark.Column.TableName + " add " + BookMark.Column.ExpColumn4 + " VARCHAR ";
            final String alterMarkSql3 = "alter TABLE " + BookMark.Column.TableName + " add " + BookMark.Column.ExpColumn5 + " VARCHAR ";
            final String alterMarkSql4 = "alter TABLE " + BookMark.Column.TableName + " add " + BookMark.Column.ExpColumn6 + " VARCHAR ";
            db.execSQL(alterMarkSql1);
            db.execSQL(alterMarkSql2);
            db.execSQL(alterMarkSql3);
            db.execSQL(alterMarkSql4);
        }

    }

    public void printLog(String log) {
        logger.i(false, log);
    }

}
