package com.mx.mxbase.model;

/**
 * Created by xj on 2018/8/8.
 */

public class LocationBookInfo {
    private  String path;
    private  String currentpage;
    private  String totalpage;

    public LocationBookInfo(String path, String currentpage, String totalpage) {
        this.path = path;
        this.currentpage = currentpage;
        this.totalpage = totalpage;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getCurrentpage() {
        int cur=0;
        try {
            cur=Integer.parseInt(currentpage);
        }catch (Exception e){

        }
        return cur;
    }

    public void setCurrentpage(String currentpage) {
        this.currentpage = currentpage;
    }

    public int getTotalpage() {
        int cur=0;
        try {
            cur=Integer.parseInt(totalpage);
        }catch (Exception e){

        }
        return cur;
    }

    public void setTotalpage(String totalpage) {
        this.totalpage = totalpage;
    }
}
