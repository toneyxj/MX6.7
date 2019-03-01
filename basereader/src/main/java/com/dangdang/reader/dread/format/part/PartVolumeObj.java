package com.dangdang.reader.dread.format.part;

import java.io.Serializable;

/**
 * 分章阅读目录卷缓存实体，精简字段
 * Created by Yhyu on 2015/5/28.
 */
public class PartVolumeObj implements Serializable {
    private static final long serialVersionUID = 1L;
    private String id;
    private String title;
    private int count;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
