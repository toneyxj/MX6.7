package com.moxi.bookstore.request;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.moxi.bookstore.bean.Media;
import com.moxi.bookstore.modle.KeyValue;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by Administrator on 2016/4/20.
 */
public class JsonData {
    // 初始化类实列
    private static JsonData instatnce = null;
    /**
     * 初始化gson
     */
    private static final Gson gson = new Gson();

    /**
     * 获得软键盘弹出类实列
     *
     * @return 返回初始化实列
     */
    public static JsonData getInstance() {
        if (instatnce == null) {
            synchronized (JsonData.class) {
                if (instatnce == null) {
                    instatnce = new JsonData();
                }
            }
        }
        return instatnce;
    }

    /**
     * gson解析模板
     */
    private List get(String jsonArray, Class c) {
        List<KeyValue> ps = gson.fromJson(jsonArray, new TypeToken<List<KeyValue>>() {
        }.getType());
        /**
         * 类转json数据
         */
        gson.toJson(ps);
        return ps;
    }

    /**
     * 获得解析数据的array字符串
     *
     * @param result 传入完整请求所得
     * @return 返回array字符串
     */
    public String getJsonArray(String result) {

        JSONObject object = null;
        try {
            object = new JSONObject(result);
            JSONObject data = object.getJSONObject("data");
            JSONArray array = data.getJSONArray("list");
            return array.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }

    public String setMediaStr(Context context, List<Media> medias) {
        SharedPreferences preferences = context.getSharedPreferences("recommend", context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        String str = gson.toJson(medias);
        editor.putString("reco", str);
        editor.commit();
        return str;
    }

    public List<Media> getMedias(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("recommend", context.MODE_PRIVATE);
        String str = preferences.getString("reco", "[]");
        List<Media> ps = gson.fromJson(str, new TypeToken<List<Media>>() {
        }.getType());
        return ps;
    }

    /**
     * 是否更新推荐数据
     * @return
     */
    public boolean isCanrequest(Context context){
        SharedPreferences preferences = context.getSharedPreferences("recommend", context.MODE_PRIVATE);
        long time=preferences.getLong("requestTime",0);
        if (Math.abs(System.currentTimeMillis()-time)>1000*60*60*24){
            setCanRequest(context,System.currentTimeMillis());
            return true;
        }else {
            return true;
//            return false;
        }
    }
    private void setCanRequest(Context context,long time){
        SharedPreferences preferences = context.getSharedPreferences("recommend", context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
         editor.putLong("requestTime", time);
        editor.commit();
    }


}
