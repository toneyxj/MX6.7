package com.dangdang.reader.moxiUtils;

import android.content.Context;
import android.graphics.Typeface;

import com.mx.mxbase.constant.APPLog;
import com.mx.mxbase.utils.StringUtils;

/**
 * Created by xj on 2017/10/30.
 */

public class TTFModel {
    //字体文件
    private String sdFilePath = StringUtils.getFilePath("font/");

    public String filePath;
    public String assetsName;
    public String ttfName;
    /**
     * 是否是assets文件
     */
    public boolean isAssets=false;
    private Typeface typeFace=null;

    public TTFModel(String filePath,String assetsName, String ttfName, boolean isAssets) {
        if (filePath!=null) {
            this.filePath = String.valueOf(filePath + assetsName);
        }else {
            this.filePath=filePath;
        }
        this.assetsName=assetsName;
        this.ttfName = ttfName;
        this.isAssets = isAssets;
    }

    public TTFModel(String filePath, String ttfName) {
        this.filePath = filePath;
        this.ttfName = ttfName;
    }
    /**
     * 获得sd卡字体文件路径
     * @return
     */
    public String getFontsSdCardPath(){
        String path=filePath;
        if (isAssets&&null!=filePath&&!filePath.equals("")) {
             path = sdFilePath + assetsName;
        }
        return path;
    }

    /**
     * 获得字体文字的类型
     * @param context
     * @return
     */
    public Typeface getTypeface(Context context){
        if (typeFace==null) {
            try {
                APPLog.e("isAssets=" + isAssets, "filePath=" + filePath);
                if (null == filePath || filePath.equals("")) {
                    typeFace = null;
                } else if (isAssets) {
                    typeFace = Typeface.createFromAsset(context.getAssets(), filePath);
                } else {
                    typeFace = Typeface.createFromFile(filePath);
                }
            }catch (Exception e){}
        }
        return typeFace;
    }

    @Override
    public String toString() {
        return "TTFModel{" +
                "sdFilePath='" + sdFilePath + '\'' +
                ", filePath='" + filePath + '\'' +
                ", assetsName='" + assetsName + '\'' +
                ", ttfName='" + ttfName + '\'' +
                ", isAssets=" + isAssets +
                '}';
    }
}
