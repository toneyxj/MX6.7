package com.dangdang.reader.moxiUtils.RunableUtils;

import android.os.Handler;

import com.dangdang.reader.moxiUtils.TTFModel;
import com.dangdang.reader.moxiUtils.TTFParser;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by xj on 2017/10/31.
 */

public class FindTTFFilesRunable implements Runnable {
    private Handler handler=new Handler();
    private String fontPath;
    private FindTTFFilesListener listener;
    private List<TTFModel> list = new ArrayList<>();
    public FindTTFFilesRunable(String fontPath,FindTTFFilesListener listener){
        this.listener=listener;
        this.fontPath=fontPath;
    }
    @Override
    public void run() {
        //添加本地asstes字体库
        list.add(new TTFModel("","","标准",true));
        list.add(new TTFModel(fontPath , "zhun_yuan.TTF","圆体",true));
        list.add(new TTFModel(fontPath , "hei_ti.TTF","黑体",true));
        list.add(new TTFModel(fontPath , "fang_son.TTF","仿宋",true));
        list.add(new TTFModel(fontPath , "kai_ti.TTF","楷体",true));
        String path = "/mnt/sdcard/fonts";
        File file = new File(path);
        if (file.isDirectory() && file.exists()) {
            File[] files = file.listFiles();
            if (null != files) {
                for (int i = 0; i < files.length; i++) {
                    File son = files[i];
                    String filename = son.getName();
                    String prefix = filename.substring(filename.lastIndexOf(".") + 1);
                    prefix = prefix.toLowerCase();
                    if (prefix.equals("ttf")&&son.isFile()&&son.canRead()) {
                        TTFParser ttfParser=new TTFParser();
                        try {
                            boolean is= ttfParser.parse(son.getAbsolutePath());
                            if (is){
                                TTFModel model=new TTFModel(son.getAbsolutePath(),ttfParser.getFontName());
                                list.add(model);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

        }
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (listener!=null){
                    listener.onTTFFiles(list);
                }
            }
        });
    }

    public interface FindTTFFilesListener{
        void onTTFFiles(List<TTFModel> ttfModel);
    }
}
