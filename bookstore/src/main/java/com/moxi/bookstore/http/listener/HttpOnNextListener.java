package com.moxi.bookstore.http.listener;

/**
 * Created by Administrator on 2016/9/20.
 * 请求成功回调处理
 */

public abstract class HttpOnNextListener <T> {
    public abstract void onNext(T t);
    public abstract void onError();
   public void onError(String msg){};
}
