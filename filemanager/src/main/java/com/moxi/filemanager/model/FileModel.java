package com.moxi.filemanager.model;

import com.moxi.filemanager.R;
import com.mx.mxbase.constant.PhotoConfig;
import com.mx.mxbase.utils.StringUtils;

import java.io.File;

/**
 * 文件管理展示数据类
 * Created by Administrator on 2016/8/29.
 */
public class FileModel {
    public File file;
    public boolean isSelect = false;
//    public String pingyin = "";

    public FileModel(File file) {
        this.file = file;
//        getPingyin();
        getStartAssic();
    }
    public void resetFile(File file){
        this.file = file;
//        getPingyin();
        getStartAssic();
    }

    public FileModel() {
    }

    public void changeSelect() {
        isSelect = !isSelect;
    }

    private String startAssic = "";

    public String getStartAssic() {
        if (startAssic.equals("")) {
            int end = subStartAscii(getFileName());
            if (end > 0)
                startAssic = getFileName().substring(0, end);
        }
        return startAssic;
    }

    private int subStartAscii(String str) {
        int index=str.lastIndexOf(".");
        if (index>0) {
            str = str.substring(0, index);
        }
        int end = 0;
        for (int i = 0; i < str.length(); i++) {
            int chr = str.charAt(i);
            if (chr >= 33 && chr <= 126) {
                end++;
            } else {
                break;
            }
        }
        if (end>1) {
            int chr = str.charAt(end - 1);
            if (chr>=58){
                end--;
            }
        }
        return end;
    }

    /**
     * 获得文件显示图片
     *
     * @return
     */
    public int ShowDrawableId() {
        if (file.isDirectory()) {
            return R.mipmap.filoder;
        } else {
            if (getFileName().contains(".")) {
                String prefix = getFileName().substring(getFileName().lastIndexOf(".") + 1);
                prefix = prefix.toLowerCase();
                if (prefix.equals("jpg") || prefix.equals("png") || prefix.equals("jpeg")) {
                    return R.mipmap.image_jpg;
                } else if (PhotoConfig.getAllFileType().contains(prefix)) {
                    return PhotoConfig.getSources(getFileName());
                } else if (file.getAbsolutePath().equals(StringUtils.getSDCardPath() + "/update.zip")) {
                    return R.mipmap.update_file;
                } else {
                    //未知
                    return R.mipmap.unknown;
                }
            } else {
                //未知
                return R.mipmap.unknown;
            }

        }
    }

    /**
     * @return 0文件夹，1图片，2pdf文件，3txt文件，4未知
     */
    public int getFileType() {
        if (file.isDirectory()) return 0;
        if (getFileName().contains(".")) {
            String prefix = getFileName().substring(getFileName().lastIndexOf(".") + 1);
            prefix = prefix.toLowerCase();
            if (prefix.equals("jpg") || prefix.equals("png") || prefix.equals("jpeg") || prefix.equals("bmp")) {
                return 1;
            } else if (PhotoConfig.getAllFileType().contains(prefix)) {
                return 2;
            }
//            else if (prefix.equals("txt")) {
//                return 3;
//            } else if (prefix.equals("pdf")||prefix.equals("epub")||prefix.equals("mobi")||prefix.equals("fb2")||prefix.equals("chm")||prefix.equals("doc")||prefix.equals("docx")) {
//                return 2;
//            }
            else {
                return 4;
            }
        } else {
            return 4;
        }
    }

    public String getSuffix() {
        if (file.isDirectory()) return "";
        if (getFileName().contains(".")) {
            String prefix = getFileName().substring(getFileName().lastIndexOf(".") + 1);
            return prefix;
        } else {
            return "";
        }
    }

    /**
     * 获得文件名
     *
     * @return
     */
    public String getFileName() {
        return file.getName();
    }

//    public String getPingyin() {
//        if (pingyin.equals("")) {
//            pingyin = PinyinUtils.getPingYin(getFileName()).toString();
//        }
//        return pingyin;
//
//    }

    /**
     * 获得文件路径
     *
     * @return
     */
    public String getFilePath() {
        return file.getPath();
    }

    @Override
    public String toString() {
        return "FileModel{" +
                "file=" + getFilePath() +
                ", isSelect=" + isSelect +
                '}';
    }
}
