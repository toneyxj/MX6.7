package com.moxi.bookstore.utils;

import com.alibaba.fastjson.JSON;
import com.moxi.bookstore.BookstoreApplication;
import com.moxi.bookstore.modle.SearchBookModel;
import com.mx.mxbase.constant.APPLog;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by xj on 2017/8/1.
 */

public class SearchFileUtils {
    // 初始化类实列
    private static SearchFileUtils instatnce = null;

    /**
     * 获得软键盘弹出类实列
     *
     * @return 返回初始化实列
     */
    public static SearchFileUtils getInstance() {
        if (instatnce == null) {
            synchronized (SearchFileUtils.class) {
                if (instatnce == null) {
                    instatnce = new SearchFileUtils();
                }
            }
        }
        return instatnce;
    }

    public void saveFiles(Map<String, SearchBookModel> addedFiles) {
        List<SearchBookModel> models = new ArrayList<>();
        List<SearchBookModel> removesPath=new ArrayList<>();
        for (Map.Entry<String, SearchBookModel> entry : addedFiles.entrySet()) {
            models.add(entry.getValue());
        }
        for (Map.Entry<String, SearchBookModel> entry : addedFiles.entrySet()) {
            String key=entry.getKey();
            for (SearchBookModel model:models){
                if (model.addType==0&&key.startsWith(model.filePath)&&!key.equals(model.filePath)){
                    removesPath.add(entry.getValue());
                    break;
                }
            }
        }
        for (SearchBookModel var:removesPath){
            models.remove(var);
        }
        APPLog.e(models.toString());
        String js = JSON.toJSON(models).toString();
        BookstoreApplication.editor.putString("selectFils", js);
        BookstoreApplication.editor.commit();
    }


    public Map<String, SearchBookModel> getFiles() {
        Map<String, SearchBookModel> map = new HashMap<>();
        String fils = BookstoreApplication.preferences.getString("selectFils", "");
        if (fils.equals("")) {
            String path = PathUtils.getExtSDCardPathList().get(0) + "/Books";
            File file=new File(path);
            if (!file.exists())file.mkdirs();

            map.put(path, new SearchBookModel().init(path));

        } else {
            List<SearchBookModel> models= JSON.parseArray(fils,SearchBookModel.class);
            for (SearchBookModel model:models){
                map.put(model.filePath,model);
            }
        }
        return map;
    }
    public List<SearchBookModel> getFils(){
        String fils = BookstoreApplication.preferences.getString("selectFils", "");
        List<SearchBookModel> models;
        if (fils.equals("")) {
            String path = PathUtils.getExtSDCardPathList().get(0) + "/Books";
            File file=new File(path);
            if (!file.exists())file.mkdirs();

            models=new ArrayList<>();
            models.add(new SearchBookModel().init(path));
        } else {
            models=  JSON.parseArray(fils,SearchBookModel.class);
        }
        return models;
    }

}
