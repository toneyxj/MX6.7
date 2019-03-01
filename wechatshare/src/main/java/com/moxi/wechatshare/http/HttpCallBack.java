package com.moxi.wechatshare.http;

/**
 * Created by along on 2017/8/31.
 */

public interface HttpCallBack {

    public void onSuccess(String result);
    public void onFaild(int code, String message);

}
