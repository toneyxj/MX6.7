package com.moxi.bookstore.http.deal;

import android.content.Context;

import java.util.HashMap;

/**
 * Created by Administrator on 2016/9/27 0027.
 */
public class ParamsMap extends HashMap<String,Object> {
    public  ParamsMap(Context context){
        //put("deviceType","android");
        put("returnType","json");
        put("permanentId","");
        //put("token","e_b13c16ccfa316342fa310ccd80048146b05067a32042cf62d29740831f6d1f2f");
    }
}
