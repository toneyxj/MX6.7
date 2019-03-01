package com.moxi.filemanager.utils;

import android.content.Context;
import android.os.AsyncTask;

import com.mx.mxbase.utils.StringUtils;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * 移动文件
 * Created by Administrator on 2016/8/31.
 */
public class MoveFile extends AsyncTask<String, Void, String> {
    private MoveListener back;// 传入接口
    private  List<File> folders;// 查询文件夹
    private WeakReference<Context> context;
    //初始化文件处理类
    private  FileOperationUtils utils=new FileOperationUtils();
    //初始化文件处理类
    private  FileCopyUtils copyUtils=new FileCopyUtils();
    private  String goalPath;//目标路径
    private List<File> movefiles=new ArrayList<>();

    /**
     * 移动文件构造方法
     * @param context 当前上下文
     * @param files 移动的文件集合
     * @param back 移动返回
     */
    public MoveFile(Context context, List<File> files, String goalPath,MoveListener back) {
        this.back = back;
        this.folders = files;
        this.context = new WeakReference<Context>(context);
        this.goalPath=goalPath;
    }

    private Context isFinish() {
        Context context = this.context.get();
        return context;
    }

    @Override
    protected String doInBackground(String... arg0) {
        //获得所有目录的觉得路径
//        utils.startMove(folders,goalPath);
        boolean isNoMove=false;
        try {
            for (File file:folders) {
                //不能是父类
                if (isCan(file,goalPath)) {
                    isNoMove=true;
                    List<File> files=new ArrayList<>();
                    files.add(file);
                    movefiles.add(file);
                    if (file.getAbsolutePath().split("/")[2].equals(goalPath.split("/")[2])) {
                        utils.startMove(context.get(),files, goalPath);
                    }else {
                        copyUtils.startCopy(context.get(),file.getAbsolutePath(), goalPath);
//                        StringUtils.deleteFile(file);
                    }
                }
            }
            return isNoMove?"移动成功":"目标目录不可粘贴";
        } catch (Exception e) {
            return "移动错误";
        }
    }
    /**
     * 是否允许复制
     * @param file 复制的文件夹
     * @param goalPath 复制到的目标路径
     * @return
     */
    private boolean isCan(File file,String goalPath){
        if (goalPath.equals(file.getParent())){
            return false;
        }
        if (goalPath.contains(file.getAbsolutePath())){
            String value=goalPath.replace(file.getAbsolutePath(),"");
            if (value.equals("")||(value.substring(0,1)).equals("/")){
                return false;
            }else {
                return true;
            }
        }else {
            return true;
        }
    }

    @Override
    protected void onPostExecute(String result) {// 在doInBackground执行完成后系统会自动调用，result是返回值
        if (isFinish()!=null){
            for (File f:movefiles){
                StringUtils.deleteFile(f);
            }
        }
        if (isFinish() == null||back==null)return;
        if (result.equals("移动成功")) {
            back.moveSucess(true,result);
        }else {
            back.moveSucess(false,result);
        }
    }

    /**
     * Created by Administrator on 2016/4/1.
     */
    public interface MoveListener {
        /*
         *
         * 是否移动成功
         */
        public void moveSucess(boolean results,String log);
    }
}