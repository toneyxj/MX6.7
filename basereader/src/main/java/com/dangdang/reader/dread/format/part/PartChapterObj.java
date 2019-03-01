package com.dangdang.reader.dread.format.part;

import java.io.Serializable;

/**
 * 分章阅读目录章缓存实体，精简字段
 * Created by Yhyu on 2015/5/28.
 */
public class PartChapterObj implements Serializable {
    private static final long serialVersionUID = 2L;
    private int id;
    private String title;
    private int index;
    private int isFree;//是否免费


    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getIsFree() {
        return isFree;
    }

    public void setIsFree(int isFree) {
        this.isFree = isFree;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
