package com.moxi.bookstore.http.entity;

import com.moxi.bookstore.BookstoreApplication;
import com.moxi.bookstore.http.HttpService;
import com.mx.mxbase.constant.APPLog;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;

/**
 * Created by Administrator on 2016/9/20.
 * 请求数据统一封装类
 */
public abstract class BaseDeal<T> implements Func1<BaseEntity<T>, T> {
    /**
     * 设置参数
     *
     * @param methods
     * @return
     */
    public abstract Observable getObservable(HttpService methods);

    /**
     * 设置回调sub
     *
     * @return
     */
    public abstract Subscriber getSubscirber();


    @Override
    public T call(BaseEntity<T> httpResult) {
        String msg=httpResult.getStatus().getMessage();
        if (null!=msg)
            APPLog.e("responseMsg:"+httpResult.getStatus().getMessage());
        if (httpResult.getStatus().getCode() != 0) {
            httpResult.getStatus().tokenNoEfficacy(BookstoreApplication.getContext());
                throw new RuntimeException(msg == null ? "数据请求失败" : msg);
//            throw new HttpTimeException(httpResult.getStatus().getCode());
        }

        return httpResult.getData();
    }
}
