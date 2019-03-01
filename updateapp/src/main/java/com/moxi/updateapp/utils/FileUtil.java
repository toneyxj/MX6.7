package com.moxi.updateapp.utils;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

/**
 * Created by zhengdelong on 2016/10/26.
 */

public class FileUtil {

    private static final String filePath = Environment.getExternalStorageDirectory().getAbsolutePath()
            + File.separator + "mx";
    private static final String fileName = "log.txt";

    public static void saveCode(String code) {
        Log.d("update","code===>" + code);
        Log.d("update","filePath===>" + filePath);
        File file = new File(filePath + File.separator +fileName);
        if (file.exists()){
            file.delete();
        }
        Log.d("update","saveCode ====> " + code);
        writeTxtToFile(code, filePath, fileName);
    }

    public static String getCode(){
        StringBuffer sb = new StringBuffer();
        try{
            File file=new File(filePath + File.separator + fileName);
            if(!file.exists()||file.isDirectory())
                throw new FileNotFoundException();
            FileInputStream fis=new FileInputStream(file);
            byte[] buf = new byte[1024];
            while((fis.read(buf))!=-1){
                sb.append(new String(buf));
//                buf=new byte[1024];//重新生成，避免和上次读取的数据重复
            }
        }catch (Exception e){
            return "";
        }

        return sb.toString();
    }

    // 将字符串写入到文本文件中
    public static void writeTxtToFile(String strcontent, String filePath, String fileName) {
        //生成文件夹之后，再生成文件，不然会出错
        makeFilePath(filePath, fileName);

        String strFilePath = filePath + File.separator + fileName;
        try {
            File file = new File(strFilePath);
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile();
            }

            FileOutputStream fout = new FileOutputStream(strFilePath);
            byte[] bytes = strcontent.getBytes();
            fout.write(bytes,0,bytes.length);
            fout.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 生成文件
    public static File makeFilePath(String filePath, String fileName) {
        File file = null;
        makeRootDirectory(filePath);
        try {
            file = new File(filePath +"/"+ fileName);
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
    }

    // 生成文件夹
    public static void makeRootDirectory(String filePath) {
        File file = null;
        try {
            file = new File(filePath);
            if (!file.exists()) {
                file.mkdir();
            }
        } catch (Exception e) {
        }
    }
    public static void startDownloadSystem(Context context){

    }
}
