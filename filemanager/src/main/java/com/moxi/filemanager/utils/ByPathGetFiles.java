package com.moxi.filemanager.utils;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;

import com.moxi.filemanager.model.FileModel;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * 通过路径获取文件集合
 * Created by Administrator on 2016/11/8.
 */
public class ByPathGetFiles extends AsyncTask<String, Void, List<FileModel>> {
    private SucessListener back;// 传入接口
    private WeakReference<Context> context;
    private String path;
    public static final  String E_DOWNLOAD_DIR= Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+"DDBooks";
    /**
     * 移动文件构造方法
     *
     * @param context 当前上下文
     * @param path    文件路径
     * @param back    移动返回
     */
    public ByPathGetFiles(Context context, String path, SucessListener back) {
        this.path = path;
        this.back = back;
        this.context = new WeakReference<Context>(context);
    }

    private Context isFinish() {
        Context context = this.context.get();
        return context;
    }

    @Override
    protected List<FileModel> doInBackground(String... arg0) {
        //获得所有目录的觉得路径
        List<FileModel> list = new ArrayList<>();
        File file = new File(path);
        File[] files = file.listFiles();
        if (files==null){
//            MyApplication.Toast("没有找到存储卡！！");
            return list ;
        }
        for (int i = 0; i < files.length; i++) {

            if (files[i].getName().substring(0, 1).equals(".")||files[i].getName().equals("com.onyx.android.data")) continue;
            if (files[i].getAbsolutePath().equals(E_DOWNLOAD_DIR)) continue;
            if (!files[i].canRead()) continue;
//            if (ExceptionFile.getIscontains(files[i].getAbsolutePath()))continue;
//            if (ConfigerUtils.isSysytemFile(files[i]))continue;
//            if (files[i].getAbsolutePath().equals(StringUtils.getSDCardPath()+"/ad.jpg"))continue;
            FileModel model = new FileModel(files[i]);
//            APPLog.e("读取文件路径="+model.getFilePath());
            list.add(model);
        }
        return list;
    }


    @Override
    protected void onPostExecute(List<FileModel> result) {// 在doInBackground执行完成后系统会自动调用，result是返回值
        if (back!=null)back.onSucess(result);
    }

    /**
     * Created by Administrator on 2016/4/1.
     */
    public interface SucessListener {
        /*
         *
         * 是否移动成功
         */
        public void onSucess(List<FileModel> sucess);
    }
}