package com.moxi.bookstore.modle;

import org.litepal.annotation.Column;
import org.litepal.crud.DataSupport;

import java.io.Serializable;

/**
 * 搜索历史记录
 * Created by Administrator on 2016/9/12.
 */
public class SearchHistory extends DataSupport implements Serializable{
    @Column(unique = true)
    public long id;
    /**
     * 搜索内容
     */
    public String searchContent;
    /**
     * 搜索时间
     */
    public long time;

    public SearchHistory(long id, String searchContent, long time) {
        this.id = id;
        this.searchContent = searchContent;
        this.time = time;
    }

    public SearchHistory(String searchContent, long time) {
        this.searchContent = searchContent;
        this.time = time;
    }
}
