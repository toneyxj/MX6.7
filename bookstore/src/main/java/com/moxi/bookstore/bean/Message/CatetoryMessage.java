package com.moxi.bookstore.bean.Message;

import com.moxi.bookstore.bean.BaseMessage;
import com.moxi.bookstore.bean.CatetoryData;



/**
 * Created by Administrator on 2016/9/20.
 * 分类message实体
 */
public class CatetoryMessage extends BaseMessage {
    private CatetoryData data;

    public CatetoryData getData() {
        return data;
    }

    public void setData(CatetoryData data) {
        this.data = data;
    }
}
