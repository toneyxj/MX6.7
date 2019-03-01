package com.moxi.filemanager.utils;

import android.content.Context;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Administrator on 2016/9/1.
 */
public class FileCopyUtils {
private Context context;
    public void startCopy(Context context,String oldpath,String path) throws IOException {
        this.context=context;
        File oldF=new File(oldpath);
//        if (isCan(oldF))return;
        if (oldF.isDirectory()){
            copyFolder(oldpath,path+File.separator+oldF.getName());
        }else{
            copyFile(oldpath,path,oldF.getName());
        }

    }
    /**
     * 复制单个文件
     *
     * @param oldPath String 原文件路径 如：c:/fqf.txt
     * @param newPath String 复制后路径 如：f:/fqf.txt
     * @return boolean
     */
    private void copyFile(String oldPath, String floder,String newPath) throws IOException {
            int bytesum = 0;
            int byteread = 0;
            File oldfile = new File(oldPath);
            if (oldfile.exists()) { //文件存在时
                InputStream inStream = new FileInputStream(oldPath); //读入原文件
                String newspath=getFilePath(floder, newPath,1);
                FileOutputStream fs = new FileOutputStream(newspath);
                byte[] buffer = new byte[2048];
                while ((byteread = inStream.read(buffer)) != -1) {
                    bytesum += byteread; //字节数 文件大小
                    fs.write(buffer, 0, byteread);
                }
                inStream.close();
                fs.close();
                FileCopyDispose.toCkeckFileIsPDF(context,new File(newspath));
            }

    }

    /**
     * 复制整个文件夹内容
     *
     * @param oldPath String 原文件路径 如：c:/fqf
     * @param newPath String 复制后路径 如：f:/fqf/ff
     * @return boolean
     */
    private void copyFolder(String oldPath, String newPath) throws IOException {

        (new File(newPath)).mkdirs(); //如果文件夹不存在 则建立新文件夹
        File a = new File(oldPath);
        String[] file = a.list();
        File temp = null;
        for (int i = 0; i < file.length; i++) {
            if (oldPath.endsWith(File.separator)) {
                temp = new File(oldPath + file[i]);
            } else {
                temp = new File(oldPath + File.separator + file[i]);
            }

            if (temp.isFile()) {
                FileInputStream input = new FileInputStream(temp);
                String newpath=newPath + "/" +
                        (temp.getName()).toString();
                File newFile=new File(getFilePath(newpath, (temp.getName()).toString(),1));
                FileOutputStream output = new FileOutputStream(newpath);
                byte[] b = new byte[1024 * 5];
                int len;
                while ((len = input.read(b)) != -1) {
                    output.write(b, 0, len);
                }
                output.flush();
                output.close();
                input.close();
            }
            if (temp.isDirectory()) {//如果是子文件夹
                copyFolder(oldPath + "/" + file[i], newPath + "/" + file[i]);
            }
        }
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
            String pathLast=filder+File.separator+oldPath.substring(0,prefix);
            int size=pathLast.length();
            if (pathLast.substring(size-3,size-2).equals("(")||pathLast.substring(size-1,size).equals(")")){
                pathLast=pathLast.substring(0,size-3);
            }
            String newPath=pathLast+"("+index+")"+oldPath.substring(prefix,oldPath.length());
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
