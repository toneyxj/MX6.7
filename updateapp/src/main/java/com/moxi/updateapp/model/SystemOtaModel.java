package com.moxi.updateapp.model;

import java.io.Serializable;

/**
 * Created by xj on 2018/5/17.
 */

public class SystemOtaModel implements Serializable {
    public String url;
    public String MD5;
    public String describe;

    public SystemOtaModel(String url, String MD5, String describe) {
        this.url = url;
        this.MD5 = MD5;
        this.describe = describe;
    }
    public SystemOtaModel() {
    }

    @Override
    public String toString() {
        return "SystemOtaModel{" +
                "url='" + url + '\'' +
                ", MD5='" + MD5 + '\'' +
                ", describe='" + describe + '\'' +
                '}';
    }
}
