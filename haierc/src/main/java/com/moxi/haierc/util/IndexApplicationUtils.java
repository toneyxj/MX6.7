package com.moxi.haierc.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.moxi.haierc.hjbook.hjutils.AssetsDatabaseManager;
import com.moxi.haierc.model.AppModel;
import com.mx.mxbase.utils.StringUtils;

import java.util.ArrayList;

/**
 * Created by King on 2017/10/17.
 */

public class IndexApplicationUtils {
    public static IndexApplicationUtils instance;
    public static SQLiteDatabase db;

    public IndexApplicationUtils(Context context) {
        AssetsDatabaseManager.initManager(context.getApplicationContext());
    }

    public static IndexApplicationUtils getInstance(Context context) {
        if (instance == null) {
            instance = new IndexApplicationUtils(context);
        }
        AssetsDatabaseManager mg = AssetsDatabaseManager.getManager();
        db = mg.getDatabase("appslist_2.0.4.db");
        return instance;
    }

    /**
     * 根据当前应用版本获取当前显示应用
     *
     * @param flag
     * @return
     */
    public ArrayList<AppModel> getCurrentShowApps(String flag) {
        ArrayList<AppModel> listTemp = new ArrayList<>();
        listTemp.clear();
        String sql;
        if (flag.equals("教育版")) {
            sql = "select * from AppEducationTable where isshow = 1 order by position,updateTime DESC";
        } else {
            sql = "select * from AppBusinessTable where isshow = 1 order by position,updateTime DESC";
        }
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast() && (cursor.getString(1) != null)) {
                listTemp.add(getAppModel(cursor));
                cursor.moveToNext();
            }
            cursor.close();
        }
        return listTemp;
    }

    /**
     * 根据当前应用版本获取当前隐藏的应用
     *
     * @param flag
     * @return
     */
    public ArrayList<AppModel> getCurrentHideApps(String flag) {
        ArrayList<AppModel> listTemp = new ArrayList<>();//DataSupport.findAll(AppModel.class)
        listTemp.clear();
        String sql;
        if (flag.equals("教育版")) {
            sql = "select * from AppEducationTable where isshow != 1 order by updateTime DESC";
        } else {
            sql = "select * from AppBusinessTable where isshow != 1 order by updateTime DESC";
        }
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast() && (cursor.getString(1) != null)) {
                listTemp.add(getAppModel(cursor));
                cursor.moveToNext();
            }
            cursor.close();
        }
        return listTemp;
    }

    /**
     * 添加数据到数据库
     *
     * @param flag
     * @param appModel
     */
    public void insertAppLog(String flag, AppModel appModel) {
        String name=appModel.getAppPackageName();
        if (StringUtils.isNull(name))return;
        if (StringUtils.isNull(appModel.getAppLauncherClass()))return;

        Cursor cursor=db.rawQuery("select apppackagename from AppEducationTable where apppackagename='"+name+"'",null);
        if (cursor!=null){
            if (cursor.moveToNext()){//已经保存所以返回不再插入
                return;
            }
            cursor.close();
        }
        ContentValues insertValues = new ContentValues();
        insertValues.put("apppackagename", appModel.getAppPackageName());
        insertValues.put("appname", appModel.getAppName());
        insertValues.put("applauncherclass", appModel.getAppLauncherClass());
        insertValues.put("updatetime", appModel.getUpdateTime());
        insertValues.put("isshow", appModel.getIsShow());
        insertValues.put("position", appModel.getPosition());
        insertValues.put("iscanreplace", appModel.getIsCanReplace());
        if (flag.equals("")) {
            insertValues.put("position", appModel.getPosition());
            long id1 = db.insert("AppEducationTable", null, insertValues);
            if (id1 > 0) {
//                Log.e("----", "新增AppEducationTable" + GsonTools.obj2json(appModel));
            }
            long id2 = db.insert("AppBusinessTable", null, insertValues);
            if (id2 > 0) {
//                Log.e("----", "新增AppBusinessTable" + GsonTools.obj2json(appModel));
            }
        } else if (flag.equals("教育版")) {
            long id1 = db.insert("AppEducationTable", null, insertValues);
            if (id1 > 0) {
//                Log.e("----", "新增AppEducationTable" + GsonTools.obj2json(appModel));
            }
        } else {
            long id2 = db.insert("AppBusinessTable", null, insertValues);
            if (id2 > 0) {
//                Log.e("----", "新增AppBusinessTable" + GsonTools.obj2json(appModel));
            }
        }
    }

    public ArrayList<AppModel> selectAppByPosition(String flag, int position) {
        ArrayList<AppModel> listTemp = new ArrayList<>();
        String sqlTemp;
        if (flag.equals("教育版")) {
            sqlTemp = "select * from AppEducationTable where position = " + position + " order by updateTime DESC";
        } else {
            sqlTemp = "select * from AppBusinessTable where position = " + position + " order by updateTime DESC";
        }
        Cursor cursorTemp = db.rawQuery(sqlTemp, null);
        if (cursorTemp.moveToFirst()) {
            do {
                AppModel appTemp = getAppModel(cursorTemp);
                listTemp.add(appTemp);
            } while (cursorTemp.moveToNext());
        }
        return listTemp;
    }

    /**
     * 更新表内容
     *
     * @param flag
     * @param appModel
     */
    public void updateAppInfo(String flag, AppModel appModel) {
        ContentValues appValues = new ContentValues();
        appValues.put("updatetime", appModel.getUpdateTime());
        appValues.put("isshow", appModel.getIsShow());
        appValues.put("position", appModel.getPosition());
        if (flag.equals("教育版")) {
            int result = db.update("appeducationtable", appValues, "apppackagename=?", new String[]{appModel.getAppPackageName()});
            if (result > 0) {
//                Log.e("----", "updateAppInfo 教育版" + GsonTools.obj2json(appModel));
            }
        } else {
            int result = db.update("appbusinesstable", appValues, "apppackagename=?", new String[]{appModel.getAppPackageName()});
            if (result > 0) {
//                Log.e("----", "updateAppInfo 商务版" + GsonTools.obj2json(appModel));
            }
        }
    }

    /**
     * 删除应用信息
     *
     * @param packageName
     */
    public void deleteAppInfo(String packageName) {
        int delete1 = db.delete("AppBusinessTable", "apppackagename = ?", new String[]{packageName});
        if (delete1 > 0) {
//            Log.e("----", "deleteAppInfo 商务版" + packageName);
        }
        int delete2 = db.delete("AppEducationTable", "apppackagename = ?", new String[]{packageName});
        if (delete2 > 0) {
//            Log.e("----", "deleteAppInfo 教育版" + packageName);
        }
    }

    /**
     * 组装model
     *
     * @param cursor
     * @return
     */
    private AppModel getAppModel(Cursor cursor) {
        int isShow = cursor.getInt(cursor.getColumnIndex("isshow"));
        int isCanReplace = cursor.getInt(cursor.getColumnIndex("iscanreplace"));
        String appPackageName = cursor.getString(cursor.getColumnIndex("apppackagename"));
        String appName = cursor.getString(cursor.getColumnIndex("appname"));
        int position = cursor.getInt(cursor.getColumnIndex("position"));
        String appLauncherClass = cursor.getString(cursor.getColumnIndex("applauncherclass"));

        AppModel model = new AppModel();
        model.setAppName(appName);
        model.setAppPackageName(appPackageName);
        model.setIsCanReplace(isCanReplace);
        model.setIsShow(isShow);
        model.setAppLauncherClass(appLauncherClass);
        model.setPosition(position);
        return model;
    }
}
