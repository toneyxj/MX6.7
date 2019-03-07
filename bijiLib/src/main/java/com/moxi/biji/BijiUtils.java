package com.moxi.biji;

import android.app.Application;

import com.evernote.client.android.EvernoteSession;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by Administrator on 2019/2/27.
 */

public class BijiUtils {
    private static final String BOOK_MALL = "在线书城";
    private static final String DEVIVE_NAME = "Topsir电子书-";
    private static final String WRITE_NOTE = "手写记本";
    //印象笔记开始
    private static final String CONSUMER_KEY = "18323282050";
    private static final String CONSUMER_SECRET = "06268a33207d85c9";

    private static final EvernoteSession.EvernoteService EVERNOTE_SERVICE = EvernoteSession.EvernoteService.PRODUCTION;
    //印象笔记结束
    private static final boolean SUPPORT_APP_LINKED_NOTEBOOKS = true;

    public static String getOnLineBookStore() {
        return DEVIVE_NAME + BOOK_MALL;
    }

    public void startBijiInit(Application application) {

        statrYingXiang(application);
    }

    private void statrYingXiang(Application application) {
        new EvernoteSession.Builder(application)
                .setEvernoteService(EVERNOTE_SERVICE)
                .setSupportAppLinkedNotebooks(SUPPORT_APP_LINKED_NOTEBOOKS)
                .setForceAuthenticationInThirdPartyApp(true)
//                .setLocale(Locale.SIMPLIFIED_CHINESE)
                .build(CONSUMER_KEY, CONSUMER_SECRET)
                .asSingleton();
    }

    /**
     * 读取文件
     *
     * @param filePath 文件名
     * @return 返回读取的数据
     * @throws IOException
     */
    public static String readFile(String filePath) {
        StringBuilder sb = new StringBuilder();
        BufferedReader br = null;
        try {
            File file = new File(filePath);
            br = new BufferedReader(new FileReader(file));
            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (br != null)
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
        if (sb.length()==0){
            return filePath;
        }
        return sb.toString();
    }
}
