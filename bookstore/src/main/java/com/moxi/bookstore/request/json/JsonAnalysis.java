package com.moxi.bookstore.request.json;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dangdang.reader.MXModel.CiBa.CiBaModel;
import com.dangdang.reader.MXModel.CiBa.hanyu.HanyuMode;
import com.moxi.bookstore.bean.ChanelData;
import com.moxi.bookstore.modle.mediaCategory.ChannelMonthlyStrategy;
import com.moxi.bookstore.modle.mediaCategory.MonthlyChannel;
import com.moxi.bookstore.modle.mediaCategory.ZYCategotyModel;

import java.util.List;

/**
 * 数据解析类
 * Created by xj on 2017/11/10.
 */

public class JsonAnalysis {
    // 初始化类实列
    private static JsonAnalysis instatnce = null;

    public static JsonAnalysis getInstance() {
        if (instatnce == null) {
            synchronized (JsonAnalysis.class) {
                if (instatnce == null) {
                    instatnce = new JsonAnalysis();
                }
            }
        }
        return instatnce;
    }

    private String getJsonAttayStr(String result,String key) {
        JSONObject object = JSON.parseObject(result);
        JSONObject data = object.getJSONObject("data");
        JSONArray array=data.getJSONArray(key);
        return array.toString();
    }

    private String getDataStr(String result) {
        JSONObject object = JSON.parseObject(result);
        JSONObject data = object.getJSONObject("data");
        return data.toString();
    }
    private String getDataKey(String result,String key) {
        JSONObject object = JSON.parseObject(result);
        JSONObject data = object.getJSONObject("data");
        return data.getString(key);
    }

//    /**
//     * 获得解析的模板数据
//     * @param t
//     * @param <T>
//     * @return
//     */
//    public <T> List<T> getList(T t){
//        List<T> list=new ArrayList<>();
//
//        return list;
//    }

    /**
     * 获取租阅分类数据列表
     * @param result
     * @return
     */
    public List<ZYCategotyModel> getZYCategotyModels(String result){
        return JSON.parseArray(getJsonAttayStr(result,"catetoryList"),ZYCategotyModel.class);
    }
    /**
     * 购买VIP获取vip产品列表
     * @param result
     * @return
     */
    public List<ChannelMonthlyStrategy> getChannelMonthlyStrategys(String result){
        JSONObject object = JSON.parseObject(result);
        JSONObject data = object.getJSONObject("data");
        JSONObject channel=data.getJSONObject("channel");
        JSONArray array=channel.getJSONArray("channelMonthlyStrategy");
        return JSON.parseArray(array.toString(),ChannelMonthlyStrategy.class);
    }
    /**
     * 获取租阅详细信息
     * @param result
     * @return
     */
    public ChanelData getChanelData(String result){
        return JSON.parseObject(getDataStr(result),ChanelData.class);
    }


    /**
     * 获得我的租阅权限数据
     * @param result
     * @return
     */
    public MonthlyChannel getMonthlyChannel(String result){
        List<MonthlyChannel> list=JSON.parseArray(getJsonAttayStr(result,"channelList"),MonthlyChannel.class);
        if (list.size()>0){
            return list.get(0);
        }else {
            return null;
        }
    }
    public String getBlock(String reulst){
        return getDataKey(reulst,"block");
    }
    /**
     * 获取翻译内容信息
     * @param result
     * @return
     */
    public CiBaModel getCiBaModel(String result){
        return JSON.parseObject(result,CiBaModel.class);
    }
    /**
     * 获取翻译内容信息
     * @param result
     * @return
     */
    public HanyuMode getHanyuModel(String result){
        return JSON.parseObject(result,HanyuMode.class);
    }
}
