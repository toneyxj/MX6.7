package com.moxi.bookstore.http;

import com.moxi.bookstore.http.entity.BaseDeal;
import com.moxi.bookstore.http.fastJson.FastJsonConverterFactory;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Administrator on 2016/9/20.
 */
public class HttpManager {

    public static final String BASE_URL = "http://e.dangdang.com/";
    private final static int CONNECT_TIMEOUT =15;
    private final static int READ_TIMEOUT=30;
    private final static int WRITE_TIMEOUT=15;
    private HttpService httpService;
        private volatile static HttpManager INSTANCE;

        //构造方法私有
        private HttpManager() {
            //手动创建一个OkHttpClient并设置超时时间
            OkHttpClient.Builder builder = new OkHttpClient.Builder()
                    .connectTimeout(CONNECT_TIMEOUT,TimeUnit.SECONDS)
                    .readTimeout(READ_TIMEOUT,TimeUnit.SECONDS)
                    .writeTimeout(WRITE_TIMEOUT,TimeUnit.SECONDS);
            Retrofit retrofit = new Retrofit.Builder()
                    .client(builder.build())
                    .addConverterFactory(FastJsonConverterFactory.create())
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .baseUrl(BASE_URL)
                    .build();
            httpService = retrofit.create(HttpService.class);
        }

        //获取单例
        public static HttpManager getInstance() {
            if (INSTANCE == null) {
                synchronized (HttpManager.class) {
                    if (INSTANCE == null) {
                        INSTANCE = new HttpManager();
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
    public void doHttpDeal(BaseDeal basePar) {
        Observable observable = basePar.getObservable(httpService)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(basePar);
        observable.subscribe(basePar.getSubscirber());
    }

}
