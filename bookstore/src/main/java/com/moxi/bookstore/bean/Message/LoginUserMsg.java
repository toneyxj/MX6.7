package com.moxi.bookstore.bean.Message;

import com.moxi.bookstore.bean.BaseMessage;
import com.moxi.bookstore.bean.LoginUserData;

/**
 * Created by Administrator on 2016/11/16.
 */

public class LoginUserMsg extends BaseMessage {
    private LoginUserData data;

    public LoginUserData getData() {
        return data;
    }

    public void setData(LoginUserData data) {
        this.data = data;
    }
}
