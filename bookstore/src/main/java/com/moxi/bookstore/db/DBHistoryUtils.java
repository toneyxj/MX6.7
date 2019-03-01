package com.moxi.bookstore.db;

import android.content.ContentValues;
import android.database.Cursor;

import com.moxi.bookstore.modle.SearchHistory;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

/**
 * 历史记录工具类
 * Created by Administrator on 2016/9/30.
 */
public class DBHistoryUtils {
    private static final String sqlSlect = "id,searchContent,time";

    /**
     * 获得历史立即集合
     *
     * @return
     */
    public static List<SearchHistory> getHistorys() {
        Cursor cursor = DataSupport.findBySQL("select " + sqlSlect + " from SearchHistory order by time desc");
        List<SearchHistory> listEvents = new ArrayList<>();
        while (cursor.moveToNext()) {
            long id = cursor.getLong(0);
            String searchContent = cursor.getString(1);
            long time = cursor.getLong(2);
            SearchHistory searchHistory = new SearchHistory(id, searchContent, time);
            listEvents.add(searchHistory);
        }
        if (cursor != null)
            cursor.close();

        return listEvents;
    }

    /**
     * 更新数据时间
     *
     * @param id
     */
    public static void UpdateTime(long id) {
        ContentValues cv = new ContentValues();
        cv.put("time", System.currentTimeMillis());
        DataSupport.update(SearchHistory.class, cv, id);
    }

    /**
     * 添加历史记录
     *
     * @param searchContent 搜索类容
     * @return 返回值
     */
    public static boolean addData(String searchContent,long farHistoryId) {
        Cursor cursor = DataSupport.findBySQL("select " + sqlSlect + " from SearchHistory where searchContent='" + searchContent + "'");
        boolean addSuces = false;
        while (cursor.moveToNext()) {
            addSuces = true;
            UpdateTime(cursor.getLong(2));
        }
        if (cursor != null) cursor.close();

        if (!addSuces) {
            SearchHistory history = new SearchHistory(searchContent, System.currentTimeMillis());
            history.save();
            DeleteFarHistory(farHistoryId);
        }

        return addSuces;
    }

    /**
     * 删除最远的一条数据
     *
     * @param id 最小的一条数据的id
     */
    private static void DeleteFarHistory(long id) {
        int count = DataSupport.count(SearchHistory.class);
        if (count > 8) {
            DataSupport.delete(SearchHistory.class, id);
        }
    }
}
