package com.moxi.bookstore.bean.Message;

import com.moxi.bookstore.bean.Data;
import com.moxi.bookstore.bean.Sale;

import java.util.List;

/**
 * Created by Administrator on 2016/10/10.
 * 推荐实体
 */
public class RecommendData extends Data {
    String colummCode;
    Long columnEndTime;
    Integer columnType;
    Integer count;
    String icon;

    Boolean isShowHorn;
    String name;
    List<Sale> saleList;
    String tips;
    Integer total;

    public String getColummCode() {
        return colummCode;
    }

    public void setColummCode(String colummCode) {
        this.colummCode = colummCode;
    }

    public Long getColumnEndTime() {
        return columnEndTime;
    }

    public void setColumnEndTime(Long columnEndTime) {
        this.columnEndTime = columnEndTime;
    }

    public Integer getColumnType() {
        return columnType;
    }

    public void setColumnType(Integer columnType) {
        this.columnType = columnType;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public Boolean getShowHorn() {
        return isShowHorn;
    }

    public void setShowHorn(Boolean showHorn) {
        isShowHorn = showHorn;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Sale> getSaleList() {
        return saleList;
    }

    public void setSaleList(List<Sale> saleList) {
        this.saleList = saleList;
    }

    public String getTips() {
        return tips;
    }

    public void setTips(String tips) {
        this.tips = tips;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }
}
