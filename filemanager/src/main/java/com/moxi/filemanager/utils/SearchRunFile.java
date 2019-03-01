package com.moxi.filemanager.utils;

import com.moxi.filemanager.model.FileModel;
import com.mx.mxbase.utils.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by xj on 2017/11/28.
 */

public class SearchRunFile extends Thread {
    private boolean isFinish=false;
    private String key;
    private SearchRunFileListener listener;
//    private String[] cards=new String[]{"/mnt/sdcard","/mnt/extsd"};
    private List<String> cards= FileUtils.getInstance().getExtSDCardPathList();

    public SearchRunFile(String key,SearchRunFileListener listener) {
        this.key = key;
        this.listener=listener;
    }

    public void cancle() {
        isFinish = true;
    }
    @Override
    public void run() {
        super.run();
        List<FileModel> list = new ArrayList<>();
        for (int h = 0; h < cards.size(); h++) {
            if (isFinish)return ;
            list.addAll(getFiles(cards.get(h)));
        }
        if (!isFinish){
            listener.onSearcFile(list);
        }
    }
    private List<FileModel> getFiles(String path){
        List<FileModel> list = new ArrayList<>();
            File file = new File(path);
            File[] files = file.listFiles();
            if (files != null) {
                for (int i = 0; i < files.length; i++) {
                    if (isFinish) return list;
                    if (files[i].isDirectory()){
                        list.addAll(getFiles(files[i].getAbsolutePath()));
                        continue;
                    }
                    if (files[i].getName().substring(0, 1).equals(".") || files[i].getName().equals("com.onyx.android.data"))
                        continue;
                    if (files[i].getAbsolutePath().equals(ByPathGetFiles.E_DOWNLOAD_DIR)) continue;
                    if (!files[i].canRead()) continue;
                    if (files[i].getName().contains(key)) {
                        FileModel model = new FileModel(files[i]);
                        list.add(model);
                    }
                }
        }
        return list;
    }

    public interface  SearchRunFileListener{
        void onSearcFile( List<FileModel> list);
    }
}
