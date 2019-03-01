package com.dangdang.reader.moxiUtils;

import java.io.Serializable;

/**
 * 阅读完成广播发送数据
 * Created by Administrator on 2016/9/27.
 */
public class BrodcastData implements Serializable{
    /**
     * 图书id
     */
    public String id;
//    /**
//     * 阅读的当前章节
//     */
//    public String chapter;
    /**
     * 阅读进度入30%
     */
    public String progress;

    public BrodcastData(){}
//
//    public BrodcastData(String id, String chapter, String progress) {
//        this.id = id;
//        this.chapter = chapter;
//        this.progress = progress;
//    }

    public BrodcastData(String id, String progress) {
        this.id = id;
        this.progress = progress;
    }

    @Override
    public String toString() {
        return "BrodcastData{" +
                "id='" + id + '\'' +
                ", progress='" + progress + '\'' +
                '}';
    }
}
