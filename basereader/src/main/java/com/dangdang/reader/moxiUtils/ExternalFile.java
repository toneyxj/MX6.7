package com.dangdang.reader.moxiUtils;

import com.dangdang.reader.dread.jni.BaseJniWarp;

import java.io.File;

/**
 * 外部文件数据集
 * Created by Administrator on 2016/9/23.
 */
public class ExternalFile {
    public ExternalFile(){
        id=String.valueOf(System.currentTimeMillis());
    }
    public String id;
    public String filePath="";
    public String bookName="";
    /**
     *
     * @return 0文件夹，1图片，2pdf文件，3txt文件，4未知
     */
    public int  getFileType(){
            String prefix = filePath.substring(filePath.lastIndexOf(".") + 1);
             if (prefix.equals("txt")) {
                return BaseJniWarp.BOOKTYPE_DD_TXT;
            } else if (prefix.equals("pdf")) {
                return BaseJniWarp.BOOKTYPE_DD_PDF;
            }else if (prefix.equals("epub")) {
                 return BaseJniWarp.BOOKTYPE_THIRD_EPUB;
             }else if (prefix.equals("mobi")) {
                 return BaseJniWarp.BOOKTYPE_THIRD_EPUB;
             }
       return BaseJniWarp.BOOKTYPE_DD_DRM_EPUB;
    }
    public boolean isJudge(){
        File file=new File(filePath);
        if (file.isDirectory()||!file.exists())return false;
        return true;
    }
}
