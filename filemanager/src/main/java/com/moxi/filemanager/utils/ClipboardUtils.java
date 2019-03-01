package com.moxi.filemanager.utils;

import com.moxi.filemanager.model.ClipboardType;
import com.mx.mxbase.constant.APPLog;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 剪切板
 * Created by Administrator on 2016/11/18.
 */
public class ClipboardUtils {
    // 初始化类实列
    private static ClipboardUtils instatnce = null;

    /**
     * 获得软键盘弹出类实列
     *
     * @return 返回初始化实列
     */
    public static ClipboardUtils getInstance() {
        if (instatnce == null) {
            synchronized (ClipboardUtils.class) {
                if (instatnce == null) {
                    instatnce = new ClipboardUtils();
                }
            }
        }
        return instatnce;
    }

    /**
     * 操作类型
     */
    private ClipboardType type;
    /**
     * 是否拥有事件
     */
    public boolean isHaveEvent = false;
    /**
     * 事件操作文件集合
     */
    public List<File> eventFiles;

    /**
     * 初始化数据
     *
     * @param type       操作类型
     * @param eventFiles 操作文件集合
     */
    public void init(ClipboardType type, List<File> eventFiles) {
        this.type = type;
        this.isHaveEvent = true;
        this.eventFiles = eventFiles;
    }

    /**
     * 初始化数据
     *
     * @param type 操作类型
     * @param file 操作文件
     */
    public void init(ClipboardType type, File file) {
        this.type = type;
        this.isHaveEvent = true;
        this.eventFiles = new ArrayList<>();
        eventFiles.add(file);
    }

    /**
     * 清除操作
     */
    public void ClearClipboard(int i) {
        APPLog.e("执行了ClearClipboard="+i);
        this.type = null;
        this.isHaveEvent = false;
        this.eventFiles = null;
    }

    public String getTypeString() {
        if (type == null) return "";
        String txt = "";
        switch (type) {
            case COPY:
                txt = "复制";
                break;
            case CUT:
                txt = "移动";
                break;
            default:
                break;
        }
        return txt;
    }

    public ClipboardType getType() {
        return type;
    }

    public void setType(ClipboardType type) {
        this.type = type;
    }

    /**
     * 获得不同的文件路径
     *
     * @return
     */
    public List<String> getDifferentPath(String setParent) {
        List<String> list = new ArrayList<>();
        for (File file : eventFiles) {
            if (!list.contains(file.getParent())&&!setParent.equals(file.getParent())) {
                list.add(file.getParent());
            }
        }
//        APPLog.e("总体移动文件路",list.toString());
        return list;
    }
}
