package com.moxi.haierc.model;

import android.content.Context;

import com.alibaba.fastjson.JSON;
import com.mx.mxbase.model.BaseModel;
import com.mx.mxbase.utils.SharePreferceUtil;
import com.mx.mxbase.utils.StringUtils;

/**
 * 开关机图片解析类
 * Created by xj on 2018/7/10.
 */

public class StopOrPowerPhoto extends BaseModel {

    private ResultPhoto result;

    public ResultPhoto getResult() {
        return result;
    }

    public void setResult(ResultPhoto result) {
        this.result = result;
    }

    public static void setPhotoString(Context context, String resultstr){
        SharePreferceUtil.getInstance(context).setCache("StopOrPowerPhoto",resultstr);
    }
    public StopOrPowerPhoto getoldString(Context context){
        String result=SharePreferceUtil.getInstance(context).getString("StopOrPowerPhoto");
        if (StringUtils.isNull(result))return null;
        return JSON.parseObject(result,StopOrPowerPhoto.class);

    }
    public ResultPhoto getUpdateImg(Context context){
        StopOrPowerPhoto photo=getoldString(context);
        try {
            if (photo!=null)   {
                if (photo.getResult().getPowerImage().equals(getResult().getPowerImage())){
                    this.getResult().setPowerImage("");
                }
                if (photo.getResult().getWaitImage().equals(getResult().getWaitImage())){
                    this.getResult().setWaitImage("");
                }
            }
        }catch (Exception e){
            return null;
        }
        return this.result;
    }

    public static class ResultPhoto {
        /**
         * extLink : null
         * imageUrl : /adimage/1_20170710170106_274_265.jpg
         */

        private String powerImage;
        private String waitImage;

        public String getPowerImage() {
            return powerImage;
        }

        public void setPowerImage(String powerImage) {
            this.powerImage = powerImage;
        }

        public String getWaitImage() {
            return waitImage;
        }

        public void setWaitImage(String waitImage) {
            this.waitImage = waitImage;
        }

        @Override
        public String toString() {
            return "ResultPhoto{" +
                    "powerImage='" + powerImage + '\'' +
                    ", waitImage='" + waitImage + '\'' +
                    '}';
        }
    }
}


