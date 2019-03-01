package com.moxi.bookstore.http;

import com.moxi.bookstore.http.deal.DownLoaddeal;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Administrator on 2016/9/20.
 */
public class DownloadManager {

        public static final String BASE_URL = "http://e.dangdang.com/";
        private static final int DEFAULT_TIMEOUT = 15;
        private HttpService httpService;
        private volatile static DownloadManager INSTANCE;

        //构造方法私有
        private DownloadManager() {
            //手动创建一个OkHttpClient并设置超时时间
            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS);
            Retrofit retrofit = new Retrofit.Builder()
                    .client(builder.build())
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .baseUrl(BASE_URL)
                    .build();
            httpService = retrofit.create(HttpService.class);
        }

        //获取单例
        public static DownloadManager getInstance() {
            if (INSTANCE == null) {
                synchronized (DownloadManager.class) {
                    if (INSTANCE == null) {
                        INSTANCE = new DownloadManager();
                    }
                }
            }
            return INSTANCE;
        }

    /**
     * 处理http请求
     *
     * @param basePar 封装的请求数据
     */
    public void doDownLoadDeal(DownLoaddeal basePar) {
        Observable observable = basePar.getObservable(httpService)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(Schedulers.io());
        observable.subscribe(basePar.getSubscirer());
    }

    public void doDownLoad( long mediaId,Subscriber subscriber){
        httpService.downloadSDMedia("downloadMediaWhole",mediaId,0,"Android","DDDS-P","")
                    .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }

}
