package com.moxi.bookstore.modle.mediaCategory;

import java.io.Serializable;

/**
 * 租阅分类子model
 * Created by xj on 2017/11/10.
 */

public class CatetoryList implements Serializable {
//    public List<CatetoryList> catetoryList;
    public String code;
    public long id;
    public String image;
    public boolean leaf;
    public String name;
    public long parentId;
    public boolean parsed;

    @Override
    public String toString() {
        return "CatetoryList{" +
                "code='" + code + '\'' +
                ", id=" + id +
                ", image='" + image + '\'' +
                ", leaf=" + leaf +
                ", name='" + name + '\'' +
                ", parentId=" + parentId +
                ", parsed=" + parsed +
                '}';
    }
}
