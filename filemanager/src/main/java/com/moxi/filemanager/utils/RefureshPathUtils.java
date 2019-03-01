package com.moxi.filemanager.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * 刷新目录
 * Created by 夏君 on 2017/3/13 0013.
 */

public class RefureshPathUtils {
    private static RefureshPathUtils instatnce = null;
    public static RefureshPathUtils getInstance() {
        if (instatnce == null) {
            synchronized (RefureshPathUtils.class) {
                if (instatnce == null) {
                    instatnce = new RefureshPathUtils();
                }
            }
        }
        return instatnce;
    }
    private List<String> refureshPaths=new ArrayList<>();
    public void addRefureshPath(String path){
        if (refureshPaths.contains(path))return;
        refureshPaths.add(path);
    }
    public boolean judgeRefureshPath(String path){
        boolean is=refureshPaths.contains(path);
        if (is){
            refureshPaths.remove(path);
        }
        return is;
    }
    public void removeRefureshPath(String path){
        refureshPaths.remove(path);
    }
    public void clearRefuresh(){
        refureshPaths.clear();
    }
}
