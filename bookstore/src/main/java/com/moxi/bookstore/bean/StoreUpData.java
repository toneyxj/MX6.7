package com.moxi.bookstore.bean;

import java.util.List;

/**
 * Created by Administrator on 2016/10/12.
 */
public class StoreUpData extends Data {
    private boolean hasNext;
    private List<StoreBook> storeUpList;
    private Integer totalCount;
    public boolean isHasNext() {
        return hasNext;
    }

    public void setHasNext(boolean hasNext) {
        this.hasNext = hasNext;
    }

    public List<StoreBook> getStoreUpList() {
        return storeUpList;
    }

    public void setStoreUpList(List<StoreBook> storeUpList) {
        this.storeUpList = storeUpList;
    }

    public Integer getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Integer totalCount) {
        this.totalCount = totalCount;
    }
}
