package com.dangdang.reader.moxiUtils;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.mx.mxbase.utils.StringUtils;
import com.mx.mxbase.utils.ToastUtils;

/**
 * 查询字典
 * Created by xj on 2017/9/11.
 */

public class CheckDirctoryUtils {
    public static final String TAG="CheckDirctoryUtils";
    public static final int DICT_STATE_PARAM_ERROR = -1;
    public static final int DICT_STATE_QUERY_SUCCESSFUL = 0;
    public static final int DICT_STATE_QUERY_FAILED = 1;
    public static final int DICT_STATE_LOADING = 2;
    public static Uri CONTENT_URI = Uri.parse("content://com.onyx.dict.DictionaryProvider");

    public void dictQuery(String inputText, Context context) {
        inputText=inputText.trim();
        if (StringUtils.isNull(inputText)) {
            ToastUtils.getInstance().showToastShort("查询内容为空");
            return;
        }
//        result.loadData("", "text/html", "UTF-8");
        int limit = 5;
        String[] selectionArgs = new String[]{inputText,String.valueOf(limit)};
        Cursor cursor = context.getContentResolver().query(CONTENT_URI, null, null, selectionArgs, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                String[] columns = new String[]{"_id", "state", "keyword", "explanation", "dictPath", "dictName", "entryIndex"};
                int column = cursor.getColumnIndex(columns[0]);
                String id = cursor.getString(column);

                column = cursor.getColumnIndex(columns[1]);
                int state = cursor.getInt(column);

                switch (state) {
                    case DICT_STATE_PARAM_ERROR:
                        break;
                    case DICT_STATE_QUERY_SUCCESSFUL:
                        break;
                    case DICT_STATE_QUERY_FAILED:
                        break;
                    case DICT_STATE_LOADING:
                        Log.i(TAG, "DICT_STATE_LOADING");
//                        handler.sendEmptyMessageDelayed(DICT_QUERY_MSG_QUERY, 1000);
                        return;
                }

                column = cursor.getColumnIndex(columns[2]);
                String keyword = cursor.getString(column);

                column = cursor.getColumnIndex(columns[3]);
                String explanation = cursor.getString(column);

                Log.i(TAG, "state:" + state);
                Log.i(TAG, "return id:" + id);
                Log.i(TAG, "keyword:" + keyword);
                Log.i(TAG, "explanation:" + explanation);

            } while (cursor.moveToNext());
            cursor.close();
        }
    }
}
