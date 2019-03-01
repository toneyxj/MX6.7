package com.moxi.bookstore.bean.Message;

import com.moxi.bookstore.bean.BaseMessage;
import com.moxi.bookstore.bean.ChanelData;

/**
 * Created by Administrator on 2016/9/21.
 * chanel消息数据
 */
public class ChanelMessage extends BaseMessage{
    private ChanelData data;

    public ChanelData getData() {
        return data;
    }

    public void setData(ChanelData data) {
        this.data = data;
    }
}
