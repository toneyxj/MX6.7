package com.moxi.bookstore.bean;

import java.util.List;

/**
 * Created by Administrator on 2016/9/21.
 * chanelData
 */
public class ChanelData extends Data {
    private String code;
    private Integer count;
    private String dimension;
    private Integer total;
    private List<Sale> saleList;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public String getDimension() {
        return dimension;
    }

    public void setDimension(String dimension) {
        this.dimension = dimension;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public List<Sale> getSaleList() {
        return saleList;
    }

    public void setSaleList(List<Sale> saleList) {
        this.saleList = saleList;
    }
}
