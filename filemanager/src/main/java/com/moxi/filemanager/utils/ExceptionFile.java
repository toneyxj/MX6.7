package com.moxi.filemanager.utils;

import com.mx.mxbase.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 天机列外文件
 * Created by Administrator on 2016/9/28.
 */
public class ExceptionFile {
    private static final String mid=StringUtils.getSDCardPath()+"/";
    private static List<String> exceptionFiles=new ArrayList<>();
     static{
         exceptionFiles.add(mid+"Books/help.pdf");
         exceptionFiles.add(mid+"Books/zhude.pdf");
         exceptionFiles.add(mid+"Android");
         exceptionFiles.add(mid+"onyx_cover");
    }

    /**
     *
     * @param files 包含文件绝对路径返回true
     * @return
     */
    public static boolean getIscontains(String files){
        return exceptionFiles.contains(files);
    }
}
