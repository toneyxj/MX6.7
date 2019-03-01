package com.moxi.bookstore.bean;

import android.content.Context;

import com.moxi.bookstore.utils.ToolUtils;

/**
 * Created by Administrator on 2016/9/20.
 */
public class Status {
    private Integer code;
    private String message;
    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * token失效
     */
    public boolean tokenNoEfficacy(){
        return code==10003;
    }
    /**
     * token失效
     */
    public boolean tokenNoEfficacy(Context context){
        boolean is= code==ToolUtils.noEfficacy;
        if (is){//token失效处理
            ToolUtils.getIntence().startDDUserBind(context);
        }
        return is;
    }

}
