package com.moxi.nexams.model.papermodel;

import java.io.Serializable;

/**
 * Created by Archer on 2017/1/9.
 */
public class DetailsTestModel implements Serializable {
    private String id;
    private String desc;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
