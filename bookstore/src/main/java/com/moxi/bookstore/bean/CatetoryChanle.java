package com.moxi.bookstore.bean;

import com.mx.mxbase.constant.APPLog;

import java.util.List;

/**
 * Created by Administrator on 2016/9/20.
 * 所有分类->二级分类
 */
public class CatetoryChanle {
    private List<CatetoryChanleItem> catetoryList ;

    private String code;

    private Integer id;

    private String image;

    private String image2;

    private Boolean leaf;

    private String name;

    private Integer parentId;

    private Boolean parsed;

    public List<CatetoryChanleItem> getCatetoryList() {
        return catetoryList;
    }

    public void setCatetoryList(List<CatetoryChanleItem> catetoryList) {
        this.catetoryList = catetoryList;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getImage2() {
        return image2;
    }

    public void setImage2(String image2) {
        this.image2 = image2;
    }

    public Boolean getLeaf() {
        return leaf;
    }

    public void setLeaf(Boolean leaf) {
        this.leaf = leaf;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    public Boolean getParsed() {
        return parsed;
    }

    public void setParsed(Boolean parsed) {
        this.parsed = parsed;
    }

    @Override
    public String toString() {
        APPLog.e("=========================开始=================================");
        String value= "CatetoryChanle{" +
                "catetoryList="+
                ", code='" + code + '\'' +
                ", id=" + id +
                ", image='" + image + '\'' +
                ", image2='" + image2 + '\'' +
                ", leaf=" + leaf +
                ", name='" + name + '\'' +
                ", parentId=" + parentId +
                ", parsed=" + parsed +
                '}';
        for (int i = 0; i < catetoryList.size(); i++) {
            APPLog.e("catetoryList-"+i,catetoryList.get(i).toString());
        }

        return value;
    }
}
