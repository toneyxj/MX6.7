package com.moxi.filemanager.configer;

import com.mx.mxbase.utils.StringUtils;
import com.mx.mxbase.utils.ToastUtils;

import java.io.File;

/**
 * Created by xiajun on 2017/3/1.
 */

public class ConfigerUtils {
    public static final String hitnInput = "输入文件名不能包含/ % -特殊字符";
    public static  final String[] systemFiles=new String[]{"Alarms","DCIM","dictionary","ivona",
            "LOST.DIR","Movies","Notifications","Podcasts","quickDic",
            "Ringtones","svox","user_manual","ddReader_offprint",
    "baseData","OReader","System Volume Information"};

    public static Integer[] showFileSizes=new Integer[]{20,10};


    public static boolean isFail(String name) {
        name = name.trim();
        if (name.equals("")) {
            ToastUtils.getInstance().showToastShort("输入不能为空");
            return true;
        } else if (name.contains("/") || name.contains("%") || name.contains("-")) {
            ToastUtils.getInstance().showToastShort("文件名称不能包含/ % -等特殊字符");
            return true;
        } else if (name.length() > 100) {
            ToastUtils.getInstance().showToastShort("文件名长度不能超过100个字符");
            return true;
        }
        return false;
    }

    /**
     *
     * @return 是否是系统文件
     */
    public static boolean isSysytemFile(File file){
        String rootFile= StringUtils.getSDCardPath();
        if (file.getParent().equals(rootFile)){
            String name=file.getName();
            for (String fileName:ConfigerUtils.systemFiles){
                if (name.equals(fileName)){
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 带提示的系统文件判断
     * @param file 文件夹
     * @return 是否是系统文件
     */
    public static  boolean isTostSystemFile(File file){
        boolean is=isSysytemFile(file);
        if (is){
            ToastUtils.getInstance().showToastShort("系统文件名不可操作");
        }
        return is;
    }

    public static int getShowFileSize(int style){
        return showFileSizes[style];
    }
}
