package com.moxi.nexams.db.newdb;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.moxi.nexams.db.AES;
import com.moxi.nexams.db.AssetsDatabaseManager;
import com.moxi.nexams.model.papermodel.PaperDetailsModel;
import com.moxi.nexams.model.papermodel.PaperModelDesc;

import java.util.ArrayList;
import java.util.List;

/**
 * 新的试卷展示sqlite工具类
 * Created by Archer on 2017/1/9.
 */
public class NewExamsSqliteUtils {
    private SQLiteDatabase db;
    private Context context;

    /**
     * 构造方法
     *
     * @param context  上下文
     * @param dbFile   db文件
     * @param filePath 文件路径
     */
    public NewExamsSqliteUtils(Context context, String filePath, String dbFile) {
        AssetsDatabaseManager.initManager(context.getApplicationContext());
        AssetsDatabaseManager mg = AssetsDatabaseManager.getManager();
        db = mg.getDatabase(filePath, dbFile);
        this.context = context;
    }

    /**
     * 获取所有试卷题目类型
     *
     * @return
     */
    public List<PaperModelDesc> getAllTypePaper() {
        List<PaperModelDesc> result = new ArrayList<>();
        String sql = "select pps_id,pps_maintitle,pps_deputytitle from T_PaperSection where 1=1";
        Cursor cursor = db.rawQuery(sql, new String[]{});
        if (cursor != null && cursor.moveToFirst()) {
            do {
                PaperModelDesc pmd = new PaperModelDesc();
                int ppsId = cursor.getInt(cursor.getColumnIndex("pps_id"));
                String ppsMainTitle = cursor.getString(cursor.getColumnIndex("pps_maintitle"));
                String ppsDeputyTitle = cursor.getString(cursor.getColumnIndex("pps_deputytitle"));
                pmd.setPpsId(ppsId);
                pmd.setPpsMainTitle(AES.decode(ppsMainTitle));
                pmd.setPpsDeputyTitle(AES.decode(ppsDeputyTitle));
                pmd.setTotal(getCountGroupByType(ppsId));
                result.add(pmd);
            } while (cursor.moveToNext());
        }
        return result;
    }

    /**
     * 根据题目id获取题目数量
     *
     * @param ppsId
     * @return
     */
    public Integer getCountGroupByType(int ppsId) {
        int temp = 1;
        String sql = "SELECT COUNT(*) as 'total' FROM T_PaperSubject WHERE T_PaperSubject.psj_pps_id = " + ppsId + " AND T_PaperSubject.psj_parentid  IS NOT NULL GROUP BY T_PaperSubject.psj_pps_id";
        Cursor cursor = db.rawQuery(sql, new String[]{});
        if (cursor != null && cursor.moveToFirst()) {
            do {
                temp = cursor.getInt(cursor.getColumnIndex("total"));
            } while (cursor.moveToNext());
        }
        return temp;
    }

    /**
     * 根据题型获取题目详情
     *
     * @param ppsId 题型id
     */
    public List<PaperDetailsModel> getPaperDetailsByPpsId(int ppsId) {
        List<PaperDetailsModel> listPaper = new ArrayList<>();
        String sql = "select psj_id,psj_title,psj_option,psj_answer,psj_analysis from T_PaperSubject where 1=1 and psj_pps_id=" + ppsId + " and psj_parentid is null";
        Cursor cursor = db.rawQuery(sql, new String[]{});
        if (cursor != null && cursor.moveToFirst()) {
            do {
                PaperDetailsModel pdm = new PaperDetailsModel();
                pdm.setPsjId(cursor.getInt(cursor.getColumnIndex("psj_id")));
                pdm.setPsjAnswer(AES.decode(cursor.getString(cursor.getColumnIndex("psj_answer"))));
                pdm.setPsjOption(AES.decode(cursor.getString(cursor.getColumnIndex("psj_option"))));
                pdm.setPsjTitle(AES.decode(cursor.getString(cursor.getColumnIndex("psj_title"))));
                pdm.setPsjAnalysis(AES.decode(cursor.getString(cursor.getColumnIndex("psj_analysis"))));
                pdm.setPsParentId("");
                listPaper.add(pdm);
            } while (cursor.moveToNext());
        }
        return listPaper;
    }

    /**
     * @param parentId
     * @return
     */
    public List<PaperDetailsModel> getPaperChildrenByParentId(String parentId) {
        List<PaperDetailsModel> listPaper = new ArrayList<>();
        String sql = "select psj_id,psj_title,psj_option,psj_answer,psj_analysis from T_PaperSubject where 1=1 and psj_parentid=" + parentId;
        Cursor cursor = db.rawQuery(sql, new String[]{});
        if (cursor != null && cursor.moveToFirst()) {
            do {
                PaperDetailsModel pdm = new PaperDetailsModel();
                pdm.setPsjId(cursor.getInt(cursor.getColumnIndex("psj_id")));
                pdm.setPsjAnswer(AES.decode(cursor.getString(cursor.getColumnIndex("psj_answer"))));
                pdm.setPsjOption(AES.decode(cursor.getString(cursor.getColumnIndex("psj_option"))));
                pdm.setPsjTitle(AES.decode(cursor.getString(cursor.getColumnIndex("psj_title"))));
                pdm.setPsjAnalysis(AES.decode(cursor.getString(cursor.getColumnIndex("psj_analysis"))));
                pdm.setPsParentId(parentId);
                listPaper.add(pdm);
            } while (cursor.moveToNext());
        }
        return listPaper;
    }
}
