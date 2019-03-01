package com.moxi.bookstore.bean;

/**
 * Created by Administrator on 2016/9/20.
 * 所有分类->二级目录->Item
 */
public class CatetoryChanleItem {
    private String code;

    private Integer id;

    private String image;

    private String image2;

    private Boolean leaf;

    private String name;

    private Integer parentId;

    private Boolean parsed;

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
        return "CatetoryChanleItem{" +
                "code='" + code + '\'' +
                ", id=" + id +
                ", image='" + image + '\'' +
                ", image2='" + image2 + '\'' +
                ", leaf=" + leaf +
                ", name='" + name + '\'' +
                ", parentId=" + parentId +
                ", parsed=" + parsed +
                '}';
    }
}
