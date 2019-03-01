package com.moxi.bookstore.modle;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/2/25.
 */
public class KeyValue implements Serializable{
    /**
     * 可以用SerializedName为与服务器字段不同的字段添加备注，以便用gson解析
     */
//    @SerializedName("id")
    private String key;
//    @SerializedName("alias")
    private String Value;

    public KeyValue() {
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return Value;
    }

    public void setValue(String value) {
        Value = value;
    }

    public KeyValue(String key, String value) {
        this.key = key;
        Value = value;
    }
}
