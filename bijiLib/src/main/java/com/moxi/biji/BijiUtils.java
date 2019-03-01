package com.moxi.biji;

import android.app.Application;

import com.evernote.client.android.EvernoteSession;

/**
 * Created by Administrator on 2019/2/27.
 */

public class BijiUtils {
    public static final String BOOK_MALL="在线书城";
    public static final String DEVIVE_NAME="Topsir电子书-";
    public static final String WRITE_NOTE="手写记本";
//印象笔记开始
    private static final String CONSUMER_KEY = "18323282050";
    private static final String CONSUMER_SECRET = "06268a33207d85c9";

    private static final EvernoteSession.EvernoteService EVERNOTE_SERVICE = EvernoteSession.EvernoteService.SANDBOX;
    //印象笔记结束
    private static final boolean SUPPORT_APP_LINKED_NOTEBOOKS = true;
    public void startBijiInit(Application application){

            statrYingXiang(application);
    }
    private void statrYingXiang(Application application){
        new EvernoteSession.Builder(application)
                .setEvernoteService(EVERNOTE_SERVICE)
                .setSupportAppLinkedNotebooks(SUPPORT_APP_LINKED_NOTEBOOKS)
                .setForceAuthenticationInThirdPartyApp(true)
//                .setLocale(Locale.SIMPLIFIED_CHINESE)
                .build(CONSUMER_KEY, CONSUMER_SECRET)
                .asSingleton();
    }
}
