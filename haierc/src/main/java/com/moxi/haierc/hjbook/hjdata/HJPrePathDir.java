package com.moxi.haierc.hjbook.hjdata;

import org.litepal.annotation.Column;
import org.litepal.crud.DataSupport;

import java.io.Serializable;

/**
 * Created by King on 2017/8/30.
 */

public class HJPrePathDir extends DataSupport implements Serializable {
    @Column(unique = true)
    public long id;
    public String dirName;
    /**
     * 阅读文件路径
     */
    @Column(unique = true)
    public String prePath;
    /**
     * 阅读文件图片路径
     */
    public String photoPath;
    /**
     * 文件夹全拼
     */
    public String fullPinyin;
    /**
     * 多少本书籍
     */
    public int number;

    public int sortIndex;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDirName() {
        return dirName;
    }

    public void setDirName(String dirName) {
        this.dirName = dirName;
    }

    public String getPrePath() {
        return prePath;
    }

    public void setPrePath(String prePath) {
        this.prePath = prePath;
    }

    public String getPhotoPath() {
        return photoPath;
    }

    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
    }

    public String getFullPinyin() {
        return fullPinyin;
    }

    public void setFullPinyin(String fullPinyin) {
        this.fullPinyin = fullPinyin;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public int getSortIndex() {
        return sortIndex;
    }

    public void setSortIndex(int sortIndex) {
        this.sortIndex = sortIndex;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
