package com.moxi.bookstore.modle.mediaCategory;

import java.io.Serializable;
import java.util.List;

/**
 * 租阅分类model
 * Created by xj on 2017/11/10.
 */

public class ZYCategotyModel implements Serializable{

    public List<CatetoryList> catetoryList;
    public String code;
    public long id;
    public String image;
    public boolean leaf;
    public String name;
    public long parentId;
    public boolean parsed;
}
