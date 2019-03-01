package com.moxi.bookstore.bean;

import java.util.List;

/**
 * Created by Administrator on 2016/9/20.
 * 分类数据实体
 */
public class CatetoryData extends Data {
    private List<DZSCatetory> catetoryList;

    public List<DZSCatetory> getCatetoryList() {
        return catetoryList;
    }

    public void setCatetoryList(List<DZSCatetory> catetoryList) {
        this.catetoryList = catetoryList;
    }
}
