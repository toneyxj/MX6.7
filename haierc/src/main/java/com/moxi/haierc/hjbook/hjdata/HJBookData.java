package com.moxi.haierc.hjbook.hjdata;

import org.litepal.annotation.Column;
import org.litepal.crud.DataSupport;

import java.io.Serializable;

/**
 * Created by King on 2017/8/30.
 */

public class HJBookData extends DataSupport implements Serializable {

    @Column(unique = true)
    public long id;
    /**
     * 阅读文件父级目录
     */
    public String prePath;
    /**
     * 阅读文件路径
     */
    @Column(unique = true)
    public String filePath;
    /**
     * 阅读文件图片路径
     */
    public String photoPath;
    /**
     * 文件路径的md5值
     */
    public String pathMd5;
    /**
     * 阅读文件索引值
     */
    public long openTime;
    /**
     * 文件全拼
     */
    public String fullPinyin;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getPrePath() {
        return prePath;
    }

    public void setPrePath(String prePath) {
        this.prePath = prePath;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getPhotoPath() {
        return photoPath;
    }

    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
    }

    public String getPathMd5() {
        return pathMd5;
    }

    public void setPathMd5(String pathMd5) {
        this.pathMd5 = pathMd5;
    }

    public long getOpenTime() {
        return openTime;
    }

    public void setOpenTime(long openTime) {
        this.openTime = openTime;
    }

    public String getFullPinyin() {
        return fullPinyin;
    }

    public void setFullPinyin(String fullPinyin) {
        this.fullPinyin = fullPinyin;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
