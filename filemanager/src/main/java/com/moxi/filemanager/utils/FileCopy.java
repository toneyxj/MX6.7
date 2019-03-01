package com.moxi.filemanager.utils;

import android.content.Context;
import android.os.AsyncTask;

import com.mx.mxbase.constant.APPLog;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.List;

/**
 * Created by Administrator on 2016/9/1.
 */
public class FileCopy extends AsyncTask<String, Void, Boolean> {
    private CopyListener back;// 传入接口
    private List<File> folders;// 查询文件夹
    private WeakReference<Context> context;
    //初始化文件处理类
    private  FileCopyUtils utils=new FileCopyUtils();
    private  String goalPath;//目标路径

    /**
     * 移动文件构造方法
     * @param context 当前上下文
     * @param files 移动的文件集合
     * @param back 移动返回
     */
    public FileCopy(Context context, List<File> files, String goalPath,CopyListener back) {
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
    protected Boolean doInBackground(String... arg0) {
        //获得所有目录的觉得路径
boolean isNoMove=false;
        try {
            for (File file:folders) {
                //不能是父类
//                if (!file.getParent().equals(goalPath)&&!goalPath.contains(file.getAbsolutePath())) {
//                    utils.startCopy(file.getAbsolutePath(), goalPath);
//                }
                if (isCan(file,goalPath)) {
                    isNoMove=true;
                    utils.startCopy(context.get(),file.getAbsolutePath(), goalPath);
                }
            }
            return isNoMove;
        } catch (IOException e) {
            return false;
        }

    }

    /**
     * 是否允许复制
     * @param file 复制的文件夹
     * @param goalPath 复制到的目标路径
     * @return
     */
    private boolean isCan(File file,String goalPath){
        APPLog.e("file.getAbsolutePath()="+file.getAbsolutePath());
        APPLog.e("goalPath="+goalPath);

        if (goalPath.contains(file.getAbsolutePath())){
            String value=goalPath.replace(file.getAbsolutePath(),"");
            APPLog.e("value="+value);
            if (value.equals("")||(value.substring(0,1)).equals("/")){
                return false;
            }else {
                return true;
            }
        }else if (file.getParent().equals(goalPath)&&file.isDirectory()){
            return false;
        }else {
            return true;
        }
    }


    @Override
    protected void onPostExecute(Boolean result) {// 在doInBackground执行完成后系统会自动调用，result是返回值
        if (isFinish() != null&&back!=null)
            back.CopyListener(result);
    }

    /**
     * Created by Administrator on 2016/4/1.
     */
    public interface CopyListener {
        /*
         *
         * 是否移动成功
         */
        public void CopyListener(boolean results);
    }
}