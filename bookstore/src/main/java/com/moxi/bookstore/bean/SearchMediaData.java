package com.moxi.bookstore.bean;

import java.util.List;

/**
 * Created by Administrator on 2016/9/26.
 * 搜索Data
 */
public class SearchMediaData extends Data {
    private String keyword;
    private List<SearchMedia> searchMediaPaperList ;
    private Integer totalCount;

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public List<SearchMedia> getSearchMediaPaperList() {
        return searchMediaPaperList;
    }

    public void setSearchMediaPaperList(List<SearchMedia> searchMediaPaperList) {
        this.searchMediaPaperList = searchMediaPaperList;
    }

    public Integer getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Integer totalCount) {
        this.totalCount = totalCount;
    }
}
