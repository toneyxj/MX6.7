package com.moxi.bookstore.utils;

import android.content.Context;

import com.mx.mxbase.utils.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/9/2.
 */
public class PathUtils {
    public static List<String> getExtSDCardPathList(Context activity) {
        List<String> paths = new ArrayList<String>();
        StorageList list = new StorageList(activity);

        String[] valus=list.getVolumePaths();

        for (int i = 0; i < valus.length; i++) {
            File file=new File(valus[i]);
            if (null!=file.listFiles())
            paths.add(valus[i]);
        }
        return paths;
    }
    public static List<String> getExtSDCardPathList(){
        return FileUtils.getInstance().getExtSDCardPathList();
    }
    public static boolean isExsit(String path){
        File file=new File(path);
        return file.exists();
    }
}
