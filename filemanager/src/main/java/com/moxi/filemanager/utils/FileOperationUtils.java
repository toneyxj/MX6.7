package com.moxi.filemanager.utils;

import android.content.Context;

import com.mx.mxbase.constant.APPLog;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created by Administrator on 2016/8/31.
 */
public class FileOperationUtils {
    private Context context;
    /**
     * 开启移动模式
     *
     * @param files    需要移动的文件集合
     * @param goalPath 目标文件路径
     */
    public void startMove(Context context,List<File> files, String goalPath) {
        this.context=context;
        File goalF = new File(goalPath);
        if (!goalF.isDirectory()) return;
        if (null == files || files.size() == 0) return;

        for (File filep : files) {
            APPLog.e("filep-name",filep.getAbsoluteFile());
            APPLog.e("filep-goalPath",goalPath);
//            if (goalPath.contains(filep.getAbsolutePath())) {
//                continue;
//            }
//            if (filep.getParent()!=null&&filep.getParent().equals(goalPath))continue;
            if (filep.isDirectory()) {
                moveDirectory(filep.getAbsolutePath(), goalPath+File.separator+filep.getName());
            } else {
                moveFile(filep.getAbsolutePath(), goalPath);
            }
        }

    }

    /**
     * 移动文件
     *
     * @param srcFileName 源文件完整路径
     * @param flofer 目的目录完整路径
     * @return 文件移动成功返回true，否则返回false
     */
    public void moveFile(String srcFileName,String flofer) {

        File srcFile = new File(srcFileName);

        File destDir = new File(flofer);
        if (!destDir.exists())
            destDir.mkdirs();
        String destDirName=srcFile.getName();

        String goalF = getFilePath(flofer,destDirName,1);
//        String goalF = flofer + File.separator + srcFile.getName();
        File file = new File(goalF);

        if (!file.exists()) {
            try {
                file.createNewFile();
                srcFile.renameTo(file);
//                FileCopyDispose.toCkeckFileIsPDF(context,file);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }

    /**
     * 移动目录
     *
     * @param srcDirName  源目录完整路径
     * @param destDirName 目的目录完整路径
     * @return 目录移动成功返回true，否则返回false
     */
    public void moveDirectory(String srcDirName, String destDirName) {
        File srcDir = new File(srcDirName);
        File destDir = new File(destDirName);
        if (!destDir.exists()) {
            destDir.mkdirs();
        }
        /**
         * 如果是文件则移动，否则递归移动文件夹。删除最终的空源文件夹
         * 注意移动文件夹时保持文件夹的树状结构
         */
        File[] sourceFiles = srcDir.listFiles();

        for (File sourceFile : sourceFiles) {
            if (sourceFile.isFile()) {
                moveFile(sourceFile.getAbsolutePath(), destDir.getAbsolutePath());
            } else if (sourceFile.isDirectory()) {
                moveDirectory(sourceFile.getAbsolutePath(),
                        destDir.getAbsolutePath() + File.separator + sourceFile.getName());
            }
        }
        srcDir.delete();
    }

    /**
     * 判断是否有存在文件并返回路径值
     * @param filder 文件夹路径
     * @param oldPath 原始文件名
     * @param index 文件索引值
     * @return
     */
    private String getFilePath(String filder,String oldPath,int index){
        File newFile=new File(filder+File.separator+oldPath);
        if (newFile.exists()){
            int prefix=oldPath.lastIndexOf(".");
            if (prefix==-1){
                prefix=oldPath.length();
            }
            String newPath=filder+File.separator+oldPath.substring(0,prefix)+"("+index+")"+oldPath.substring(prefix,oldPath.length());
            File file=new File(newPath);
            if (file.exists()){
                return getFilePath(filder,oldPath,++index);
            }else{
                return newPath;
            }
        }else{
            return filder+File.separator+oldPath;
        }
    }
}
